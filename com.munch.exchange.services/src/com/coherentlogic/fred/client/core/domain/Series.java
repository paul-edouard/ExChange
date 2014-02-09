package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.FREQUENCY_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.FREQUENCY_SHORT_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.LAST_UPDATED_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.NOTES_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.POPULARITY_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.SEASONAL_ADJUSTMENT_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.SEASONAL_ADJUSTMENT_SHORT_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.TITLE_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.UNITS_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.UNITS_SHORT_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.FREQUENCY;
import static com.coherentlogic.fred.client.core.util.Constants.FREQUENCY_SHORT;
import static com.coherentlogic.fred.client.core.util.Constants.FREQUENCY_SHORT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.FREQUENCY_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.LAST_UPDATED;
import static com.coherentlogic.fred.client.core.util.Constants.NOTES;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATION_END;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATION_START;
import static com.coherentlogic.fred.client.core.util.Constants.POPULARITY;
import static com.coherentlogic.fred.client.core.util.Constants.SEASONAL_ADJUSTMENT;
import static com.coherentlogic.fred.client.core.util.Constants.SEASONAL_ADJUSTMENT_SHORT;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES;
import static com.coherentlogic.fred.client.core.util.Constants.TITLE;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS_SHORT;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS_SHORT_VALUE;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS_VALUE;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.IdentityBean;
import com.coherentlogic.fred.client.core.converters.PopularityConverter;
import com.coherentlogic.fred.client.core.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * A class which represents an economic data series.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series.html">series</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=SERIES)
@XStreamAlias(SERIES)
public class Series extends IdentityBean
    implements RealtimeBoundSpecification,
        ObservationBoundSpecification {

    private static final long serialVersionUID = -1977820595461889063L;

    @XStreamAlias(Constants.REALTIME_START)
    @XStreamAsAttribute
    private Date realtimeStart = null;

    @XStreamAlias(Constants.REALTIME_END)
    @XStreamAsAttribute
    private Date realtimeEnd = null;

    @XStreamAlias(TITLE)
    @XStreamAsAttribute
    private String title = null;

    @XStreamAlias(OBSERVATION_START)
    @XStreamAsAttribute
    private Date observationStart = null;

    @XStreamAlias(OBSERVATION_END)
    @XStreamAsAttribute
    private Date observationEnd = null;

    @XStreamAlias(FREQUENCY)
    @XStreamAsAttribute
    private String frequency = null;

    @XStreamAlias(FREQUENCY_SHORT)
    @XStreamAsAttribute
    private String frequencyShort = null;

    @XStreamAlias(UNITS)
    @XStreamAsAttribute
    private String units = null;

    @XStreamAlias(UNITS_SHORT)
    @XStreamAsAttribute
    private String unitsShort = null;

    @XStreamAlias(SEASONAL_ADJUSTMENT)
    @XStreamAsAttribute
    private String seasonalAdjustment = null;

    @XStreamAlias(SEASONAL_ADJUSTMENT_SHORT)
    @XStreamAsAttribute
    private String seasonalAdjustmentShort = null;

    @XStreamAlias(LAST_UPDATED)
    @XStreamAsAttribute
    private Date lastUpdated = null;

    @XStreamAlias(POPULARITY)
    @XStreamAsAttribute
    @XStreamConverter(PopularityConverter.class)
    private Integer popularity = null;

    @XStreamAlias(NOTES)
    @XStreamAsAttribute
    private String notes = null;

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

    /**
     * @todo Getter method for the title property.
     */
    public String getTitle () {
        return title;
    }

    /**
     * Setter method for the title property.
     */
    public void setTitle (String title) {

        String oldValue = this.title;

        this.title = title;

        firePropertyChange(
            TITLE_PROPERTY,
            oldValue,
            title
        );
    }

    /**
     * Getter method for the observation start date property.
     */
    public Date getObservationStart () {
        return clone (observationStart);
    }

    /**
     * Setter method for the observation start date property.
     */
    public void setObservationStart (Date observationStart) {

        Date oldValue = this.observationStart;

        this.observationStart = clone (observationStart);

        firePropertyChange(
            OBSERVATION_START_PROPERTY,
            oldValue,
            observationStart
        );
    }

    /**
     * Getter method for the observation end date property.
     */
    public Date getObservationEnd () {
        return clone (observationEnd);
    }

    /**
     * Setter method for the observation end date property.
     */
    public void setObservationEnd (Date observationEnd) {

        Date oldValue = this.observationEnd;

        this.observationEnd = clone (observationEnd);

        firePropertyChange(
            OBSERVATION_END_PROPERTY,
            oldValue,
            observationEnd
        );
    }

    /**
     * Getter method for the frequency property.
     *
     * @return The frequency as a string.
     *
     * @todo Should this be returning an enum?
     */
    @Column(name=FREQUENCY_VALUE)
    public String getFrequency () {
        return frequency;
    }

    /**
     * Setter method for the frequency property.
     *
     * @param frequency The frequency as a string.
     *
     * @todo Should this be using an enum? The answer is "no" as this is the
     *  long frequency description -- the short one should be changed to a
     *  Frequency type however there are values returned from FRED web services
     *  which haven't been mapped yet (ie. "W" and the long description is
     *  "Weekly, Ending Friday"). We may want to consider not processing the
     *  long/short frequencies and instead just create one Frequency data point,
     *  using the Frequency enum, and use that exclusively.
     */
    public void setFrequency (String frequency) {

        String oldValue = this.frequency;

        this.frequency = frequency;

        firePropertyChange(
            FREQUENCY_PROPERTY,
            oldValue,
            frequency
        );
    }

    /**
     * Getter method for the short frequency property.
     *
     * @return The short frequency as a string.
     *
     * @todo Should this be returning an enum?
     */
    @Column(name=FREQUENCY_SHORT_VALUE)
    public String getFrequencyShort () {
        return frequencyShort;
    }

    /**
     * Setter method for the short frequency property.
     */
    public void setFrequencyShort (String frequencyShort) {

        String oldValue = this.frequencyShort;

        this.frequencyShort = frequencyShort;

        firePropertyChange(
            FREQUENCY_SHORT_PROPERTY,
            oldValue,
            frequencyShort
        );
    }

    /**
     * Getter method for the units property.
     *
     * @return The units as a string.
     *
     * @todo Should this be returning an enum?
     */
    @Column(name=UNITS_VALUE)
    public String getUnits () {
        return units;
    }

    /**
     * Setter method for the units property.
     */
    public void setUnits (String units) {

        String oldValue = this.units;

        this.units = units;

        firePropertyChange(
            UNITS_PROPERTY,
            oldValue,
            units
        );
    }

    /**
     * Getter method for the short units property.
     *
     * @return The short units as a string.
     *
     * @todo Should this be returning an enum?
     */
    @Column(name=UNITS_SHORT_VALUE)
    public String getUnitsShort () {
        return unitsShort;
    }

    /**
     * Setter method for the short units property.
     */
    public void setUnitsShort (String unitsShort) {

        String oldValue = this.unitsShort;

        this.unitsShort = unitsShort;

        firePropertyChange(
            UNITS_SHORT_PROPERTY,
            oldValue,
            unitsShort
        );
    }

    /**
     * Getter method for the seasonal adjustment property.
     */
    public String getSeasonalAdjustment () {
        return seasonalAdjustment;
    }

    /**
     * Setter method for the seasonal adjustment property.
     */
    public void setSeasonalAdjustment (String seasonalAdjustment) {

        String oldValue = this.seasonalAdjustment;

        this.seasonalAdjustment = seasonalAdjustment;

        firePropertyChange(
            SEASONAL_ADJUSTMENT_PROPERTY,
            oldValue,
            seasonalAdjustment
        );
    }

    /**
     * Getter method for the short seasonal adjustment property.
     */
    public String getSeasonalAdjustmentShort () {
        return seasonalAdjustmentShort;
    }

    /**
     * Setter method for the short seasonal adjustment property.
     */
    public void setSeasonalAdjustmentShort (String seasonalAdjustmentShort) {

        String oldValue = this.seasonalAdjustmentShort;

        this.seasonalAdjustmentShort = seasonalAdjustmentShort;

        firePropertyChange(
            SEASONAL_ADJUSTMENT_SHORT_PROPERTY,
            oldValue,
            seasonalAdjustmentShort
        );
    }

    /**
     * Getter method for the last updated date property.
     */
    public Date getLastUpdated () {
        return clone (lastUpdated);
    }

    /**
     * Setter method for the last updated date property.
     */
    public void setLastUpdated (Date lastUpdated) {

        Date oldValue = this.lastUpdated;

        this.lastUpdated = clone (lastUpdated);

        firePropertyChange(
            LAST_UPDATED_PROPERTY,
            oldValue,
            lastUpdated
        );
    }

    /**
     * Getter method for the popularity property.
     */
    public Integer getPopularity () {
        return popularity;
    }

    /**
     * Setter method for the popularity property.
     */
    public void setPopularity (Integer popularity) {

        Integer oldValue = this.popularity;

        this.popularity = popularity;

        firePropertyChange(
            POPULARITY_PROPERTY,
            oldValue,
            popularity
        );
    }

    /**
     * Getter method for the notes property.
     */
    public String getNotes () {
        return notes;
    }

    /**
     * Setter method for the notes property.
     */
    public void setNotes (String notes) {

        String oldValue = this.notes;

        this.notes = notes;

        firePropertyChange(
            NOTES_PROPERTY,
            oldValue,
            notes
        );
    }
}
