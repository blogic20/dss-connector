package ru.blogic.dss.mapper.usermanagement;

import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ObjectFactory;
import ru.blogic.dss.api.dto.usermanagement.userrequest.UserFilter;
import ru.blogic.dss.mapper.NullSafeMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Created by pkupershteyn on 07.04.2016.
 */
@ApplicationScoped
public class UserFilterMapper extends NullSafeMapper<UserFilter, org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserFilter> {

    @Inject
    private ColumnTypeMapper columnTypeMapper;

    @Inject
    private FilterOperationMapper filterOperationMapper;

    @Override
    protected UserFilter nullSafeFrom(org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserFilter source) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserFilter nullSafeTo(UserFilter source) {

        org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserFilter result = new org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserFilter();

        result.setValue(new ObjectFactory().createUserFilterValue(source.getValue()));

        result.setOperation(filterOperationMapper.to(source.getOperation()));

        result.setColumn(columnTypeMapper.to(source.getColumn()));

        return result;

    }
}
