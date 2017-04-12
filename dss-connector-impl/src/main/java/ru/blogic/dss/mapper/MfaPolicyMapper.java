package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.MfaPolicyState;
import ru.cryptopro.dss.services.schemas._2014._06.MfaPolicy;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class MfaPolicyMapper extends ExactEnumMapper<MfaPolicy, MfaPolicyState> {
    @Override
    protected Class<MfaPolicy> getEnumA() {
       return MfaPolicy.class;
    }

    @Override
    protected Class<MfaPolicyState> getEnumB() {
        return MfaPolicyState.class;
    }
}
