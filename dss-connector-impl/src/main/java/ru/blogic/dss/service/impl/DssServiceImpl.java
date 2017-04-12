package ru.blogic.dss.service.impl;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I;
import org.apache.commons.lang.StringUtils;
import ru.blogic.dss.ExceptionTranslatingInterceptor;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.DssService;
import ru.blogic.dss.api.dto.CertSubject;
import ru.blogic.dss.api.dto.CertificateStatus;
import ru.blogic.dss.api.dto.DssAction;
import ru.blogic.dss.api.dto.DssConfirmableAction;
import ru.blogic.dss.api.dto.DssRequestStatus;
import ru.blogic.dss.api.dto.DssStoredCertificate;
import ru.blogic.dss.api.dto.EkuTemplate;
import ru.blogic.dss.api.dto.InteractiveChallengeConfirmation;
import ru.blogic.dss.api.dto.InteractiveChallengeRequest;
import ru.blogic.dss.api.dto.SignatureType;
import ru.blogic.dss.api.dto.SignerInfo;
import ru.blogic.dss.api.dto.VerificationResult;
import ru.blogic.dss.api.dto.XMLDSigType;
import ru.blogic.dss.api.dto.dsspolicy.DssCAPolicy;
import ru.blogic.dss.api.dto.dsspolicy.DssPolicy;
import ru.blogic.dss.api.exception.DssServiceException;
import ru.blogic.dss.common.util.SecurityUtils;
import ru.blogic.dss.common.util.X509Utils;
import ru.blogic.dss.domain.CAdESType;
import ru.blogic.dss.domain.PdfSignatureFormat;
import ru.blogic.dss.mapper.DssConfirmableActionMapper;
import ru.blogic.dss.mapper.DssPolicyMapper;
import ru.blogic.dss.mapper.DssRequestStatusMapper;
import ru.blogic.dss.mapper.DssStoredCertificateMapper;
import ru.blogic.dss.mapper.SignatureTypeMapper;
import ru.blogic.dss.mapper.SignersInfoMapper;
import ru.blogic.dss.mapper.VerificationResultMapper;
import ru.blogic.dss.mapper.XmlDSigTypeMapper;
import ru.blogic.dss.provider.DssPolicyCachedProvider;
import ru.blogic.dss.provider.WsPortProvider;
import ru.blogic.dss.service.MfaService;
import ru.cryptopro.dss.services._2014._06.ISignService;
import ru.cryptopro.dss.services._2014._06.ISignServiceGetCertificateDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2014._06.ISignServiceGetCertificatesDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2014._06.ISignServiceGetPolicyDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2014._06.ISignServiceGetRequestDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2014._06.ISignServiceGetTransactionIDDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2014._06.ISignServiceSignDocumentDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceGetSignersInfoDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceVerifyCertificateDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceVerifyDetachedSignatureAllDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceVerifyDetachedSignatureDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceVerifyGost34102001DssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceVerifySignatureAllDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._04.IVerificationServiceVerifySignatureDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services.schemas._2014._06.ArrayOfDSSCertificate;
import ru.cryptopro.dss.services.schemas._2014._06.ArrayOfVerificationResult;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCertRequest;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCertificate;
import ru.cryptopro.dss.services.schemas._2014._06.RequestParams;
import ru.cryptopro.dss.services.schemas._2014._06.SignatureParams;
import ru.cryptopro.dss.services.schemas._2014._06.VerifyParams;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.net.URI;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I.KeyValueOfSignatureParamsstring1Iy7Z97I;

/**
 * @author dgolubev
 */
@Stateless
@Interceptors(ExceptionTranslatingInterceptor.class)
public class DssServiceImpl implements DssService {

    @Inject
    private DssConfigService dssConfigService;

    @Inject
    private WsPortProvider wsPortProvider;

    @Inject
    private MfaService mfaService;

    @Inject
    private DssStoredCertificateMapper dssStoredCertificateMapper;

    @Inject
    private DssRequestStatusMapper dssRequestStatusMapper;

    @Inject
    private XmlDSigTypeMapper xmlDSigTypeMapper;

    @Inject
    private DssConfirmableActionMapper dssConfirmableActionMapper;

    @Inject
    private DssPolicyMapper dssPolicyMapper;

    @Inject
    private SignatureTypeMapper signatureTypeMapper;

    @Inject
    private VerificationResultMapper verificationResultMapper;

    @Inject
    private SignersInfoMapper signersInfoMapper;

    @Inject
    private DssPolicyCachedProvider dssPolicyCachedProvider;

    @Override
    public List<DssStoredCertificate> getCertificates() throws DssServiceException {
        return getCertificates(true);
    }

    @Override
    public List<DssStoredCertificate> getCertificates(boolean onlyActive) throws DssServiceException {
        try {
            final ArrayOfDSSCertificate certificates = wsPortProvider.getSignPort().getCertificates();
            final List<DSSCertificate> dssCertificates = certificates.getDSSCertificate();
            final List<DssStoredCertificate> unfilteredCerts = dssStoredCertificateMapper.from(dssCertificates);

            final List<DssStoredCertificate> certs;

            if (onlyActive) {
                certs = new ArrayList<DssStoredCertificate>();
                for (DssStoredCertificate cert : unfilteredCerts) {
                    if (cert.getStatus() == CertificateStatus.ACTIVE) {
                        certs.add(cert);
                    }
                }
            } else {
                certs = unfilteredCerts;
            }

            return certs;
        } catch (ISignServiceGetCertificatesDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to get user's certificates", e);
        }
    }

    @Override
    public DssStoredCertificate getCertificate(int id) throws DssServiceException {
        try {
            DSSCertificate certificate = wsPortProvider.getSignPort().getCertificate(id);
            return dssStoredCertificateMapper.from(certificate);
        } catch (ISignServiceGetCertificateDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to get certificate by id " + id, e);
        }
    }

    @Override
    public int requestCertificate(EkuTemplate ekuTemplate) throws DssServiceException {
        return requestCertificate((String) null, ekuTemplate);
    }

    @Override
    public int requestCertificate(String pin, EkuTemplate ekuTemplate) throws DssServiceException {
        return requestCertificate(new CertSubject().setCommonName(SecurityUtils.getCallerName()), ekuTemplate);
    }

    @Override
    public int requestCertificate(CertSubject owner, EkuTemplate ekuTemplate) throws DssServiceException {
        return requestCertificate(owner, null, ekuTemplate);
    }

    @Override
    public int requestCertificate(CertSubject owner, String pin, EkuTemplate ekuTemplate) throws DssServiceException {
        return requestCertificate(owner, pin, ekuTemplate, null);
    }

    private ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I createRequestCertParams(final Map<RequestParams, String> paramsStringMap) {
        ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I params = new ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I();
        final List<ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I.KeyValueOfRequestParamsstring1Iy7Z97I> keyValueOfRequestParamsstring1Iy7Z97I
                = params.getKeyValueOfRequestParamsstring1Iy7Z97I();

        for (Map.Entry<RequestParams, String> entry : paramsStringMap.entrySet()) {
            final ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I.KeyValueOfRequestParamsstring1Iy7Z97I keyValue =
                    new ArrayOfKeyValueOfRequestParamsstring1Iy7Z97I.KeyValueOfRequestParamsstring1Iy7Z97I();

            keyValue.setKey(entry.getKey());
            keyValue.setValue(entry.getValue());

            keyValueOfRequestParamsstring1Iy7Z97I.add(keyValue);
        }

        return params;
    }

    public int requestCertificate(CertSubject owner, String pin, final EkuTemplate ekuTemplate, String delegateUserId) throws DssServiceException {
        final String oids = StringUtils.join(ekuTemplate.getOids(), ",");
        int dssCertIssuerCAId = dssConfigService.getDssCertIssuerCAId();
        try {
            Map<RequestParams, String> paramsMap = new LinkedHashMap<RequestParams, String>();

            switch (getCurrentCaPolicy().getCAType()) {
                case CRYPTO_PRO_CA_15_ENROLL:
                    paramsMap.put(RequestParams.EKU_STRING, oids);
                    break;
                case CRYPTO_PRO_CA_20_ENROLL:
                    paramsMap.put(RequestParams.TEMPLATE_OID, oids);
                    break;
                default:
                    throw new DssServiceException("Unsupported CA type: "+getCurrentCaPolicy().getCAType().name());
            }

            paramsMap.put(RequestParams.REQUEST_TYPE, "First");

            return wsPortProvider.getSignPort(null, delegateUserId).createRequestEx(
                    owner.asDistinguishedName(),
                    pin,
                    dssCertIssuerCAId,
                    createRequestCertParams(paramsMap)
            );

        } catch (Exception e) {
            throw DssServiceExceptionFactory.newInstance("Unable to create certificate request", e);
        }
    }

    @Override
    public DssRequestStatus getRequestStatus(int requestId, String delegateUserId) throws DssServiceException {
        try {

            DSSCertRequest request = wsPortProvider.getSignPort(null,delegateUserId).getRequest(requestId);
            return dssRequestStatusMapper.from(request.getStatus());

        } catch (ISignServiceGetRequestDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to get sertificate request by id " + requestId, e);
        }
    }

    @Override
    public byte[] signXMLDSig(byte[] data, int certId, XMLDSigType type, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = new ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I();
        final List<KeyValueOfSignatureParamsstring1Iy7Z97I> paramList = params.getKeyValueOfSignatureParamsstring1Iy7Z97I();

        final KeyValueOfSignatureParamsstring1Iy7Z97I xmlDSigTypeParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        xmlDSigTypeParam.setKey(SignatureParams.XMLD_SIG_TYPE);
        xmlDSigTypeParam.setValue(xmlDSigTypeMapper.to(type));

        paramList.add(xmlDSigTypeParam);

        return sign(data, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.XMLD_SIG, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signGOST3410(byte[] data, int certId, boolean hash, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = new ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I();
        final List<KeyValueOfSignatureParamsstring1Iy7Z97I> paramList = params.getKeyValueOfSignatureParamsstring1Iy7Z97I();

        paramList.add(getHashParam(hash));

        return sign(data, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.GOST_3410, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signCAdESBES(byte[] data, int certId, boolean detached, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = buildCommonCAdESParams(CAdESType.BES, false, detached, null);
        return sign(data, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.C_AD_ES, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signHashCAdES_BES(byte[] hash, int certId, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = buildCommonCAdESParams(CAdESType.BES, true, true, null);
        return sign(hash, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.C_AD_ES, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signCAdES_XLT1(byte[] data, int certId, boolean detached, URI tspAddress, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        if (tspAddress == null) {
            throw new IllegalArgumentException("Time Stamp Protocol URL is required");
        }
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = buildCommonCAdESParams(CAdESType.XLT1, false, detached, tspAddress.toString());
        return sign(data, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.C_AD_ES, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signHashCAdES_XLT1(byte[] hash, int certId, URI tspAddress, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        if (tspAddress == null) {
            throw new IllegalArgumentException("Time Stamp Protocol URL is required");
        }
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = buildCommonCAdESParams(CAdESType.XLT1, true, true, tspAddress.toString());
        return sign(hash, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.C_AD_ES, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signPDF_CMS(byte[] data, int certId, String reason, String location, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = buildCommonPdfParams(PdfSignatureFormat.CMS, reason, location);
        return sign(data, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.PDF, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signPDF_CAdES(byte[] data, int certId, String reason, String location, URI tspAddress, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = buildCommonPdfParams(PdfSignatureFormat.CADES, reason, location);

        final KeyValueOfSignatureParamsstring1Iy7Z97I tspAddressParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        tspAddressParam.setKey(SignatureParams.TSP_ADDRESS);
        tspAddressParam.setValue(tspAddress.toString());

        params.getKeyValueOfSignatureParamsstring1Iy7Z97I().add(tspAddressParam);

        return sign(data, ru.cryptopro.dss.services.schemas._2014._06.SignatureType.PDF, certId, params, pin, confirmation);
    }

    @Override
    public byte[] signMsOffice(byte[] data, int certId, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        return sign(
                data,
                ru.cryptopro.dss.services.schemas._2014._06.SignatureType.MS_OFFICE,
                certId,
                new ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I(),
                pin,
                confirmation
        );
    }

    @Override
    public byte[] signCMS(byte[] data, int certId, boolean detached, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        return sign(
                data,
                ru.cryptopro.dss.services.schemas._2014._06.SignatureType.CMS,
                certId,
                buildCommonCmsParams(false, detached),
                pin,
                confirmation
        );
    }

    @Override
    public byte[] signHashCMS(byte[] hash, int certId, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        return sign(
                hash,
                ru.cryptopro.dss.services.schemas._2014._06.SignatureType.CMS,
                certId,
                buildCommonCmsParams(true, true),
                pin,
                confirmation
        );
    }

    @Override
    public VerificationResult verifySignature(SignatureType type, byte[] signedDocument, int signatureNum) throws DssServiceException {
        if (signatureNum < 1) {
            throw new IllegalArgumentException("Signature sequence number must be greater then 0");
        }

        final ru.cryptopro.dss.services.schemas._2014._06.VerificationResult result;
        try {
            result = wsPortProvider.getVerificationPort().verifySignature(
                    signatureTypeMapper.from(type),
                    signedDocument,
                    buildVerifyParams(false, signatureNum, null)
            );
        } catch (IVerificationServiceVerifySignatureDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify signature", e);
        }

        return verificationResultMapper.from(result);
    }

    @Override
    public VerificationResult verifySignature(SignatureType type, byte[] signedDocument, String signatureId) throws DssServiceException {
        final ru.cryptopro.dss.services.schemas._2014._06.VerificationResult result;
        try {
            result = wsPortProvider.getVerificationPort().verifySignature(
                    signatureTypeMapper.from(type),
                    signedDocument,
                    buildVerifyParams(false, null, signatureId)
            );
        } catch (IVerificationServiceVerifySignatureDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify signature", e);
        }

        return verificationResultMapper.from(result);
    }

    @Override
    public List<VerificationResult> verifyAllSignatures(SignatureType type, byte[] signedDocument) throws DssServiceException {
        final ArrayOfVerificationResult response;
        try {
            response = wsPortProvider.getVerificationPort().verifySignatureAll(
                    signatureTypeMapper.from(type),
                    signedDocument,
                    null
            );
        } catch (IVerificationServiceVerifySignatureAllDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify signatures", e);
        }

        return verificationResultMapper.from(response.getVerificationResult());
    }

    @Override
    public VerificationResult verifyDetachedSignature(byte[] document, byte[] signature, boolean hash) throws DssServiceException {
        final ru.cryptopro.dss.services.schemas._2014._06.VerificationResult result;
        try {
            result = wsPortProvider.getVerificationPort().verifyDetachedSignature(
                    /*
                     * Согласно докам, допустимые типы здесь - CMS и C_AD_ES,
                     * и они равносильны в контексте сервиса верификации.
                     */
                    ru.cryptopro.dss.services.schemas._2014._06.SignatureType.CMS,
                    document,
                    signature,
                    buildVerifyParams(hash, null, null)
            );
        } catch (IVerificationServiceVerifyDetachedSignatureDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify signature", e);
        }

        return verificationResultMapper.from(result);
    }

    @Override
    public List<VerificationResult> verifyAllDetachedSignatures(byte[] document, byte[] signature) throws DssServiceException {
        final ArrayOfVerificationResult response;
        try {
            response = wsPortProvider.getVerificationPort().verifyDetachedSignatureAll(
                    /*
                     * Согласно докам, допустимые типы здесь - CMS и C_AD_ES,
                     * и они равносильны в контексте сервиса верификации.
                     */
                    ru.cryptopro.dss.services.schemas._2014._06.SignatureType.CMS,
                    document,
                    signature,
                    null
            );
        } catch (IVerificationServiceVerifyDetachedSignatureAllDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify signatures", e);
        }

        return verificationResultMapper.from(response.getVerificationResult());
    }

    @Override
    public VerificationResult verifyGost34102001(byte[] document, byte[] signature, X509Certificate certificate, boolean hash) throws DssServiceException {
        final ru.cryptopro.dss.services.schemas._2014._06.VerificationResult result;
        try {
            result = wsPortProvider.getVerificationPort().verifyGost34102001(
                    certificate.getEncoded(),
                    signature,
                    document,
                    buildVerifyParams(hash, null, null)
            );
        } catch (IVerificationServiceVerifyGost34102001DssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify signature", e);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }

        return verificationResultMapper.from(result);
    }

    @Override
    public VerificationResult verifyCertificate(byte[] cert) throws DssServiceException {
        final ru.cryptopro.dss.services.schemas._2014._06.VerificationResult result;
        try {
            result = wsPortProvider.getVerificationPort().verifyCertificate(cert);
        } catch (IVerificationServiceVerifyCertificateDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to verify certificate", e);
        }

        return verificationResultMapper.from(result);
    }

    @Override
    public List<SignerInfo> getSignersInfo(SignatureType signatureType, byte[] document) throws DssServiceException {
        final ru.cryptopro.dss.services.schemas._2014._06.SignersInfo result;
        try {
            result = wsPortProvider
                    .getVerificationPort().getSignersInfo(signatureTypeMapper.from(signatureType), document);
        } catch (IVerificationServiceGetSignersInfoDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to get signers info", e);
        }

        return signersInfoMapper.from(result);
    }

    @Override
    public boolean isMfaRequired(DssAction action) throws DssServiceException {
        try {
            final DssConfirmableAction dssAction = dssConfirmableActionMapper.to(action);
            return mfaService.isMfaRequired(dssAction);
        } catch (DssServiceException e) {
            throw DssServiceExceptionFactory.newInstance("Can not obtain MFA policy for action " + action, e);
        }
    }

    @Override
    public InteractiveChallengeRequest initMfaTransaction(String smsFragment) throws DssServiceException {
        final Integer txId;
        try {
            txId = wsPortProvider.getSignPort().getTransactionID();
        } catch (ISignServiceGetTransactionIDDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to get transaction ID", e);
        }
        return mfaService.startInteractiveChallenge(dssConfigService.getSignServiceEndpoint(), smsFragment, txId);
    }

    @Override
    public DssPolicy getPolicy() throws DssServiceException {
        try {
            return dssPolicyCachedProvider.getDssPolicy(wsPortProvider);
        } catch (ISignServiceGetPolicyDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not get DSS Policy", e);
        }
    }

    @Override
    public DssCAPolicy getCurrentCaPolicy() throws DssServiceException {
        int id = dssConfigService.getDssCertIssuerCAId();
        DssPolicy policy = getPolicy();
        for (DssCAPolicy dssCAPolicy : policy.getCAPolicy()) {
            if (dssCAPolicy.getID() != null && id == dssCAPolicy.getID()) {
                return dssCAPolicy;
            }
        }
        throw new DssServiceException("Unable to get current DSS CA Policy by id " + id);
    }

    @Override
    public X509Certificate getCMSSignerCertificate(byte[] CMSData) throws DssServiceException {
        try {
            return X509Utils.getCMSSignerCertificate(CMSData);
        } catch (Exception e) {
            throw new DssServiceException(e);
        }
    }

    private ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I buildCommonCAdESParams(
            CAdESType type,
            boolean hash,
            boolean detached,
            String tspAddress) {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = new ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I();
        final List<KeyValueOfSignatureParamsstring1Iy7Z97I> paramList = params.getKeyValueOfSignatureParamsstring1Iy7Z97I();

        final KeyValueOfSignatureParamsstring1Iy7Z97I sigTypeParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        sigTypeParam.setKey(SignatureParams.CADES_TYPE);
        sigTypeParam.setValue(type.getValue());

        paramList.add(sigTypeParam);

        paramList.add(getHashParam(hash));
        paramList.add(getIsDetachedParam(detached));

        if (StringUtils.isNotBlank(tspAddress)) {
            final KeyValueOfSignatureParamsstring1Iy7Z97I tspAddressParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
            tspAddressParam.setKey(SignatureParams.TSP_ADDRESS);
            tspAddressParam.setValue(tspAddress);

            paramList.add(tspAddressParam);
        }

        return params;
    }

    private ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I buildCommonCmsParams(boolean hash, boolean detached) {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = new ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I();
        final List<KeyValueOfSignatureParamsstring1Iy7Z97I> paramList = params.getKeyValueOfSignatureParamsstring1Iy7Z97I();

        paramList.add(getHashParam(hash));
        paramList.add(getIsDetachedParam(detached));

        return params;
    }

    private ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I buildCommonPdfParams(PdfSignatureFormat pdfSignatureFormat, String reason, String location) {
        final ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params = new ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I();
        final List<KeyValueOfSignatureParamsstring1Iy7Z97I> paramList = params.getKeyValueOfSignatureParamsstring1Iy7Z97I();

        final KeyValueOfSignatureParamsstring1Iy7Z97I formatParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        formatParam.setKey(SignatureParams.PDF_FORMAT);
        formatParam.setValue(pdfSignatureFormat.getValue());

        paramList.add(formatParam);

        final KeyValueOfSignatureParamsstring1Iy7Z97I reasonParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        reasonParam.setKey(SignatureParams.PDF_REASON);
        reasonParam.setValue(reason);

        paramList.add(reasonParam);

        final KeyValueOfSignatureParamsstring1Iy7Z97I locationParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        locationParam.setKey(SignatureParams.PDF_LOCATION);
        locationParam.setValue(location);

        paramList.add(locationParam);

        return params;
    }

    private KeyValueOfSignatureParamsstring1Iy7Z97I getIsDetachedParam(boolean detached) {
        final KeyValueOfSignatureParamsstring1Iy7Z97I isDetachedParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        isDetachedParam.setKey(SignatureParams.IS_DETACHED);
        isDetachedParam.setValue(Boolean.valueOf(detached).toString());
        return isDetachedParam;
    }

    private KeyValueOfSignatureParamsstring1Iy7Z97I getHashParam(boolean hash) {
        final KeyValueOfSignatureParamsstring1Iy7Z97I hashParam = new KeyValueOfSignatureParamsstring1Iy7Z97I();
        hashParam.setKey(SignatureParams.HASH);
        hashParam.setValue(Boolean.valueOf(hash).toString());
        return hashParam;
    }

    private byte[] sign(
            byte[] data,
            ru.cryptopro.dss.services.schemas._2014._06.SignatureType signatureType,
            int certId,
            ArrayOfKeyValueOfSignatureParamsstring1Iy7Z97I params,
            String pin,
            InteractiveChallengeConfirmation confirmation) throws DssServiceException {
        try {
            final ISignService signPort = confirmation != null
                    ? wsPortProvider.getSignPort(confirmation)
                    : wsPortProvider.getSignPort();
            return signPort.signDocument(data, signatureType, certId, pin, params);
        } catch (ISignServiceSignDocumentDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to sign data", e);
        }
    }

    private ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I buildVerifyParams(Boolean verifyHash, Integer signatureNum, String signatureId) {
        final ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I result = new ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I();
        final List<ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I> params
                = result.getKeyValueOfVerifyParamsstring1Iy7Z97I();

        if (verifyHash != null) {
            final ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I param
                    = new ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I();
            param.setKey(VerifyParams.HASH);
            param.setValue(verifyHash.toString());
            params.add(param);
        }

        if (signatureNum != null && signatureNum > 0) {
            final ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I param
                    = new ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I();
            param.setKey(VerifyParams.SIGNATURE_INDEX);
            param.setValue(signatureNum.toString());
            params.add(param);
        }

        if (StringUtils.isNotBlank(signatureId)) {
            final ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I param
                    = new ArrayOfKeyValueOfVerifyParamsstring1Iy7Z97I.KeyValueOfVerifyParamsstring1Iy7Z97I();
            param.setKey(VerifyParams.SIGNATURE_ID);
            param.setValue(signatureId);
            params.add(param);
        }

        return result;
    }
}