package com.coherentlogic.fred.client.core.domain;

import java.io.InputStream;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;

/**
 * A domain class which represents the content of a response from a call to a
 * web service.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_observations.html">
 * series_observations</a>
 *
 * @TODO Should this be an entity?
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class Message extends SerializableBean {

    private static final long serialVersionUID = -5474386915074647931L;

    private transient final InputStream inputStream;

    public Message (InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getBody() {
        return inputStream;
    }
}
