package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.SOURCE_LIST_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.DEFAULT_LIMIT;
import static com.coherentlogic.fred.client.core.util.Constants.LIMIT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OFFSET_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.SOURCES;

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
 * A class which represents sources of economic data.
 *
 * @see <a href="http://research.stlouisfed.org/fred2/sources">sources</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/source.html">source</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_sources.html">
 * release_sources</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=SOURCES)
@XStreamAlias(SOURCES)
public class Sources extends SerializableBean
    implements
        RealtimeBoundSpecification,
        PaginationSpecification,
        OrderBySpecification,
        SortOrderSpecification {

    private static final long serialVersionUID = -8056788979079673100L;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(Constants.ORDER_BY)
    @XStreamAsAttribute
    private OrderBy orderBy = OrderBy.seriesId;

    @XStreamAlias(Constants.SORT_ORDER)
    @XStreamAsAttribute
    private SortOrder sortOrder = SortOrder.asc;

    @XStreamAlias(COUNT)
    @XStreamAsAttribute
    private int count = 0;

    @XStreamAlias(Constants.OFFSET)
    @XStreamAsAttribute
    private int offset = 0;

    @XStreamAlias(Constants.LIMIT)
    @XStreamAsAttribute
    private long limit = DEFAULT_LIMIT;

    @XStreamImplicit
    private List<Source> sourceList = null;

    /**
     * @see {@link RealtimeBoundSpecification#getRealtimeStart()}
     */
    @Override
    public Date getRealtimeStart() {
        return clone (realtimeStart);
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
     * @see {@link RealtimeBoundSpecification#getRealtimeEnd()}
     */
    @Override
    public Date getRealtimeEnd() {
        return clone (realtimeEnd);
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
    public SortOrder getSortOrder() {
        return sortOrder;
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

    @Column(name=COUNT_VALUE)
    public int getCount() {
        return count;
    }

    /**
     * Setter method for the count value.
     */
    public void setCount(int count) {
        int oldValue = this.count;

        this.count = count;

        firePropertyChange(COUNT_PROPERTY, oldValue, count);
    }

    @Column(name=OFFSET_VALUE)
    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {

        int oldValue = this.offset;

        this.offset = offset;

        firePropertyChange(OFFSET_PROPERTY, oldValue, offset);
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

    @Override
    public void setLimit(long limit) {

        long oldValue = this.limit;

        this.limit = limit;

        firePropertyChange(LIMIT_PROPERTY, oldValue, limit);
    }

    @OneToMany(cascade=CascadeType.ALL)
    public List<Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Source> sourceList) {

        List<Source> oldValue = this.sourceList;

        this.sourceList = sourceList;

        firePropertyChange(
            SOURCE_LIST_PROPERTY,
            oldValue,
            sourceList
        );
    }
}
