package ru.blogic.dss.api.exception;

/**
 * @author dgolubev
 */
public class DssServiceException extends ApiException {

    public DssServiceException(String message, Throwable cause) {
            super(message, cause);
    }

    public DssServiceException(Throwable cause) {
        super(cause);
    }

    public DssServiceException(String message) {
        super(message);
    }
}
