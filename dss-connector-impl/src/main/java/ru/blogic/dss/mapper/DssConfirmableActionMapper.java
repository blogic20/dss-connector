package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.DssAction;
import ru.blogic.dss.api.dto.DssConfirmableAction;

/**
 * @author dgolubev
 */
public class DssConfirmableActionMapper extends ExactEnumMapper<DssAction, DssConfirmableAction> {

    @Override
    protected Class<DssAction> getEnumA() {
        return DssAction.class;
    }

    @Override
    protected Class<DssConfirmableAction> getEnumB() {
        return DssConfirmableAction.class;
    }
}
