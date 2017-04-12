package ru.blogic.dss.mapper.usermanagement;

import ru.blogic.dss.api.dto.usermanagement.DssUserInfo;
import ru.blogic.dss.mapper.NullSafeMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

/**
 * Created by pkupershteyn on 05.04.2016.
 */
@ApplicationScoped
public class DssUserInfoMapper extends NullSafeMapper<DssUserInfo, org.datacontract.schemas._2004._07.cryptopro_dss_common.DssUserInfo> {
    @Override
    protected DssUserInfo nullSafeFrom(org.datacontract.schemas._2004._07.cryptopro_dss_common.DssUserInfo source) {
        DssUserInfo result = new DssUserInfo();

        result.setAccountLocked( source.isAccountLocked() );
        result.setCreationDate(getDate( source.getCreationDate() ));
        result.setDisplayName(getString( source.getDisplayName() ));
        result.setDistinguishName(getString( source.getDistinguishName() ));
        result.setEmail(getString( source.getEmail() ));
        result.setEmailConfirmed( source.isEmailConfirmed() );
        result.setGroup(getString( source.getGroup() ));
        result.setLastLoginDate(getDate(source.getLastLoginDate()));
        result.setLockoutDate(getDate(source.getLockoutDate()));
        result.setLogin(getString( source.getLogin() ));
        result.setPhoneConfirmed( source.isPhoneConfirmed() );
        result.setPhoneNumber(getString( source.getPhoneNumber() ));
        result.setUserId(getString( source.getUserId() ));

        return result;
    }

    private static String getString(JAXBElement<String> element) {
        return element != null ? element.getValue() : null;
    }

    private static Date getDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar != null ? xmlGregorianCalendar.toGregorianCalendar().getTime() : null;
    }

    private static Date getDate(JAXBElement<XMLGregorianCalendar> element) {
        return element != null ? getDate(element.getValue()) : null;
    }

    @Override
    protected org.datacontract.schemas._2004._07.cryptopro_dss_common.DssUserInfo nullSafeTo(DssUserInfo source) {
        throw new UnsupportedOperationException("Mapping to DssUserInfo is not supported");
    }
}
