package ru.blogic.dss;

import ru.blogic.dss.api.exception.ApiException;
import ru.blogic.dss.api.exception.ApiRuntimeException;
import ru.blogic.dss.api.exception.DssServiceException;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * Транслирует внутренние исключения реализации сервиса подписи в исключения, объявленные в его API.
 * Данный подход необходим для предотвращения запутанных исключений типа {@link ClassNotFoundException} при вызове
 * методов сервиса, т.к. некоторые классы исключений могут быть (и будут!) недоступны в classpath клиента.
 *
 * @author dgolubev
 * @see DssServiceException
 * @see ApiRuntimeException
 */
public class ExceptionTranslatingInterceptor {

    @AroundInvoke
    public Object translateExceptionIfRequired(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (Exception caught) {
            throw caught instanceof ApiException || caught instanceof ApiRuntimeException
                    ? caught
                    : new DssServiceException(caught);
        }
    }
}
