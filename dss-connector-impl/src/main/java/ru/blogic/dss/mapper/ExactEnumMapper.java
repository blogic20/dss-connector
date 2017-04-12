package ru.blogic.dss.mapper;

/**
 * Маппер, осуществляющий преобразование констант перечислений в точном соответсвии с их именем.
 *
 * @author dgolubev
 * @see NullSafeMapper
 */
public abstract class ExactEnumMapper<ENUM_A extends Enum<ENUM_A>, ENUM_B extends Enum<ENUM_B>> extends NullSafeMapper<ENUM_A, ENUM_B> {

    protected abstract Class<ENUM_A> getEnumA();
    protected abstract Class<ENUM_B> getEnumB();

    @Override
    protected ENUM_A nullSafeFrom(ENUM_B source) {
        return Enum.valueOf(getEnumA(), source.name());
    }

    @Override
    protected ENUM_B nullSafeTo(ENUM_A source) {
        return Enum.valueOf(getEnumB(), source.name());
    }
}
