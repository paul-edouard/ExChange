package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.CREATED_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.GROUP_ID_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.NAME_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.NOTES_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.POPULARITY_PROPERTY;
import static com.coherentlogic.fred.client.core.domain.PropertyNames.SERIES_COUNT_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.CREATED;
import static com.coherentlogic.fred.client.core.util.Constants.GROUP_ID;
import static com.coherentlogic.fred.client.core.util.Constants.NAME;
import static com.coherentlogic.fred.client.core.util.Constants.NOTES;
import static com.coherentlogic.fred.client.core.util.Constants.POPULARITY;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES_COUNT;
import static com.coherentlogic.fred.client.core.util.Constants.TAG;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A single tag entry.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=TAG)
@XStreamAlias(TAG)
public class Tag extends SerializableBean {

    private static final long serialVersionUID = -492197910607674506L;

    @XStreamAlias(NAME)
    @XStreamAsAttribute
    private String name = null;

    @XStreamAlias(GROUP_ID)
    @XStreamAsAttribute
    private String groupId = null;

    @XStreamAlias(NOTES)
    @XStreamAsAttribute
    private String notes = null;

    @XStreamAlias(CREATED)
    @XStreamAsAttribute
    private String created = null;

    @XStreamAlias(POPULARITY)
    @XStreamAsAttribute
    private Long popularity = null;

    @XStreamAlias(SERIES_COUNT)
    @XStreamAsAttribute
    private Long seriesCount = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {

        String oldValue = this.name;

        this.name = name;

        firePropertyChange(NAME_PROPERTY, oldValue, name);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {

        String oldValue = this.groupId;

        this.groupId = groupId;

        firePropertyChange(GROUP_ID_PROPERTY, oldValue, groupId);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {

        String oldValue = this.notes;

        this.notes = notes;

        firePropertyChange(NOTES_PROPERTY, oldValue, notes);
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {

        String oldValue = this.created;

        this.created = created;

        firePropertyChange(CREATED_PROPERTY, oldValue, created);
    }

    public Long getPopularity() {
        return popularity;
    }

    public void setPopularity(Long popularity) {

        Long oldValue = this.popularity;

        this.popularity = popularity;

        firePropertyChange(
            POPULARITY_PROPERTY,
            oldValue,
            popularity
        );
    }

    public Long getSeriesCount() {
        return seriesCount;
    }

    public void setSeriesCount(Long seriesCount) {

        Long oldValue = this.seriesCount;

        this.seriesCount = seriesCount;

        firePropertyChange(
            SERIES_COUNT_PROPERTY,
            oldValue,
            seriesCount
        );
    }
}
