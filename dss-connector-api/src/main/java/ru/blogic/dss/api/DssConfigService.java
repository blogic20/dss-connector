package ru.blogic.dss.api;

import ru.blogic.dss.api.dto.DssConfirmableAction;
import ru.blogic.dss.api.dto.KeyPair;
import ru.blogic.dss.api.dto.MfaPolicyState;

import javax.ejb.Local;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
@Local
public interface DssConfigService {

    KeyPair getLecmStsKeyPair();

    X509Certificate getDssCertificate();

    MfaPolicyState getPolicyState(DssConfirmableAction action);

    String getHomeStsEndpoint();

    String getDssStsEndpoint();

    String getSignServiceEndpoint();

    String getVerificationServiceEndpoint();

    String getUserManagementServiceEndpoint();

    int getDssPolicyCacheTtlMinutes();

    int getDssCertIssuerCAId();

    String getDssOperator();

}
