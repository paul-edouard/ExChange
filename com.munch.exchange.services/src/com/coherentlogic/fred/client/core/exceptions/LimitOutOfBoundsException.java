package com.coherentlogic.fred.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the limit is less than 1 or greater than
 * 10000.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class LimitOutOfBoundsException extends NestedRuntimeException {

    private static final long serialVersionUID = -5918423598873067879L;

    public LimitOutOfBoundsException(long limit) {
        super("The limit " + limit + " must be between 1 and 100000.");
    }
}
