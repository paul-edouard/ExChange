package com.coherentlogic.fred.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the offset value is less than zero.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class OffsetOutOfBoundsException extends NestedRuntimeException {

    private static final long serialVersionUID = 2553637573959609566L;

    public OffsetOutOfBoundsException(int offset) {
        super("The offset must be a non-negative integer -- in this case " +
            "the value (" + offset + ") is invalid.");
    }
}
