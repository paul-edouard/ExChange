package com.coherentlogic.coherent.data.model.core.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.coherentlogic.coherent.data.model.core.exceptions.CloneFailedException;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * A class which all domain classes inherit from and that contains
 * implementations of the hashCode, equals, and toString methods.
 *
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/beans/
 *  PropertyChangeSupport.html"> PropertyChangeSupport</a>
 *
 * http://rolandtapken.de/blog/2010-08/
 * let-xstream-call-default-constructor-where-possible
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity()
@Table(name=SerializableBean.SERIALIZABLE_BEAN)
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class SerializableBean implements Serializable {

    private static final long serialVersionUID = 3805246572039788310L;

    public static final String SERIALIZABLE_BEAN = "serializable_bean",
        PRIMARY_KEY = "primaryKey";

//    private static final String FRAMEWORK_MISCONFIGURED_TEXT =
//        "The propertyChangeSupport instance is null which indicates that " +
//        "the framework has not been configured properly. Refer to the " +
//        "example applications (specifically to the Spring application " +
//        "context configuration) and note that if the bean with the id " +
//        "xstreamMarshaller is set to org.springframework.oxm.xstream." +
//        "XStreamMarshaller then this needs to be changed so that it uses the " +
//        "com.coherentlogic.coherent.data.model.core.xstream." +
//        "CustomXStreamMarshaller class instead." +
//        "" +
//        "See also http://bit.ly/1hducQL";

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long primaryKey = null;

    /**
     * @see {@link java.beans.VetoableChangeSupport}
     */
    @Transient
    @XStreamOmitField
    private transient final VetoableChangeSupport vetoableChangeSupport =
        new VetoableChangeSupport (this);

    /**
     * Note that this property must be set to a <b>non-null value</b> otherwise
     * attempts to register an instance of {@link PropertyChangeListener} with
     * an instance of this class will fail, as well as any attempt to invoke the
     * {@link java.beans.PropertyChangeSupport#firePropertyChange(
     *  java.beans.PropertyChangeEvent)} method (and/or possibly other methods).
     *
     * Note that the {@link #propertyChangeSupport} property is not final. Since
     * we're using XStream and this class implements {@link
     * java.io.Serializable} then we need to either supply a readObject method
     * or the {@link #propertyChangeSupport} property will be null when XStream
     * unmarshalls the XML. We don't want to set this property to final,
     * however, as the user may want to use a different default value than the
     * one we supply.
     *
     * @see {@link java.beans.PropertyChangeSupport}
     */
    @Transient
    @XStreamOmitField
    private transient PropertyChangeSupport propertyChangeSupport = null;

    /**
     * Default constructor -- note that the propertyChangeSupport property will
     * be set to an instance of {@link java.beans.PropertyChangeSupport}.
     */
    public SerializableBean () {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * A constructor that allows the propertyChangeSupport to be set when the
     * class is instantiated.
     *
     * @todo This constructor may not make sense so for the moment we're going
     * to leave the scope set to package.
     */
    SerializableBean (PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

//    /**
//     * Method is used to ensure that the user has not misconfigured the
//     * framework due to the introduction of PropertyChangeSupport in all classes
//     * that inherit from this class.
//     *
//     * Note that this assertion will not remain indefinitely as part of every
//     * method which currently calls it. The reason for this is that it's
//     * inefficient to require continuous non-null checks as is below.
//     */
//    private void assertPropertyChangeSupportNotNull () {
//        if (propertyChangeSupport == null)
//            throw new FrameworkMisconfiguredException (
//                FRAMEWORK_MISCONFIGURED_TEXT);
//    }

    @Override
    public int hashCode () {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals (Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    public Long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Long primaryKey) {

        Long oldValue = this.primaryKey;

        this.primaryKey = primaryKey;

        firePropertyChange(PRIMARY_KEY, oldValue, primaryKey);
    }

    /**
     * Method returns a copy of the date parameter or null if the reference to
     * the date is null.
     *
     * @param date The date to be cloned.
     */
    protected static Date clone (Date date) {

        Date newDate = null;

        if (date != null)
            newDate = (Date) date.clone();

        return newDate;
    }

    public <T> T clone (Class<T> type) {

        Object object = clone ();

        T result = type.cast(object);

        return result;
    }

    @Override
    public Object clone () {
        try {
            return BeanUtils.cloneBean(this);
        } catch (IllegalAccessException illegalAccessException) {
            throw new CloneFailedException("Illegal access exception thrown",
                illegalAccessException);
        } catch (InstantiationException instantiationException) {
            throw new CloneFailedException("Failed to instantiate the class.",
                instantiationException);
        } catch (InvocationTargetException invocationTargetException) {
            throw new CloneFailedException(
                "Invocation target exception thrown.",
                invocationTargetException);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new CloneFailedException(
                "No such method exception thrown.", noSuchMethodException);
        }
    }

    /**
     * Getter method for the {@link VetoableChangeSupport} instance.
     */
    @Transient
    public VetoableChangeSupport getVetoableChangeSupport() {
        // Why is this marked as transient?
        // http://stackoverflow.com/questions/3022958/
        // java-hibernate-annotations-how-to-add-methods-to-pojo-object
        return vetoableChangeSupport;
    }

    /**
     * Method adds the specified listener to the {@link #vetoableChangeSupport}
     * instance.
     */
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    /**
     * Method removes the specified listener to the {@link
     * #vetoableChangeSupport} instance.
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    /**
     * Getter method for the {@link #propertyChangeSupport} property.
     */
    @Transient
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * Setter method for the {@link #propertyChangeSupport} property.
     */
    public void setPropertyChangeSupport(
        PropertyChangeSupport propertyChangeSupport
    ) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    /**
     * Method adds the specified listener to the {@link
     * #propertyChangeSupport} instance.
     */
    public void addPropertyChangeListener(
        PropertyChangeListener listener
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
            propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Method removes the specified listener from the {@link
     * #propertyChangeSupport} instance.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#firePropertyChange(
     * String, Object, Object)} method.
     */
    protected void firePropertyChange (
        String propertyName,
        Object oldValue,
        Object newValue
    ) {

//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
	        propertyChangeSupport.firePropertyChange(
	            propertyName,
	            oldValue,
	            newValue
	        );
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#firePropertyChange(
     * String, boolean, boolean)} method.
     */
    protected void firePropertyChange (
        String propertyName,
        boolean oldValue,
        boolean newValue
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
	        propertyChangeSupport.firePropertyChange(
	            propertyName,
	            oldValue,
	            newValue
	        );
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#firePropertyChange(String, int,
     * int)} method.
     */
    protected void firePropertyChange (
        String propertyName,
        int oldValue,
        int newValue
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
	        propertyChangeSupport.firePropertyChange(
	            propertyName,
	            oldValue,
	            newValue
	        );
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#firePropertyChange(
     * PropertyChangeEvent)}
     * method.
     */
    protected void firePropertyChange (
        PropertyChangeEvent propertyChangeEvent
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
            propertyChangeSupport.firePropertyChange(propertyChangeEvent);
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#fireIndexedPropertyChange(String,
     * int, Object, Object)} method.
     */
    protected void fireIndexedPropertyChange (
        String propertyName,
        int index,
        Object oldValue,
        Object newValue
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
	        propertyChangeSupport.fireIndexedPropertyChange(
	            propertyName, index, oldValue, newValue);
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#fireIndexedPropertyChange(String,
     * int, int, int)} method.
     */
    protected void fireIndexedPropertyChange (
        String propertyName,
        int index,
        int oldValue,
        int newValue
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
        	propertyChangeSupport.fireIndexedPropertyChange(
                propertyName, index, oldValue, newValue);
    }

    /**
     * Method delegates to the
     * {@link java.beans.PropertyChangeSupport#fireIndexedPropertyChange(String,
     * int, boolean, boolean)} method.
     */
    protected void fireIndexedPropertyChange (
        String propertyName,
        int index,
        boolean oldValue,
        boolean newValue
    ) {
//        assertPropertyChangeSupportNotNull ();

    	if (propertyChangeSupport != null)
            propertyChangeSupport.fireIndexedPropertyChange(
                propertyName, index, oldValue, newValue);
    }

    /**
     * Method uses the Apache {@link ToStringBuilder} to return a stringified
     * representation of this instance.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
