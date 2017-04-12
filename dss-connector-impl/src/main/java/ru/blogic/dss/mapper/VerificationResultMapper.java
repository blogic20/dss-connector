package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.VerificationResult;
import ru.blogic.dss.common.util.X509Utils;

import javax.xml.bind.JAXBElement;

/**
 * @author dgolubev
 */
public class VerificationResultMapper
        extends NullSafeMapper<VerificationResult, ru.cryptopro.dss.services.schemas._2014._06.VerificationResult> {

    VerificationResultMapper() {
    }

    @Override
    protected VerificationResult nullSafeFrom(ru.cryptopro.dss.services.schemas._2014._06.VerificationResult source) {
        final JAXBElement<String> message = source.getMessage();
        final JAXBElement<byte[]> signerCertificate = source.getSignerCertificate();

        return new VerificationResult(
                source.isResult(),
                message != null ? message.getValue() : null,
                signerCertificate != null ? X509Utils.decode(signerCertificate.getValue()) : null
        );
    }

    @Override
    protected ru.cryptopro.dss.services.schemas._2014._06.VerificationResult nullSafeTo(VerificationResult source) {
        throw new UnsupportedOperationException();
    }
}
