package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.DEFAULT_LIMIT;
import static com.coherentlogic.fred.client.core.util.Constants.ORDER_BY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.TAGS;

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
 * FRED tags are attributes assigned to series. See the related request
 * <a href="http://api.stlouisfed.org/docs/fred/related_tags.html">
 * fred/related_tags</a>. 
 *
 * @todo Consider creating a CountableSpecification as the count property is
 *  repeated in a few places.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=TAGS)
@XStreamAlias(TAGS)
public class Tags extends SerializableBean
    implements
        RealtimeBoundSpecification,
        PaginationSpecification,
        OrderBySpecification,
        SortOrderSpecification {

    private static final long serialVersionUID = 49355614287651169L;

    static final String TAG_LIST_PROPERTY = "tagList";

    @XStreamAlias(COUNT)
    @XStreamAsAttribute
    private int count = 0;

    @XStreamImplicit
    private List<Tag> tagList = null;

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

    /**
     * @todo Make this value a constant.
     */
    @XStreamAlias(Constants.LIMIT)
    @XStreamAsAttribute
    private long limit = DEFAULT_LIMIT;

    @XStreamAlias(Constants.OFFSET)
    @XStreamAsAttribute
    private int offset = 0;

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
     * Getter method for the count value.
     */
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
     * Getter method for the list of {@link Tag} objects.
     */
    @OneToMany(cascade=CascadeType.ALL)
    public List<Tag> getTagList() {
        return tagList;
    }

    /**
     * Setter method for the list of {@link Tag} objects.
     */
    public void setTagList(List<Tag> tagList) {

        List<Tag> oldValue = this.tagList;

        this.tagList = tagList;

        firePropertyChange(TAG_LIST_PROPERTY, oldValue, tagList);
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

    public long getLimit() {
        return limit;
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

    public int getOffset() {
        return offset;
    }
}
