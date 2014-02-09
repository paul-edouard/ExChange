package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.LINK_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.NAME_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.LINK;
import static com.coherentlogic.fred.client.core.util.Constants.NAME;
import static com.coherentlogic.fred.client.core.util.Constants.SOURCE;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.IdentityBean;
import com.coherentlogic.fred.client.core.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A class which represents a source of economic data.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_sources.html">
 * release_sources</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/sources.html">
 * sources</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/source.html">
 * source</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=SOURCE)
@XStreamAlias(SOURCE)
public class Source
    extends IdentityBean implements RealtimeBoundSpecification {

    private static final long serialVersionUID = 5030690758234873018L;

    /**
     * For example "2008-07-29".
     */
    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    /**
     * For example "2008-07-29".
     */
    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    /**
     * For example "Real Gross National Product".
     */
    @XStreamAlias(NAME)
    @XStreamAsAttribute
    private String name = null;

    @XStreamAlias(LINK)
    @XStreamAsAttribute
    private String link = null;

    /**
     * @see {@link RealtimeBoundSpecification#getRealtimeStart()}
     */
    @Override
    public Date getRealtimeStart () {
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
    public Date getRealtimeEnd () {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {

        String oldValue = this.name;

        this.name = name;

        firePropertyChange(
            NAME_PROPERTY,
            oldValue,
            name
        );
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {

        String oldValue = this.link;

        this.link = link;

        firePropertyChange(
            LINK_PROPERTY,
            oldValue,
            link
        );
    }
}
