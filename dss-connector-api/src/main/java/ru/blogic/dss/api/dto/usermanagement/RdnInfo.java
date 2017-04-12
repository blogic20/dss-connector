package ru.blogic.dss.api.dto.usermanagement;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 08.04.2016.
 * Информация о компоненте различительного имени
 */
public class RdnInfo implements Serializable {
    //согласно схеме у всех элементов minoccurs=0, поэтому для примитивов используются врапперы
    private String oid;
    private String displayName;
    private String stringIdentifier;
    private Integer order;
    private Integer minLength;
    private Integer maxLength;
    private Boolean required;
    private String defaultValue;
    private Integer id;

    /**
     * @return идентификатор компонента
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Объектный идентификатор компонента имени
     */
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return Отображаемое имя компонента имени
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return Строковый идентификатор компонента имени
     */
    public String getStringIdentifier() {
        return stringIdentifier;
    }

    public void setStringIdentifier(String stringIdentifier) {
        this.stringIdentifier = stringIdentifier;
    }

    /**
     * @return Порядковый номер компонента имени
     */
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * @return Минимальная длина значения компонента имени
     */
    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * @return Максимальная длина значения компонента имени
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return Значение, показывающее, является ли данный компонент обязательным
     */
    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @return Значение по умолчанию для компонента
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
