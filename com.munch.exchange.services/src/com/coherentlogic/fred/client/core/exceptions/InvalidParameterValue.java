package com.coherentlogic.fred.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown whenever a method is called with an invalid
 * parameter value (ie. a null array).
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class InvalidParameterValue extends NestedRuntimeException {

    private static final long serialVersionUID = 1997646623571680677L;

    public InvalidParameterValue(String msg) {
        super(msg);
    }

    public InvalidParameterValue(String msg, Throwable cause) {
        super(msg, cause);
    }
}
