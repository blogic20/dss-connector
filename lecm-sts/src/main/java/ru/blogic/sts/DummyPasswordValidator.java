package ru.blogic.sts;

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;

/**
 * Валидация пароля через этот валидатор всегда завершается успешно, т.к. реальная аутентификация пользователя
 * происходит при входе в приложение. Данный валидатор нужен только для успешного создания токена безопасности.
 *
 * @author dgolubev
 */
public class DummyPasswordValidator implements PasswordValidationCallback.PasswordValidator {

    @Override
    public boolean validate(PasswordValidationCallback.Request request)
            throws PasswordValidationCallback.PasswordValidationException {
        return true;
    }
}
