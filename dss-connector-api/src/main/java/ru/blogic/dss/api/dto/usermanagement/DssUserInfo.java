package ru.blogic.dss.api.dto.usermanagement;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pkupershteyn on 05.04.2016.
 * Информация о зарегистрированном пользователе
 */
public class DssUserInfo implements Serializable{
    //согласно схеме у всех элементов minoccurs=0, поэтому для примитивов используются врапперы
    private Boolean accountLocked;
    private Date creationDate;
    private String displayName;
    private String distinguishName;
    private String email;
    private Boolean emailConfirmed;
    private String group;
    private Date lastLoginDate;
    private Date lockoutDate;
    private String login;
    private Boolean phoneConfirmed;
    private String phoneNumber;
    private String userId;

    /**
     * @return Значение, показывающее, является ли запись заблокированной
     */
    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    /**
     * @return Дата создания учетной записи пользователя
     */
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return Отображаемое имя пользователя
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return Различительное имя пользователя
     */
    public String getDistinguishName() {
        return distinguishName;
    }

    public void setDistinguishName(String distinguishName) {
        this.distinguishName = distinguishName;
    }

    /**
     * @return Адрес электронной почты пользователя
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Значение, показывающее, подтвержден ли адрес электронной почты пользователя
     */
    public Boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    /**
     * @return Имя группы, в которой состоит пользователь
     */
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return Дата последнего удачного входа пользователя
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * @return Дата снятия блокировки с учетной записи пользователя
     */
    public Date getLockoutDate() {
        return lockoutDate;
    }

    public void setLockoutDate(Date lockoutDate) {
        this.lockoutDate = lockoutDate;
    }

    /**
     * @return Логин пользователя
     */
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return Значение, показывающее, подтвержден ли номер телефона пользователя
     */
    public Boolean getPhoneConfirmed() {
        return phoneConfirmed;
    }

    public void setPhoneConfirmed(Boolean phoneConfirmed) {
        this.phoneConfirmed = phoneConfirmed;
    }

    /**
     * @return Номер мобильного телефона пользователя
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return Глобальный идентификатор пользователя
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
