package com.coherentlogic.fred.client.core.converters;

import com.coherentlogic.fred.client.core.domain.OutputType;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * It turns out only SingleValueConverter(s) work for attributes.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class OutputTypeEnumConverter extends EnumSingleValueConverter {

    public OutputTypeEnumConverter(Class<? extends Enum<?>> type) {
        super(type);
    }

    @Override
    public Object fromString(String value) {

        OutputType result = null;

        if (OutputType.observationsByRealTimePeriod.toString().equals(value))
            result = OutputType.observationsByRealTimePeriod;
        else if (OutputType
            .observationsByVintageDateAllObservations
            .toString()
            .equals(value))
            result = OutputType.observationsByVintageDateAllObservations;
        else if (
            OutputType
                .observationsByVintageDateNewAndRevisedObservationsOnly
                .toString()
                .equals(value))
            result = OutputType
                .observationsByVintageDateNewAndRevisedObservationsOnly;
        else if (
            OutputType
                .observationsInitialReleaseOnly
                .toString()
                .equals(value))
            result = OutputType.observationsInitialReleaseOnly;
        else
            throw new IllegalArgumentException(
                "The value " + value + " cannot be converted into an " +
                "instance of this enumeration.");

        return result;
    }
}
