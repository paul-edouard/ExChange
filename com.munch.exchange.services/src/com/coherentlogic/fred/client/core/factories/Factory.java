package com.coherentlogic.fred.client.core.factories;

/**
 * A specification for classes that follow the factory pattern.
 *
 * @param <T> The type of object returned from the call to the
 * {@link #getInstance()} method.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public interface Factory<T> {

    public T getInstance ();
}
