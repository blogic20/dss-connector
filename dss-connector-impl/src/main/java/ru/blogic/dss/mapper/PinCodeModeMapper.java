package ru.blogic.dss.mapper;

import ru.cryptopro.dss.services.schemas._2014._06.PinCodeMode;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class PinCodeModeMapper extends ExactEnumMapper<PinCodeMode, ru.blogic.dss.api.dto.PinCodeMode> {
    @Override
    protected Class<PinCodeMode> getEnumA() {
        return PinCodeMode.class;
    }

    @Override
    protected Class<ru.blogic.dss.api.dto.PinCodeMode> getEnumB() {
        return ru.blogic.dss.api.dto.PinCodeMode.class;
    }
}
