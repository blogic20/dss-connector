package ru.blogic.dss;

import org.apache.commons.io.IOUtils;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.DssConfirmableAction;
import ru.blogic.dss.api.dto.KeyPair;
import ru.blogic.dss.api.dto.MfaPolicyState;
import ru.blogic.dss.common.util.SecurityUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
@Stateless
public class DssConfigServiceImpl implements DssConfigService {

    @Resource(name = "home.sts.endpoint")
    private String homeStsEndpoint;

    @Resource(name = "dss.sts.endpoint")
    private String dssStsEndpoint;

    @Resource(name = "dss.signService.endpoint")
    private String signServiceEndpoint;

    @Resource(name = "verificationService.endpoint")
    private String verificationServiceEndpoint;

    @Resource(name = "dss.userManagementService.endpoint")
    private String userManagementServiceEndpoint;

    @Resource(name = "dss.policyCache.ttlMinutes")
    private int dssPolicyCacheTtlMinutes;

    @Resource(name = "dss.certIssuerCA.id")
    private int dssCertIssuerCAId;

    @Resource(name = "dss.operator.username")
    private String operatorUserName;

    @Resource(name = "dss.certificate.url")
    private String dssCertUrl;

    @Resource(name = "home.sts.keystore.url")
    private String homeStsKeystoreUrl;

    @Resource(name = "home.sts.keystore.password")
    private String homeStsKeystorePassword;


    @Resource(name = "dss.mfa.confirmIssue")
    private Boolean confirmIssue;

    @Resource(name = "dss.mfa.confirmSignDocument")
    private Boolean confirmSignDocument;

    @Resource(name = "dss.mfa.confirmSignDocuments")
    private Boolean confirmSignDocuments;

    @Resource(name = "dss.mfa.confirmDecryptDocument")
    private Boolean confirmDecryptDocument;

    @Resource(name = "dss.mfa.confirmCreateRequest")
    private Boolean confirmCreateRequest;

    @Resource(name = "dss.mfa.confirmDeleteCertificate")
    private Boolean confirmDeleteCertificate;

    @Resource(name = "dss.mfa.confirmChangePin")
    private Boolean confirmChangePin;

    @Resource(name = "dss.mfa.confirmRenewCertificate")
    private Boolean confirmRenewCertificate;

    @Resource(name = "dss.mfa.confirmRevokeCertificate")
    private Boolean confirmRevokeCertificate;

    @Resource(name = "dss.mfa.confirmHoldCertificate")
    private Boolean confirmHoldCertificate;

    @Resource(name = "dss.mfa.confirmUnholdCertificate")
    private Boolean confirmUnholdCertificate;

    private KeyPair keyPair;
    private X509Certificate certificate;

    @PostConstruct
    protected void init() {
        URL keystoreUrl;
        URL certUrl;
        try {
            keystoreUrl = new URL(homeStsKeystoreUrl);
            certUrl = new URL(dssCertUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("An invalid URL was provided");
        }

        final byte[] keyStoreContent;
        try {
            keyStoreContent = IOUtils.toByteArray(keystoreUrl);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read keystore content", e);
        }

        final byte[] certContent;
        try {
            certContent = IOUtils.toByteArray(certUrl);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read keystore content", e);
        }

        keyPair = SecurityUtils.toKeyPair(keyStoreContent, homeStsKeystorePassword);
        certificate = SecurityUtils.toX509Cert(certContent);
    }

    @Override
    public KeyPair getLecmStsKeyPair() {
        return keyPair;
    }

    @Override
    public X509Certificate getDssCertificate() {
        return certificate;
    }

    @Override
    public MfaPolicyState getPolicyState(DssConfirmableAction action) {
        final Boolean required;

        switch (action) {
            case UNHOLD_CERTIFICATE:
                required = confirmUnholdCertificate;
                break;
            case SIGN_DOCUMENT:
                required = confirmSignDocument;
                break;
            case SIGN_DOCUMENTS:
                required = confirmSignDocuments;
                break;
            case REVOKE_CERTIFICATE:
                required = confirmRevokeCertificate;
                break;
            case CHANGE_PIN:
                required = confirmChangePin;
                break;
            case CREATE_REQUEST:
                required = confirmCreateRequest;
                break;
            case DECRYPT_DOCUMENT:
                required = confirmDecryptDocument;
                break;
            case DELETE_CERTIFICATE:
                required = confirmDeleteCertificate;
                break;
            case HOLD_CERTIFICATE:
                required = confirmHoldCertificate;
                break;
            case ISSUE:
                required = confirmIssue;
                break;
            case RENEW_CERTIFICATE:
                required = confirmRenewCertificate;
                break;
            default:
                throw new IllegalArgumentException("There is no MFA policy for the DSS action " + action.getDssName());
        }

        if (required == null) {
            return MfaPolicyState.NOT_SET;
        }

        return required ? MfaPolicyState.ON : MfaPolicyState.OFF;
    }

    public String getHomeStsEndpoint() {
        return homeStsEndpoint;
    }

    @Override
    public String getDssStsEndpoint() {
        return dssStsEndpoint;
    }

    public String getSignServiceEndpoint() {
        return signServiceEndpoint;
    }

    @Override
    public String getVerificationServiceEndpoint() {
        return verificationServiceEndpoint;
    }

    public String getUserManagementServiceEndpoint() {
        return userManagementServiceEndpoint;
    }

    @Override
    public int getDssPolicyCacheTtlMinutes() {
        return dssPolicyCacheTtlMinutes;
    }

    @Override
    public int getDssCertIssuerCAId() {
        return dssCertIssuerCAId;
    }

    @Override
    public String getDssOperator() {
        return operatorUserName;
    }
}
