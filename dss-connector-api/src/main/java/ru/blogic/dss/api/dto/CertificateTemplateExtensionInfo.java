package ru.blogic.dss.api.dto;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 22.01.2016.
 */
public class CertificateTemplateExtensionInfo implements Serializable{

    final String oid;
    final int majorVersion;
    final int minorVersion;

    public CertificateTemplateExtensionInfo(String oid, int majorVersion, int minorVersion) {
        this.oid = oid;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public String getOid() {
        return oid;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    @Override
    public String toString() {
        return "CertTemplateInfo:{\n\toid: " + oid
                + "\n\tmajor version: " + majorVersion
                + "\n\tminor version: " + minorVersion
                + "\n}";
    }
}