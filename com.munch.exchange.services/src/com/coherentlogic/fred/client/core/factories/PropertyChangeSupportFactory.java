package com.coherentlogic.fred.client.core.factories;

import java.beans.PropertyChangeSupport;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;

public class PropertyChangeSupportFactory implements
    PropertyChangeSupportFactorySpecification
        <PropertyChangeSupport, SerializableBean> {

    public PropertyChangeSupportFactory () {
    }

    @Override
    public PropertyChangeSupport getInstance(SerializableBean sourceBean) {
        return new PropertyChangeSupport (sourceBean);
    }
}
