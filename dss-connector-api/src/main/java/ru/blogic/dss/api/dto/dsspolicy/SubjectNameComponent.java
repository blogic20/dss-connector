package ru.blogic.dss.api.dto.dsspolicy;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class SubjectNameComponent implements Serializable{
    private Boolean required;
    private String name;
    private String OID;
    private Integer order;
    private String stringIdentifier;
    private String value;

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOID() {
        return OID;
    }

    public void setOID(String OID) {
        this.OID = OID;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getStringIdentifier() {
        return stringIdentifier;
    }

    public void setStringIdentifier(String stringIdentifier) {
        this.stringIdentifier = stringIdentifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
