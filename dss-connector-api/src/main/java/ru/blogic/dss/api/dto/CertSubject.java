package ru.blogic.dss.api.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author dgolubev
 */
public class CertSubject implements Serializable {

    private String commonName;
    private String organizationalUnit;
    private String organization;
    private String locality;
    private String state;
    private String countryCode;

    public String getCommonName() {
        return commonName;
    }

    public CertSubject setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public CertSubject setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
        return this;
    }

    public String getOrganization() {
        return organization;
    }

    public CertSubject setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public String getLocality() {
        return locality;
    }

    public CertSubject setLocality(String locality) {
        this.locality = locality;
        return this;
    }

    public String getState() {
        return state;
    }

    public CertSubject setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public CertSubject setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String asDistinguishedName() {
        final StringBuilder name = new StringBuilder();

        if (StringUtils.isNotBlank(commonName)) {
            name.append("CN=").append(commonName);
        }
        if (StringUtils.isNotBlank(organizationalUnit)) {
            if (name.length() > 0) {
                name.append(",");
            }
            name.append("OU=").append(organizationalUnit);
        }
        if (StringUtils.isNotBlank(organization)) {
            if (name.length() > 0) {
                name.append(",");
            }
            name.append("O=").append(organization);
        }
        if (StringUtils.isNotBlank(locality)) {
            if (name.length() > 0) {
                name.append(",");
            }
            name.append("L=").append(locality);
        }
        if (StringUtils.isNotBlank(state)) {
            if (name.length() > 0) {
                name.append(",");
            }
            name.append("ST=").append(state);
        }
        if (StringUtils.isNotBlank(countryCode)) {
            if (name.length() > 0) {
                name.append(",");
            }
            name.append("C=").append(countryCode);
        }

        return name.toString();
    }

    @Override
    public String toString() {
        return asDistinguishedName();
    }
}
