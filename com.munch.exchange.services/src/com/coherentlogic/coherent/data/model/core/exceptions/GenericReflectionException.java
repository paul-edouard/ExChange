package com.coherentlogic.coherent.data.model.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception which is thrown when executing reflective logic on some object
 * fails, thus causing an exception to be thrown.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class GenericReflectionException extends NestedRuntimeException {

    private static final long serialVersionUID = 2695043941954727287L;

    public GenericReflectionException(String msg) {
        super(msg);
    }

    public GenericReflectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
