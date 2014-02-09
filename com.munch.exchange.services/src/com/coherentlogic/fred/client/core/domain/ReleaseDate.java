package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.DATE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_DATE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_DATE_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_ID;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_NAME;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * A class which represents a release date for all releases of economic data. 
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases_dates.html">
 * releases_dates</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_dates.html">
 * release_dates</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=RELEASE_DATE)
@XStreamAlias(RELEASE_DATE)
@XStreamConverter(
    value=ToAttributedValueConverter.class,
    strings={DATE})
public class ReleaseDate extends SerializableBean {

    private static final long serialVersionUID = -6232704249359164028L;

    static final String RELEASE_NAME_PROPERTY = "releaseName",
        RELEASE_ID_PROPERTY = "releaseId",
        DATE_PROPERTY = "date";

    @XStreamAlias(RELEASE_ID)
    @XStreamAsAttribute
    private Long releaseId = null;

    @XStreamAlias(RELEASE_NAME)
    @XStreamAsAttribute
    private String releaseName = null;

    /**
     * This is not an attribute -- instead the date appears in the body of the
     * XML element and we need a converter to manage this -- see the
     * ToAttributedValueConverter above, as this handles the conversion.
     */
    private Date date = null;

    /**
     * Getter method for the release name.
     */
    public String getReleaseName() {
        return releaseName;
    }

    /**
     * Setter method for the release name.
     */
    public void setReleaseName(String releaseName) {

        String oldValue = this.releaseName;

        this.releaseName = releaseName;

        firePropertyChange(RELEASE_NAME_PROPERTY, oldValue, releaseName);
    }

    /**
     * Getter method for the release id.
     */
    public Long getReleaseId() {
        return releaseId;
    }

    /**
     * Setter method for the release id.
     */
    public void setReleaseId(Long releaseId) {

        Long oldValue = this.releaseId;

        this.releaseId = releaseId;

        firePropertyChange(RELEASE_ID_PROPERTY, oldValue, releaseId);
    }

    /**
     * Getter method for the release date.
     */
    @Column(name=RELEASE_DATE_VALUE)
    public Date getDate() {
        return clone (date);
    }

    /**
     * Setter method for the release date.
     */
    public void setDate(Date date) {

        Date oldValue = this.date;

        this.date = clone (date);

        firePropertyChange(DATE_PROPERTY, oldValue, date);
    }
}
