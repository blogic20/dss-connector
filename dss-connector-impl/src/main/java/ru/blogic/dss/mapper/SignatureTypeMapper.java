package ru.blogic.dss.mapper;

import ru.cryptopro.dss.services.schemas._2014._06.SignatureType;

/**
 * @author dgolubev
 */
public class SignatureTypeMapper extends ExactEnumMapper<SignatureType, ru.blogic.dss.api.dto.SignatureType> {

    @Override
    protected Class<SignatureType> getEnumA() {
        return SignatureType.class;
    }

    @Override
    protected Class<ru.blogic.dss.api.dto.SignatureType> getEnumB() {
        return ru.blogic.dss.api.dto.SignatureType.class;
    }
}
