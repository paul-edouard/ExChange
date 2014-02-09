package com.coherentlogic.fred.client.core.converters;

import com.coherentlogic.fred.client.core.domain.FilterValue;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * It turns out only SingleValueConverter(s) work for attributes.
 * 
 * @author <a href="support@coherentlogic.com">Support</a>
 *
 */
public class FilterValueEnumConverter extends EnumSingleValueConverter {

    public FilterValueEnumConverter(Class<? extends Enum<?>> type) {
        super(type);
    }

    @Override
    public Object fromString(String value) {

        FilterValue result = null;

        if (FilterValue.all.toString().equals(value))
            result = FilterValue.all;
        else if (FilterValue.macro.toString().equals(value))
            result = FilterValue.macro;
        else if (FilterValue.regional.toString().equals(value))
            result = FilterValue.regional;
        else
            throw new IllegalArgumentException(
                "The value " + value + " cannot be converted into an " +
                "instance of this enumeration.");

        return result;
    }
}
