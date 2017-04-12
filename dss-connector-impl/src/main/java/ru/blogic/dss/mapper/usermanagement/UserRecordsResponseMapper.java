package ru.blogic.dss.mapper.usermanagement;

import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfDssUserInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.DssUserInfo;
import ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsResponse;
import ru.blogic.dss.mapper.NullSafeMapper;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pkupershteyn on 07.04.2016.
 */
public class UserRecordsResponseMapper extends NullSafeMapper<UserRecordsResponse, org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsResponse> {

    @Inject
    DssUserInfoMapper dssUserInfoMapper;

    @Override
    protected UserRecordsResponse nullSafeFrom(org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsResponse source) {
        UserRecordsResponse result = new UserRecordsResponse();

        result.setTotalCount(source.getTotalCount());
        result.setAffectedCount(source.getAffectedCount());

        List<ru.blogic.dss.api.dto.usermanagement.DssUserInfo> dssUserInfos = new ArrayList<ru.blogic.dss.api.dto.usermanagement.DssUserInfo>();
        JAXBElement<ArrayOfDssUserInfo> userInfos = source.getUserInfos();
        if (userInfos != null) {
            ArrayOfDssUserInfo arrayOfDssUserInfo = userInfos.getValue();
            if (arrayOfDssUserInfo != null) {
                List<DssUserInfo> dssUserInfoList = arrayOfDssUserInfo.getDssUserInfo();
                if (dssUserInfoList != null) {
                    for (DssUserInfo sourceDssUserInfo : dssUserInfoList) {
                        dssUserInfos.add(dssUserInfoMapper.from(sourceDssUserInfo));
                    }
                }
            }
        }
        result.setUserInfos(dssUserInfos);

        return result;
    }

    @Override
    protected org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsResponse nullSafeTo(UserRecordsResponse source) {
        throw new UnsupportedOperationException();
    }
}
