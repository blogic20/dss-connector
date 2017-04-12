package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.DssRequestStatus;
import ru.cryptopro.dss.services.schemas._2014._06.DSSRequestStatusEnum;

/**
 * Created by pkupershteyn on 26.02.2016.
 */
public class DssRequestStatusMapper extends ExactEnumMapper<DssRequestStatus,DSSRequestStatusEnum> {
    @Override
    protected Class<DssRequestStatus> getEnumA() {
        return DssRequestStatus.class;
    }

    @Override
    protected Class<DSSRequestStatusEnum> getEnumB() {
        return DSSRequestStatusEnum.class;
    }
}
