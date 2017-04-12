package ru.blogic.dss.api.exception;

/**
 * Суперкласс для исключений, выбрасываемых во внешние компоненты, которые не разделяют общий classpath с
 * кодом компонента, в котором это исключение возникло.
 * <p/>
 * Данное исключение скрывает породившее его "caused by"-исключение (если таковое существует), изолируя вызывающий код
 * от деталей реализации компонента, в котором оно произошло, и оповещая его только о самом факте возникновения
 * исключительной ситуации. Подробная информация при необходимости должна передаваться в полях класса-наследника и
 * учитывать тот факт, что некоторые классы могут быть недоступны в classpath клиентского кода.
 *
 * @see ApiRuntimeException
 *
 * @author dgolubev
 */
public abstract class ApiException extends Exception {

    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause != null ? new MimicException(cause) : null);
    }

    public ApiException(Throwable cause) {
        super(cause != null ? new MimicException(cause) : null);
    }
}
