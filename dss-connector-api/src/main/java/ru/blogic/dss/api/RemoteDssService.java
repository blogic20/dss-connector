package ru.blogic.dss.api;

import ru.blogic.dss.api.dto.CertSubject;
import ru.blogic.dss.api.dto.DssAction;
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

import javax.ejb.Remote;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Сервис интеграции с КриптоПро DSS
 *
 * @author dgolubev
 */
@Remote
public interface RemoteDssService {

    /**
     * Получение спсика действующих сертификатов пользователя
     *
     * @return список зарегистрированных сертификатов пользователя
     * @see RemoteDssService#getCertificates(boolean)
     */
    List<DssStoredCertificate> getCertificates() throws DssServiceException;

    /**
     * Получение спсика зарегестрированных сертификатов пользователя
     *
     * @param onlyActive получить только действующие сертификаты
     * @return список сертификатов пользователя
     * @see RemoteDssService#getCertificates()
     */
    List<DssStoredCertificate> getCertificates(boolean onlyActive) throws DssServiceException;


    /**
     * Получение сертификата оп ID
     * @param certId
     * @return
     */
    DssStoredCertificate getCertificate(int certId) throws DssServiceException;


    /**
     * Сформировать запрос на сертификат. В качестве владельца используется текущий пользователь.
     * @param ekuTemplate Шаблон сертификата, на создание которого отправляется запрос
     * @return идентификатор сформированного запроса
     * @see RemoteDssService#requestCertificate(CertSubject, EkuTemplate)
     */
    int requestCertificate(EkuTemplate ekuTemplate) throws DssServiceException;

    /**
     * Сформировать запрос на сертификат, защищённый pin-кодом. В качестве владельца используется текущий пользователь.
     *
     * @param pin pin-код для защиты сертификата или {@code null}, если таковая защита не требуется
     * @param ekuTemplate Шаблон сертификата, на создание которого отправляется запрос
     * @return идентификатор сформированного запроса
     * @see RemoteDssService#requestCertificate(CertSubject, String, EkuTemplate)
     */
    int requestCertificate(String pin, EkuTemplate ekuTemplate) throws DssServiceException;

    /**
     * Сформировать запрос на сертификат
     *
     * @param owner владелец сертификата
     * @param ekuTemplate Шаблон сертификата, на создание которого отправляется запрос
     * @return идентификатор сформированного запроса
     * @see RemoteDssService#requestCertificate(CertSubject, String, EkuTemplate)
     */
    int requestCertificate(CertSubject owner, EkuTemplate ekuTemplate) throws DssServiceException;

    /**
     * Сформировать запрос на сертификат, защищённый pin-кодом
     *
     * @param owner владелец сертификата
     * @param pin   pin-код для защиты сертификата или {@code null}, если таковая защита не требуется
     * @param ekuTemplate Шаблон сертификата, на создание которого отправляется запрос
     * @return идентификатор сформированного запроса
     * @see RemoteDssService#requestCertificate(CertSubject, EkuTemplate)
     */
    int requestCertificate(CertSubject owner, String pin,EkuTemplate ekuTemplate) throws DssServiceException;

    /**
     * Получить статус запроса на сертификат
     * @param requestId Идентификатор запроса (@see {@link #requestCertificate(CertSubject, String, EkuTemplate, String)})
     * @param delegateUserId Id пользователя, от имени которого отправлялся запрос (null означает, что запрос будет искаться среди запросов текущего пользвоателя)
     * @return Статус запроса
     * @throws DssServiceException
     */
    DssRequestStatus getRequestStatus(int requestId, String delegateUserId) throws DssServiceException;

    /**
     * Сформировать запрос на сертификат от имени другого лица (доступно только оператору)
     *
     * @param owner владелец сертификата
     * @param pin   pin-код для защиты сертификата или {@code null}, если таковая защита не требуется
     * @param ekuTemplate Шаблон сертификата, на создание которого отправляется запрос
     * @param delegateUserId Id пользователя, от имени которого необходимо отправить запрос
     * @return идентификатор сформированного запроса
     * @see RemoteDssService#requestCertificate(CertSubject, EkuTemplate)
     */
    int requestCertificate(CertSubject owner, String pin, EkuTemplate ekuTemplate, String delegateUserId) throws DssServiceException;

    /**
     * Подпись документа в формате XMLDSig
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param type         типа подписи
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signXMLDSig(byte[] data, int certId, XMLDSigType type, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Электронная подпись по ГОСТ Р 34.10 - 2001
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param hash         если {@code true} - подпись значения хеш-функции ГОСТ Р 34.11 - 94, иначе - подпись данных
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signGOST3410(byte[] data, int certId, boolean hash, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись формата CAdES BES
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param detached     если {@code true} - отделённая подпись, иначе - присоединённая
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signCAdESBES(byte[] data, int certId, boolean detached, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись значения хеш-функции ГОСТ Р 34.11 - 94
     *
     * @param hash         значение хеш-функции
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signHashCAdES_BES(byte[] hash, int certId, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись формата CAdES X Long Type 1
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param detached     если {@code true} - отделённая подпись, иначе - присоединённая
     * @param tspAddress   Адрес TSP службы
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signCAdES_XLT1(byte[] data, int certId, boolean detached, URI tspAddress, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись значения хеш-функции ГОСТ Р 34.11 - 94
     *
     * @param hash         значение хеш-функции
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param tspAddress   Адрес TSP службы
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signHashCAdES_XLT1(byte[] hash, int certId, URI tspAddress, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись PDF документов с использованием формата PKCS7
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param reason       Цель подписания документа
     * @param location     Место подписания документа
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signPDF_CMS(byte[] data, int certId, String reason, String location, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись PDF документов с использованием формата CAdES
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param reason       Цель подписания документа
     * @param location     Место подписания документа
     * @param tspAddress   Адрес TSP службы
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signPDF_CAdES(byte[] data, int certId, String reason, String location, URI tspAddress, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись документов MS Word и Excel
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signMsOffice(byte[] data, int certId, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Подпись формата CMS
     *
     * @param data         подписываемые данные
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param detached     если {@code true} - отделённая подпись, иначе - присоединённая
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signCMS(byte[] data, int certId, boolean detached, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * подпись значения хеш-функции ГОСТ Р 34.11 - 94
     *
     * @param hash         значение хеш-функции
     * @param certId       идентификатор сертификата пользователя в КриптоПро DSS. Если передан {@code 0},
     *                     будет использован сертификат по умолчанию (при условии что таковой задан).
     * @param pin          pin-код сертификата ({@code null}, если не задан для используемого сертификата)
     * @param confirmation параметры подтверждения операции ({@code null}, если подтверждение не требуется)
     * @return результат выполнения операции
     */
    byte[] signHashCMS(byte[] hash, int certId, String pin, InteractiveChallengeConfirmation confirmation) throws DssServiceException;

    /**
     * Проверяет прикреплённую подпись документа
     *
     * @param type           тип подписи
     * @param signedDocument документ с прикреплённой подписью (или несколькими подписями)
     * @param signatureNum   номер подписи для проверки, начиная с 1
     * @return результат проверки
     * @see RemoteDssService#getSignersInfo(SignatureType, byte[])
     */
    VerificationResult verifySignature(SignatureType type, byte[] signedDocument, int signatureNum) throws DssServiceException;

    /**
     * Проверяет прикреплённую подпись документа
     *
     * @param type           тип подписи
     * @param signedDocument документ с прикреплённой подписью (или несколькими подписями)
     * @param signatureId    идентификатор подписи
     * @return результат проверки
     * @see RemoteDssService#getSignersInfo(SignatureType, byte[])
     */
    VerificationResult verifySignature(SignatureType type, byte[] signedDocument, String signatureId) throws DssServiceException;

    /**
     * Проверяет все прикреплённые подписи документа
     *
     * @param type           тип подписи
     * @param signedDocument документ с прикреплённой подписью (или несколькими подписями)
     * @return результат проверки
     */
    List<VerificationResult> verifyAllSignatures(SignatureType type, byte[] signedDocument) throws DssServiceException;

    /**
     * Проверяет откреплённую подпись документа
     *
     * @param document     подписанный документ
     * @param signature    подпись или хэш-значение (см. параметр {@code hash})
     * @param hash         признак того, что для проверки передаётся хэш-значение от исходного документа
     * @return результат проверки
     */
    VerificationResult verifyDetachedSignature(byte[] document, byte[] signature, boolean hash) throws DssServiceException;

    /**
     * Проверяет откреплённые подписи документа
     *
     * @param document  подписанный документ
     * @param signature подпись
     * @return результат проверки
     */
    List<VerificationResult> verifyAllDetachedSignatures(byte[] document, byte[] signature) throws DssServiceException;

    /**
     * Проверка простой подписи ГОСТ Р 34.10-2001
     *
     * @param document    подписанные данные
     * @param signature   подпись или хэш-значение (см. параметр {@code hash})
     * @param certificate сертификат ключа проверки подписи
     * @param hash        признак того, что для проверки передаётся хэш-значение от исходного документа
     * @return результат проверки
     */
    VerificationResult verifyGost34102001(byte[] document, byte[] signature, X509Certificate certificate, boolean hash) throws DssServiceException;

    /**
     * Проверка сертификата
     *
     * @param cert сертификат
     * @return результат проверки
     */
    VerificationResult verifyCertificate(byte[] cert) throws DssServiceException;

    /**
     * Получение информации о подписантах документа
     *
     * @param signatureType тип подписи
     * @param document      документ с прикреплёнными подписями
     * @return информация о подписантах
     */
    List<SignerInfo> getSignersInfo(SignatureType signatureType, byte[] document) throws DssServiceException;

    /**
     * Проверяет, требуется ли многофакторная аутентификация для указанного действия
     *
     * @param action действие для проверки политики MFA
     * @return требуется ли многофакторная аутентификация
     */
    boolean isMfaRequired(DssAction action) throws DssServiceException;

    /**
     * Инициирует процедуру многофакторной аутентификации
     * @param smsFragment Фрагмент СМС сообщения дл яотправи пользователю.
     * @return информация, необходимая для прохождения процедуры MFA
     */
    InteractiveChallengeRequest initMfaTransaction(String smsFragment) throws DssServiceException;

    /**
     * Получает от DSS структуру DSS policy
     * @return Структура DSS Policy
     */
    DssPolicy getPolicy() throws DssServiceException;

    /**
     * Получает от DSS CA policy, соответствующую текущему CA (указанному в настройках DSS Service)
     * @return политика текущего УЦ (CA)
     */
    DssCAPolicy getCurrentCaPolicy() throws DssServiceException;

    /**
     * Получение сертификата подписанта из структуры CMS подписи.
     * @param CMSData данные подписи
     * @return Сертификат подписанта, или null если он не найден.
     */
    X509Certificate getCMSSignerCertificate(byte[] CMSData) throws DssServiceException;
}
