package ru.blogic.dss.service.impl;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenManager;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import org.oasis_open.docs.ws_sx.ws_trust._200802.InteractiveChallengeType;
import org.oasis_open.docs.ws_sx.ws_trust._200802.TextChallengeType;
import ru.blogic.dss.DssConstants;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.DssService;
import ru.blogic.dss.api.dto.DssConfirmableAction;
import ru.blogic.dss.api.dto.InteractiveChallengeRequest;
import ru.blogic.dss.api.dto.MfaPolicyState;
import ru.blogic.dss.api.dto.dsspolicy.DssPolicy;
import ru.blogic.dss.api.exception.DssServiceException;
import ru.blogic.dss.provider.StsConfigProvider;
import ru.blogic.dss.provider.WsPortProvider;
import ru.blogic.dss.service.MfaService;

import javax.inject.Inject;

/**
 * @author dgolubev
 */
public class MfaServiceImpl implements MfaService {

    private IssuedTokenManager manager = IssuedTokenManager.getInstance();

    @Inject
    private DssService dssService;

    @Inject
    private DssConfigService dsConfigService;

    @Inject
    private WsPortProvider wsPortProvider;

    @Inject
    private StsConfigProvider stsConfigProvider;

    @Override
    public boolean isMfaRequired(DssConfirmableAction action) throws DssServiceException {
        final MfaPolicyState policy = getPolicy().getTransactionConfirmation();
        return policy == MfaPolicyState.ON
                || policy != MfaPolicyState.OFF && dsConfigService.getPolicyState(action) == MfaPolicyState.ON;
    }

    @Override
    public InteractiveChallengeRequest startInteractiveChallenge(String endPoint, String smsFragment, int transactionId) {
        final DefaultSTSIssuedTokenConfiguration cfg = stsConfigProvider.getStsIssuedTokenConfiguration(endPoint, smsFragment, transactionId);

        final IssuedTokenContext ctx = manager.createIssuedTokenContext(cfg, endPoint);
        try {
            manager.getIssuedToken(ctx);
        } catch (WSTrustException e) {
            throw new RuntimeException("Unable to start interactive challenge procedure ", e);
        }
        final InteractiveChallengeType challenge = (InteractiveChallengeType) ctx.getOtherProperties().get(DssConstants.INTERACTIVE_CHALLENGE);
        if (challenge == null || challenge.getTextChallenge().isEmpty()) {
            throw new NullPointerException("Service response does not contain interactive challenge info");
        }
        final TextChallengeType textChallenge = challenge.getTextChallenge().get(0);
        final String usedEntropy = (String) ctx.getOtherProperties().get(DssConstants.INTERACTIVE_CHALLENGE_ENTROPY);

        return new InteractiveChallengeRequest(
                textChallenge.getRefID(),
                usedEntropy,
                challenge.getTitle().getValue(),
                textChallenge.getLabel()
        );
    }

    private DssPolicy getPolicy() throws DssServiceException {
        return dssService.getPolicy();
    }
}
