package com.coherentlogic.fred.client.core.converters;

import com.thoughtworks.xstream.converters.basic.IntConverter;

/**
 * The value of popularity can be "" and hence we need to treat this conversion
 * as a special case, since the IntConverter will throw an exception if an empty
 * string is passed to it.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class PopularityConverter extends IntConverter {

    @Override
    public Object fromString(String value) {

        Object result = null;

        if (value != null && !("".equals(value)))
            result = super.fromString(value);

        return result;
    }
}
