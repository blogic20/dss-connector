package ru.blogic.dss.api.dto.usermanagement.umspolicy;

/**
 * Created by pkupershteyn on 08.04.2016.
 * Тип профиля криптопровайдера
 */
public enum CryptoProviderProfileType {

    /**
     * Провайдером используются алгоритмы семейства ГОСТ
     */
    GOST,

    /**
     * Провайдером используются связка алгоритмов RSA+AES.
     */
    RSA_AES
}
