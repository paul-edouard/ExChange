package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.DEFAULT_LIMIT;
import static com.coherentlogic.fred.client.core.util.Constants.LIMIT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OFFSET_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_DATES;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.IdentityBean;
import com.coherentlogic.fred.client.core.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * A class which represents the release dates for all releases of economic data.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases_dates.html">
 * releases_dates</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_dates.html">
 * release_dates</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=RELEASE_DATES)
@XStreamAlias(RELEASE_DATES)
public class ReleaseDates extends IdentityBean
    implements
        RealtimeBoundSpecification,
        PaginationSpecification,
        OrderBySpecification,
        SortOrderSpecification {

    private static final long serialVersionUID = 2905630043088967146L;

    static final String RELEASE_DATE_LIST_PROPERTY = "releaseDateList";

    @XStreamImplicit
    private List<ReleaseDate> releaseDateList = null;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStartDate = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEndDate = null;

    @XStreamAlias(Constants.SORT_ORDER)
    @XStreamAsAttribute
    private SortOrder sortOrder = SortOrder.asc;

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

    /**
     * Setter method for the sort order.
     */
    @Override
    public void setSortOrder(SortOrder sortOrder) {

        SortOrder oldValue = this.sortOrder;

        this.sortOrder = sortOrder;

        firePropertyChange(
            SORT_ORDER_PROPERTY, oldValue, sortOrder);
    }

    /**
     * Getter method for the sort order.
     */
    @Override
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * Getter method for the order-by value.
     */
    @Column(name=ORDER_BY_VALUE)
    @Override
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Setter method for the  order-by value.
     */
    @Override
    public void setOrderBy(OrderBy orderBy) {

        OrderBy oldValue = this.orderBy;

        this.orderBy = orderBy;

        firePropertyChange(
            ORDER_BY_PROPERTY, oldValue, orderBy);
    }

    /**
     * @see {@link PaginationSpecification#setLimit(long)}
     */
    @Override
    public void setLimit(long limit) {

        long oldValue = this.limit;

        this.limit = limit;

        firePropertyChange(
            LIMIT_PROPERTY, oldValue, limit);
    }

    /**
     * @see {@link PaginationSpecification#setOffset(int)}
     */
    @Override
    public void setOffset(int offset) {

        int oldValue = this.offset;

        this.offset = offset;

        firePropertyChange(
            OFFSET_PROPERTY, oldValue, offset);
    }

    /**
     * @see {@link PaginationSpecification#getLimit()}
     *
     * In the H2 database the limit is a reserved keyword so we are renaming
     * this, otherwise we'll see an exception along the lines of "Table
     * 'DEFAULTOBJECT' not found; SQL statement".
     */
    @Column(name=LIMIT_VALUE)
    @Override
    public long getLimit() {
        return limit;
    }

    /**
     * @see {@link PaginationSpecification#getOffset()}
     */
    @Column(name=OFFSET_VALUE)
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * Getter method for the list of {@link ReleaseDate} objects.
     */
    @OneToMany(cascade=CascadeType.ALL)
    public List<ReleaseDate> getReleaseDateList() {
        return releaseDateList;
    }

    /**
     * Setter method for the list of {@link ReleaseDate} objects.
     */
    public void setReleaseDateList(List<ReleaseDate> releaseDateList) {

        List<ReleaseDate> oldValue = this.releaseDateList;

        this.releaseDateList = releaseDateList;

        firePropertyChange(
            RELEASE_DATE_LIST_PROPERTY, oldValue, releaseDateList);
    }

    /**
     * Getter method for the count value.
     */
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

    /**
     * @see {@link RealtimeBoundSpecification#setRealtimeStart(Date)}
     */
    @Override
    public void setRealtimeStart(Date realtimeStartDate) {

        Date oldValue = this.realtimeStartDate;

        this.realtimeStartDate = clone (realtimeStartDate);

        firePropertyChange(
            REALTIME_START_PROPERTY, oldValue, realtimeStartDate);
    }

    /**
     * @see {@link RealtimeBoundSpecification#setRealtimeEnd(Date)}
     */
    @Override
    public void setRealtimeEnd(Date realtimeEndDate) {

        Date oldValue = this.realtimeEndDate;

        this.realtimeEndDate = clone (realtimeEndDate);

        firePropertyChange(REALTIME_END_PROPERTY, oldValue, realtimeEndDate);
    }

    /**
     * @see {@link RealtimeBoundSpecification#getRealtimeStart()}
     */
    @Override
    public Date getRealtimeStart() {
        return clone (realtimeStartDate);
    }

    /**
     * @see {@link RealtimeBoundSpecification#getRealtimeEnd()}
     */
    @Override
    public Date getRealtimeEnd() {
        return clone (realtimeEndDate);
    }
}
