package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.CertificateStatus;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCertificateStatusEnum;

/**
 * @author dgolubev
 */
public class CertificateStatusMapper extends ExactEnumMapper<CertificateStatus, DSSCertificateStatusEnum> {

    @Override
    protected DSSCertificateStatusEnum getNullValueB() {
        return DSSCertificateStatusEnum.NULL;
    }

    @Override
    protected CertificateStatus getNullValueA() {
        return CertificateStatus.NULL;
    }

    @Override
    protected Class<CertificateStatus> getEnumA() {
        return CertificateStatus.class;
    }

    @Override
    protected Class<DSSCertificateStatusEnum> getEnumB() {
        return DSSCertificateStatusEnum.class;
    }
}
