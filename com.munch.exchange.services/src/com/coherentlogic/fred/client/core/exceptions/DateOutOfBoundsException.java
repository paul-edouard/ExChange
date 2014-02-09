package com.coherentlogic.fred.client.core.exceptions;

import java.util.Date;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the date is prior to 1776-07-04 or after
 * 9999-12-31.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class DateOutOfBoundsException extends NestedRuntimeException {

    private static final long serialVersionUID = 1172568104077678026L;

    public DateOutOfBoundsException(Date actualDate) {
        super("The date " + actualDate + " must be between 1776-07-04 " +
            "and 9999-12-31.");
    }
}
