package ru.blogic.dss.api.dto;

/**
 * @author dgolubev
 */
public enum CertificateStatus {
    NULL,
    /**
     * Сертификат действителен
     */
    ACTIVE,
    /**
     * Сертификат отозван
     */
    REVOKED,
    /**
     * Действие сертификата приостановлено
     */
    HOLD,
    /**
     * Сертификат не действителен
     */
    NOT_VALID,
    /**
     * Сертификат доступен только для просмотра. Действия «отзыв», «приостановка» невозможны.
     */
    OUT_OF_ORDER
}
