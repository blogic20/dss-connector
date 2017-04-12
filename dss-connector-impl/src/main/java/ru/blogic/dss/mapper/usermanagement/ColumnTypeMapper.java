package ru.blogic.dss.mapper.usermanagement;

import ru.blogic.dss.api.dto.usermanagement.userrequest.ColumnTypeEnum;
import ru.blogic.dss.mapper.ExactEnumMapper;

/**
 * Created by pkupershteyn on 07.04.2016.
 */
public class ColumnTypeMapper extends ExactEnumMapper<ColumnTypeEnum, org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ColumnTypeEnum> {
    @Override
    protected Class<ColumnTypeEnum> getEnumA() {
        return ColumnTypeEnum.class;
    }

    @Override
    protected Class<org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ColumnTypeEnum> getEnumB() {
        return org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ColumnTypeEnum.class;
    }

}
