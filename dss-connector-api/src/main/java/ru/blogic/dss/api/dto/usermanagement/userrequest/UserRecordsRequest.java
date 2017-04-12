package ru.blogic.dss.api.dto.usermanagement.userrequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Запрос на получение отфильтрованного списка пользователей (@see {@link ru.blogic.dss.api.RemoteDssUserManagementService#getUsers(UserRecordsRequest)})
 * Created by pkupershteyn on 07.04.2016.
 */
public class UserRecordsRequest implements Serializable {
    //согласно схеме у startPosition minoccurs=0, поэтому используется враппер
    private Integer startPosition;
    //согласно схеме у endPosition minoccurs=0, поэтому используется враппер
    private Integer endPosition;
    private List<UserFilter> filters;

    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition Стартовая позиция выборки
     */
    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    /**
     * @param endPosition Конечная позиция выборки
     */
    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    public List<UserFilter> getFilters() {
        return filters;
    }

    /**
     * @param filters Список фильтров, по которым осуществляется выбор пользователя
     */
    public void setFilters(List<UserFilter> filters) {
        this.filters = filters != null ? new ArrayList<UserFilter>(filters) : new ArrayList<UserFilter>();
    }

    @Override
    public String toString() {
        return "{startPosition: " + startPosition + ", endPosition: " + endPosition + ",  filters: " + filters + "}";
    }
}
