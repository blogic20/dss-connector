package ru.blogic.dss.service;

import ru.blogic.dss.api.dto.DssConfirmableAction;
import ru.blogic.dss.api.dto.InteractiveChallengeRequest;
import ru.blogic.dss.api.exception.DssServiceException;

import java.io.Serializable;

/**
 * @author dgolubev
 * Сервис, ответственный за организацию мультифакторной аутентификации
 */
public interface MfaService extends Serializable {

    /**
     * Проверяет согласно политике подписи, требуется ли мультафакторная аутентификация
     * для совершения указанной операции
     * @param action операция для проверки
     * @return Требуется ли мультифакторная аутентификация
     * @throws DssServiceException
     */
    boolean isMfaRequired(DssConfirmableAction action) throws DssServiceException;

    /**
     * Инифиация процедуры мультафакторной аутентификации
     * @param endPoint URL сервиса
     * @param smsFragment Фрагмент SMS для отправки с конекстным сообщением
     * @param transactionId ID транзакции (получаенный ранее у этого же сервиса)
     * @return Данные для подтверждения пользователем.
     */
    InteractiveChallengeRequest startInteractiveChallenge(String endPoint, String smsFragment, int transactionId);
}
