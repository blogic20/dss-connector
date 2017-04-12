package ru.blogic.sts;

import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.api.security.trust.config.STSConfigurationProvider;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.impl.DefaultSTSConfiguration;
import com.sun.xml.ws.security.trust.impl.DefaultTrustSPMetadata;
import com.sun.xml.ws.security.trust.impl.WSTrustContractImpl;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.common.Constants;
import ru.blogic.dss.common.util.ServiceLocator;

/**
 * @author dgolubev
 */
public class LecmStsConfigurationProvider implements STSConfigurationProvider {

    @Override
    public STSConfiguration getSTSConfiguration() {
        final DefaultSTSConfiguration config = new DefaultSTSConfiguration();

        final DssConfigService dssConfigService = ServiceLocator.getService(DssConfigService.class);

        config.setEncryptIssuedKey(false);
        config.setEncryptIssuedToken(false);

        config.setIssuedTokenTimeout(36000);
        config.setType(WSTrustContractImpl.class.getName());

        config.setIssuer(Constants.LECM_STS_ISSUER_NAME);

        addDssStsTrustSPMetadata(config, dssConfigService.getDssStsEndpoint());
        addDssStsTrustSPMetadata(config, dssConfigService.getSignServiceEndpoint());
        addDssStsTrustSPMetadata(config, dssConfigService.getUserManagementServiceEndpoint());

        return config;
    }

    private void addDssStsTrustSPMetadata(DefaultSTSConfiguration config, String endpoint) {
        final DefaultTrustSPMetadata metadata = new DefaultTrustSPMetadata(endpoint);
        metadata.setCertAlias("dss.cert");
        metadata.setTokenType(WSTrustConstants.SAML11_ASSERTION_TOKEN_TYPE);
        metadata.setKeyType(Constants.URI_OASIS_WS_TRUST_200512_PUBLICKEY);
        config.addTrustSPMetadata(metadata, endpoint);
    }
}
