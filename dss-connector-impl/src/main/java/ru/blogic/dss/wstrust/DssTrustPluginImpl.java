package ru.blogic.dss.wstrust;

import com.sun.xml.security.core.ai.IdentityType;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.api.security.trust.client.SecondaryIssuedTokenParameters;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.secext10.BinarySecurityTokenType;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import com.sun.xml.ws.security.trust.WSTrustClientContract;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.elements.ActAs;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.OnBehalfOf;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.SecondaryParameters;
import com.sun.xml.ws.security.trust.elements.UseKey;
import com.sun.xml.ws.security.trust.impl.TrustPluginImpl;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.dsig.WSSPolicyConsumerImpl;
import org.apache.commons.codec.binary.Base64;
import org.oasis_open.docs.ws_sx.ws_trust._200802.InteractiveChallengeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.blogic.dss.DssConstants;
import ru.blogic.dss.domain.DssRequestSecurityTokenResponseImpl;

import javax.xml.bind.JAXBElement;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dgolubev
 */
public class DssTrustPluginImpl extends TrustPluginImpl {

    private WSTrustElementFactory wsTrustElementFactory = new DssWSTrustElementFactoryImpl();

    private WSTrustClientContract wsTrustClientContract = new DssWSTrustClientContractImpl();

    private static final Logger log = Logger.getLogger(DssTrustPluginImpl.class.getName());

    DssTrustPluginImpl() {
    }

    @Override
    public void process(IssuedTokenContext itc) throws WSTrustException {
        String signWith;
        String encryptWith;
        String appliesTo = itc.getEndpointAddress();
        STSIssuedTokenConfiguration stsConfig = (STSIssuedTokenConfiguration) itc.getSecurityPolicy().get(0);
        String stsURI = stsConfig.getSTSEndpoint();
        if (stsURI == null) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0029_COULD_NOT_GET_STS_LOCATION(appliesTo));
            throw new WebServiceException(LogStringsMessages.WST_0029_COULD_NOT_GET_STS_LOCATION(appliesTo));
        }
        Token oboToken = stsConfig.getOBOToken();

        BaseSTSResponse result;
        try {
            final RequestSecurityToken request = createRequest(stsConfig, appliesTo, oboToken);

            final RequestSecurityTokenResponse rstr = (RequestSecurityTokenResponse) stsConfig.getOtherOptions().get(
                    DssConstants.REQUEST_SECURITY_TOKEN_RESPONSE
            );
            if (rstr != null
                    && WSTrustUtil.getAppliesToURI(rstr.getAppliesTo()).equals(stsConfig.getOtherOptions().get(STSIssuedTokenConfiguration.APPLIES_TO))) {

                result = invokeRST(rstr, stsConfig);

                final String usedEntropy = (String) stsConfig.getOtherOptions().get(DssConstants.INTERACTIVE_CHALLENGE_ENTROPY);
                final BinarySecret binarySecret = wsTrustElementFactory.createBinarySecret(
                        Base64.decodeBase64(usedEntropy),
                        WSTrustVersion.getInstance(stsConfig.getProtocol()).getNonceBinarySecretTypeURI()
                );

                request.setEntropy(wsTrustElementFactory.createEntropy(binarySecret));

                wsTrustClientContract.handleRSTR(request, result, itc);
            } else {
                result = invokeRST(request, stsConfig);

                final RequestSecurityTokenResponse response = getResponse(result);
                if (wsTrustClientContract.containsChallenge(response)
                        && response instanceof DssRequestSecurityTokenResponseImpl) {
                    final DssRequestSecurityTokenResponseImpl dssStsResponse
                            = (DssRequestSecurityTokenResponseImpl) response;
                    final InteractiveChallengeType challenge = dssStsResponse.getChallenge();
                    itc.getOtherProperties().put(DssConstants.INTERACTIVE_CHALLENGE, challenge);
                    itc.getOtherProperties().put(DssConstants.INTERACTIVE_CHALLENGE_ENTROPY, request.getEntropy().getBinarySecret().getTextValue());
                } else {
                    wsTrustClientContract.handleRSTR(request, result, itc);
                }
            }

            KeyPair keyPair = (KeyPair) stsConfig.getOtherOptions().get(WSTrustConstants.USE_KEY_RSA_KEY_PAIR);
            if (keyPair != null) {
                itc.setProofKeyPair(keyPair);
            }

            encryptWith = stsConfig.getEncryptWith();
            if (encryptWith != null) {
                itc.setEncryptWith(encryptWith);
            }

            signWith = stsConfig.getSignWith();
            if (signWith != null) {
                itc.setSignWith(signWith);
            }

        } catch (RemoteException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0016_PROBLEM_IT_CTX(stsURI, appliesTo), ex);
            throw new WSTrustException(LogStringsMessages.WST_0016_PROBLEM_IT_CTX(stsURI, appliesTo), ex);
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0016_PROBLEM_IT_CTX(stsURI, appliesTo), ex);
            throw new WSTrustException(LogStringsMessages.WST_0016_PROBLEM_IT_CTX(stsURI, appliesTo));
        }
    }

    private RequestSecurityToken createRequest(final STSIssuedTokenConfiguration stsConfig, final String appliesTo, final Token oboToken) throws URISyntaxException, WSTrustException, NumberFormatException {
        WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
        final URI requestType = URI.create(wstVer.getIssueRequestTypeURI());
        AppliesTo applTo = null;
        if (appliesTo != null) {
            applTo = WSTrustUtil.createAppliesTo(appliesTo);
            if (stsConfig.getOtherOptions().containsKey("Identity")) {
                addServerIdentity(applTo, stsConfig.getOtherOptions().get("Identity"));
            }
        }

        final RequestSecurityToken rst = wsTrustElementFactory.createRSTForIssue(null, requestType, null, applTo, null, null, null);

        // Handle OnBehalfOf token
        if (oboToken != null) {
            OnBehalfOf obo = wsTrustElementFactory.createOnBehalfOf(oboToken);
            rst.setOnBehalfOf(obo);
        }

        // Handle ActAs token
        Token actAsToken = (Token) stsConfig.getOtherOptions().get(STSIssuedTokenConfiguration.ACT_AS);
        if (actAsToken != null) {
            ActAs actAs = wsTrustElementFactory.createActAs(actAsToken);
            rst.setActAs(actAs);
        }

        // Handle LifeTime requirement
        Integer lf = (Integer) stsConfig.getOtherOptions().get(STSIssuedTokenConfiguration.LIFE_TIME);
        if (lf != null) {
            // Create Lifetime
            long lfValue = lf.longValue();
            if (lfValue > 0) {
                long currentTime = WSTrustUtil.getCurrentTimeWithOffset();
                Lifetime lifetime = WSTrustUtil.createLifetime(currentTime, lfValue, wstVer);
                rst.setLifetime(lifetime);
            }
        }

        String tokenType = null;
        String keyType = null;
        long keySize = -1;
        String signatureAlgorithm = null;
        String encryptionAlgorithm = null;
        String keyWrapAlgorithm;
        String canonicalizationAlgorithm = null;
        Claims claims = null;
        if (wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_13.getNamespaceURI())) {
            SecondaryIssuedTokenParameters sitp = stsConfig.getSecondaryIssuedTokenParameters();
            if (sitp != null) {
                SecondaryParameters sp = wsTrustElementFactory.createSecondaryParameters();
                tokenType = sitp.getTokenType();
                if (tokenType != null) {
                    sp.setTokenType(URI.create(tokenType));
                }
                keyType = sitp.getKeyType();
                if (keyType != null) {
                    sp.setKeyType(URI.create(keyType));
                }
                keySize = sitp.getKeySize();
                if (keySize > 0) {
                    sp.setKeySize(keySize);
                }

                signatureAlgorithm = sitp.getSignatureAlgorithm();
                if (signatureAlgorithm != null) {
                    sp.setSignatureAlgorithm(URI.create(signatureAlgorithm));
                }
                encryptionAlgorithm = sitp.getEncryptionAlgorithm();
                if (encryptionAlgorithm != null) {
                    sp.setEncryptionAlgorithm(URI.create(encryptionAlgorithm));
                }

                canonicalizationAlgorithm = sitp.getCanonicalizationAlgorithm();
                if (canonicalizationAlgorithm != null) {
                    sp.setCanonicalizationAlgorithm(URI.create(canonicalizationAlgorithm));
                }

                keyWrapAlgorithm = sitp.getKeyWrapAlgorithm();
                if (keyWrapAlgorithm != null) {
                    sp.setKeyWrapAlgorithm(URI.create(keyWrapAlgorithm));
                }

                claims = sitp.getClaims();
                if (claims != null) {
                    sp.setClaims(claims);
                }
                rst.setSecondaryParameters(sp);
            }
        }

        if (tokenType == null) {
            tokenType = stsConfig.getTokenType();
            if (tokenType != null) {
                rst.setTokenType(URI.create(tokenType));
            }
        }

        if (keyType == null) {
            keyType = stsConfig.getKeyType();
            if (keyType != null) {
                rst.setKeyType(URI.create(keyType));
            }
        }

        if (keySize < 1) {
            keySize = stsConfig.getKeySize();
            if (keySize > 0) {
                rst.setKeySize(keySize);
            }
        }

        if (signatureAlgorithm == null) {
            signatureAlgorithm = stsConfig.getSignatureAlgorithm();
            if (signatureAlgorithm != null) {
                rst.setSignatureAlgorithm(URI.create(signatureAlgorithm));
            }
        }

        if (encryptionAlgorithm == null) {
            encryptionAlgorithm = stsConfig.getEncryptionAlgorithm();
            if (encryptionAlgorithm != null) {
                rst.setEncryptionAlgorithm(URI.create(encryptionAlgorithm));
            }
        }

        if (canonicalizationAlgorithm == null) {
            canonicalizationAlgorithm = stsConfig.getCanonicalizationAlgorithm();
            if (canonicalizationAlgorithm != null) {
                rst.setCanonicalizationAlgorithm(URI.create(canonicalizationAlgorithm));
            }
        }

        if (claims == null) {
            claims = stsConfig.getClaims();
            if (claims != null) {
                rst.setClaims(wsTrustElementFactory.createClaims(claims));
            }
        }

        int len = 32;
        if (keySize > 0) {
            len = (int) keySize / 8;
        }

        if (wstVer.getSymmetricKeyTypeURI().equals(keyType)) {
            final SecureRandom secRandom = new SecureRandom();
            final byte[] nonce = new byte[len];
            secRandom.nextBytes(nonce);
            final BinarySecret binarySecret = wsTrustElementFactory.createBinarySecret(nonce, wstVer.getNonceBinarySecretTypeURI());
            final Entropy entropy = wsTrustElementFactory.createEntropy(binarySecret);
            rst.setEntropy(entropy);
            rst.setComputedKeyAlgorithm(URI.create(wstVer.getCKPSHA1algorithmURI()));
        } else if (wstVer.getPublicKeyTypeURI().equals(keyType) && keySize > 1) {
            // Create a RSA key pairs for use with UseKey
            KeyPairGenerator kpg;
            try {
                kpg = KeyPairGenerator.getInstance("RSA");
                //RSAKeyGenParameterSpec rsaSpec = new RSAKeyGenParameterSpec((int)keySize, RSAKeyGenParameterSpec.F0);
                //kpg.initialize(rsaSpec);
            } catch (NoSuchAlgorithmException ex) {
                throw new WSTrustException("Unable to create key pairs for UseKey", ex);
            }
            //catch (InvalidAlgorithmParameterException ex){
            //    throw new WSTrustException("Unable to create key pairs for UseKey", ex);
            //}
            kpg.initialize((int) keySize);
            KeyPair keyPair = kpg.generateKeyPair();

            // Create the Sig attribute Value for UseKey
            // String sig = "uuid-" + UUID.randomUUID().toString();

            // Create the UseKey element in RST
            KeyInfo keyInfo = createKeyInfo(keyPair.getPublic());
            final DocumentBuilderFactory docFactory = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            Document doc;
            try {
                doc = docFactory.newDocumentBuilder().newDocument();
                keyInfo.marshal(new DOMStructure(doc), null);
            } catch (ParserConfigurationException ex) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
            } catch (MarshalException ex) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
            }
            Token token = new GenericToken(doc.getDocumentElement());
            UseKey useKey = wsTrustElementFactory.createUseKey(token, null);
            rst.setUseKey(useKey);

            // Put the key pair and the sig in the STSConfiguration
            stsConfig.getOtherOptions().put(WSTrustConstants.USE_KEY_RSA_KEY_PAIR, keyPair);
            //stsConfig.getOtherOptions().put(WSTrustConstants.USE_KEY_SIGNATURE_ID, sig); */
        }

        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WST_1006_CREATED_RST_ISSUE(WSTrustUtil.elemToString(rst, wstVer)));
        }

        return rst;
    }

    private KeyInfo createKeyInfo(final PublicKey pubKey) throws WSTrustException {
        KeyInfoFactory kif = WSSPolicyConsumerImpl.getInstance().getKeyInfoFactory();
        KeyValue kv;
        try {
            kv = kif.newKeyValue(pubKey);
        } catch (KeyException ex) {
            throw new WSTrustException("Unable to create key value", ex);
        }
        List<KeyValue> kvs = new ArrayList<KeyValue>();
        kvs.add(kv);
        return kif.newKeyInfo(kvs);
    }

    private void addServerIdentity(AppliesTo aplTo, Object identity) throws WSTrustException {
        if (identity instanceof Element) {
            aplTo.getAny().add(identity);
        } else if (identity instanceof X509Certificate) {
            // Create Identity element with a BinarySecurityTOken for
            // the server certificate

            // Create BinarySecurityToken
            String id = UUID.randomUUID().toString();
            BinarySecurityTokenType bst = new BinarySecurityTokenType();
            bst.setValueType(MessageConstants.X509v3_NS);
            bst.setId(id);
            bst.setEncodingType(MessageConstants.BASE64_ENCODING_NS);
            try {
                bst.setValue(((X509Certificate) identity).getEncoded());
            } catch (CertificateEncodingException ex) {
                throw new WSTrustException(ex.getMessage());
            }
            JAXBElement<BinarySecurityTokenType> bstElem = new com.sun.xml.ws.security.secext10.ObjectFactory().createBinarySecurityToken(bst);

            // Cretae Identity element
            IdentityType idElem = new IdentityType();
            idElem.getDnsOrSpnOrUpn().add(bstElem);
            aplTo.getAny().add(new com.sun.xml.security.core.ai.ObjectFactory().createIdentity(idElem));
        }
    }

    @SuppressWarnings("unchecked")
    private BaseSTSResponse invokeRST(final RequestSecurityToken request, STSIssuedTokenConfiguration stsConfig) throws RemoteException, WSTrustException {

        String stsURI = stsConfig.getSTSEndpoint();
        STSIssuedTokenConfiguration rtConfig = (STSIssuedTokenConfiguration) stsConfig.getOtherOptions().get(DssConstants.RUNTIME_STS_CONFIG);
        Dispatch<Message> dispatch;
        WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
        if (rtConfig != null) {
            dispatch = (Dispatch<Message>) rtConfig.getOtherOptions().get(stsURI);
        } else {
            dispatch = (Dispatch<Message>) stsConfig.getOtherOptions().get(stsURI);
        }

        if (dispatch == null) {
            URI wsdlLocation;
            QName serviceName = null;
            QName portName = null;

            final String metadataStr = stsConfig.getSTSMEXAddress();
            if (metadataStr != null) {
                wsdlLocation = URI.create(metadataStr);
            } else {
                final String namespace = stsConfig.getSTSNamespace();
                String wsdlLocationStr = stsConfig.getSTSWSDLLocation();
                if (wsdlLocationStr == null) {
                    wsdlLocationStr = stsURI;
                } else {
                    final String serviceNameStr = stsConfig.getSTSServiceName();
                    if (serviceNameStr != null && namespace != null) {
                        serviceName = new QName(namespace, serviceNameStr);
                    }

                    final String portNameStr = stsConfig.getSTSPortName();
                    if (portNameStr != null && namespace != null) {
                        portName = new QName(namespace, portNameStr);
                    }
                }
                wsdlLocation = URI.create(wsdlLocationStr);
            }

            if (serviceName == null || portName == null) {
                //we have to get the serviceName and portName through MEX
                if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE,
                            LogStringsMessages.WST_1012_SERVICE_PORTNAME_MEX(serviceName, portName));
                }

                final QName[] names = doMexRequest(wsdlLocation.toString(), stsURI);
                serviceName = names[0];
                portName = names[1];
            }

            Service service;
            try {
                // Work around for issue 338
                String url = wsdlLocation.toString();

                /* Fix of JCAPS Issue 866 (Fix is : use the container got from JCAPS
                 * through JAX-WS and pass that into the client for the STS )
                 */
                Container container = (Container) stsConfig.getOtherOptions().get("CONTAINER");
                if (container != null) {
                    WSService.InitParams initParams = new WSService.InitParams();
                    initParams.setContainer(container);
                    service = WSService.create(new URL(url), serviceName, initParams);
                } else {
                    service = Service.create(new URL(url), serviceName);
                }
            } catch (MalformedURLException ex) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0041_SERVICE_NOT_CREATED(wsdlLocation.toString()), ex);
                throw new WebServiceException(LogStringsMessages.WST_0041_SERVICE_NOT_CREATED(wsdlLocation.toString()), ex);
            }

            WebServiceFeature[] wsFeatures;
            if (rtConfig != null) {
                wsFeatures = new WebServiceFeature[]{new RespectBindingFeature(),
                        new AddressingFeature(false),
                        new STSIssuedTokenFeature(rtConfig)};
            } else {
                wsFeatures = new WebServiceFeature[]{new RespectBindingFeature(), new AddressingFeature(false)};
            }
            dispatch = service.createDispatch(portName, Message.class, Service.Mode.MESSAGE, wsFeatures);
            if (rtConfig != null) {
                rtConfig.getOtherOptions().put(stsURI, dispatch);
            } else {
                stsConfig.getOtherOptions().put(stsURI, dispatch);
            }
        }

        if (stsURI != null) {
            dispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, stsURI);
        }
        dispatch.getRequestContext().put(WSTrustConstants.IS_TRUST_MESSAGE, "true");

        setMessageProperties(dispatch.getRequestContext(), stsConfig);
        dispatch.getRequestContext().put(WSTrustConstants.TRUST_ACTION, getAction(wstVer, request.getRequestType().toString()));

        final Object additionalContext = stsConfig.getOtherOptions().get(DssConstants.ADDITIONAL_CONTEXT);
        if (additionalContext != null) {
            request.getAny().add(additionalContext);
        }

        Message reqMsg = Messages.createUsingPayload(wsTrustElementFactory.toSource(request), ((WSBinding) dispatch.getBinding()).getSOAPVersion());
        Message respMsg = dispatch.invoke(reqMsg);
        Source respSrc = respMsg.readPayloadAsSource();
        final BaseSTSResponse resp = parseRSTR(respSrc, wstVer);

        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WST_1007_CREATED_RSTR_ISSUE(WSTrustUtil.elemToString(resp, wstVer)));
        }

        return resp;
    }

    @SuppressWarnings("unchecked")
    private BaseSTSResponse invokeRST(final RequestSecurityTokenResponse response, STSIssuedTokenConfiguration stsConfig) throws RemoteException, WSTrustException {

        String stsURI = stsConfig.getSTSEndpoint();
        STSIssuedTokenConfiguration rtConfig = (STSIssuedTokenConfiguration) stsConfig.getOtherOptions().get(DssConstants.RUNTIME_STS_CONFIG);
        Dispatch<Message> dispatch;
        WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
        if (rtConfig != null) {
            dispatch = (Dispatch<Message>) rtConfig.getOtherOptions().get(stsURI);
        } else {
            dispatch = (Dispatch<Message>) stsConfig.getOtherOptions().get(stsURI);
        }

        if (dispatch == null) {
            URI wsdlLocation;
            QName serviceName = null;
            QName portName = null;

            final String metadataStr = stsConfig.getSTSMEXAddress();
            if (metadataStr != null) {
                wsdlLocation = URI.create(metadataStr);
            } else {
                final String namespace = stsConfig.getSTSNamespace();
                String wsdlLocationStr = stsConfig.getSTSWSDLLocation();
                if (wsdlLocationStr == null) {
                    wsdlLocationStr = stsURI;
                } else {
                    final String serviceNameStr = stsConfig.getSTSServiceName();
                    if (serviceNameStr != null && namespace != null) {
                        serviceName = new QName(namespace, serviceNameStr);
                    }

                    final String portNameStr = stsConfig.getSTSPortName();
                    if (portNameStr != null && namespace != null) {
                        portName = new QName(namespace, portNameStr);
                    }
                }
                wsdlLocation = URI.create(wsdlLocationStr);
            }

            if (serviceName == null || portName == null) {
                //we have to get the serviceName and portName through MEX
                if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE,
                            LogStringsMessages.WST_1012_SERVICE_PORTNAME_MEX(serviceName, portName));
                }

                final QName[] names = doMexRequest(wsdlLocation.toString(), stsURI);
                serviceName = names[0];
                portName = names[1];
            }

            Service service;
            try {
                // Work around for issue 338
                String url = wsdlLocation.toString();

                /* Fix of JCAPS Issue 866 (Fix is : use the container got from JCAPS
                 * through JAX-WS and pass that into the client for the STS )
                 */
                Container container = (Container) stsConfig.getOtherOptions().get("CONTAINER");
                if (container != null) {
                    WSService.InitParams initParams = new WSService.InitParams();
                    initParams.setContainer(container);
                    service = WSService.create(new URL(url), serviceName, initParams);
                } else {
                    service = Service.create(new URL(url), serviceName);
                }
            } catch (MalformedURLException ex) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0041_SERVICE_NOT_CREATED(wsdlLocation.toString()), ex);
                throw new WebServiceException(LogStringsMessages.WST_0041_SERVICE_NOT_CREATED(wsdlLocation.toString()), ex);
            }

            WebServiceFeature[] wsFeatures;
            if (rtConfig != null) {
                wsFeatures = new WebServiceFeature[]{new RespectBindingFeature(),
                        new AddressingFeature(false),
                        new STSIssuedTokenFeature(rtConfig)};
            } else {
                wsFeatures = new WebServiceFeature[]{new RespectBindingFeature(), new AddressingFeature(false)};
            }
            dispatch = service.createDispatch(portName, Message.class, Service.Mode.MESSAGE, wsFeatures);
            if (rtConfig != null) {
                rtConfig.getOtherOptions().put(stsURI, dispatch);
            } else {
                stsConfig.getOtherOptions().put(stsURI, dispatch);
            }
        }

        if (stsURI != null) {
            dispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, stsURI);
        }
        dispatch.getRequestContext().put(WSTrustConstants.IS_TRUST_MESSAGE, "true");

        setMessageProperties(dispatch.getRequestContext(), stsConfig);

        dispatch.getRequestContext().put(WSTrustConstants.TRUST_ACTION, wstVer.getIssueResponseAction());

        Message reqMsg = Messages.createUsingPayload(wsTrustElementFactory.toSource(response), ((WSBinding) dispatch.getBinding()).getSOAPVersion());
        Message respMsg = dispatch.invoke(reqMsg);
        Source respSrc = respMsg.readPayloadAsSource();
        final BaseSTSResponse resp = parseRSTR(respSrc, wstVer);

        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WST_1007_CREATED_RSTR_ISSUE(WSTrustUtil.elemToString(resp, wstVer)));
        }

        return resp;
    }

    private String getAction(WSTrustVersion wstVer, String requestType) {
        if (wstVer.getIssueRequestTypeURI().equals(requestType)) {
            return wstVer.getIssueRequestAction();
        }
        if (wstVer.getValidateRequestTypeURI().equals(requestType)) {
            return wstVer.getValidateRequestAction();
        }
        if (wstVer.getRenewRequestTypeURI().equals(requestType)) {
            return wstVer.getRenewRequestAction();
        }
        if (wstVer.getCancelRequestTypeURI().equals(requestType)) {
            return wstVer.getCancelRequestAction();
        }

        return wstVer.getIssueRequestAction();
    }

    private BaseSTSResponse parseRSTR(Source source, WSTrustVersion wstVer) throws WSTrustException {
        Element ele = null;
        try {
            DOMResult result = new DOMResult();
            Transformer tf = WSITXMLFactory.createTransformerFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING).newTransformer();
            tf.transform(source, result);

            Node node = result.getNode();
            if (node instanceof Document) {
                ele = ((Document) node).getDocumentElement();
            } else if (node instanceof Element) {
                ele = (Element) node;
            }
        } catch (Exception xe) {
            throw new WSTrustException("Error occurred while trying to parse RSTR stream", xe);
        }

        RequestedSecurityToken rdst = null;
        NodeList list = ele.getElementsByTagNameNS(ele.getNamespaceURI(), "RequestedSecurityToken");
        if (list.getLength() > 0) {
            Element issuedToken = (Element) list.item(0).getChildNodes().item(0);
            GenericToken token = new GenericToken(issuedToken);
            rdst = wsTrustElementFactory.createRequestedSecurityToken(token);
        }
        BaseSTSResponse rstr;
        if (wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_13.getNamespaceURI())) {
            rstr = wsTrustElementFactory.createRSTRCollectionFrom(ele);
            final RequestSecurityTokenResponseCollection rstrc = (RequestSecurityTokenResponseCollection) rstr;
            if (!rstrc.getRequestSecurityTokenResponses().isEmpty()) {
                rstrc.getRequestSecurityTokenResponses().get(0).setRequestedSecurityToken(rdst);
            }
        } else {
            rstr = wsTrustElementFactory.createRSTRFrom(ele);
            ((RequestSecurityTokenResponse) rstr).setRequestedSecurityToken(rdst);
        }
        return rstr;
    }

    private void setMessageProperties(Map<String, Object> context, STSIssuedTokenConfiguration stsConfig) {
        context.putAll(stsConfig.getOtherOptions());
        if (context.containsKey(com.sun.xml.wss.jaxws.impl.Constants.SC_ASSERTION)) {
            context.remove(com.sun.xml.wss.jaxws.impl.Constants.SC_ASSERTION);
        }
    }

    private RequestSecurityTokenResponse getResponse(BaseSTSResponse stsResponse) {
        RequestSecurityTokenResponseCollection rstrc
                = (RequestSecurityTokenResponseCollection) stsResponse;
        return rstrc.getRequestSecurityTokenResponses().get(0);
    }

}

