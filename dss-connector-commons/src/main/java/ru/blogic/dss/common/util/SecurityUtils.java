package ru.blogic.dss.common.util;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.ws.security.util.AccessController;
import com.ibm.ws.util.Base64;
import com.ibm.wsspi.security.token.SingleSignonToken;
import ru.blogic.dss.api.dto.KeyPair;

import javax.ejb.SessionContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Set;

/**
 * @author dgolubev
 */
public class SecurityUtils {

    private static final String SESSION_CONTEXT_JNDI_NAME = "java:comp/EJBContext";
    private static final String CERT_TYPE = "X.509";

    private SecurityUtils() {
    }

    public static String getCallerName() {
        final SessionContext ctx = ServiceLocator.getService(SESSION_CONTEXT_JNDI_NAME);
        return ctx.getCallerPrincipal().getName();
    }

    public static String getLtpaTokenCookie() {
        final SingleSignonToken token = (SingleSignonToken) AccessController.doPrivileged(new PrivilegedAction<SingleSignonToken>() {
            @Override
            public SingleSignonToken run() {
                try {
                    final Set<SingleSignonToken> tokens = WSSubject.getCallerSubject().getPrivateCredentials(SingleSignonToken.class);
                    for (SingleSignonToken token : tokens) {
                        if ("LtpaToken".equalsIgnoreCase(token.getName())) {
                            return token;
                        }
                    }
                    return null;
                } catch (WSSecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return "LtpaToken2=" + Base64.encode(token.getBytes());
    }

    public static KeyPair toKeyPair(byte[] content, String password) {
        final KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Unable to create keystore", e);
        }
        try {
            keyStore.load(new ByteArrayInputStream(content), password.toCharArray());
        } catch (IOException e) {
            if (e.getCause() instanceof UnrecoverableKeyException) {
                throw new RuntimeException("Invalid keystore password");
            } else {
                throw new RuntimeException("Invalid keystore format");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final Enumeration<String> aliases;
        try {
            aliases = keyStore.aliases();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        if (!aliases.hasMoreElements()) {
            throw new RuntimeException("Keystore is empty");
        }

        final String alias = aliases.nextElement();

        final PrivateKey privateKey;
        try {
            privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException("Invalid private key password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final X509Certificate cert;
        try {
            cert = (X509Certificate) keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        final Certificate[] certChain;
        try {
            certChain = keyStore.getCertificateChain(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        return new KeyPair(cert, privateKey, certChain);
    }

    public static X509Certificate toX509Cert(byte[] certBytes) {
        final CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance(CERT_TYPE);
        } catch (CertificateException e) {
            throw new RuntimeException(String.format("Unable to create factory for the certificates of type %s", CERT_TYPE), e);
        }
        try {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (CertificateException e) {
            throw new RuntimeException(
                    String.format("Unable to generate certificate of type %s from the provided content", CERT_TYPE), e
            );
        }
    }
}
