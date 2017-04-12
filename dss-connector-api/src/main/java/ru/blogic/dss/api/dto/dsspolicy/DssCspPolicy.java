package ru.blogic.dss.api.dto.dsspolicy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class DssCspPolicy implements Serializable{
    private String alias;
    private List<String> hashAlgorithms;
    private Integer ID;
    private Integer keyLength;
    private String providerName;
    private Integer providerType;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getHashAlgorithms() {
        return hashAlgorithms;
    }

    public void setHashAlgorithms(List<String> hashAlgorithms) {
        this.hashAlgorithms = hashAlgorithms;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(Integer keyLength) {
        this.keyLength = keyLength;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Integer getProviderType() {
        return providerType;
    }

    public void setProviderType(Integer providerType) {
        this.providerType = providerType;
    }
}
