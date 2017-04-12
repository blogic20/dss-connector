package ru.blogic.dss.api.dto;

/**
 * Created by pkupershteyn on 26.02.2016.
 */
public enum DssRequestStatus {

    REGISTRATION,
    PENDING,
    ACCEPTED,
    REJECTED;

    public String value() {
        return name();
    }

    public static DssRequestStatus fromValue(String v) {
        return valueOf(v);
    }

}
