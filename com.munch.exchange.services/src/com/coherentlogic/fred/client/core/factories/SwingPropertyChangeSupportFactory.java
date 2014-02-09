package com.coherentlogic.fred.client.core.factories;

import javax.swing.event.SwingPropertyChangeSupport;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;

public class SwingPropertyChangeSupportFactory implements
    PropertyChangeSupportFactorySpecification
        <SwingPropertyChangeSupport, SerializableBean> {

    public SwingPropertyChangeSupportFactory () {
    }

    @Override
    public SwingPropertyChangeSupport getInstance(SerializableBean sourceBean) {
        return new SwingPropertyChangeSupport (sourceBean);
    }
}
