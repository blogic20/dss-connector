package ru.blogic.dss.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: VorobyovPM
 * Date: 20.11.14
 */
public class MapUtilsExt {

    public static final Transformer IDENT_TRANSFORMER = new Transformer() {
        @Override
        public Object transform(Object value) {
            return value;
        }
    };

    public static <VALUE> Grouping<String, VALUE> getStringGrouping() {
        return new Grouping<String, VALUE>() {
            @Override
            public String group(VALUE value) {
                return value != null ? value.toString() : null;
            }
        };
    }

    public static <KEY, VALUE> boolean containsAny(Map<KEY, VALUE> map, Collection<KEY> keys) {
        if (map == null || keys == null) {
            return false;
        }
        for(KEY key: keys) {
            if (map.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public static <KEY, VALUE> Collection<VALUE> getAllValues(Map<KEY, VALUE> map, Collection<KEY> keys) {
        if (map == null || keys == null) {
            return Collections.emptyList();
        }
        Collection<VALUE> result = new LinkedList<VALUE>();
        for(KEY key: keys) {
            if (map.containsKey(key)) {
                result.add(map.get(key));
            }
        }
        return result;
    }

    /**
     * Группировка коллекции. Порядок сохраняется.
     * @param collection    коллекция
     * @param grouping      группировка
     * @return              мапа с группированными значениями
     */
    @SuppressWarnings("unchecked")
    public static <KEY, ELEMENT> Map<KEY, List<ELEMENT>> group(Collection<ELEMENT> collection, Grouping<KEY, ELEMENT> grouping) {
        return group(collection, grouping, IDENT_TRANSFORMER);
    }

    /**
     * Группировка коллекции. Порядок сохраняется. Также трансормируется значение коллекции
     * @param collection    коллекция
     * @param grouping      группировка
     * @param transformer   трансформер коллекции
     * @return              мапа с группированными значениями
     */
    @SuppressWarnings("unchecked")
    public static <KEY, ELEMENT, MAPPED_ELEMENT> Map<KEY, List<MAPPED_ELEMENT>> group(Collection<ELEMENT> collection, Grouping<KEY, ELEMENT> grouping, Transformer<ELEMENT, MAPPED_ELEMENT> transformer) {
        if (grouping == null || transformer == null || CollectionUtilsExt.isEmpty(collection)) {
            return Collections.emptyMap();
        }
        Map<KEY, List<MAPPED_ELEMENT>> result = new LinkedHashMap<KEY, List<MAPPED_ELEMENT>>();
        for (ELEMENT element : collection) {
            KEY key = grouping.group(element);
            List<MAPPED_ELEMENT> elements = result.get(key);
            if (elements == null) {
                elements = new ArrayList<MAPPED_ELEMENT>();
                result.put(key, elements);
            }
            elements.add(transformer.transform(element));
        }
        return result;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> filterByValue(Map<KEY, VALUE> map, Predicate<VALUE> valuePredicate) {
        if (map == null || valuePredicate == null) {
            return map;
        }
        Map<KEY, VALUE> result = new HashMap<KEY, VALUE>();
        for (Map.Entry<KEY, VALUE> entry : map.entrySet()) {
            if (valuePredicate.evaluate(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> filterByKey(Map<KEY, VALUE> map, Predicate<KEY> keyPredicate) {
        if (map == null || keyPredicate == null) {
            return map;
        }
        Map<KEY, VALUE> result = new HashMap<KEY, VALUE>();
        for (Map.Entry<KEY, VALUE> entry : map.entrySet()) {
            if (keyPredicate.evaluate(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> byKey(Iterable<VALUE> valueCollection, Grouping<KEY, VALUE> grouping) {
        if (valueCollection == null || grouping == null) {
            return Collections.emptyMap();
        }

        Map<KEY, VALUE> result = new HashMap<KEY, VALUE>();
        for (VALUE value : valueCollection) {
            result.put(grouping.group(value), value);
        }

        return result;
    }

    /**
     * Сконвертировать значения в map
     * @param map           мапа
     * @param transformer   value трансформер
     * @param <KEY>         тип ключа
     * @param <ELEMENT_1>   тип значений до
     * @param <ELEMENT_2>   тип значений после
     * @return              сконвертированная мапа
     */
    public static <KEY, ELEMENT_1, ELEMENT_2> Map<KEY, ELEMENT_2> transform(Map<KEY, ELEMENT_1> map, Transformer<ELEMENT_1, ELEMENT_2> transformer) {
        if (map == null || transformer == null) {
            return Collections.emptyMap();
        }
        final HashMap<KEY, ELEMENT_2> result = new HashMap<KEY, ELEMENT_2>();
        for (Map.Entry<KEY, ELEMENT_1> entry : map.entrySet()) {
            result.put(entry.getKey(), transformer.transform(entry.getValue()));
        }
        return result;
    }

    /**
     * Сконвертировать значения в map
     * @param map           мапа
     * @param transformer   key трансформер
     * @param <KEY_1>       тип ключа до
     * @param <KEY_2>       тип ключа после
     * @param <ELEMENT>     тип значения
     * @return              сконвертированная мапа
     */
    public static <KEY_1, KEY_2, ELEMENT> Map<KEY_2, ELEMENT> transformKeys(Map<KEY_1, ELEMENT> map, Transformer<KEY_1, KEY_2> transformer) {
        if (map == null || transformer == null) {
            return Collections.emptyMap();
        }
        final HashMap<KEY_2, ELEMENT> result = new HashMap<KEY_2, ELEMENT>();
        for (Map.Entry<KEY_1, ELEMENT> entry : map.entrySet()) {
            result.put(transformer.transform(entry.getKey()), entry.getValue());
        }
        return result;
    }

    /**
     * Поменять ключи и значения
     * @param map   исходная мапа
     * @return      результирующая мапа
     */
    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        final Map<V, K> inverted = new HashMap<V, K>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            inverted.put(entry.getValue(), entry.getKey());
        }
        return inverted;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}
