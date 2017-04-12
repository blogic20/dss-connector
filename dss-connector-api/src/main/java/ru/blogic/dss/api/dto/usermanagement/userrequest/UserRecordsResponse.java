package ru.blogic.dss.api.dto.usermanagement.userrequest;

import ru.blogic.dss.api.dto.usermanagement.DssUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ответ сервиса по запросу списка пользователей (@see {@link ru.blogic.dss.api.RemoteDssUserManagementService#getUsers(UserRecordsRequest)})
 * Created by pkupershteyn on 07.04.2016.
 */
public class UserRecordsResponse implements Serializable{
    //согласно схеме у totalCount minoccurs=0, поэтому используется враппер
    private Integer totalCount;
    //согласно схеме у affectedCount minoccurs=0, поэтому используется враппер
    private Integer affectedCount;
    private List<DssUserInfo> userInfos;

    /**
     * @return Общее количество записей
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return Количество отфильтрованных записей
     */
    public Integer getAffectedCount() {
        return affectedCount;
    }

    public void setAffectedCount(Integer affectedCount) {
        this.affectedCount = affectedCount;
    }

    /**
     * @return Список учетных записей, соответствующих заданным фильтрам
     */
    public List<DssUserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<DssUserInfo> userInfos) {
        this.userInfos = userInfos!=null ? new ArrayList<DssUserInfo>(userInfos) : new ArrayList<DssUserInfo>();
    }
}
