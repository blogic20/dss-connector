package ru.blogic.dss.api.dto.dsspolicy;

import ru.blogic.dss.api.dto.DssCAType;
import ru.blogic.dss.api.dto.EkuTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class DssCAPolicy implements Serializable {
    private String name;
    private Boolean active;
    private Integer ID;
    private Boolean allowUserMode;
    private DssCAType CAType;
    private List<EkuTemplate> ekuTemplates;
    private Boolean SNChangesEnable;
    private List<SubjectNameComponent> namePolicy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Boolean getAllowUserMode() {
        return allowUserMode;
    }

    public void setAllowUserMode(Boolean allowUserMode) {
        this.allowUserMode = allowUserMode;
    }

    public DssCAType getCAType() {
        return CAType;
    }

    public void setCAType(DssCAType CAType) {
        this.CAType = CAType;
    }

    public List<EkuTemplate> getEkuTemplates() {
        return ekuTemplates;
    }

    public void setEkuTemplates(List<EkuTemplate> ekuTemplates) {
        this.ekuTemplates = ekuTemplates;
    }

    public Boolean getSNChangesEnable() {
        return SNChangesEnable;
    }

    public void setSNChangesEnable(Boolean SNChangesEnable) {
        this.SNChangesEnable = SNChangesEnable;
    }

    public List<SubjectNameComponent> getNamePolicy() {
        return namePolicy;
    }

    public void setNamePolicy(List<SubjectNameComponent> namePolicy) {
        this.namePolicy = namePolicy;
    }
}
