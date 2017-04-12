package ru.blogic.dss.common.callback;

import com.sun.xml.wss.impl.callback.KeyStoreCallback;
import com.sun.xml.wss.impl.callback.PrivateKeyCallback;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.KeyPair;
import ru.blogic.dss.common.util.ServiceLocator;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.TrustStoreCallback;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
public class KeyStoreCallbackHandler implements CallbackHandler {

    private static final char[] DUMMY_PASSWORD = "iddqd".toCharArray();

    private static final String LECM_STS_CERT_ALIAS = "lecm-sts.cert";
    private static final String LECM_STS_KEY_ALIAS = "lecm-sts.key";
    private static final String DSS_CERT_ALIAS = "dss.cert";

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof KeyStoreCallback) {
                handleCallback((KeyStoreCallback) callback);
            } else if (callback instanceof TrustStoreCallback) {
                handleCallback((TrustStoreCallback) callback);
            } else if (callback instanceof PrivateKeyCallback) {
                handleCallback((PrivateKeyCallback) callback);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }

    private void handleCallback(KeyStoreCallback callback) {
        callback.setKeystore(loadKeyStore());
    }

    private void handleCallback(TrustStoreCallback callback) {
        callback.setTrustStore(loadKeyStore());
    }

    private void handleCallback(PrivateKeyCallback callback) {
        try {
            KeyStore keyStore = loadKeyStore();
            callback.setKeystore(keyStore);
            final Key key = keyStore.getKey(callback.getAlias(), DUMMY_PASSWORD);
            callback.setKey((PrivateKey) key);
        } catch (Exception e) {
            throw new RuntimeException("Unable to handle callback " + callback.getClass().getName(), e);
        }
    }

    private KeyStore loadKeyStore() {
        try {
            final DssConfigService dssConfigService = ServiceLocator.getService(DssConfigService.class);

            final KeyPair keyPair = dssConfigService.getLecmStsKeyPair();
            final X509Certificate dssCert = dssConfigService.getDssCertificate();

            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, DUMMY_PASSWORD);

            keyStore.setKeyEntry(
                    LECM_STS_KEY_ALIAS,
                    keyPair.getPrivateKey(),
                    DUMMY_PASSWORD,
                    keyPair.getCertificateChain()
            );
            keyStore.setCertificateEntry(LECM_STS_CERT_ALIAS, keyPair.getCertificate());
            keyStore.setCertificateEntry(DSS_CERT_ALIAS, dssCert);

            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load keystore", e);
        }
    }
}
