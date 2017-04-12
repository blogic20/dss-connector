package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 08.04.2016.
 * Информация о криптографическом провайдере, зарегистрированных в ЦИ DSS
 */
public class CryptoProviderInfo implements Serializable{
    private String id;
    private String name;
    private String description;
    private CryptoProviderProfileType type;

    /**
     * @return Идентификатор профиля провайдера
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Имя профиля провайдера
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Описание профиля провайдера
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Тип профиля провайдера
     */
    public CryptoProviderProfileType getType() {
        return type;
    }

    public void setType(CryptoProviderProfileType type) {
        this.type = type;
    }
}
