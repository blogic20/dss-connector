package ru.blogic.dss.common.util;

import org.apache.commons.lang.Validate;

/**
 * Класс, реализующий отрицание для внутренней спецификации
 * User: VorobyovPM
 * Date: 27.11.15
 */
public class NotSpecification<T> implements Specification<T> {
    private final Specification<T> inner;

    public NotSpecification(Specification<T> inner) {
        Validate.notNull(inner, "Inner specification cannot be null");
        this.inner = inner;
    }

    @Override
    public boolean isSatisfied(T object) {
        return !inner.isSatisfied(object);
    }
}
