package ru.blogic.dss.api.dto;

import java.io.Serializable;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
public class DssStoredCertificate implements Serializable {
    private final int id;
    private final CertificateStatus status;
    private final String thumbprint;
    private final CertificateTemplateExtensionInfo templateExtensionInfo;
    private final X509Certificate certificate;


    public DssStoredCertificate(int id, CertificateStatus status, String thumbprint, X509Certificate certificate,CertificateTemplateExtensionInfo extensionInfo) {
        this.id = id;
        this.status = status;
        this.thumbprint = thumbprint;
        this.certificate = certificate;
        this.templateExtensionInfo=extensionInfo;
    }

    public int getId() {
        return id;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public CertificateTemplateExtensionInfo getTemplateExtensionInfo() {
        return templateExtensionInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DssStoredCertificate)) return false;

        DssStoredCertificate that = (DssStoredCertificate) o;

        return thumbprint.equals(that.thumbprint);
    }

    @Override
    public int hashCode() {
        return thumbprint.hashCode();
    }

    @Override
    public String toString() {
        return "DssStoredCertificate{" +
                "id=" + id +
                ", status=" + status +
                ", thumbprint='" + thumbprint + '\'' +
                ", certificate=" + certificate +
                ", templateExtensionInfo=" + templateExtensionInfo +
                '}';
    }
}