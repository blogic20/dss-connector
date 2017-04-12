package ru.blogic.dss.api.dto.usermanagement.umspolicy;

import ru.blogic.dss.api.dto.DssAction;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 12.04.2016.
 */
public class OperationPolicy implements Serializable {
    private DssAction action;
    //согласно схеме у confirmationRequired minoccurs=0, поэтому используется враппер
    private Boolean confirmationRequired;

    public DssAction getAction() {
        return action;
    }

    public void setAction(DssAction action) {
        this.action = action;
    }

    public Boolean getConfirmationRequired() {
        return confirmationRequired;
    }

    public void setConfirmationRequired(Boolean confirmationRequired) {
        this.confirmationRequired = confirmationRequired;
    }
}
