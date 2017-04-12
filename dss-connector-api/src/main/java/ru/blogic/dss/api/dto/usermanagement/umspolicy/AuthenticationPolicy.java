package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import ru.blogic.dss.api.dto.MfaPolicyState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pkupershteyn on 08.04.2016.
 * Политика аутентификации ЦИ DSS
 */
public class AuthenticationPolicy implements Serializable {
    private MfaPolicyState mfaPolicy;
    //согласно схеме у editableByUser minoccurs=0, поэтому используется враппер
    private Boolean editableByUser;
    private List<AuthMethodDescription> authMethods;
    private List<OperationPolicy> operationPolicies;

    /**
     * @return Глобальная политика подтверждения операций, требующих доступа к закрытому ключу пользователя.
     */
    public MfaPolicyState getMfaPolicy() {
        return mfaPolicy;
    }

    public void setMfaPolicy(MfaPolicyState mfaPolicy) {
        this.mfaPolicy = mfaPolicy;
    }

    /**
     * @return Значение, показывающее, позволено ли пользователю редактировать свою политику подтверждения операций
     */
    public Boolean getEditableByUser() {
        return editableByUser;
    }

    public void setEditableByUser(Boolean editableByUser) {
        this.editableByUser = editableByUser;
    }

    /**
     * @return Список доступных методов аутентификации
     */
    public List<AuthMethodDescription> getAuthMethods() {
        return authMethods;
    }

    public void setAuthMethods(List<AuthMethodDescription> authMethods) {
        this.authMethods = authMethods!=null ? new ArrayList<AuthMethodDescription>(authMethods) : new ArrayList<AuthMethodDescription>();
    }

    /**
     * @return Политика подтверждения операций по умолчанию.
     */
    public List<OperationPolicy> getOperationPolicies() {
        return operationPolicies;
    }

    public void setOperationPolicies(List<OperationPolicy> operationPolicies) {
        this.operationPolicies = operationPolicies!=null ? new ArrayList<OperationPolicy>(operationPolicies) : new ArrayList<OperationPolicy>();
    }
}
