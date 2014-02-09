package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.LIMIT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.OFFSET_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASES;

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
 * A class which represents the releases of an economic data series.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases.html">releases</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release.html">release</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_release.html">
 * series_release</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/source_releases.html">
 * source_releases</a>
 * @see <a href="http://research.stlouisfed.org/fred2/releases/">releases</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=RELEASES)
@XStreamAlias(RELEASES)
public class Releases extends SerializableBean
    implements
        RealtimeBoundSpecification,
        OrderBySpecification,
        SortOrderSpecification,
        PaginationSpecification {

    private static final long serialVersionUID = -1490415291101451411L;

    static final String RELEASE_LIST_PROPERTY = "releaseList";

    @XStreamImplicit
    private List<Release> releaseList = null;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(COUNT)
    @XStreamAsAttribute
    private int count = 0;

    @XStreamAlias(Constants.LIMIT)
    @XStreamAsAttribute
    private long limit = 0;

    @XStreamAlias(Constants.OFFSET)
    @XStreamAsAttribute
    private int offset = 0;

    @XStreamAlias(Constants.SORT_ORDER)
    @XStreamAsAttribute
    private SortOrder sortOrder = null;

    @XStreamAlias(Constants.ORDER_BY)
    @XStreamAsAttribute
    private OrderBy orderBy = null;

    @OneToMany(cascade=CascadeType.ALL)
    public List<Release> getReleaseList () {
        return releaseList;
    }

    public void setReleaseList (List<Release> releaseList) {

        List<Release> oldValue = this.releaseList;

        this.releaseList = releaseList;

        firePropertyChange(RELEASE_LIST_PROPERTY, oldValue, releaseList);
    }

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
}
