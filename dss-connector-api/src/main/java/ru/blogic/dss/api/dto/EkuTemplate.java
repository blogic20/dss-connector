package ru.blogic.dss.api.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Шаблон Extended Key Usage (шаблон сертификата)
 *
 * @author dgolubev
 */
public class EkuTemplate implements Serializable {

    private final String name;
    private final List<String> oids;

    public EkuTemplate(String name, List<String> oids) {
        this.name = name;
        this.oids = oids != null ? Collections.unmodifiableList(oids) : Collections.<String>emptyList();
    }

    public String getName() {
        return name;
    }

    public List<String> getOids() {
        return oids;
    }

    @Override
    public String toString() {
        return "EkuTemplate{" +
                "name='" + name + '\'' +
                '}';
    }
}
