package ru.blogic.dss.provider;

import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.InteractiveChallengeConfirmation;
import ru.blogic.dss.common.util.SecurityUtils;
import ru.cryptopro.dss.services._2014._06.ISignService;
import ru.cryptopro.dss.services._2014._06.SignService;
import ru.cryptopro.dss.services._2015._04.IVerificationService;
import ru.cryptopro.dss.services._2015._04.VerificationService;
import ru.cryptopro.dss.services._2015._12.IUserManagementService;
import ru.cryptopro.dss.services._2015._12.UserManagementService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author dgolubev
 */
@RequestScoped
public class WsPortProvider {

    private static final SignService SIGN_SERVICE = new SignService();
    private static final VerificationService VERIFICATION_SERVICE = new VerificationService();
    private static UserManagementService USER_MANAGEMENT_SERVICE = new UserManagementService();

    @Inject
    private DssConfigService dssConfigService;

    @Inject
    private StsConfigProvider stsConfigProvider;

    private ISignService simpleSignPort;
    private IVerificationService verificationPort;
    private IUserManagementService userManagementPort;

    protected WsPortProvider() {
    }

    public ISignService getSignPort(InteractiveChallengeConfirmation confirmation) {
        return createSignPort(stsConfigProvider.getStsIssuedTokenConfiguration(dssConfigService.getSignServiceEndpoint(), confirmation));
    }

    public ISignService getSignPort() {
        if (simpleSignPort == null) {
            simpleSignPort = createSignPort(stsConfigProvider.getStsIssuedTokenConfiguration(dssConfigService.getSignServiceEndpoint()));
        }
        return simpleSignPort;
    }

    public ISignService getSignPort(String smsFragment, String delegateUserId) {
        if (delegateUserId != null) {
            return createSignPort(stsConfigProvider.getStsIssuedTokenConfiguration(dssConfigService.getSignServiceEndpoint(), smsFragment, null, delegateUserId));
        }
        return getSignPort();
    }

    public IVerificationService getVerificationPort() {
        if (verificationPort == null) {
            verificationPort = VERIFICATION_SERVICE.getBasicHttpBindingIVerificationService();

            final BindingProvider provider = (BindingProvider) verificationPort;
            provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, dssConfigService.getVerificationServiceEndpoint());

            setAuthInfo(provider);
        }
        return verificationPort;
    }

    public IUserManagementService getUserManagementPort() {
        if (userManagementPort == null) {
            userManagementPort = createUserManagementPort(
                    // Переделана логикуа получения токена - токен от локального STS прямо используется для запросов на сервис.
                    stsConfigProvider.getStraightStsIssuedTokenConfiguration(dssConfigService.getUserManagementServiceEndpoint())
            );
        }
        return userManagementPort;
    }

    private ISignService createSignPort(STSIssuedTokenConfiguration stsIssuedTokenConfiguration) {
        final ISignService port = SIGN_SERVICE.getWSHttpBindingISignService1(
                new STSIssuedTokenFeature(stsIssuedTokenConfiguration)
        );

        final BindingProvider provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, dssConfigService.getSignServiceEndpoint());

        setAuthInfo(provider);
        return port;
    }

    private IUserManagementService createUserManagementPort(STSIssuedTokenConfiguration stsIssuedTokenConfiguration) {

        //final IUserManagementService port = USER_MANAGEMENT_SERVICE.getWSHttpBindingIUserManagementService1(
        //WS2007HttpBinding_IUserManagementService
        final IUserManagementService port = USER_MANAGEMENT_SERVICE.getWS2007HttpBindingIUserManagementService(
                new STSIssuedTokenFeature(stsIssuedTokenConfiguration)
        );
        final BindingProvider provider = (BindingProvider) port;

        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, dssConfigService.getUserManagementServiceEndpoint());

        setAuthInfo(provider);
        return port;
    }

    protected void setAuthInfo(BindingProvider provider) {
        final Map<String, List<String>> cookieHeader
                = Collections.singletonMap("Cookie", Collections.singletonList(SecurityUtils.getLtpaTokenCookie()));
        provider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, cookieHeader);
    }
}
