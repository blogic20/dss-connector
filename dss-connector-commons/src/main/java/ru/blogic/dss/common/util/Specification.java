package ru.blogic.dss.common.util;

/**
 * User: VorobyovPM
 * Date: 12.03.15
 */
public interface Specification<T> {
    Specification SATISFIED = new Specification() {
        @Override
        public boolean isSatisfied(Object object) {
            return true;
        }
    };

    boolean isSatisfied(T object);
}
