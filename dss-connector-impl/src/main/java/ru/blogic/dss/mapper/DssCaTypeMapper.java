package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.DssCAType;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCAType;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class DssCaTypeMapper extends ExactEnumMapper<DSSCAType, DssCAType> {
    @Override
    protected Class<DSSCAType> getEnumA() {
        return DSSCAType.class;
    }

    @Override
    protected Class<DssCAType> getEnumB() {
        return DssCAType.class;
    }
}
