package ru.atott.mapper.introspection;

public class NotBeanException extends RuntimeException {

    public NotBeanException() { }

    public NotBeanException(String message) {
        super(message);
    }

    public NotBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotBeanException(Throwable cause) {
        super(cause);
    }

    public NotBeanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
