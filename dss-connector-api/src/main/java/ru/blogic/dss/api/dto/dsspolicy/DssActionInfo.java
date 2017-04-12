package ru.blogic.dss.api.dto.dsspolicy;

import ru.blogic.dss.api.dto.DssAction;

import java.io.Serializable;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class DssActionInfo implements Serializable {
    private DssAction action;
    private String displayName;
    private String uri;
    private Boolean mfaRequired;

    public DssAction getAction() {
        return action;
    }

    public void setAction(DssAction action) {
        this.action = action;
    }

    public Boolean getMfaRequired() {
        return mfaRequired;
    }

    public void setMfaRequired(Boolean mfaRequired) {
        this.mfaRequired = mfaRequired;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
