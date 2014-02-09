package com.coherentlogic.fred.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception which is thrown when the date is not of the format yyyy-MM-dd --
 * for example 2012-06-18.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class InvalidDateFormatException extends NestedRuntimeException {

    private static final long serialVersionUID = 6612520701978353717L;

    public InvalidDateFormatException (String msg) {
        super(msg);
    }

    public InvalidDateFormatException (String msg, Throwable cause) {
        super(msg, cause);
    }
}
