package com.coherentlogic.fred.client.core.converters;

import java.lang.reflect.Field;

import javax.swing.event.SwingPropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 
 *
 * @see <a href="http://rolandtapken.de/blog/2010-08/
 *  let-xstream-call-default-constructor-where-possible">Let XStream call the
 *  default constructor where possible</a>
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class SwingPropertyChangeSupportConverter extends ReflectionConverter {

    private static final Logger log =
        LoggerFactory.getLogger(SwingPropertyChangeSupportConverter.class);

    public static final String PROPERTY_CHANGE_SUPPORT =
        "propertyChangeSupport";

    private final boolean notifyOnEDT;

    public SwingPropertyChangeSupportConverter(
        Mapper mapper,
        ReflectionProvider reflectionProvider,
        boolean notifyOnEDT
    ) {
        super(mapper, reflectionProvider);
        this.notifyOnEDT = notifyOnEDT;
    }

    @Override
    public boolean canConvert(Class type) {

        boolean result = false;

        if (ClassUtils.isAssignable(SerializableBean.class, type))
            result = true;

        return result;
    }

    @Override
    protected Object instantiateNewInstance(
        HierarchicalStreamReader reader,
        UnmarshallingContext context
    ) {

        String nodeName = reader.getNodeName();

        if (log.isDebugEnabled())
            log.debug("nodeName: " + nodeName);

        SerializableBean result = null;

        try {
            Class clazz = Class.forName(nodeName);

            Object object = clazz.newInstance();

            result = (SerializableBean) object;

            Field field = clazz.getDeclaredField(PROPERTY_CHANGE_SUPPORT);

            SwingPropertyChangeSupport swingPropertyChangeSupport =
                new SwingPropertyChangeSupport(result, notifyOnEDT);

            ReflectionUtils.setField(field, result, swingPropertyChangeSupport);

        } catch (Exception exception) {
            throw new ConversionException(
                "Could not create instance of class " + reader.getNodeName(),
                exception);
        }
        return result;
    }
}
