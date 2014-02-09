package com.coherentlogic.fred.client.core.converters;

import com.coherentlogic.fred.client.core.domain.SortOrder;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * It turns out only SingleValueConverter(s) work for attributes.
 * 
 * @author <a href="support@coherentlogic.com">Support</a>
 *
 */
public class SortOrderEnumConverter extends EnumSingleValueConverter {

    public SortOrderEnumConverter(Class<? extends Enum<?>> type) {
        super(type);
    }

    @Override
    public Object fromString(String value) {

        SortOrder result = null;

        if (SortOrder.asc.toString().equals(value))
            result = SortOrder.asc;
        else if (SortOrder.desc.toString().equals(value))
            result = SortOrder.desc;
        else
            throw new IllegalArgumentException(
                "The value " + value + " cannot be converted into an " +
                "instance of this enumeration.");

        return result;
    }
}
