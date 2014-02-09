package com.coherentlogic.coherent.data.model.core.util;

/**
 * An action is executed on some data. The action is similar to a closure,
 * however since Java does not have closures (yet), we can get around this by
 * using an instance of this class as an anonymous inner class.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public interface Action<D> {

    /**
     * Method represents the logic to execute on the data.
     *
     * @param data Any data which you'd like to apply the logic to.
     */
    void execute (D data);
}
