package ru.blogic.dss.api.dto;

/**
 * @author dgolubev
 */
public enum DssConfirmableAction {

    ISSUE("Issue"),
    SIGN_DOCUMENT("SignDocument"),
    SIGN_DOCUMENTS("SignDocuments"),
    DECRYPT_DOCUMENT("DecryptDocument"),
    CREATE_REQUEST("CreateRequest"),
    CHANGE_PIN("ChangePin"),
    RENEW_CERTIFICATE("RenewCertificate"),
    REVOKE_CERTIFICATE("RevokeCertificate"),
    HOLD_CERTIFICATE("HoldCertificate"),
    UNHOLD_CERTIFICATE("UnholdCertificate"),
    DELETE_CERTIFICATE("DeleteCertificate");

    private final String dssName;

    DssConfirmableAction(String dssName) {
        this.dssName = dssName;
    }

    public String getDssName() {
        return dssName;
    }
}
