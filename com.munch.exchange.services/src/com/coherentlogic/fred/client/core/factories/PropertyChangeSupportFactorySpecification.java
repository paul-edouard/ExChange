package com.coherentlogic.fred.client.core.factories;

import java.beans.PropertyChangeSupport;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;

/**
 * 
 *
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 *
 * @param <R> The resultant PropertyChangeSupport instance type.
 * @param <T> The type of DefaultObject
 */
public interface PropertyChangeSupportFactorySpecification
    <R extends PropertyChangeSupport, T extends SerializableBean> {

    public R getInstance (T sourceBean);
}
