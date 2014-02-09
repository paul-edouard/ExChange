package com.coherentlogic.coherent.data.model.core.domain;

import static com.coherentlogic.coherent.data.model.core.util.Constants.ID;
import static com.coherentlogic.coherent.data.model.core.util.Constants.IDENTITY_BEAN;
import static com.coherentlogic.coherent.data.model.core.util.Constants.IDENTITY_VALUE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A bean that has an id property and associated getter and setter methods.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=IDENTITY_BEAN)
public class IdentityBean extends SerializableBean
    implements IdentitySpecification<String> {

    private static final long serialVersionUID = 6114953858586102298L;

	@XStreamAlias(ID)
    @XStreamAsAttribute
    private String id = null;

    @Column(name=IDENTITY_VALUE)
    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }
}
