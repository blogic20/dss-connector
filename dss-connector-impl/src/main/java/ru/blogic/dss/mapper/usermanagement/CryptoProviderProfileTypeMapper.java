package ru.blogic.dss.mapper.usermanagement;

import ru.blogic.dss.api.dto.usermanagement.umspolicy.CryptoProviderProfileType;
import ru.blogic.dss.mapper.ExactEnumMapper;

/**
 * Created by pkupershteyn on 11.04.2016.
 */
public class CryptoProviderProfileTypeMapper extends ExactEnumMapper<CryptoProviderProfileType, org.datacontract.schemas._2004._07.cryptopro_dss_common.CryptoProviderProfileType>{
    @Override
    protected Class<CryptoProviderProfileType> getEnumA() {
        return CryptoProviderProfileType.class;
    }

    @Override
    protected Class<org.datacontract.schemas._2004._07.cryptopro_dss_common.CryptoProviderProfileType> getEnumB() {
        return org.datacontract.schemas._2004._07.cryptopro_dss_common.CryptoProviderProfileType.class;
    }
}
