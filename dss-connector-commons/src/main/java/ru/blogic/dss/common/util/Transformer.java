package ru.blogic.dss.common.util;

/**
 * User: VorobyovPM
 * Date: 10.09.14
 */
public interface Transformer<FROM, TO> {
    TO transform(FROM value);
}
