package ru.blogic.dss.common;

/**
 * @author dgolubev
 */
public interface Constants {
    /**
     * Наименование внутреннего сервиса идентификации в DSS
     *(Security Token Service, STS)
     */
    String LECM_STS_SERVICE_NAME = "LECM_SecurityTokenService";
    /**
     * Порт внутреннего сервиса идентификации
     */
    String LECM_STS_PORT_NAME = "LecmSTS_port";
    /**
      *Идентификатор текущей системы как центра идентификации в DSS
     */
    String LECM_STS_ISSUER_NAME = "LECM-STS";
    /**
     * Наименование сервиса подписи DSS
     */
    String DSS_STS_SERVICE_NAME = "SecurityTokenService";
    /**
     * Порт сервиса подписи DSS
     */
    String DSS_STS_PORT_NAME = "WS2007HttpBinding_IWSTrust13Sync_Token";


    long STS_KEY_SIZE = 256;
    String STS_SIGN_WITH = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    String STS_C14N_ALGORITHM = "http://www.w3.org/2001/10/xml-exc-c14n#";
    String STS_ENCRYPTION_ALGORITHM = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";

    String ECHO_CLAIMS_DIALECT = "http://lecm.blogic.ru/claims/echo";


    String URI_CPRO_IDENTITY = "http://dss.cryptopro.ru/identity/";
    String URI_CPRO_IDENTITY_CLAIMS = URI_CPRO_IDENTITY + "claims/";
    String URI_CPRO_IDENTITY_CLAIMS_DELEGATE_USER_ID = URI_CPRO_IDENTITY_CLAIMS + "delegateuserid";
    String URI_CPRO_IDENTITY_CLAIMS_TRANSACTION_ID = URI_CPRO_IDENTITY_CLAIMS + "dsstransactionid";
    String URI_CPRO_IDENTITY_CLAIMS_TRANSACTION_EXPIREDATE = URI_CPRO_IDENTITY_CLAIMS + "dsstransactionexpiredate";

    String URI_CPRO_IDFENTITY_CONFIRMATIONTEXT_UNFORMATTED = URI_CPRO_IDENTITY + "confirmationtext/unformatted";


    String URI_MS_STS = "http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice";


    String URI_OASIS_AUTH_CLAIMS = "http://docs.oasis-open.org/wsfed/authorization/200706/authclaims";

    String URI_OASIS_WS_TRUST_200512 = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/";
    String URI_OASIS_WS_TRUST_200512_SYMMETRICKEY = URI_OASIS_WS_TRUST_200512 + "SymmetricKey";
    String URI_OASIS_WS_TRUST_200512_PUBLICKEY = URI_OASIS_WS_TRUST_200512 + "PublicKey";


}
