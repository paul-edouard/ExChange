package com.coherentlogic.coherent.data.model.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when a call to the clone method has failed.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class CloneFailedException extends NestedRuntimeException {

    private static final long serialVersionUID = -4238659933848465833L;

    public CloneFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CloneFailedException(String msg) {
        super(msg);
    }
}
