package ru.blogic.dss.mapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Маппер, использующий таблицу соответсвий для преобразования объектов типа A в тип B и наоборот.
 *
 * @author dgolubev
 */
public abstract class ExplicitMapper<TYPE_A, TYPE_B> extends NullSafeMapper<TYPE_A, TYPE_B> {

    private final BiMap<TYPE_A, TYPE_B> directMappings;
    private final BiMap<TYPE_B, TYPE_A> invertedMappings;

    private final boolean failOnNoMapping;

    public ExplicitMapper() {
        this(true);
    }

    public ExplicitMapper(boolean failOnNoMapping) {
        this.failOnNoMapping = failOnNoMapping;

        final Map<TYPE_A, TYPE_B> mappings = new HashMap<TYPE_A, TYPE_B>();
        map(mappings);

        directMappings = HashBiMap.create(mappings);
        invertedMappings = directMappings.inverse();
    }

    @Override
    protected TYPE_A nullSafeFrom(TYPE_B source) {
        if (failOnNoMapping && !invertedMappings.containsKey(source)) {
            throw new IllegalArgumentException("There is no mapping for value " + source);
        }

        return invertedMappings.get(source);
    }

    @Override
    protected TYPE_B nullSafeTo(TYPE_A source) {
        if (failOnNoMapping && !directMappings.containsKey(source)) {
            throw new IllegalArgumentException("There is no mapping for value " + source);
        }

        return directMappings.get(source);
    }

    /**
     * Метод для установления соответствий между объектами двух типов.
     *
     * @param mappings таблица соответсвий. Ключи и значения должны быть уникальны.
     */
    protected abstract void map(Map<TYPE_A, TYPE_B> mappings);
}
