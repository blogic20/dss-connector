package ru.blogic.dss.provider;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import com.sun.xml.ws.security.trust.impl.wssx.WSTrustVersion13;
import com.sun.xml.ws.security.trust.impl.wssx.elements.ClaimsImpl;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import org.oasis_open.docs.ws_sx.ws_trust._200802.InteractiveChallengeResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200802.TextChallengeResponseType;
import org.oasis_open.docs.wsfed.authorization._200706.AdditionalContext;
import org.oasis_open.docs.wsfed.authorization._200706.ClaimType;
import org.oasis_open.docs.wsfed.authorization._200706.ContextItemType;
import ru.blogic.dss.DssConstants;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.InteractiveChallengeConfirmation;
import ru.blogic.dss.common.Constants;
import ru.blogic.dss.common.util.SecurityUtils;
import ru.blogic.dss.domain.DssRequestSecurityTokenResponseImpl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.xml.ws.handler.MessageContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author dgolubev
 */
@RequestScoped
public class StsConfigProvider {

    public static final String DSS_STS_WSDL_LOCATION
            = StsConfigProvider.class.getResource("/META-INF/Active.wsdl").toExternalForm();
    public static final String LECM_STS_WSDL_LOCATION
            = StsConfigProvider.class.getResource("/META-INF/LECM-STS.wsdl").toExternalForm();

    @Inject
    private DssConfigService dssConfigService;

    protected StsConfigProvider() {
    }

    private static void setCommonConfigurationParameters(DefaultSTSIssuedTokenConfiguration config) {
        config.setTokenType(WSTrustConstants.SAML11_ASSERTION_TOKEN_TYPE);
        config.setKeyType(Constants.URI_OASIS_WS_TRUST_200512_SYMMETRICKEY);
        config.setKeySize(Constants.STS_KEY_SIZE);
        config.setEncryptWith(Constants.STS_ENCRYPTION_ALGORITHM);
        config.setSignWith(Constants.STS_SIGN_WITH);
        config.setCanonicalizationAlgorithm(Constants.STS_C14N_ALGORITHM);
        config.setEncryptionAlgorithm(Constants.STS_ENCRYPTION_ALGORITHM);
    }

    public DefaultSTSIssuedTokenConfiguration getStsIssuedTokenConfiguration(String endPoint) {
        final DefaultSTSIssuedTokenConfiguration dssStsConfig = new DefaultSTSIssuedTokenConfiguration(
                STSIssuedTokenConfiguration.PROTOCOL_13,
                dssConfigService.getDssStsEndpoint(),
                DSS_STS_WSDL_LOCATION,
                Constants.DSS_STS_SERVICE_NAME,
                Constants.DSS_STS_PORT_NAME,
                Constants.URI_MS_STS);

        setCommonConfigurationParameters(dssStsConfig);

        // цепочка аутентификации: токены для доступа к DSS STS генерирует LECM STS
        final Map<String, Object> otherOptions = dssStsConfig.getOtherOptions();
        otherOptions.put(DssConstants.RUNTIME_STS_CONFIG, createtLecmStsIssuedTokenConfiguration());
        otherOptions.put(STSIssuedTokenConfiguration.STS_ENDPOINT, dssConfigService.getHomeStsEndpoint());
        otherOptions.put(STSIssuedTokenConfiguration.STS_WSDL_LOCATION, LECM_STS_WSDL_LOCATION);
        otherOptions.put(STSIssuedTokenConfiguration.STS_SERVICE_NAME, Constants.LECM_STS_SERVICE_NAME);
        otherOptions.put(STSIssuedTokenConfiguration.STS_PORT_NAME, Constants.LECM_STS_PORT_NAME);
        otherOptions.put(STSIssuedTokenConfiguration.STS_NAMESPACE, Constants.URI_OASIS_WS_TRUST_200512);

        dssStsConfig.getOtherOptions().put(STSIssuedTokenConfiguration.APPLIES_TO, endPoint);

        return dssStsConfig;
    }


    //Метод для usermanagemуnt.svc - токен генерится на нашем STS и напрямую используется в запросе к сервису
    public DefaultSTSIssuedTokenConfiguration getStraightStsIssuedTokenConfiguration(String endPoint) {

        final DefaultSTSIssuedTokenConfiguration dssStsConfig = new DefaultSTSIssuedTokenConfiguration(
                STSIssuedTokenConfiguration.PROTOCOL_13,
                (String) dssConfigService.getHomeStsEndpoint(),
                LECM_STS_WSDL_LOCATION,
                Constants.LECM_STS_SERVICE_NAME,
                Constants.LECM_STS_PORT_NAME,
                Constants.URI_OASIS_WS_TRUST_200512);

        setCommonConfigurationParameters(dssStsConfig);

        dssStsConfig.getOtherOptions().put(STSIssuedTokenConfiguration.APPLIES_TO, endPoint);

        return dssStsConfig;
    }

    private static Claims getClaims(DefaultSTSIssuedTokenConfiguration config) {
        Claims claims = config.getClaims();
        if (claims == null) {
            claims = new ClaimsImpl(Constants.URI_OASIS_AUTH_CLAIMS);
            config.setClaims(claims);
        }
        return claims;
    }


    public DefaultSTSIssuedTokenConfiguration getStsIssuedTokenConfiguration(String endPoint, String smsFragment, Integer transactionId) {
        try {
            final DefaultSTSIssuedTokenConfiguration config = getStsIssuedTokenConfiguration(endPoint);

            if (transactionId != null) {
                final Claims claims = getClaims(config);

                final ClaimType txIdClaim = new ClaimType();
                txIdClaim.setUri(Constants.URI_CPRO_IDENTITY_CLAIMS_TRANSACTION_ID);
                txIdClaim.setValue(Integer.toString(transactionId));
                txIdClaim.setOptional(Boolean.FALSE);

                claims.getAny().add(txIdClaim);

                final ClaimType txExpClaim = new ClaimType();
                txExpClaim.setUri(Constants.URI_CPRO_IDENTITY_CLAIMS_TRANSACTION_EXPIREDATE);
                txExpClaim.setOptional(Boolean.FALSE);

                claims.getAny().add(txExpClaim);

                if (smsFragment != null) {
                    final AdditionalContext additionalContext = new AdditionalContext();

                    final ContextItemType contextItem = new ContextItemType();
                    contextItem.setName(Constants.URI_CPRO_IDFENTITY_CONFIRMATIONTEXT_UNFORMATTED);
                    contextItem.setValue(smsFragment);

                    additionalContext.getContextItem().add(contextItem);

                    config.getOtherOptions().put(DssConstants.ADDITIONAL_CONTEXT, additionalContext);
                }
            }
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Unable to build claims of DSS STS request", e);
        }
    }

    public DefaultSTSIssuedTokenConfiguration getStsIssuedTokenConfiguration(String endPoint, String smsFragment, Integer transactionId, String delegateUserId) {
        try {
            final DefaultSTSIssuedTokenConfiguration config = getStsIssuedTokenConfiguration(endPoint, smsFragment, transactionId);
            if (delegateUserId != null) {
                final Claims claims = getClaims(config);

                final ClaimType txIdClaim = new ClaimType();

                txIdClaim.setUri(Constants.URI_CPRO_IDENTITY_CLAIMS_DELEGATE_USER_ID);
                txIdClaim.setValue(delegateUserId);

                claims.getAny().add(txIdClaim);

            }
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get token configuration for operator", e);
        }
    }


    private DefaultSTSIssuedTokenConfiguration createtLecmStsIssuedTokenConfiguration() {
        final DefaultSTSIssuedTokenConfiguration lecmStsConfig = new DefaultSTSIssuedTokenConfiguration();

        setCommonConfigurationParameters(lecmStsConfig);

        lecmStsConfig.getOtherOptions().put(STSIssuedTokenConfiguration.APPLIES_TO, dssConfigService.getDssStsEndpoint());

        setAuthInfo(lecmStsConfig);

        return lecmStsConfig;
    }

    public DefaultSTSIssuedTokenConfiguration getStsIssuedTokenConfiguration(String endPoint, InteractiveChallengeConfirmation confirmation) {
        final DefaultSTSIssuedTokenConfiguration config = getStsIssuedTokenConfiguration((String) null);

        final TextChallengeResponseType tcResponse = new TextChallengeResponseType();
        tcResponse.setRefId(confirmation.getChallengeId());
        tcResponse.setValue(confirmation.getConfirmationText());

        final InteractiveChallengeResponseType icResponse = new InteractiveChallengeResponseType();
        icResponse.getTextChallengeResponse().add(tcResponse);

        final DssRequestSecurityTokenResponseImpl response = new DssRequestSecurityTokenResponseImpl();
        response.setChallengeResponse(icResponse);

        response.setAppliesTo(WSTrustUtil.createAppliesTo(endPoint));
        try {
            response.setRequestType(new URI(WSTrustVersion13.WS_TRUST_13.getIssueRequestTypeURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final Map<String, Object> otherOptions = config.getOtherOptions();
        otherOptions.put(DssConstants.REQUEST_SECURITY_TOKEN_RESPONSE, response);
        otherOptions.put(DssConstants.INTERACTIVE_CHALLENGE_ENTROPY, confirmation.getUsedEntropy());

        return config;
    }

    protected void setAuthInfo(DefaultSTSIssuedTokenConfiguration configuration) {
        final Map<String, List<String>> cookieHeader
                = Collections.singletonMap("Cookie", Collections.singletonList(SecurityUtils.getLtpaTokenCookie()));
        configuration.getOtherOptions().put(MessageContext.HTTP_REQUEST_HEADERS, cookieHeader);
    }
}
