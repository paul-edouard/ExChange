package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.FILE_TYPE_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.OUTPUT_TYPE_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.SERIES_LIST_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.UNITS_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.DEFAULT_LIMIT;
import static com.coherentlogic.fred.client.core.util.Constants.FILE_TYPE;
import static com.coherentlogic.fred.client.core.util.Constants.FILTER_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.FILTER_VARIABLE;
import static com.coherentlogic.fred.client.core.util.Constants.LIMIT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OFFSET_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OUTPUT_TYPE;
import static com.coherentlogic.fred.client.core.util.Constants.SERIESS;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.coherentlogic.fred.client.core.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * A class which represents an economic data series.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/category_series.html">
 * category_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_series.html">
 * release_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series.html">series</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=SERIESS)
@XStreamAlias(SERIESS)
public class Seriess extends SerializableBean
    implements RealtimeBoundSpecification,
        FilterSpecification,
        PaginationSpecification,
        OrderBySpecification,
        SortOrderSpecification {

    private static final long serialVersionUID = 4924681115628356708L;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(Constants.SORT_ORDER)
    @XStreamAsAttribute
    private SortOrder sortOrder = SortOrder.asc;

    @XStreamAlias(Constants.ORDER_BY)
    @XStreamAsAttribute
    private OrderBy orderBy = OrderBy.seriesId;

    @XStreamAlias(Constants.LIMIT)
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

    @XStreamAlias(FILTER_VARIABLE)
    @XStreamAsAttribute
    private FilterVariable filterVariable = null;

    @XStreamAlias(FILTER_VALUE)
    @XStreamAsAttribute
    private FilterValue filterValue = null;

    @XStreamImplicit
    private List<Series> seriesList = null;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Series> getSeriesList () {
        return seriesList;
    }

    public void setSeriesList (List<Series> seriesList) {

        List<Series> oldValue = this.seriesList;

        this.seriesList = seriesList;

        firePropertyChange(
            SERIES_LIST_PROPERTY,
            oldValue,
            seriesList
        );
    }

    /**
     * @see {@link RealtimeBoundSpecification#setRealtimeStart(Date)}
     */
    @Override
    public void setRealtimeStart(Date realtimeStart) {

        Date oldValue = this.realtimeStart;

        this.realtimeStart = clone (realtimeStart);

        firePropertyChange(
            REALTIME_START_PROPERTY,
            oldValue,
            realtimeStart
        );
    }

    /**
     * @see {@link RealtimeBoundSpecification#setRealtimeEnd(Date)}
     */
    @Override
    public void setRealtimeEnd(Date realtimeEnd) {

        Date oldValue = this.realtimeEnd;

        this.realtimeEnd = clone (realtimeEnd);

        firePropertyChange(
            REALTIME_END_PROPERTY,
            oldValue,
            realtimeEnd
        );
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

    @Override
    public void setSortOrder(SortOrder sortOrder) {

        SortOrder oldValue = this.sortOrder;

        this.sortOrder = sortOrder;

        firePropertyChange(
            SORT_ORDER_PROPERTY,
            oldValue,
            sortOrder
        );
    }

    @Override
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    @Column(name=ORDER_BY_VALUE)
    @Override
    public OrderBy getOrderBy() {
        return orderBy;
    }

    @Override
    public void setOrderBy(OrderBy orderBy) {

        OrderBy oldValue = this.orderBy;

        this.orderBy = orderBy;

        firePropertyChange(
            ORDER_BY_PROPERTY,
            oldValue,
            orderBy
        );
    }

    @Override
    public void setLimit(long limit) {

        long oldValue = this.limit;

        this.limit = limit;

        firePropertyChange(
            LIMIT_PROPERTY,
            oldValue,
            limit
        );
    }

    @Override
    public void setOffset(int offset) {
        int oldValue = this.offset;

        this.offset = offset;

        firePropertyChange(
            OFFSET_PROPERTY,
            oldValue,
            offset
        );
    }

    /**
     * In the H2 database the limit is a reserved keyword so we are renaming
     * this, otherwise we'll see an exception along the lines of "Table
     * 'DEFAULTOBJECT' not found; SQL statement".
     */
    @Column(name=LIMIT_VALUE)
    @Override
    public long getLimit() {
        return limit;
    }

    @Column(name=OFFSET_VALUE)
    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public FilterVariable getFilterVariable() {
        return filterVariable;
    }

    @Override
    public void setFilterVariable(FilterVariable filterVariable) {

        FilterVariable oldValue = this.filterVariable;

        this.filterVariable = filterVariable;

        firePropertyChange(
            FILTER_VARIABLE_PROPERTY,
            oldValue,
            filterVariable
        );
    }

    @Override
    public FilterValue getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(FilterValue filterValue) {

        FilterValue oldValue = this.filterValue;

        this.filterValue = filterValue;

        firePropertyChange(
            FILTER_VALUE_PROPERTY,
            oldValue,
            filterValue
        );
    }

    public Unit getUnits() {
        return units;
    }

    public void setUnits(Unit units) {

        Unit oldValue = this.units;

        this.units = units;

        firePropertyChange(
            UNITS_PROPERTY,
            oldValue,
            units
        );
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {

        OutputType oldValue = this.outputType;

        this.outputType = outputType;

        firePropertyChange(
            OUTPUT_TYPE_PROPERTY,
            oldValue,
            outputType
        );
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {

        FileType oldValue = this.fileType;

        this.fileType = fileType;

        firePropertyChange(
            FILE_TYPE_PROPERTY,
            oldValue,
            fileType
        );
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {

        int oldValue = this.count;

        this.count = count;

        firePropertyChange(COUNT_PROPERTY, oldValue, count);
    }
}
