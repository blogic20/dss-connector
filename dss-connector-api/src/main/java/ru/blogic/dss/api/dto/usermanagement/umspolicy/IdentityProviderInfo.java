package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 08.04.2016.
 * Информация о стороннем ЦИ, рарегистрированном в DSS
 */
public class IdentityProviderInfo implements Serializable{
    private String description;
    private String issuerName;
    private String displayName;

    /**
     * @return Описание внешнего ЦИ
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Имя доверенного издателя Центра Идентификации
     */
    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * @return Отображаемое имя Центра Идентификации
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
