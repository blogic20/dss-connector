package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import java.io.Serializable;

/**
 * Описание доступного метода аутентификации
 * Created by pkupershteyn on 08.04.2016.
 */
public class AuthMethodDescription implements Serializable {
    private String identifier;
    private AuthLevel type;

    /**
     * Аутентификация только по логину. Используется как первый шаг в мультифакторной аутентификации
     */
    public static AuthMethodDescription LOGIN_ONLY = new AuthMethodDescription(
            "http://schemas.microsoft.com/ws/2012/09/identity/authenticationmethod/none",
            AuthLevel.PRIMARY);

    /**
     * Аутентификация по паролю DSS. Используется как самостоятельный метод, или как первый шаг в мультифакторной аутентификации
     */
    public static AuthMethodDescription DSS_PASSWORD = new AuthMethodDescription(
            "http://dss.cryptopro.ru/identity/authenticationmethod/password",
            AuthLevel.PRIMARY);

    /**
     * Аутентификация по SMS. Имспользуется только как второй или более шаг в мультифакторной аутентификации
     */
    public static AuthMethodDescription MFA_BY_SMS = new AuthMethodDescription(
            "http://dss.cryptopro.ru/identity/authenticationmethod/otpviasms",
            AuthLevel.SECONDARY);

    /**
     * Аутентификация с помощъю OTP\TOTP токена. Имспользуется только как второй или более шаг в мультифакторной аутентификации
     */
    public static AuthMethodDescription MFA_BY_OTP_TOKEN = new AuthMethodDescription(
            "http://dss.cryptopro.ru/identity/authenticationmethod/oath",
            AuthLevel.SECONDARY);

    /**
     * Аутентификация по id SIM карты. Имспользуется только как второй или более шаг в мультифакторной аутентификации
     */
    public static AuthMethodDescription MFA_BY_SIM = new AuthMethodDescription(
            "http://dss.cryptopro.ru/identity/authenticationmethod/simauth",
            AuthLevel.SECONDARY);


    /**
     * Публичный конструктор
     */
    public AuthMethodDescription() {
    }

    /**
     * Частный конструктор, только для создания статических свойств
     *
     * @param identifier идентификатор (uri) метода
     * @param type       тип метода
     * @see {@link #LOGIN_ONLY}, {@link #DSS_PASSWORD}, {@link #MFA_BY_OTP_TOKEN}, {@link #MFA_BY_SIM}, {@link #MFA_BY_SMS}
     */
    private AuthMethodDescription(String identifier, AuthLevel type) {
        this.identifier = identifier;
        this.type = type;
    }

    /**
     * @return Идентификатор (uri) метода аутентификации
     */
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return Тип метода аутентификации
     */
    public AuthLevel getType() {
        return type;
    }

    public void setType(AuthLevel type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !( obj instanceof AuthMethodDescription )) {
            return false;
        }
        AuthMethodDescription amd = (AuthMethodDescription) obj;

        return ( identifier != null ? identifier.equals(amd.identifier) : amd.identifier == null )
                && ( type != null ? type.equals(amd.type) : amd.type == null );
    }

    @Override
    public int hashCode() {
        int hash = 11;
        hash = 19 * hash + ( identifier != null ? identifier.hashCode() : 0 );
        hash = 19 * hash + ( type != null ? type.hashCode() : 0 );
        return hash;
    }
}
