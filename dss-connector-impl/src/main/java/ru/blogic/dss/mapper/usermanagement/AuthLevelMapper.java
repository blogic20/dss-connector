package ru.blogic.dss.mapper.usermanagement;

import org.datacontract.schemas._2004._07.cryptopro_dss_common.AuthnLevel;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthLevel;
import ru.blogic.dss.mapper.ExactEnumMapper;

/**
 * Created by pkupershteyn on 12.04.2016.
 */
public class AuthLevelMapper extends ExactEnumMapper<AuthLevel, AuthnLevel> {
    @Override
    protected Class<AuthLevel> getEnumA() {
        return AuthLevel.class;
    }

    @Override
    protected Class<AuthnLevel> getEnumB() {
        return AuthnLevel.class;
    }
}
