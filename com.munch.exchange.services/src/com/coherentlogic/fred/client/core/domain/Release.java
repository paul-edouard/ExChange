package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.LINK_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.NAME_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.PRESS_RELEASE_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.ID;
import static com.coherentlogic.fred.client.core.util.Constants.IDENTITY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.LINK;
import static com.coherentlogic.fred.client.core.util.Constants.NAME;
import static com.coherentlogic.fred.client.core.util.Constants.PRESS_RELEASE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_TABLE;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.IdentitySpecification;
import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.coherentlogic.fred.client.core.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A class which represents a release of an economic data series.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases.html">releases</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release.html">release</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_release.html">
 * series_release</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/source_releases.html">
 * source_releases</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity//@Entity(name=RELEASE_TABLE) NOT SURE WE NEED THE TABLE NAME AT THIS
// TIME, HOWEVER IT WILL STAY HERE AS A HINT IF THE TEST FAILS WHEN WORKING
// WITH MYSQL.
@Table(name=RELEASE_TABLE)
@XStreamAlias(RELEASE)
public class Release extends SerializableBean
    implements IdentitySpecification<Long>, RealtimeBoundSpecification {

    private static final long serialVersionUID = -7960368525706991411L;

    @XStreamAlias(ID)
    @XStreamAsAttribute
    private Long id;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(NAME)
    @XStreamAsAttribute
    private String name = null;

    @XStreamAlias(PRESS_RELEASE)
    @XStreamAsAttribute
    private boolean pressRelease = false;

    @XStreamAlias(LINK)
    @XStreamAsAttribute
    private String link = null;

    /**
     * Getter method for the identity value.
     *
     * Note that this is the primary key value and is only set after an instance
     * of this object has been persisted to/from a database.
     */
    @Column(name=IDENTITY_VALUE)
    @Override
    public Long getId() {
        return id;
    }
    /**
     * Setter method for the identity value.
     */
    @Override
    public void setId(Long id) {
        this.id = id;
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

    /**
     * Getter method for the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for the name.
     */
    public void setName(String name) {

        String oldValue = this.name;

        this.name = name;

        firePropertyChange(NAME_PROPERTY, oldValue, name);
    }

    /**
     * Getter method for the press release flag.
     */
    public boolean isPressRelease() {
        return pressRelease;
    }

    /**
     * Setter method for the press release flag.
     */
    public void setPressRelease(boolean pressRelease) {
        boolean oldValue = this.pressRelease;

        this.pressRelease = pressRelease;

        firePropertyChange(
            PRESS_RELEASE_PROPERTY, oldValue, pressRelease);
    }

    /**
     * Getter method for the link value -- for example
     * "http://www.federalreserve.gov/releases/h6/".
     */
    public String getLink() {
        return link;
    }

    /**
     * Setter method for the link value.
     */
    public void setLink(String link) {
        String oldValue = this.link;

        this.link = link;

        firePropertyChange(LINK_PROPERTY, oldValue, link);
    }
}
