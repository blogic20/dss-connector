package ru.blogic.dss.api.dto.usermanagement.userrequest;

/**
 * Created by pkupershteyn on 07.04.2016.
 * Операция, используемая в фильтре. @see {@link UserFilter}
 */
public enum FilterOperationEnum {
        /**
         * Операция равенства
         */
        EQUAL,
        /**
         * Операция неравенства
         */
        NOT_EQUAL,
        /**
         * Операция подобия
         */
        LIKE,
        /**
         * Операция "больше чем"
         */
        GREATER,
        /**
         * Операция "меньше чем"
         */
        LESS
}
