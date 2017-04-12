package ru.blogic.dss.domain;

/**
 * @author dgolubev
 */
public enum PdfSignatureFormat {

    /**
     * Подпись формата PKCS7
     */
    CMS("CMS"),
    /**
     * Подпись формата CAdES
     */
    CADES("CAdES");

    private final String value;

    PdfSignatureFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
