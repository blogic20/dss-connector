package ru.blogic.dss.api.dto;

import java.io.Serializable;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
public class VerificationResult implements Serializable {

    private final boolean result;
    private final String message;
    private final X509Certificate signerCertificate;

    public VerificationResult(boolean result, String message, X509Certificate signerCertificate) {
        this.result = result;
        this.message = message;
        this.signerCertificate = signerCertificate;
    }

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public X509Certificate getSignerCertificate() {
        return signerCertificate;
    }

    @Override
    public String toString() {
        return "VerificationResult{" +
                "result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
