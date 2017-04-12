package ru.blogic.dss.mapper.usermanagement;

import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.FilterOperationEnum;
import ru.blogic.dss.mapper.ExactEnumMapper;

/**
 * Created by pkupershteyn on 07.04.2016.
 */
public class FilterOperationMapper extends ExactEnumMapper<ru.blogic.dss.api.dto.usermanagement.userrequest.FilterOperationEnum, FilterOperationEnum> {
    @Override
    protected Class<ru.blogic.dss.api.dto.usermanagement.userrequest.FilterOperationEnum> getEnumA() {
        return ru.blogic.dss.api.dto.usermanagement.userrequest.FilterOperationEnum.class;
    }

    @Override
    protected Class<FilterOperationEnum> getEnumB() {
        return FilterOperationEnum.class;
    }
}
