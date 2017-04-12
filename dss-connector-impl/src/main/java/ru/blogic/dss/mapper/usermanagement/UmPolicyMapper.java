package ru.blogic.dss.mapper.usermanagement;

import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfAuthnMethodDescription;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfCryptoProviderInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfIdentifierType;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfIdentityGroupInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfIdentityProviderInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfOperationPolicy;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfRdnInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.AuthenticationPolicy;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.AuthnMethodDescription;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.CryptoProviderInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentifierType;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentityGroupInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentityProviderInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.OperationPolicy;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.RdnInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.UmsPolicy;
import ru.blogic.dss.api.dto.DssAction;
import ru.blogic.dss.api.dto.MfaPolicyState;
import ru.blogic.dss.api.dto.usermanagement.Rdns;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthMethodDescription;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.DssUmsPolicy;
import ru.blogic.dss.mapper.DssConfirmableActionMapper;
import ru.blogic.dss.mapper.MfaPolicyMapper;
import ru.blogic.dss.mapper.NullSafeMapper;
import ru.cryptopro.dss.services.schemas._2014._06.userManagement.MfaPolicy;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pkupershteyn on 11.04.2016.
 */
@ApplicationScoped
public class UmPolicyMapper extends NullSafeMapper<DssUmsPolicy, UmsPolicy> {

    @Inject
    private IdentifierTypeMapper identifierTypeMapper;

    @Inject
    private CryptoProviderProfileTypeMapper cryptoProviderProfileTypeMapper;

    @Inject
    private AuthLevelMapper authLevelMapper;

    @Inject
    private MfaPolicyMapper mfaPolicyMapper;

    @Inject
    private DssConfirmableActionMapper dssConfirmableActionMapper;

    @Override
    protected DssUmsPolicy nullSafeFrom(UmsPolicy source) {
        DssUmsPolicy dssUmsPolicy = new DssUmsPolicy();

        //availableIdentifierTypes
        List<ru.blogic.dss.api.dto.dsspolicy.IdentifierType> types = new ArrayList<ru.blogic.dss.api.dto.dsspolicy.IdentifierType>();
        JAXBElement<ArrayOfIdentifierType> avaliableIdentifierTypes = source.getAvaliableIdentifierTypes();
        if (avaliableIdentifierTypes != null) {
            ArrayOfIdentifierType arrayOfIdentifierType = avaliableIdentifierTypes.getValue();
            if (arrayOfIdentifierType != null) {
                List<IdentifierType> identifierTypeList = arrayOfIdentifierType.getIdentifierType();
                if (identifierTypeList != null) {
                    for (IdentifierType identifierType : identifierTypeList) {
                        types.add(identifierTypeMapper.from(identifierType));
                    }
                }
            }
        }
        dssUmsPolicy.setAvailableIdentifierTypes(types);

        //AuthenticationPolicy
        dssUmsPolicy.setAuthenticationPolicy(fromAuthenticationPolicy(source.getAuthenticationPolicy().getValue()));


        // allow user registration
        dssUmsPolicy.setAllowUserRegistration(source.isAllowUserRegistration());

        //groups
        Map<String, List<String>> groups = new LinkedHashMap<String, List<String>>();
        JAXBElement<ArrayOfIdentityGroupInfo> sourceGroups = source.getGroups();
        if (sourceGroups != null) {
            ArrayOfIdentityGroupInfo arrayOfIdentityGroupInfo = sourceGroups.getValue();
            if (arrayOfIdentityGroupInfo != null) {
                List<IdentityGroupInfo> identityGroupInfoList = arrayOfIdentityGroupInfo.getIdentityGroupInfo();
                if (identityGroupInfoList != null) {
                    for (IdentityGroupInfo identityGroupInfo : identityGroupInfoList) {
                        List<String> groupNames = new ArrayList<String>(identityGroupInfo.getGroupList().getValue().getString());
                        groups.put(identityGroupInfo.getIdentityProviderName().getValue(), groupNames);
                    }
                }
            }
        }
        dssUmsPolicy.setGroups(groups);

        //Rdns
        List<ru.blogic.dss.api.dto.usermanagement.RdnInfo> rdnInfos = new ArrayList<ru.blogic.dss.api.dto.usermanagement.RdnInfo>();

        JAXBElement<ArrayOfRdnInfo> sourceRdns = source.getRdns();
        if (sourceRdns != null) {
            ArrayOfRdnInfo arrayOfRdnInfo = sourceRdns.getValue();
            if (arrayOfRdnInfo != null) {
                List<RdnInfo> rdnInfoList = arrayOfRdnInfo.getRdnInfo();
                if (rdnInfoList != null) {
                    for (RdnInfo sourceRdnInfo : rdnInfoList) {
                        ru.blogic.dss.api.dto.usermanagement.RdnInfo rdnInfo = new ru.blogic.dss.api.dto.usermanagement.RdnInfo();
                        rdnInfo.setId(sourceRdnInfo.getId());
                        rdnInfo.setDefaultValue(sourceRdnInfo.getDefaultValue().getValue());
                        rdnInfo.setDisplayName(sourceRdnInfo.getDisplayName().getValue());
                        rdnInfo.setMaxLength(sourceRdnInfo.getMaxLength());
                        rdnInfo.setMinLength(sourceRdnInfo.getMinLength());
                        rdnInfo.setOid(sourceRdnInfo.getOid().getValue());
                        rdnInfo.setOrder(sourceRdnInfo.getOrder());
                        rdnInfo.setRequired(sourceRdnInfo.isRequired());
                        rdnInfo.setStringIdentifier(sourceRdnInfo.getStringIdentifier().getValue());
                        rdnInfos.add(rdnInfo);
                    }
                }
            }
        }
        Rdns rdns = new Rdns();
        rdns.setRdns(rdnInfos);
        dssUmsPolicy.setRdns(rdns);

        //IdentityProviders
        List<ru.blogic.dss.api.dto.usermanagement.umspolicy.IdentityProviderInfo> identityProviderInfos = new ArrayList<ru.blogic.dss.api.dto.usermanagement.umspolicy.IdentityProviderInfo>();
        JAXBElement<ArrayOfIdentityProviderInfo> identityProviders = source.getIdentityProviders();
        if (identityProviders != null) {
            ArrayOfIdentityProviderInfo arrayOfIdentityProviderInfo = identityProviders.getValue();
            if (arrayOfIdentityProviderInfo != null) {
                List<IdentityProviderInfo> identityProviderInfoList = arrayOfIdentityProviderInfo.getIdentityProviderInfo();
                if (identityProviderInfoList != null) {
                    for (IdentityProviderInfo sourceIdentityProviderInfo : identityProviderInfoList) {
                        ru.blogic.dss.api.dto.usermanagement.umspolicy.IdentityProviderInfo identityProviderInfo = new ru.blogic.dss.api.dto.usermanagement.umspolicy.IdentityProviderInfo();
                        identityProviderInfo.setDescription(sourceIdentityProviderInfo.getDescription().getValue());
                        identityProviderInfo.setIssuerName(sourceIdentityProviderInfo.getIssuerName().getValue());
                        identityProviderInfo.setDisplayName(sourceIdentityProviderInfo.getDisplayName().getValue());
                        identityProviderInfos.add(identityProviderInfo);
                    }
                }
            }
        }

        dssUmsPolicy.setIdentityProviders(identityProviderInfos);

        //CryptoProviders
        List<ru.blogic.dss.api.dto.usermanagement.umspolicy.CryptoProviderInfo> cryptoProviderInfos = new ArrayList<ru.blogic.dss.api.dto.usermanagement.umspolicy.CryptoProviderInfo>();
        JAXBElement<ArrayOfCryptoProviderInfo> cryptoProviders = source.getCryptoProviders();
        if (cryptoProviders != null) {
            ArrayOfCryptoProviderInfo arrayOfCryptoProviderInfo = cryptoProviders.getValue();
            if (arrayOfCryptoProviderInfo != null) {
                List<CryptoProviderInfo> cryptoProviderInfoList = arrayOfCryptoProviderInfo.getCryptoProviderInfo();
                if (cryptoProviderInfoList != null) {
                    for (CryptoProviderInfo sourceCryptoProviderInfo : cryptoProviderInfoList) {
                        ru.blogic.dss.api.dto.usermanagement.umspolicy.CryptoProviderInfo cryptoProviderInfo = new ru.blogic.dss.api.dto.usermanagement.umspolicy.CryptoProviderInfo();

                        cryptoProviderInfo.setDescription(sourceCryptoProviderInfo.getDescription().getValue());
                        cryptoProviderInfo.setId(sourceCryptoProviderInfo.getId().toString());
                        cryptoProviderInfo.setName(sourceCryptoProviderInfo.getName().getValue());
                        cryptoProviderInfo.setType(cryptoProviderProfileTypeMapper.from(sourceCryptoProviderInfo.getType()));

                        cryptoProviderInfos.add(cryptoProviderInfo);
                    }
                }
            }
        }

        dssUmsPolicy.setCryptoProviders(cryptoProviderInfos);


        return dssUmsPolicy;
    }

    private ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationPolicy fromAuthenticationPolicy(AuthenticationPolicy sourceAuthenticationPolicy) {
        ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationPolicy authenticationPolicy = new ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationPolicy();

        //EditableByUser
        authenticationPolicy.setEditableByUser(sourceAuthenticationPolicy.isEditableByUser());

        //AuthMethods
        List<AuthMethodDescription> authMethodDescriptions = new ArrayList<AuthMethodDescription>();

        JAXBElement<ArrayOfAuthnMethodDescription> authMethods = sourceAuthenticationPolicy.getAuthMethods();
        if (authMethods != null) {
            ArrayOfAuthnMethodDescription arrayOfAuthnMethodDescription = authMethods.getValue();
            if (arrayOfAuthnMethodDescription != null) {
                List<AuthnMethodDescription> authnMethodDescriptionList = arrayOfAuthnMethodDescription.getAuthnMethodDescription();
                if (authnMethodDescriptionList != null) {
                    for (AuthnMethodDescription sourceAuthnMethodDescription : authnMethodDescriptionList) {
                        AuthMethodDescription authMethodDescription = new AuthMethodDescription();
                        authMethodDescription.setIdentifier(sourceAuthnMethodDescription.getIdentifier().getValue());
                        authMethodDescription.setType(authLevelMapper.from(sourceAuthnMethodDescription.getType()));
                        authMethodDescriptions.add(authMethodDescription);
                    }
                }
            }
        }

        authenticationPolicy.setAuthMethods(authMethodDescriptions);

        //Mode (mfa policy)
        MfaPolicy mode = sourceAuthenticationPolicy.getMode();
        //authenticationPolicy.setMfaPolicy(mfaPolicyMapper.to(mode));
        //TODO Т.к. MfaPloicy сейчас дублируется из-за импорта из разных схем, выставляю вручную. Необходимо решить проблему разных схем с одним namespace
        MfaPolicyState mfaPolicyState = MfaPolicy.ON.equals(mode) ? MfaPolicyState.ON :
                MfaPolicy.OFF.equals(mode) ? MfaPolicyState.OFF :
                        MfaPolicy.NOT_SET.equals(mode) ? MfaPolicyState.NOT_SET : null;

        authenticationPolicy.setMfaPolicy(mfaPolicyState);

        //operation policies
        List<ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy> operationPolicies = new ArrayList<ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy>();

        JAXBElement<ArrayOfOperationPolicy> operationPolicyJaxb = sourceAuthenticationPolicy.getOperationPolicy();
        if (operationPolicyJaxb != null) {
            ArrayOfOperationPolicy arrayOfOperationPolicy = operationPolicyJaxb.getValue();
            if (arrayOfOperationPolicy != null) {
                List<OperationPolicy> operationPolicyList = arrayOfOperationPolicy.getOperationPolicy();
                if (operationPolicyList != null) {
                    for (OperationPolicy sourceOperationPolicy : operationPolicyList) {
                        ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy operationPolicy = new ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy();

                        operationPolicy.setConfirmationRequired(sourceOperationPolicy.isConfirmationRequired());

                        List<String> action = sourceOperationPolicy.getAction();
                        if (!action.isEmpty()) {
                            operationPolicy.setAction(DssAction.fromValue(action.get(0)));
                        }
                        operationPolicies.add(operationPolicy);
                    }
                }
            }
        }
        authenticationPolicy.setOperationPolicies(operationPolicies);


        return authenticationPolicy;
    }

    @Override
    protected UmsPolicy nullSafeTo(DssUmsPolicy source) {
        throw new UnsupportedOperationException("mapping to UM policy is not supported");
    }
}
