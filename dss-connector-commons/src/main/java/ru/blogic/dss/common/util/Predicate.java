package ru.blogic.dss.common.util;

/**
 * User: VorobyovPM
 * Date: 16.03.16
 */
public interface Predicate<T> {
    Predicate TRUE = new Predicate() {
        @Override
        public boolean evaluate(Object object) {
            return true;
        }
    };

    Predicate FALSE = new Predicate() {
        @Override
        public boolean evaluate(Object object) {
            return false;
        }
    };

    boolean evaluate(T object);
}
