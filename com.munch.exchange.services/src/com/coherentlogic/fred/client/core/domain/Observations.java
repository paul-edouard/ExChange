package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.UNITS_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.DEFAULT_LIMIT;
import static com.coherentlogic.fred.client.core.util.Constants.FILE_TYPE;
import static com.coherentlogic.fred.client.core.util.Constants.LIMIT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATIONS;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATION_END;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATION_START;
import static com.coherentlogic.fred.client.core.util.Constants.OFFSET_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OUTPUT_TYPE;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.coherentlogic.fred.client.core.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * A class which represents the observations or data values for an economic
 * data series.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_observations.html">
 * series_observations</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=OBSERVATIONS)
@XStreamAlias(OBSERVATIONS)
public class Observations extends SerializableBean
    implements RealtimeBoundSpecification,
        ObservationBoundSpecification,
        PaginationSpecification,
        OrderBySpecification,
        SortOrderSpecification {

    private static final long serialVersionUID = -8749696888928925402L;

    static final String OBSERVATION_LIST_PROPERTY = "observationList",
        OUTPUT_TYPE_PROPERTY = "outputType",
        FILE_TYPE_PROPERTY = "fileType",
        MESSAGE_PROPERTY = "message";

    @XStreamImplicit
    private List<Observation> observationList = null;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(OBSERVATION_START)
    @XStreamAsAttribute
    private Date observationStart = null;

    @XStreamAlias(OBSERVATION_END)
    @XStreamAsAttribute
    private Date observationEnd = null;

    @XStreamAlias(Constants.SORT_ORDER)
    @XStreamAsAttribute
    private SortOrder sortOrder = SortOrder.asc;

    @XStreamAsAttribute
    private OrderBy orderBy = OrderBy.seriesId;

    @XStreamAlias(LIMIT_PROPERTY)
    @XStreamAsAttribute
    private long limit = DEFAULT_LIMIT;

    @XStreamAlias(Constants.OFFSET)
    @XStreamAsAttribute
    private int offset = 0;

    @XStreamAlias(UNITS)
    @XStreamAsAttribute
    private Unit units = null;

    @XStreamAlias(OUTPUT_TYPE)
    @XStreamAsAttribute
    private OutputType outputType = null;

    @XStreamAlias(FILE_TYPE)
    @XStreamAsAttribute
    private FileType fileType = null;

    @XStreamAlias(COUNT)
    @XStreamAsAttribute
    private int count = 0;

    /**
     * The message is transient since it only holds an instance of {@link
     * java.io.InputStream} and this should not be saved to a database.
     */
    private transient Message message;

    /**
     * Method returns the result of the call to the {@link DefaultObject}'s
     * equals method.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Method returns the result of the call to the {@link DefaultObject}'s
     * hashCode method.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Getter method for the observation list property.
     */
    @OneToMany(cascade=CascadeType.ALL)
    public List<Observation> getObservationList() {
        return observationList;
    }

    /**
     * Setter method for the observation list property.
     */
    public void setObservationList(List<Observation> observationList) {

        List<Observation> oldValue = this.observationList;

        this.observationList = observationList;

        firePropertyChange(
            OBSERVATION_LIST_PROPERTY,
            oldValue,
            observationList
        );
    }

    /**
     * Setter method for the sort order property.
     */
    @Override
    public void setSortOrder(SortOrder sortOrder) {

        SortOrder oldValue = this.sortOrder;

        this.sortOrder = sortOrder;

        firePropertyChange(
            SORT_ORDER_PROPERTY, oldValue, sortOrder);
    }

    /**
     * Getter method for the sort order property.
     */
    @Override
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * Getter method for the order by property.
     */
    @Column(name=ORDER_BY_VALUE)
    @Override
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Setter method for the order by property.
     */
    @Override
    public void setOrderBy(OrderBy orderBy) {

        OrderBy oldValue = this.orderBy;

        this.orderBy = orderBy;

        firePropertyChange(
            ORDER_BY_PROPERTY, oldValue, orderBy);
    }

    /**
     * Setter method for the limit property.
     */
    @Override
    public void setLimit(long limit) {

        long oldValue = this.limit;

        this.limit = limit;

        firePropertyChange(
            LIMIT_PROPERTY, oldValue, limit);
    }

    /**
     * Setter method for the offset property.
     */
    @Override
    public void setOffset(int offset) {

        int oldValue = this.offset;

        this.offset = offset;

        firePropertyChange(
            OFFSET_PROPERTY, oldValue, offset);
    }

    /**
     * Getter method for the limit property.
     *
     * Note that in the H2 database the limit is a reserved keyword so we are
     * renaming this, otherwise we'll see an exception along the lines of "Table
     * 'DEFAULTOBJECT' not found; SQL statement".
     */
    @Column(name=LIMIT_VALUE)
    @Override
    public long getLimit() {
        return limit;
    }

    /**
     * Getter method for the offset property.
     */
    @Column(name=OFFSET_VALUE)
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * @see {@link ObservationBoundSpecification#setObservationStart(Date)}
     */
    @Override
    public void setObservationStart(Date observationStart) {

        Date oldValue = this.observationStart;

        this.observationStart = clone (observationStart);

        firePropertyChange(
            OBSERVATION_START_PROPERTY, oldValue, observationStart);
    }

    /**
     * @see {@link ObservationBoundSpecification#setObservationEnd(Date)}
     */
    @Override
    public void setObservationEnd(Date observationEnd) {

        Date oldValue = this.observationEnd;

        this.observationEnd = clone (observationEnd);

        firePropertyChange(
            OBSERVATION_END_PROPERTY, oldValue, observationEnd);
    }

    /**
     * @see {@link ObservationBoundSpecification#getObservationStart()}
     */
    @Override
    public Date getObservationStart() {
        return clone (observationStart);
    }

    /**
     * @see {@link ObservationBoundSpecification#getObservationEnd()}
     */
    @Override
    public Date getObservationEnd() {
        return clone (observationEnd);
    }

    /**
     * @see {@link RealtimeBoundSpecification#setRealtimeStart(Date)}
     */
    @Override
    public void setRealtimeStart(Date realtimeStart) {

        Date oldValue = this.realtimeStart;

        this.realtimeStart = clone (realtimeStart);

        firePropertyChange(REALTIME_START_PROPERTY, oldValue, realtimeStart);
    }

    /**
     * @see {@link RealtimeBoundSpecification#setRealtimeEnd(Date)}
     */
    @Override
    public void setRealtimeEnd(Date realtimeEnd) {

        Date oldValue = this.realtimeEnd;

        this.realtimeStart = clone (realtimeEnd);

        firePropertyChange(REALTIME_END_PROPERTY, oldValue, realtimeEnd);
    }

    /**
     * @see {@link RealtimeBoundSpecification#getRealtimeStart()}
     */
    @Override
    public Date getRealtimeStart() {
        return clone (realtimeStart);
    }

    /**
     * @see {@link RealtimeBoundSpecification#getRealtimeEnd()}
     */
    @Override
    public Date getRealtimeEnd() {
        return clone (realtimeEnd);
    }

    /**
     * Getter method for the units property.
     */
    public Unit getUnits() {
        return units;
    }

    /**
     * Setter method for the units property.
     */
    public void setUnits(Unit units) {

        Unit oldValue = this.units;

        this.units = units;

        firePropertyChange(UNITS_PROPERTY, oldValue, units);
    }

    /**
     * Getter method for the output type property.
     */
    public OutputType getOutputType() {
        return outputType;
    }

    /**
     * Setter method for the output type property.
     */
    public void setOutputType(OutputType outputType) {

        OutputType oldValue = this.outputType;

        this.outputType = outputType;

        firePropertyChange(
            OUTPUT_TYPE_PROPERTY, oldValue, outputType);
    }

    /**
     * Getter method for the file type property.
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Setter method for the file type property.
     */
    public void setFileType(FileType fileType) {

        FileType oldValue = this.fileType;

        this.fileType = fileType;

        firePropertyChange(FILE_TYPE_PROPERTY, oldValue, fileType);
    }

    /**
     * Getter method for the count property.
     */
    public int getCount() {
        return count;
    }

    /**
     * Setter method for the count property.
     */
    public void setCount(int count) {

        int oldValue = this.count;

        this.count = count;

        firePropertyChange(COUNT_PROPERTY, oldValue, count);
    }

    /**
     * Getter method for the message property.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Setter method for the message property.
     */
    public void setMessage(Message message) {

        Message oldValue = this.message;

        this.message = message;

        firePropertyChange(MESSAGE_PROPERTY, oldValue, message);
    }
}
