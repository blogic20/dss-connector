package ru.blogic.dss.api;

import ru.blogic.dss.api.dto.DssAction;
import ru.blogic.dss.api.dto.usermanagement.DssUserInfo;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthMethodDescription;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.AuthenticationInfo;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.DssUmsPolicy;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.OperationPolicy;
import ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsRequest;
import ru.blogic.dss.api.dto.usermanagement.userrequest.UserRecordsResponse;
import ru.blogic.dss.api.exception.DssServiceException;

import javax.ejb.Remote;
import javax.naming.ldap.LdapName;
import java.util.List;
import java.util.Map;

/**
 * Created by pkupershteyn on 07.04.2016.
 * Обёртка вокруг сервиса управления пользователями DSS криптоПро
 */

@Remote
public interface RemoteDssUserManagementService {

    /**
     * Регистрирует нового пользователя
     * @param login Логин
     * @param phoneNumber Тел. номер
     * @param email E-mail
     * @param externalLogins Массив логинов пользователя во внешних ЦИ, присоеденённых к DSS @see {@link #addExternalLogin(String, String, String)}
     * @return Строковый идентификатор (id) зарегистрированного пользователя
     * @throws DssServiceException
     */
    String registerUser(String login, String phoneNumber, String email, Map<String, String> externalLogins) throws DssServiceException;

    /**
     * Добавляет внешний логин к учётной записи пользователя
     * @param userId Идентификатор пользователя
     * @param login логин, под которым пользователь известен во внешнем ЦИ
     * @param issuerName Идентификатор внешней системы
     * @throws DssServiceException
     */
    void addExternalLogin(String userId, String login, String issuerName) throws DssServiceException;

    /**Назначает пользователю метод аутентификации "только логин" (т.е. для аутентификации нужен только логин)
     *
     * @param userId Id пользователя
     * @throws DssServiceException
     */
    void assignLoginOnlyAuthenticationMethod(String userId) throws DssServiceException;

    /**
     * Назначает пользователю аутентификацию по SMS
     * @param userId Id пользователя
     * @param phoneNumber Тел. номер
     * @param level Шаг аутентификации. (>=2)
     * @throws DssServiceException
     */
    void assignSMSAuthenticationMethod(String userId, String phoneNumber, Integer level) throws DssServiceException;

    /**
     * Назначает пользователю метод аутентификации по id SIM карты
     * @param userId Id пользователя
     * @param iccId Id SIM карты
     * @param providerId ID провайдера
     * @param level Шаг аутентификации (>=2)
     * @throws DssServiceException
     */
    void assignSimAuthAuthenticationMethod(String userId, String iccId, String providerId, Integer level) throws DssServiceException;

    /**
     * Назначает пользователю метод аутентификации с помощъю OTP\TOTP токена
     * @param userId Id пользователя
     * @param tokenSerial серийный номер OTP-токена
     * @param firstOtp первый одноразовый пароль, сгенерированный OTP-токеном
     * @param secondOtp второй одноразовый пароль, следующий непосредственно после первого одноразового пароля в цепочке одноразовых паролей, сгенерированных OTP-токеном. В случае, если в качестве токена выступает TOTP-токен, то в качестве второго пароля повторно передается первый пароль.
     * @param level Шаг аутентификации (>=2)
     * @throws DssServiceException
     */
    void assignOtpTokenAuthenticationMethod(String userId, String tokenSerial, String firstOtp, String secondOtp, Integer level) throws DssServiceException;

    /**
     * Удаляет из схемы аутентификации пользователя указанный метод
     * @param userId
     * @param authenticationMethod метод аутентификации, который нужно удалить
     * @throws DssServiceException
     */
    void removeAuthenticationMethod(String userId, AuthMethodDescription authenticationMethod) throws DssServiceException;

    /**
     * Возвращает схему аутентификации пользователя, представляющую собой последовательность шагов аутентификации
     * @param userId Id пользователя
     * @return Последовательность шагов аутентификации
     * @throws DssServiceException
     */
    List<AuthenticationInfo> getUserAuthenticationScheme(String userId) throws DssServiceException;

    /**
     * Возвращает детальную информацию о пользователе
     * @param id Id пользователя
     * @return Детальная информация о пользователе
     * @throws DssServiceException
     */
    DssUserInfo getUserById(String id) throws DssServiceException;

    /**
     * Возвращает детальную информацию о пользователе по его login
     * @param login Логин пользователя
     * @return Детальная информация о пользователе
     * @throws DssServiceException
     */
    DssUserInfo getUserByLogin(String login) throws DssServiceException;

    /**
     * Удаляет пользователя
     * @param userId Id пользователя
     * @throws DssServiceException
     */
    void deleteUser(String userId) throws DssServiceException;

    /**
     * Включает пользователя в группу
     * @param userId Id пользователя
     * @param groupName Имя группы
     * @throws DssServiceException
     */
    void setUserGroup(String userId, String groupName) throws DssServiceException;

    /**
     * Назначение пользователю списа разрешённых ему операций
     * @param userId id пользователя
     * @param dssActions Список разрешений
     * @throws DssServiceException
     */
    void setUserOperationPolicy(String userId, List<DssAction> dssActions) throws DssServiceException;

    /**
     * Получает список разрешений для пользователя
     * @param userId Id пользователя
     * @return Список разрешений
     * @throws DssServiceException
     */
    List<OperationPolicy> getUserOperationPolicy(String userId) throws DssServiceException;

    /**
     * Устанавливает отображаемое имя для пользователя
     * @param userId Id пользователя
     * @param DisplayName отображаемое имя
     * @throws DssServiceException
     */
    void setUserDisplayName(String userId, String DisplayName) throws DssServiceException;

    /**
     * Устанавливает состояние учетной записи пользователя (заблокировано/разблокировано)
     * @param userId Id пользователя
     * @param accountState состояние учетной записи пользователя
     * @throws DssServiceException
     */
    void setUserAccountState(String userId, boolean accountState) throws DssServiceException;

    /**
     * Присваивает пользователю distinguished name
     * @param userId Id пользователя
     * @param dn Distinguished name пользователя
     * @throws DssServiceException
     */
    void setUserDistinguishName(String userId, LdapName dn) throws DssServiceException;

    /**
     * Возвращает политику сервиса управления пользователями
     * @return политика сервиса управления пользователями
     * @throws DssServiceException
     */
    DssUmsPolicy getPolicy() throws DssServiceException;

    /**
     * Возвращает все учётные записи DSS, привязанные к тому же внешнему issuer name, что и текущий оператор DSS
     * @return Все найденные учётные записи
     * @throws DssServiceException
     */
    UserRecordsResponse getAllUsers() throws DssServiceException;

    /**
     * Возвращает учётные записи DSS, привязанные к тому же внешнему issuer name, что и текущий оператор DSS, согласно переданному фильтру
     * @param recordsRequest Фильтр
     * @return Учётные записи, отобранне сообразно фильтру
     * @throws DssServiceException
     */
    UserRecordsResponse getUsers(UserRecordsRequest recordsRequest) throws DssServiceException;
}
