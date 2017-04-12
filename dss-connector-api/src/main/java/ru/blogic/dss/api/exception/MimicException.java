package ru.blogic.dss.api.exception;

/**
 * Мимикрирует под оригинальное исключение, скрывая его реализацию.
 *
 * @author dgolubev
 * @see ApiException
 * @see ApiRuntimeException
 */
public final class MimicException extends Throwable {

    private final String originalToString;

    public MimicException(Throwable original) {
        super(original.getLocalizedMessage(), original.getCause() != null ? new MimicException(original.getCause()) : null);
        originalToString = original.toString();
        setStackTrace(original.getStackTrace());
    }

    @Override
    public final synchronized Throwable initCause(Throwable cause) {
        if (cause == this) {
            throw new IllegalArgumentException("Self-causation not permitted", this);
        }
        return super.initCause(cause != null ? new MimicException(cause) : null);
    }

    @Override
    public final String toString() {
        return originalToString;
    }
}