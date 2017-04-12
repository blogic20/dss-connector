package ru.blogic.dss.api.dto;

/**
 * @author dgolubev
 */
public enum DssAction {
    /**
     * Выпуск маркера безопасности («вход» пользователя)
     */
    ISSUE("Issue"),
    /**
     * Подпись документа
     */
    SIGN_DOCUMENT("SignDocument"),
    /**
     * Подпись пакета документов
     */
    SIGN_DOCUMENTS("SignDocuments"),
    /**
     * Расшифрование документа
     */
    DECRYPT_DOCUMENT("DecryptDocument"),
    /**
     * Создание запроса на сертификат
     */
    CREATE_REQUEST("CreateRequest"),
    /**
     * Смена пин-кода для доступа к закрытому ключу сертификата
     */
    CHANGE_PIN("ChangePin"),
    /**
     * Создание запроса на обновление сертификата
     */
    RENEW_CERTIFICATE("RenewCertificate"),
    /**
     * Создание запроса на отзыв сертификата
     */
    REVOKE_CERTIFICATE("RevokeCertificate"),
    /**
     * Создание запроса на приостановление действия сертификата
     */
    HOLD_CERTIFICATE("HoldCertificate"),
    /**
     * Возобновление действия сертификата
     */
    UNHOLD_CERTIFICATE("UnholdCertificate"),
    /**
     * Удаление сертификата
     */
    DELETE_CERTIFICATE("DeleteCertificate");

    private final String value;

    DssAction(String value){
        this.value=value;
    }

    public String value(){
        return value;
    }

    public static DssAction fromValue(String v) {
        for (DssAction c: DssAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
