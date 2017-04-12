package ru.blogic.dss.api.dto.usermanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Информация о компонентах различительного имени субъекта, зарегистрированных в БД ЦИ DSS
 * Created by pkupershteyn on 12.04.2016.
 */
public class Rdns implements Serializable {
    private final Map<String, RdnInfo> rdnsByStringIdentifier = new LinkedHashMap<String, RdnInfo>();

    public void setRdns(List<RdnInfo> rdns) {
        if (rdns != null) {
            for (RdnInfo rdnInfo : rdns) {
                rdnsByStringIdentifier.put(rdnInfo.getStringIdentifier(), rdnInfo);
            }
        }
    }

    /**
     * @return Плоский список информации о компонентах различительного имени
     */
    public List<RdnInfo> getRdns() {
        return new ArrayList<RdnInfo>(rdnsByStringIdentifier.values());
    }

    /**
     * Возвращает компонент различительного имени по его идентификатору
     *
     * @param identifier Идентификатор различительного имени (cn, oid, email и т.д.)
     * @return компонент различительного имени, или null, если компонент не найден
     */
    public RdnInfo byStringIdentifier(String identifier) {
        return rdnsByStringIdentifier.get(identifier);
    }
}
