package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import ru.blogic.dss.api.dto.dsspolicy.IdentifierType;
import ru.blogic.dss.api.dto.usermanagement.Rdns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pkupershteyn on 08.04.2016.
 * Политика Сервиса управления пользователями ЦИ DSS
 */
public class DssUmsPolicy implements Serializable{
    private List<IdentifierType> availableIdentifierTypes;
    private AuthenticationPolicy authenticationPolicy;
    //Согласно схеме у allowUserRegistration minoccurs=0, поэтому используется враппер
    private Boolean allowUserRegistration;
    private Map<String,List<String>> groups;
    private Rdns rdns;
    private List<IdentityProviderInfo> identityProviders;
    private List<CryptoProviderInfo> cryptoProviders;

    /**
     * @return Список типов идентификаторов, доступных для использования при регистрации учетной записи пользователя
     */
    public List<IdentifierType> getAvailableIdentifierTypes() {
        return availableIdentifierTypes;
    }

    public void setAvailableIdentifierTypes(List<IdentifierType> availableIdentifierTypes) {
        this.availableIdentifierTypes = availableIdentifierTypes!=null ? new ArrayList<IdentifierType>(availableIdentifierTypes) : new ArrayList<IdentifierType>();
    }

    /**
     * @return Политика аутентификации ЦИ DSS
     */
    public AuthenticationPolicy getAuthenticationPolicy() {
        return authenticationPolicy;
    }

    public void setAuthenticationPolicy(AuthenticationPolicy authenticationPolicy) {
        this.authenticationPolicy = authenticationPolicy;
    }

    /**
     * @return разрешена ли самостоятельная регистрация пользователей в ЦИ DSS
     */
    public Boolean getAllowUserRegistration() {
        return allowUserRegistration;
    }

    public void setAllowUserRegistration(Boolean allowUserRegistration) {
        this.allowUserRegistration = allowUserRegistration;
    }

    /**
     *
     * @return Списки известных ЦИ DSS групп, структурированные по именам доверенных издателей зарегистрированных в DSS ЦИ
     */
    public Map<String, List<String>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, List<String>> groups) {
        this.groups = groups!=null ? new HashMap<String, List<String>>(groups) : new HashMap<String, List<String>>();
    }

    /**
     * @return Информация о компонентах различительного имени субъекта, зарегистрированных в БД ЦИ DSS
     */
    public Rdns getRdns() {
        return rdns;
    }

    public void setRdns(Rdns rdns) {
        this.rdns = rdns;
    }

    /**
     * @return Информация о сторонних Центрах Идентификации, зарегистрированных в ЦИ DSS
     */
    public List<IdentityProviderInfo> getIdentityProviders() {
        return identityProviders;
    }

    public void setIdentityProviders(List<IdentityProviderInfo> identityProviders) {
        this.identityProviders = identityProviders!=null ? new ArrayList<IdentityProviderInfo>(identityProviders) : new ArrayList<IdentityProviderInfo>();
    }

    /**
     * @return Информация о криптографических провайдерах, зарегистрированных в ЦИ DSS
     */
    public List<CryptoProviderInfo> getCryptoProviders() {
        return cryptoProviders;
    }

    public void setCryptoProviders(List<CryptoProviderInfo> cryptoProviders) {
        this.cryptoProviders = cryptoProviders!=null ? new ArrayList<CryptoProviderInfo>(cryptoProviders) : new ArrayList<CryptoProviderInfo>();
    }
}
