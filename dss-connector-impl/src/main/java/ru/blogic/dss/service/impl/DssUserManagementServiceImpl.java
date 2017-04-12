package ru.blogic.dss.service.impl;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfintstring;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfAuthenticationInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ArrayOfOperationPolicy;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.AuthenticationInfo;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.IdentifierType;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.ObjectFactory;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.OperationPolicy;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.UserProperty;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.UserPropertyType;
import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ArrayOfUserFilter;
import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsRequest;
import org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.UserRecordsResponse;
import ru.blogic.dss.ExceptionTranslatingInterceptor;
import ru.blogic.dss.api.RemoteDssUserManagementService;
import ru.blogic.dss.api.dto.DssAction;
import ru.blogic.dss.api.dto.usermanagement.DssUserInfo;
import ru.blogic.dss.api.dto.usermanagement.RdnInfo;
import ru.blogic.dss.api.dto.usermanagement.Rdns;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthLevel;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthMethodDescription;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.DssUmsPolicy;
import ru.blogic.dss.api.exception.DssServiceException;
import ru.blogic.dss.mapper.usermanagement.DssUserInfoMapper;
import ru.blogic.dss.mapper.usermanagement.UserRecordsRequestMapper;
import ru.blogic.dss.mapper.usermanagement.UserRecordsResponseMapper;
import ru.blogic.dss.provider.DssUmPolicyCachedProvider;
import ru.blogic.dss.provider.WsPortProvider;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceAddExternalLoginDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceAssignAuthenticationMethodDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceDeleteUserDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceGetPolicyDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceGetUserAuthenticationSchemeDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceGetUserByIdDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceGetUserDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceGetUsersDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceRegisterUserDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceRemoveAuthenticationMethodDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserDistinguishNameDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserGroupDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserOperationPolicyDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserOtpTokenDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserPhoneNumberDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserPropertyDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceSetUserSimAuthTokenDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services.schemas._2014._06.userManagement.ArrayOfDSSActions;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pkupershteyn on 07.04.2016.
 */

@Stateless
@Interceptors(ExceptionTranslatingInterceptor.class)
public class DssUserManagementServiceImpl implements RemoteDssUserManagementService {

    @Inject
    private WsPortProvider wsPortProvider;

    @Inject
    private DssUserInfoMapper dssUserInfoMapper;

    @Inject
    private UserRecordsRequestMapper userRecordsRequestMapper;

    @Inject
    UserRecordsResponseMapper userRecordsResponseMapper;

    @Inject
    DssUmPolicyCachedProvider dssUmPolicyCachedProvider;


    @Override
    public String registerUser(String login, String phoneNumber, String email, Map<String, String> externalLogins) throws DssServiceException {

        ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm identifiers = new ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm();

        List<ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm.KeyValueOfIdentifierTypestringMJV4W3Gm> identifierList = identifiers.getKeyValueOfIdentifierTypestringMJV4W3Gm();

        addUserIdentifier(identifiers, IdentifierType.LOGIN, login);
        addUserIdentifier(identifiers, IdentifierType.PHONE_NUMBER, phoneNumber);
        addUserIdentifier(identifiers, IdentifierType.EMAIL, email);

        try {

            //Регистрация пользователя
            String userId = wsPortProvider.getUserManagementPort().registerUser(identifiers);

            Map<String, String> extLogins = new LinkedHashMap();
            //Добавление внешего логина для текущего "своего ЦИ"
            // Похоже, он добавляется автоматом, явно указывать не надо.
            //extLogins.put(Constants.LECM_STS_ISSUER_NAME, login);
            if (externalLogins != null) {
                extLogins.putAll(externalLogins);
            }
            Map<String, String> extLoginsAdded = new LinkedHashMap<String, String>();

            for (Map.Entry<String, String> extLoginEntry : extLogins.entrySet()) {
                String issuerName = extLoginEntry.getKey();
                String extLogin = extLoginEntry.getValue();
                try {
                    addExternalLogin(userId, extLogin, issuerName);
                    extLoginsAdded.put(issuerName, extLogin);

                } catch (DssServiceException dssEx) {
                    throw DssServiceExceptionFactory.newInstance("User " + login + " has been registered successfully (userid assigned:'" + userId + "')" +
                            ", but external login for issuer '" + issuerName + "' has not been added."
                            + ( !extLoginsAdded.isEmpty() ? " Successfully added external logins: " + extLoginsAdded : "" )
                            , dssEx.getCause());
                }
            }

            return userId;
        } catch (IUserManagementServiceRegisterUserDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to register user '" + login + "'", e);
        }
    }


    private void addUserIdentifier(ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm identifiers, IdentifierType identifierType, String value) {
        if (value != null) {
            ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm.KeyValueOfIdentifierTypestringMJV4W3Gm identifier = new ArrayOfKeyValueOfIdentifierTypestringMJV4W3Gm.KeyValueOfIdentifierTypestringMJV4W3Gm();
            identifier.setKey(identifierType);
            identifier.setValue(value);
            identifiers.getKeyValueOfIdentifierTypestringMJV4W3Gm().add(identifier);
        }
    }

    @Override
    public void addExternalLogin(String userId, String login, String issuerName) throws DssServiceException {
        try {
            wsPortProvider.getUserManagementPort().addExternalLogin(userId, login, issuerName);
        } catch (IUserManagementServiceAddExternalLoginDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to add external login '" + login + "' for issuer '" + issuerName + "' to user '" + userId + "'", e);
        }
    }

    @Override
    public DssUserInfo getUserById(String id) throws DssServiceException {
        try {
            return dssUserInfoMapper.from(
                    wsPortProvider.getUserManagementPort().getUserById(id)
            );

        } catch (IUserManagementServiceGetUserByIdDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not get user by id '" + id + "'", e);
        }
    }

    @Override
    public DssUserInfo getUserByLogin(String login) throws DssServiceException {
        try {
            return dssUserInfoMapper.from(
                    wsPortProvider.getUserManagementPort().getUser(IdentifierType.LOGIN, login)
            );
        } catch (IUserManagementServiceGetUserDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to get user by login '" + login + "'", e);
        }
    }

    private void assignAuthenticationMethod(String userId, AuthMethodDescription authenticationMethod, int level) throws DssServiceException {

        if (( level == 1 && AuthLevel.SECONDARY.equals(authenticationMethod.getType()) )
                || ( level > 1 && AuthLevel.PRIMARY.equals(authenticationMethod.getType()) )
                ) {
            throw new DssServiceException("Incompatible level for DSS auth metod " + authenticationMethod);
        }
        try {
            wsPortProvider.getUserManagementPort().assignAuthenticationMethod(userId, authenticationMethod.getIdentifier(), level);
        } catch (IUserManagementServiceAssignAuthenticationMethodDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Unable to assign authentication method to user '" + userId + "' (method identifier: " + authenticationMethod.getIdentifier() + ", level: " + level + ")", e);
        }
    }

    @Override
    public void assignLoginOnlyAuthenticationMethod(String userId) throws DssServiceException {
        assignAuthenticationMethod(userId, AuthMethodDescription.LOGIN_ONLY, 1);
    }

    @Override
    public void assignOtpTokenAuthenticationMethod(String userId, String tokenSerial, String firstOtp, String secondOtp, Integer level) throws DssServiceException {
        try {
            wsPortProvider.getUserManagementPort().setUserOtpToken(userId, tokenSerial, firstOtp, secondOtp);
            assignAuthenticationMethod(userId, AuthMethodDescription.MFA_BY_OTP_TOKEN, level == null ? 2 : level);
        } catch (IUserManagementServiceSetUserOtpTokenDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Otp token authentication method is assigned to user '" + userId + "', but could not set otp token info.", e);
        }
    }

    @Override
    public void assignSMSAuthenticationMethod(String userId, String phoneNumber, Integer level) throws DssServiceException {
        try {
            //Сначала назначается номер телефона
            wsPortProvider.getUserManagementPort().setUserPhoneNumber(userId, phoneNumber);
            /*  Затем присваивается метод аутентификации по SMS, по умолчанию на шаге 2
                предполагается что до этого уже назначен как минимум метод {@link  AuthMethodDescription.LOGIN_ONLY}
            */
            assignAuthenticationMethod(userId, AuthMethodDescription.MFA_BY_SMS, level == null ? 2 : level);
        } catch (IUserManagementServiceSetUserPhoneNumberDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("SMS authentication methos is assigned for user '" + userId + "', but could not set phone number", e);
        }
    }

    @Override
    public void assignSimAuthAuthenticationMethod(String userId, String iccId, String providerId, Integer level) throws DssServiceException {
        try {
            wsPortProvider.getUserManagementPort().setUserSimAuthToken(userId, iccId, providerId);
            assignAuthenticationMethod(userId, AuthMethodDescription.MFA_BY_SIM, level == null ? 2 : level);
        } catch (IUserManagementServiceSetUserSimAuthTokenDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Sim authentication methos is assigned for user '" + userId + "' but could not set sim info", e);
        }
    }

    @Override
    public void removeAuthenticationMethod(String userId, AuthMethodDescription authenticationMethod) throws DssServiceException {
        try {
            wsPortProvider.getUserManagementPort().removeAuthenticationMethod(userId, authenticationMethod.getIdentifier());
        } catch (IUserManagementServiceRemoveAuthenticationMethodDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not remove auth method " + authenticationMethod.getIdentifier() + " for user '" + userId + "'", e);
        }
    }

    @Override
    public List<ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationInfo> getUserAuthenticationScheme(String userId) throws DssServiceException {
        try {
            ArrayOfAuthenticationInfo userAuthenticationScheme = wsPortProvider.getUserManagementPort().getUserAuthenticationScheme(userId);
            List<ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationInfo> result = new ArrayList<ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationInfo>();
            for (AuthenticationInfo sourceAuthenticationInfo : userAuthenticationScheme.getAuthenticationInfo()) {
                ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationInfo authenticationInfo = new ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationInfo();
                authenticationInfo.setLevel(sourceAuthenticationInfo.getLevel());
                authenticationInfo.setMethodUri(sourceAuthenticationInfo.getMethodUri().getValue());
                result.add(authenticationInfo);
            }
            ;
            return result;
        } catch (IUserManagementServiceGetUserAuthenticationSchemeDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not get authentication scheme for user '" + userId + "'", e);
        }
    }

    @Override
    public void deleteUser(String userId) throws DssServiceException {
        try {
            wsPortProvider.getUserManagementPort().deleteUser(userId);
        } catch (IUserManagementServiceDeleteUserDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not delete user '" + userId + "'", e);
        }
    }

    @Override
    public void setUserGroup(String userId, String groupName) throws DssServiceException {
        try {
            wsPortProvider.getUserManagementPort().setUserGroup(userId, groupName);
        } catch (IUserManagementServiceSetUserGroupDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not set group '" + groupName + "' for user '" + userId + "'", e);
        }
    }

    @Override
    public void setUserOperationPolicy(String userId, List<DssAction> dssActions) throws DssServiceException {
        List<String> actions = new ArrayList<String>();
        for (DssAction dssAction : dssActions) {
            actions.add(dssAction.value());
        }

        ru.cryptopro.dss.services.schemas._2014._06.userManagement.ObjectFactory objectFactory = new ru.cryptopro.dss.services.schemas._2014._06.userManagement.ObjectFactory();
        ArrayOfDSSActions arrayOfDSSActions = objectFactory.createArrayOfDSSActions();
        arrayOfDSSActions.getDSSActions().add(objectFactory.createArrayOfDSSActionsDSSActions(actions));
        try {
            wsPortProvider.getUserManagementPort().setUserOperationPolicy(userId, arrayOfDSSActions);
        } catch (IUserManagementServiceSetUserOperationPolicyDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not set user operation policy for user '" + userId + "'", e);
        }
    }

    @Override
    public List<ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy> getUserOperationPolicy(String userId) throws DssServiceException {

        ArrayOfOperationPolicy userOperationPolicy = null;
        try {
            userOperationPolicy = wsPortProvider.getUserManagementPort().getUserOperationPolicy(userId);
            List<OperationPolicy> operationPolicyList = userOperationPolicy.getOperationPolicy();
            List<ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy> operationPolicies = new ArrayList<ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy>();
            for (OperationPolicy sourceOperationPolicy : operationPolicyList) {
                ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy operationPolicy = new ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy();
                operationPolicy.setConfirmationRequired(sourceOperationPolicy.isConfirmationRequired());
                List<String> action = sourceOperationPolicy.getAction();
                if (!action.isEmpty()) {
                    operationPolicy.setAction(DssAction.fromValue(action.get(0)));
                }
                operationPolicies.add(operationPolicy);
            }
            return operationPolicies;
        } catch (Exception e) {
            throw DssServiceExceptionFactory.newInstance("Could not get user '" + userId + " operation policy", e);
        }

    }

    private void setUserProperty(String userId, UserPropertyType type, Object value) throws DssServiceException {
        org.datacontract.schemas._2004._07.cryptopro_dss_common.ObjectFactory objectFactory = new ObjectFactory();

        UserProperty up = objectFactory.createUserProperty();
        up.setPropertyType(type);
        up.setValue(objectFactory.createUserPropertyValue(value));

        try {
            wsPortProvider.getUserManagementPort().setUserProperty(userId, up);
        } catch (IUserManagementServiceSetUserPropertyDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not set property " + type + " with value " + value + " for user  '" + userId + "'", e);
        }
    }


    @Override
    public void setUserDisplayName(String userId, String displayName) throws DssServiceException {
        setUserProperty(userId, UserPropertyType.DISPLAY_NAME, displayName);
    }

    @Override
    public void setUserAccountState(String userId, boolean accountState) throws DssServiceException {
        setUserProperty(userId, UserPropertyType.ACCOUNT_LOCKED_STATE, accountState);
    }

    @Override
    public void setUserDistinguishName(String userId, LdapName dn) throws DssServiceException {
        Rdns dssRdns = getPolicy().getRdns();
        ArrayOfKeyValueOfintstring arrayOfKeyValueOfintstring = new ArrayOfKeyValueOfintstring();
        List<ArrayOfKeyValueOfintstring.KeyValueOfintstring> keyValues = arrayOfKeyValueOfintstring.getKeyValueOfintstring();

        for (Rdn rdn : dn.getRdns()) {
            RdnInfo rdnInfo = dssRdns.byStringIdentifier(rdn.getType());
            if (rdnInfo == null) {
                throw new DssServiceException("Could not set distinguished name " + dn + " for user '" + userId + "':  dn type '" + rdn.getType() + "' is not included in service policy");
            }
            ArrayOfKeyValueOfintstring.KeyValueOfintstring keyValue = new ArrayOfKeyValueOfintstring.KeyValueOfintstring();
            keyValue.setKey(rdnInfo.getId());
            keyValue.setValue((String) rdn.getValue());
            keyValues.add(keyValue);
        }
        try {
            wsPortProvider.getUserManagementPort().setUserDistinguishName(userId, arrayOfKeyValueOfintstring);
        } catch (IUserManagementServiceSetUserDistinguishNameDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not set distinguished name " + dn + " for user '" + userId + "'", e);
        }
    }

    @Override
    public DssUmsPolicy getPolicy() throws DssServiceException {
        try {
            return dssUmPolicyCachedProvider.getDssUmsPolicy(wsPortProvider);
        } catch (IUserManagementServiceGetPolicyDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not get UM policy", e);
        }
    }

    @Override
    public ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsResponse getAllUsers() throws DssServiceException {
        org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ObjectFactory factory = new org.datacontract.schemas._2004._07.cryptopro_dss_common_usermanagementservice.ObjectFactory();

        UserRecordsRequest recordsRequest = new UserRecordsRequest();
        ArrayOfUserFilter arrayOfUserFilter = factory.createArrayOfUserFilter();
        recordsRequest.setFilters(factory.createArrayOfUserFilter(arrayOfUserFilter));
        recordsRequest.setStartPosition(0);
        recordsRequest.setEndPosition(-1);

        try {
            UserRecordsResponse users = wsPortProvider.getUserManagementPort().getUsers(recordsRequest);

            return userRecordsResponseMapper.from(users);

        } catch (IUserManagementServiceGetUsersDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not get users", e);
        }
    }

    public ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsResponse getUsers(ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsRequest recordsRequest) throws DssServiceException {
        try {

            UserRecordsResponse recordsResponse = wsPortProvider.getUserManagementPort().getUsers(
                    userRecordsRequestMapper.to(recordsRequest)
            );

            return userRecordsResponseMapper.from(recordsResponse);

        } catch (IUserManagementServiceGetUsersDssFaultFaultFaultMessage e) {
            throw DssServiceExceptionFactory.newInstance("Could not get users by request: " + recordsRequest, e);
        }
    }
}
