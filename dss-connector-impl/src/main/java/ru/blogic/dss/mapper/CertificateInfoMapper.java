package ru.blogic.dss.mapper;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfCertificateInfoParamsstring1Iy7Z97I;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfCertificateInfoParamsstring1Iy7Z97I.KeyValueOfCertificateInfoParamsstring1Iy7Z97I;
import ru.blogic.dss.api.dto.CertificateInfo;
import ru.blogic.dss.common.util.Grouping;
import ru.blogic.dss.common.util.MapUtilsExt;
import ru.cryptopro.dss.services.schemas._2014._06.CertificateInfoParams;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author dgolubev
 */
public class CertificateInfoMapper extends NullSafeMapper<CertificateInfo, ArrayOfKeyValueOfCertificateInfoParamsstring1Iy7Z97I> {

    @Override
    protected CertificateInfo nullSafeFrom(ArrayOfKeyValueOfCertificateInfoParamsstring1Iy7Z97I source) {
        final Map<CertificateInfoParams, KeyValueOfCertificateInfoParamsstring1Iy7Z97I> params
                = MapUtilsExt.byKey(
                source.getKeyValueOfCertificateInfoParamsstring1Iy7Z97I(),
                new Grouping<CertificateInfoParams, KeyValueOfCertificateInfoParamsstring1Iy7Z97I>() {
                    @Override
                    public CertificateInfoParams group(KeyValueOfCertificateInfoParamsstring1Iy7Z97I value) {
                        return value.getKey();
                    }
                });

        return new CertificateInfo(
                getParam(params, CertificateInfoParams.SUBJECT_NAME, String.class),
                getParam(params, CertificateInfoParams.ISSUER_NAME, String.class),
                getParam(params, CertificateInfoParams.NOT_BEFORE, Date.class),
                getParam(params, CertificateInfoParams.NOT_AFTER, Date.class),
                getParam(params, CertificateInfoParams.SERIAL_NUMBER, String.class),
                getParam(params, CertificateInfoParams.THUMBPRINT, String.class),
                getParam(params, CertificateInfoParams.KEY_IDENTIFIER, String.class)
        );
    }

    @Override
    protected ArrayOfKeyValueOfCertificateInfoParamsstring1Iy7Z97I nullSafeTo(CertificateInfo source) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private <T> T getParam(
            Map<CertificateInfoParams, KeyValueOfCertificateInfoParamsstring1Iy7Z97I> map,
            CertificateInfoParams key,
            Class<T> targetClass) {
        final KeyValueOfCertificateInfoParamsstring1Iy7Z97I pair = map.get(key);
        if (pair == null || pair.getValue() == null) {
            return null;
        }

        final T result;

        if (String.class.isAssignableFrom(targetClass)) {
            result = (T) pair.getValue();
        } else if (Date.class.isAssignableFrom(targetClass)) {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                result = (T) dateFormat.parse(pair.getValue());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException();
        }

        return result;
    }
}
