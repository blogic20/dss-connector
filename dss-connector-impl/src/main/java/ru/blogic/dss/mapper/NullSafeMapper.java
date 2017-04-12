package ru.blogic.dss.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Суперкласс преобразователей объектов. Позволяет не проверять исходный объект на {@code null}
 * (в этом случае результатом приобразования является возвращаемое значение из метода
 * {@link NullSafeMapper#getNullValueA()} или {@link NullSafeMapper#getNullValueB()},
 * по умолчанию - {@code null})
 *
 * @author dgolubev
 */
public abstract class NullSafeMapper<TYPE_A, TYPE_B> implements Mapper<TYPE_A, TYPE_B> {

    @Override
    public TYPE_A from(TYPE_B source) {
        return source != null ? nullSafeFrom(source) : getNullValueA();
    }

    @Override
    public List<TYPE_A> from(Collection<TYPE_B> source) {
        if (source == null) {
            return new ArrayList<TYPE_A>();
        }

        final List<TYPE_A> result = new ArrayList<TYPE_A>(source.size());
        for (TYPE_B element : source) {
            TYPE_A mappedElement = from(element);
            if (mappedElement != null) {
                result.add(mappedElement);
            }
        }

        return result;
    }

    @Override
    public TYPE_B to(TYPE_A source) {
        return source != null ? nullSafeTo(source) : getNullValueB();
    }

    @Override
    public List<TYPE_B> to(Collection<TYPE_A> source) {
        if (source == null) {
            return new ArrayList<TYPE_B>();
        }

        final List<TYPE_B> result = new ArrayList<TYPE_B>(source.size());
        for (TYPE_A element : source) {
            TYPE_B mappedElement = to(element);
            if (mappedElement != null) {
                result.add(mappedElement);
            }
        }

        return result;
    }

    protected abstract TYPE_A nullSafeFrom(TYPE_B source);

    protected abstract TYPE_B nullSafeTo(TYPE_A source);

    protected TYPE_A getNullValueA() {
        return null;
    }

    protected TYPE_B getNullValueB() {
        return null;
    }
}
