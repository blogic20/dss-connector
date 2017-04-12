package ru.blogic.dss.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dgolubev
 */
public class CertificateInfo implements Serializable {

    private final String subjectName;
    private final String issuerName;
    private final Date notBefore;
    private final Date notAfter;
    private final String serialNumber;
    private final String thumbprint;
    private final String keyIdentifier;

    public CertificateInfo(String subjectName, String issuerName, Date notBefore, Date notAfter, String serialNumber,
                           String thumbprint, String keyIdentifier) {
        this.subjectName = subjectName;
        this.issuerName = issuerName;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.serialNumber = serialNumber;
        this.thumbprint = thumbprint;
        this.keyIdentifier = keyIdentifier;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public String getKeyIdentifier() {
        return keyIdentifier;
    }

    @Override
    public String toString() {
        return "CertificateInfo{" +
                "subjectName='" + subjectName + '\'' +
                ", issuerName='" + issuerName + '\'' +
                ", notBefore=" + notBefore +
                ", notAfter=" + notAfter +
                ", serialNumber='" + serialNumber + '\'' +
                ", thumbprint='" + thumbprint + '\'' +
                ", keyIdentifier='" + keyIdentifier + '\'' +
                '}';
    }
}
