package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.VINTAGE_DATE;
import static com.coherentlogic.fred.client.core.util.Constants.VINTAGE_DATE_ALT;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * A class which represents a date in history when a series' data values were
 * revised or new data values were released.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_vintagedates.html">
 * series_vintagedates</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=VINTAGE_DATE)
@XStreamAlias(VINTAGE_DATE)
@XStreamConverter(
    value=ToAttributedValueConverter.class, strings={VINTAGE_DATE_ALT})
public class VintageDate extends SerializableBean {

    private static final long serialVersionUID = 1276255845590512472L;

    static final String VINTAGE_DATE = "vintageDate";

    private Date vintageDate = null;

    public Date getVintageDate() {
        return clone (vintageDate);
    }

    public void setVintageDate(Date vintageDate) {

        Date oldValue = this.vintageDate;

        this.vintageDate = clone (vintageDate);

        firePropertyChange(VINTAGE_DATE, oldValue, vintageDate);
    }
}
