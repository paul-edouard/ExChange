package com.coherentlogic.coherent.data.model.core.xstream;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.coherentlogic.coherent.data.model.core.exceptions.GenericReflectionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathUnmarshaller;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * A marshalling strategy that assigns an instance of the {@link
 * #propertyChangeSupportType} to any object which extends from {@link
 * com.coherentlogic.coherent.data.model.core.domain.SerializableBean}.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class CustomMarshallingStrategy extends
    ReferenceByXPathMarshallingStrategy {

    private static final Logger log = LoggerFactory
        .getLogger(CustomMarshallingStrategy.class);

    private final Class<? extends PropertyChangeSupport>
        propertyChangeSupportType;

    /**
     * A default constructor that sets the mode to {@link
     * ReferenceByXPathMarshallingStrategy#RELATIVE} and the
     * {@link #propertyChangeSupportType} to {@link
     * java.beans.PropertyChangeSupport}.
     */
    public CustomMarshallingStrategy() {
        this (RELATIVE, PropertyChangeSupport.class);
    }

    /**
     * A constructor that sets the mode to {@link
     * ReferenceByXPathMarshallingStrategy#RELATIVE} and the
     * {@link #propertyChangeSupportType} to the propertyChangeSupportType
     * parameter value.
     */
    public CustomMarshallingStrategy(
        Class<? extends PropertyChangeSupport> propertyChangeSupportType
    ) {
        this (RELATIVE, propertyChangeSupportType);
    }

    /**
     * A constructor that sets the mode to the value parameter value and the
     * {@link #propertyChangeSupportType} to the propertyChangeSupportType
     * parameter value.
     */
    public CustomMarshallingStrategy(
        int mode,
        Class<? extends PropertyChangeSupport> propertyChangeSupportType
    ) {
        super(mode);
        this.propertyChangeSupportType = propertyChangeSupportType;
    }

    /**
     * Method assigns an instance of {@link java.beans.PropertyChangeSupport} to
     * all objects of type {@link
     * com.coherentlogic.coherent.data.model.core.domain.SerializableBean}.
     */
    static void assignPropertyChangeSupport (
        Object object,
        Class<? extends PropertyChangeSupport> propertyChangeSupportType
    ) {
        if (object instanceof SerializableBean)
            assignPropertyChangeSupport (
                (SerializableBean) object,
                propertyChangeSupportType);
    }

    /**
     * Method assigns an instance of {@link java.beans.PropertyChangeSupport} to
     * the {@link defaultObject
     * com.coherentlogic.coherent.data.model.core.domain.SerializableBean}
     * parameter.
     */
    static void assignPropertyChangeSupport (
        SerializableBean serializableBean,
        Class<? extends PropertyChangeSupport> propertyChangeSupportType
    ) {
        PropertyChangeSupport propertyChangeSupport =
            new PropertyChangeSupport (serializableBean);

        Constructor<? extends PropertyChangeSupport> constructor;

        try {
            constructor =
                propertyChangeSupportType.getConstructor(Object.class);

            PropertyChangeSupport proopertyChangeSupport =
                    (PropertyChangeSupport) constructor.newInstance(
                        serializableBean);

            serializableBean.setPropertyChangeSupport(propertyChangeSupport);
        } catch (
            NoSuchMethodException |
            SecurityException |
            InstantiationException |
            IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException exception
        ) {
            throw new GenericReflectionException (
                "Unable to set the propertyChangeSupport property on the " +
                "serializableBean: " + serializableBean,
                exception
            );
        }
    }

    /**
     * This method will call the super class convert method and then check the
     * resultant object type and if it is an instance of {@link
     * com.coherentlogic.coherent.data.model.core.domain.SerializableBean} an
     * instance of the {@link #propertyChangeSupportType} will be created and
     * assigned to that object.
     */
    @Override
    protected TreeUnmarshaller createUnmarshallingContext(
        Object root,
        HierarchicalStreamReader reader,
        ConverterLookup converterLookup,
        Mapper mapper
    ) {
        return new ReferenceByXPathUnmarshaller (
            root,
            reader,
            converterLookup,
            mapper
        ) {
            @Override
            protected Object convert(
                Object parent,
                Class type,
                Converter converter
            ) {
                Object result = super.convert(parent, type, converter);

                assignPropertyChangeSupport(result, propertyChangeSupportType);

                return result;
            }
        };
    }
}
