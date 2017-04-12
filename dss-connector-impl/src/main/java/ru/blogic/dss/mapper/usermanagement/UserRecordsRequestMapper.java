package ru.blogic.dss.mapper.usermanagement;

import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ArrayOfUserFilter;
import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ObjectFactory;
import ru.blogic.dss.api.dto.usermanagement.userrequest.UserFilter;
import ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsRequest;
import ru.blogic.dss.mapper.NullSafeMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by pkupershteyn on 07.04.2016.
 */

@ApplicationScoped
public class UserRecordsRequestMapper extends NullSafeMapper<UserRecordsRequest, org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsRequest> {

    static ObjectFactory objectFactory=new ObjectFactory();

    @Inject
    private UserFilterMapper userFilterMapper;

    @Override
    protected UserRecordsRequest nullSafeFrom(org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsRequest source) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsRequest nullSafeTo(UserRecordsRequest source) {
        org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsRequest result=objectFactory.createUserRecordsRequest();;

        result.setStartPosition(source.getStartPosition());
        result.setEndPosition(source.getEndPosition());



        ArrayOfUserFilter arrayOfUserFilter=objectFactory.createArrayOfUserFilter();

        List<org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserFilter> userFilters = arrayOfUserFilter.getUserFilter();

        for(UserFilter userFilter : source.getFilters()){
            userFilters.add( userFilterMapper.to(userFilter) );
        }

        result.setFilters( objectFactory.createArrayOfUserFilter(arrayOfUserFilter) );

        return result;
    }
}
