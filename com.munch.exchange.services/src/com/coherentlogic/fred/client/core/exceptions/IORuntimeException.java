package com.coherentlogic.fred.client.core.exceptions;

/**
 * An exception that is used to convert (checked) instances of IOException into
 * a corresponding runtime exception.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class IORuntimeException extends RuntimeException {

    private static final long serialVersionUID = -5284077828661187603L;

    public IORuntimeException() {
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(Throwable cause) {
        super(cause);
    }

    public IORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
