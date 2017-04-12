package ru.blogic.dss.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: VorobyovPM
 * Date: 28.08.14
 */
public final class CollectionUtilsExt {

    private static final Specification NOT_NULL_SPECIFICATION = new Specification() {
        @Override
        public boolean isSatisfied(Object object) {
            return object != null;
        }
    };

    public static final Transformer<Object, String> STRING_TRANSFORMER = new Transformer<Object, String>() {
        @Override
        public String transform(Object value) {
            return value != null ? value.toString() : null;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, String> getStringTransformer() {
        return (Transformer<T, String>) STRING_TRANSFORMER;
    }

    public static <T, K, V> Map<K, V> transformToMap(Iterable<T> iterable, Transformer<T, Map.Entry<K, V>> transformer) {
        final Map<K, V> map = new HashMap<K, V>();

        if (iterable != null) {
            for (T source : iterable) {
                final Map.Entry<K, V> entry = transformer.transform(source);
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public interface Action<T> {
        void perform(T value);
    }

    public static <ELEMENT_1, ELEMENT_2> List<ELEMENT_2> transform(Iterable<? extends ELEMENT_1> iterable, Transformer<ELEMENT_1, ELEMENT_2> transformer) {
        if (iterable == null || transformer == null) {
            return Collections.emptyList();
        }
        ArrayList<ELEMENT_2> result = new ArrayList<ELEMENT_2>();
        for (ELEMENT_1 element1 : iterable) {
            result.add(transformer.transform(element1));
        }
        result.trimToSize();
        return result;
    }

    public static <ELEMENT_1, ELEMENT_2> Set<ELEMENT_2> transformToSet(Collection<ELEMENT_1> collection, Transformer<ELEMENT_1, ELEMENT_2> transformer) {
        return new HashSet<ELEMENT_2>(transform(collection, transformer));
    }

    public static <KEY, SOURCE_VALUE, TARGET_VALUE> Map<KEY, TARGET_VALUE> transformValues(Map<KEY, SOURCE_VALUE> in,
                                                                                           Transformer<SOURCE_VALUE, TARGET_VALUE> transformer) {
        final Map<KEY, TARGET_VALUE> result = new HashMap<KEY, TARGET_VALUE>();
        for (Map.Entry<KEY, SOURCE_VALUE> entry : in.entrySet()) {
            result.put(entry.getKey(), transformer.transform(entry.getValue()));
        }
        return result;
    }

    /**
     * Возвращает коллекцию, сортированную в соответствии с компаратором
     *
     * @param in
     * @param comparator
     * @param <ELEMENT>
     * @return
     */
    public static <ELEMENT> Collection<ELEMENT> sort(Collection<ELEMENT> in, Comparator<ELEMENT> comparator) {
        if (isEmpty(in)) {
            return Collections.EMPTY_LIST;
        }
        List<ELEMENT> result = in instanceof List ? (List<ELEMENT>) in : toList(in);
        Collections.sort(result, comparator);
        return result;
    }

    /**
     * Фильтрует коллекцию по условию.
     *
     * @param in            Фильтруемая коллекция
     * @param specification филтр для коллекции см{@link Specification}
     * @param <ELEMENT>
     * @return Возвращает новую коллекцию согласно условию фильтрации. Если параметр in == null- вернет пустую коллекцию.
     * Если параметр specification ==  null - вернет копию исходной коллекции.
     */
    public static <ELEMENT> Collection<ELEMENT> filter(Collection<ELEMENT> in, Specification<ELEMENT> specification) {
        if (isEmpty(in)) {
            return Collections.emptyList();
        }
        if (specification == null) {
            specification = Specification.SATISFIED;
        }
        ArrayList<ELEMENT> result = new ArrayList<ELEMENT>(in.size());
        for (ELEMENT element : in) {
            if (specification.isSatisfied(element)) {
                result.add(element);
            }
        }
        result.trimToSize();
        return result;
    }

    public static <ELEMENT> ELEMENT findFirst(Collection<ELEMENT> in, Specification<ELEMENT> specification) {
        final Collection<ELEMENT> found = filter(in, specification);
        return found.isEmpty() ? null : found.iterator().next();
    }

    public static <T> void forEach(Iterable<T> in, Action<T> action) {
        if (in == null || action == null) {
            return;
        }
        for (T value : in) {
            action.perform(value);
        }
    }

    public static <ELEMENT> List<ELEMENT> toList(Iterable<? extends ELEMENT> iterable) {
        return toList(iterable.iterator());
    }

    public static <ELEMENT> List<ELEMENT> toList(Iterator<? extends ELEMENT> iterator) {
        final ArrayList<ELEMENT> result = new ArrayList<ELEMENT>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        result.trimToSize();
        return result;
    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> Specification<T> getNotNullSpec() {
        return (Specification<T>) NOT_NULL_SPECIFICATION;
    }

    public static <T> Specification<T> not(Specification<T> specification) {
        return new NotSpecification<T>(specification);
    }

    /**
     * Пребразовать nullable лист в немодифицируемый лист
     */
    public static <T> List<T> nullableListToUnmodifiable(List<T> nullable) {
        if (isEmpty(nullable)) {
            return Collections.emptyList();
        } else if (nullable.size() == 1) {
            return Collections.singletonList(nullable.get(0));
        } else {
            return Collections.unmodifiableList(new ArrayList<T>(nullable));
        }
    }

    /**
     * Преобразовать nullable коллекцию к сериализуемой версии листа
     */
    public static <T> List<T> nullableCollectionToSerializableList(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        } else if (collection.size() == 1) {
            return Collections.singletonList(collection.iterator().next());
        } else {
            return new ArrayList<T>(collection);
        }
    }

    /**
     * Возвращает true, если хотя бы один элемент удовлетворяет условию
     *
     * @param collection
     * @param specification
     * @param <ELEMENT>
     * @return
     */
    public static <ELEMENT> boolean exists(Collection<ELEMENT> collection, Specification<ELEMENT> specification) {
        if (collection != null && specification != null) {
            return (findFirst(collection, specification) != null);
        }
        return false;
    }

    /**
     * сравнить содержимое коллекций
     *
     * @param col1
     * @param col2
     * @return
     */
    public static boolean isValuesEquals(Collection col1, Collection col2) {
        if (col1 == col2) {
            return true;
        }

        if (col1 != null && col2 != null && col1.size() == col2.size()) {
            Set list2Set = new HashSet<Object>(col2);
            for (Object list1Element : new HashSet<Object>(col1)) {
                if (!list2Set.contains(list1Element)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
