package ru.blogic.dss.wstrust;

import com.sun.xml.ws.api.security.trust.Status;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.elements.BaseSTSRequest;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.elements.RequestedAttachedReference;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.elements.SecondaryParameters;
import com.sun.xml.ws.security.trust.impl.WSTrustClientContractImpl;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import com.sun.xml.ws.security.wsu10.AttributedDateTime;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import ru.blogic.dss.domain.DssRequestSecurityTokenResponseImpl;

import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dgolubev
 */
public class DssWSTrustClientContractImpl extends WSTrustClientContractImpl {

    private static final Logger logger = Logger.getLogger(DssWSTrustClientContractImpl.class.getName());

    DssWSTrustClientContractImpl() {
    }

    @Override
    public boolean containsChallenge(RequestSecurityTokenResponse rstr) {
        if (rstr instanceof DssRequestSecurityTokenResponseImpl) {
            DssRequestSecurityTokenResponseImpl dssRSTR
                    = (DssRequestSecurityTokenResponseImpl) rstr;
            return null != dssRSTR.getChallenge()
                    && dssRSTR.getChallenge().getTextChallenge() != null
                    && !dssRSTR.getChallenge().getTextChallenge().isEmpty();
        }
        return false;
    }

    @Override
    public void handleRSTR(
            final BaseSTSRequest request, final BaseSTSResponse response, final IssuedTokenContext context) throws WSTrustException {
        WSTrustVersion wstVer = WSTrustVersion.getInstance(((STSIssuedTokenConfiguration) context.getSecurityPolicy().get(0)).getProtocol());
        RequestSecurityToken rst = (RequestSecurityToken) request;
        RequestSecurityTokenResponse rstr;
        if (response instanceof RequestSecurityTokenResponse) {
            rstr = (RequestSecurityTokenResponse) response;
        } else if (response instanceof RequestSecurityTokenResponseCollection) {
            rstr = ((RequestSecurityTokenResponseCollection) response).getRequestSecurityTokenResponses().get(0);
        } else {
            throw new NullPointerException("RSTR is incorrect");
        }
        if (rst.getRequestType().toString().equals(wstVer.getIssueRequestTypeURI())) {

            String appliesTo = null;
            AppliesTo requestAppliesTo = rst.getAppliesTo();
            if (requestAppliesTo != null) {
                appliesTo = WSTrustUtil.getAppliesToURI(requestAppliesTo);
            }

            final RequestedSecurityToken securityToken = rstr.getRequestedSecurityToken();

            if (securityToken == null) {
                throw new RuntimeException("Can not confirm transaction: invalid confirmation text?");
            }

            // Requested References
            final RequestedAttachedReference attachedRef = rstr.getRequestedAttachedReference();
            final RequestedUnattachedReference unattachedRef = rstr.getRequestedUnattachedReference();

            // RequestedProofToken
            final RequestedProofToken proofToken = rstr.getRequestedProofToken();

            // Obtain the secret key for the context
            final byte[] key = getKey(wstVer, rstr, proofToken, rst, appliesTo);

            if (key != null) {
                context.setProofKey(key);
            }

            //get the creation time and expires time and set it in the context
            setLifetime(rstr, context);

            // if securityToken == null and proofToken == null
            // throw exception

            context.setSecurityToken(securityToken.getToken());

            if (attachedRef != null) {
                context.setAttachedSecurityTokenReference(attachedRef.getSTR());
            }

            if (unattachedRef != null) {
                context.setUnAttachedSecurityTokenReference(unattachedRef.getSTR());
            }
        } else if (rst.getRequestType().toString().equals(wstVer.getValidateRequestTypeURI())) {
            Status status = rstr.getStatus();
            context.getOtherProperties().put(IssuedTokenContext.STATUS, status);
            final RequestedSecurityToken securityToken = rstr.getRequestedSecurityToken();
            if (securityToken != null) {
                context.setSecurityToken(securityToken.getToken());
            }
        }
    }

    private void setLifetime(final RequestSecurityTokenResponse rstr, final IssuedTokenContext context) {
        // Get Created and Expires from Lifetime
        final Lifetime lifetime = rstr.getLifetime();
        final AttributedDateTime created = lifetime.getCreated();
        final AttributedDateTime expires = lifetime.getExpires();

        // populate the IssuedTokenContext
        if (created != null) {
            context.setCreationTime(WSTrustUtil.parseAttributedDateTime(created));
        } else {
            context.setCreationTime(new Date());
        }
        if (expires != null) {
            context.setExpirationTime(WSTrustUtil.parseAttributedDateTime(expires));
        }
    }

    private byte[] getKey(final WSTrustVersion wstVer, final RequestSecurityTokenResponse rstr, final RequestedProofToken proofToken, final RequestSecurityToken rst, final String appliesTo)
            throws WSTrustException {
        byte[] key = null;
        if (proofToken != null) {
            final String proofTokenType = proofToken.getProofTokenType();
            if (RequestedProofToken.COMPUTED_KEY_TYPE.equals(proofTokenType)) {
                key = computeKey(wstVer, rstr, proofToken, rst);
            } else if (RequestedProofToken.TOKEN_REF_TYPE.equals(proofTokenType)) {
                //ToDo
                logger.log(Level.SEVERE,
                        LogStringsMessages.WST_0001_UNSUPPORTED_PROOF_TOKEN_TYPE(proofTokenType, appliesTo));
                throw new WSTrustException(LogStringsMessages.WST_0001_UNSUPPORTED_PROOF_TOKEN_TYPE(proofTokenType, appliesTo));
            } else if (RequestedProofToken.ENCRYPTED_KEY_TYPE.equals(proofTokenType)) {
                // ToDo
                logger.log(Level.SEVERE,
                        LogStringsMessages.WST_0001_UNSUPPORTED_PROOF_TOKEN_TYPE(proofTokenType, appliesTo));
                throw new WSTrustException(LogStringsMessages.WST_0001_UNSUPPORTED_PROOF_TOKEN_TYPE(proofTokenType, appliesTo));
            } else if (RequestedProofToken.BINARY_SECRET_TYPE.equals(proofTokenType)) {
                final BinarySecret binarySecret = proofToken.getBinarySecret();
                key = binarySecret.getRawValue();
            } else {
                logger.log(Level.SEVERE,
                        LogStringsMessages.WST_0019_INVALID_PROOF_TOKEN_TYPE(proofTokenType, appliesTo));
                throw new WSTrustException(LogStringsMessages.WST_0019_INVALID_PROOF_TOKEN_TYPE(proofTokenType, appliesTo));
            }
        } else {
            Entropy clientEntropy = rst.getEntropy();
            if (clientEntropy != null) {
                BinarySecret bs = clientEntropy.getBinarySecret();
                if (bs != null) {
                    key = bs.getRawValue();
                }
            }
        }
        return key;
    }

    private byte[] computeKey(WSTrustVersion wstVer, final RequestSecurityTokenResponse rstr, final RequestedProofToken proofToken, final RequestSecurityToken rst) throws WSTrustException, UnsupportedOperationException {
        // get ComputeKey algorithm URI, client entropy, server entropy and compute
        // the SecretKey
        final URI computedKey = proofToken.getComputedKey();
        final Entropy clientEntropy = rst.getEntropy();
        final Entropy serverEntropy = rstr.getEntropy();
        final BinarySecret clientBinarySecret = clientEntropy.getBinarySecret();
        final BinarySecret serverBinarySecret = serverEntropy.getBinarySecret();
        byte[] clientEntropyBytes = null;
        byte[] serverEntropyBytes = null;
        if (clientBinarySecret != null) {
            clientEntropyBytes = clientBinarySecret.getRawValue();
        }
        if (serverBinarySecret != null) {
            serverEntropyBytes = serverBinarySecret.getRawValue();
        }

        int keySize = (int) rstr.getKeySize() / 8;
        if (keySize == 0) {
            keySize = (int) rst.getKeySize() / 8;
            if (keySize == 0 && wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_13_NS_URI)) {
                SecondaryParameters secPara = rst.getSecondaryParameters();
                if (secPara != null) {
                    keySize = (int) secPara.getKeySize() / 8;
                }
            }
        }
        byte[] key;
        if (computedKey.toString().equals(wstVer.getCKPSHA1algorithmURI())) {
            try {
                key = SecurityUtil.P_SHA1(clientEntropyBytes, serverEntropyBytes, keySize);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,
                        LogStringsMessages.WST_0037_ERROR_COMPUTING_KEY(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0037_ERROR_COMPUTING_KEY(), ex);
            }
        } else {
            logger.log(Level.SEVERE,
                    LogStringsMessages.WST_0026_INVALID_CK_ALGORITHM(computedKey));
            throw new WSTrustException(LogStringsMessages.WST_0026_INVALID_CK_ALGORITHM_E(computedKey));
        }
        return key;
    }
}
