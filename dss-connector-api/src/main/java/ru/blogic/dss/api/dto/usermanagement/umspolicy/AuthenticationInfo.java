package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 12.04.2016.
 * Описание шага схемы аутентификации пользователя
 */
public class AuthenticationInfo implements Serializable {
    private String methodUri;
    //согласно схеме у level minoccurs=0, поэтому используется враппер
    private Integer level;

    /**
     * @return Идентификатор метода аутентификации
     */
    public String getMethodUri() {
        return methodUri;
    }

    public void setMethodUri(String methodUri) {
        this.methodUri = methodUri;
    }

    /**
     * @return Уровень, на котором применяется данный метод аутентификации.
     */
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
