package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.DssStoredCertificate;
import ru.blogic.dss.common.util.X509Utils;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCertificate;

import javax.inject.Inject;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
public class DssStoredCertificateMapper extends NullSafeMapper<DssStoredCertificate, DSSCertificate> {

    @Inject
    private CertificateStatusMapper certStatusMapper;

    DssStoredCertificateMapper() {
    }

    @Override
    protected DssStoredCertificate nullSafeFrom(DSSCertificate source) {
        final X509Certificate cert = X509Utils.decode(source.getCertificateBase64().getValue());
        return new DssStoredCertificate(
                source.getID(),
                certStatusMapper.from(source.getStatus().getValue().getValue()),
                X509Utils.getThumbprint(cert),
                cert,
                X509Utils.getTemplateExtensionInfo(cert)
        );
    }

    @Override
    protected DSSCertificate nullSafeTo(DssStoredCertificate source) {
        throw new UnsupportedOperationException();
    }
}
