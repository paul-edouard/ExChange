package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.VINTAGE_DATE_LIST_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.DEFAULT_LIMIT;
import static com.coherentlogic.fred.client.core.util.Constants.LIMIT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OFFSET_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.VINTAGE_DATES;

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
 * A class which represents the dates in history when a series' data values were
 * revised or new data values were released.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_vintagedates.html">
 * series_vintagedates</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=VINTAGE_DATES)
@XStreamAlias(VINTAGE_DATES)
public class VintageDates extends SerializableBean
    implements
        RealtimeBoundSpecification,
        PaginationSpecification,
        OrderBySpecification,
        SortOrderSpecification {

    private static final long serialVersionUID = 6819386088595842198L;

    static final String
        COUNT = "count",
        VINTAGE_DATE_LIST = "vintageDateList";

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(Constants.SORT_ORDER)
    @XStreamAsAttribute
    private SortOrder sortOrder = SortOrder.asc;

    /**
     * @todo Should this be the enum OrderBy?
     */
    @XStreamAlias(Constants.ORDER_BY)
    @XStreamAsAttribute
    private OrderBy orderBy = null;

    @XStreamAlias(Constants.LIMIT)
    @XStreamAsAttribute
    private long limit = DEFAULT_LIMIT;

    @XStreamAlias(Constants.OFFSET)
    @XStreamAsAttribute
    private int offset = 0;

    @XStreamAlias(COUNT)
    @XStreamAsAttribute
    private int count = 0;

    @XStreamImplicit
    private List<VintageDate> vintageDateList = null;

    @OneToMany(cascade=CascadeType.ALL)
    public List<VintageDate> getVintageDateList() {
        return vintageDateList;
    }

    public void setVintageDateList(List<VintageDate> vintageDateList) {

        List<VintageDate> oldValue = this.vintageDateList;

        this.vintageDateList = vintageDateList;

        firePropertyChange(
            VINTAGE_DATE_LIST_PROPERTY,
            oldValue,
            vintageDateList
        );
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

        firePropertyChange(LIMIT_PROPERTY, oldValue, limit);
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

    @Column(name=OFFSET_VALUE)
    @Override
    public int getOffset() {
        return offset;
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

    @Column(name=COUNT_VALUE)
    public int getCount() {
        return count;
    }

    public void setCount(int count) {

        int oldValue = this.count;

        this.count = count;

        firePropertyChange(COUNT, oldValue, count);
    }
}
