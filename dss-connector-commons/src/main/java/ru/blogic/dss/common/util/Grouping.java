package ru.blogic.dss.common.util;

/**
 * User: VorobyovPM
 * Date: 26.02.16
 */
public interface Grouping<KEY, ELEMENT> {
    KEY group(ELEMENT element);
}
