package ru.blogic.dss.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Интерфейс для двунаправленного преобразования типов объектов.
 *
 * @author dgolubev
 */
public interface Mapper<TYPE_A, TYPE_B> extends Serializable {

    TYPE_A from(TYPE_B source);

    List<TYPE_A> from(Collection<TYPE_B> source);

    TYPE_B to(TYPE_A source);

    List<TYPE_B> to(Collection<TYPE_A> source);
}
