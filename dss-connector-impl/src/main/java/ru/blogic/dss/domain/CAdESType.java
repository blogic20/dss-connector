package ru.blogic.dss.domain;

/**
 * @author dgolubev
 */
public enum CAdESType {
    /**
     * Подпись в формате CAdES BES
     */
    BES("BES"),
    /**
     * Подпись в формате CAdES X Long Type 1
     */
    XLT1("XLT1");

    private final String value;

    CAdESType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
