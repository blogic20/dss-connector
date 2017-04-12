package ru.blogic.dss.common.util;

//import com.objsys.asn1j.runtime.Asn1BerDecodeBuffer;
//import com.objsys.asn1j.runtime.Asn1DerDecodeBuffer;
//import com.objsys.asn1j.runtime.Asn1Exception;
//import com.objsys.asn1j.runtime.Asn1Integer;
//import com.objsys.asn1j.runtime.Asn1ObjectIdentifier;
//import com.objsys.asn1j.runtime.Asn1OctetString;
//import com.objsys.asn1j.runtime.Asn1Tag;
//import com.objsys.asn1j.runtime.Asn1Type;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import ru.blogic.dss.api.dto.CertificateTemplateExtensionInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

//import org.bouncycastle.asn1.ASN1ObjectIdentifier;
//import org.bouncycastle.asn1.ASN1Sequence;
//import org.bouncycastle.asn1.cms.Attribute;
//import ru.CryptoPro.CAdES.CAdESSignature;
//import ru.CryptoPro.CAdES.CAdESSigner;
//import ru.CryptoPro.CAdES.CAdESType;
//import ru.CryptoPro.CAdES.exception.CAdESException;

/**
 * @author dgolubev
 */
public class X509Utils {

    private static final String CERT_TYPE = "X.509";

    private static final String OID_EXTENSION_CERTIFICATE_TEMPLATE = "1.3.6.1.4.1.311.21.7";
    private static final String OID_EXTENSION_SIGN_CERTIFICATES = "1.2.840.113549.1.9.16.2.23";

    private X509Utils() {
    }

    public static X509Certificate decode(byte[] certBytes) {
        final CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance(CERT_TYPE);
        } catch (CertificateException e) {
            throw new RuntimeException("Unable to create factory for certificates of type " + CERT_TYPE, e);
        }

        try {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (CertificateException e) {
            throw new RuntimeException("Unable to generate certificate", e);
        }
    }

    public static X509Certificate decode(String base64EncodedCert) {
        final byte[] certBytes = Base64.decodeBase64(base64EncodedCert);
        return decode(certBytes);
    }

    public static String getThumbprint(X509Certificate certificate) {
        try {
            return DigestUtils.sha1Hex(certificate.getEncoded()).toUpperCase();
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("Unable to compute certificate thumbprint", e);
        }
    }

    /**
     * Получение сертификата подписанта из структуры CMS подписи.
     *
     * @param CMSData Данные CMS подписи
     * @return Сертификат подписанта, или null если он не найден.
     * @throws IOException
//     * @throws CAdESException
     */
    // FIXME: зависимость от JCP
    public static X509Certificate getCMSSignerCertificate(byte[] CMSData) throws IOException/*, CAdESException*/ {
//        CAdESSignature cAdESSignature = new CAdESSignature(CMSData, null, CAdESType.CAdES_X_Long_Type_1);
//        //Предполагаем, что данные содержат одну подпись.
//        CAdESSigner cAdESSignerInfo = cAdESSignature.getCAdESSignerInfo(0);
//
//        //поскольку CAdESSigner.getSignerCertificate() почему-то не возвращает искомое, приходится парсить ASN1 самостоятельно
//        // Для работы с ASN1 исползуем bouncycastle, используемый cryptopro JCP.
//
//        //Запоминаем серийный номер сертификата подписанта
//        BigInteger signerSerial = cAdESSignerInfo.getSignerInfo().getSID().getSerialNumber();
//
//        //Получаем расширение cert values из массива расширений
//        ASN1ObjectIdentifier cerValues = new ASN1ObjectIdentifier(OID_EXTENSION_SIGN_CERTIFICATES);
//
//        Attribute attribute = cAdESSignerInfo.getSignerInfo().getUnsignedAttributes().get(cerValues);
//
//        if (attribute != null || attribute.getAttrValues() != null || attribute.getAttrValues().size() > 0) {
//
//            // Получаем значение атрибута - sequence сертификатов
//            ASN1Sequence seq = (ASN1Sequence) attribute.getAttrValues().getObjectAt(0);
//            //цикл по сертификатам
//            for (int i = 0; i < seq.size(); i++) {
//                //чтение сертификата
//                ASN1Sequence seq2 = (ASN1Sequence) seq.getObjectAt(i);
//                X509Certificate cert = decode(seq2.getEncoded());
//
//                if (signerSerial.equals(cert.getSerialNumber())) {
//                    //найденный сертификат - сертификат подписанта, возвращаем его.
//                    return cert;
//                }
//            }
//        }
        //Сертификат подписанта не найден.
        return null;
    }

    /**
     * Получение расширения "шаблон сертификата"
     *
     * @param certificate сертификат x.509
     * @return информация о расширении
     */
    public static CertificateTemplateExtensionInfo getTemplateExtensionInfo(X509Certificate certificate) {
        byte[] ext = certificate.getExtensionValue(OID_EXTENSION_CERTIFICATE_TEMPLATE);
        CertificateTemplateExtensionInfo info = null;
        // FIXME: зависимость от JCP
//        if (ext != null) {
//            Asn1DerDecodeBuffer decodeBuffer = new Asn1DerDecodeBuffer(ext);
//            Asn1OctetString os1 = new Asn1OctetString();
//            Asn1TemplateInfoSequence sequence = new Asn1TemplateInfoSequence();
//            try {
//                os1.decode(decodeBuffer);
//                decodeBuffer = new Asn1DerDecodeBuffer(os1.value);
//                sequence.decode(decodeBuffer);
//            } catch (Exception e) {
//                throw new RuntimeException("Could not get CertificateTemplateExtensionInfo: ", e);
//            }
//
//            int[] oidInts = sequence.oid.value;
//            String[] oid = new String[oidInts.length];
//            for (int i = 0; i < oidInts.length; i++) {
//                oid[i] = Integer.toString(oidInts[i]);
//            }
//            info = new CertificateTemplateExtensionInfo(StringUtils.join(oid, '.'), sequence.getMajorVersion(), sequence.getMinorVersion());
//        }
        return info;
    }


//    FIXME: зависимость от JCP
//    private static class Asn1TemplateInfoSequence extends Asn1Type {
//        private Asn1ObjectIdentifier oid;
//        private Asn1Integer majorVersion;
//        private Asn1Integer minorVersion;
//
//        public int[] getOid() {
//            return oid.value;
//        }
//
//        public int getMajorVersion() {
//            return new Long(majorVersion.value).intValue();
//        }
//
//        public int getMinorVersion() {
//            return new Long(minorVersion.value).intValue();
//        }
//
//        public void decode(Asn1BerDecodeBuffer decodeBuff, boolean doMatchTag, int tag) throws Asn1Exception, IOException {
//            this.matchTag(decodeBuff, Asn1Tag.SEQUENCE);
//
//            oid = new Asn1ObjectIdentifier();
//            oid.decode(decodeBuff);
//            majorVersion = new Asn1Integer();
//            majorVersion.decode(decodeBuff);
//
//            minorVersion = new Asn1Integer();
//            minorVersion.decode(decodeBuff);
//
//        }
//
//    }
}
