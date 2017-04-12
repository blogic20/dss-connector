package ru.blogic.dss.api.exception;


/**
 * Unchecked версия {@link ApiException}
 *
 * @see ApiException
 *
 * @author dgolubev
 */
public abstract class ApiRuntimeException extends RuntimeException {

    public ApiRuntimeException() {
    }

    public ApiRuntimeException(String message) {
        super(message);
    }

    public ApiRuntimeException(String message, Throwable cause) {
        super(message, cause != null ? new MimicException(cause) : null);
    }

    public ApiRuntimeException(Throwable cause) {
        super(cause != null ? new MimicException(cause) : null);
    }

}
