package ru.blogic.dss.mapper.usermanagement;

import ru.blogic.dss.api.dto.dsspolicy.IdentifierType;
import ru.blogic.dss.mapper.ExactEnumMapper;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by pkupershteyn on 11.04.2016.
 */
@ApplicationScoped
public class IdentifierTypeMapper extends ExactEnumMapper<IdentifierType, org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentifierType> {
    @Override
    protected Class<IdentifierType> getEnumA() {
        return IdentifierType.class;
    }

    @Override
    protected Class<org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentifierType> getEnumB() {
        return org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentifierType.class;
    }
}
