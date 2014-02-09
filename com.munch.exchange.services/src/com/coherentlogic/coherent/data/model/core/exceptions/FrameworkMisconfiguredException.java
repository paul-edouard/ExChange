package com.coherentlogic.coherent.data.model.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the framework is not configured properly.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class FrameworkMisconfiguredException extends NestedRuntimeException {

    private static final long serialVersionUID = 8813225924591445071L;

    public FrameworkMisconfiguredException(String msg) {
        super(msg);
    }

    public FrameworkMisconfiguredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
