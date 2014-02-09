package com.coherentlogic.fred.client.core.converters;

import com.coherentlogic.fred.client.core.domain.OrderBy;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * It turns out only SingleValueConverter(s) work for attributes.
 *
 * @todo Unit test this class.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class OrderByEnumConverter extends EnumSingleValueConverter {

    public OrderByEnumConverter(Class<? extends Enum<?>> type) {
        super(type);
    }

    @Override
    public Object fromString(String value) {

        OrderBy result = null;

        if (OrderBy.frequency.toString().equals(value))
            result = OrderBy.frequency;
        else if (OrderBy.lastUpdated.toString().equals(value))
            result = OrderBy.lastUpdated;
        else if (OrderBy.observationEnd.toString().equals(value))
            result = OrderBy.observationEnd;
        else if (OrderBy.observationStart.toString().equals(value))
            result = OrderBy.observationStart;
        else if (OrderBy.popularity.toString().equals(value))
            result = OrderBy.popularity;
        else if (OrderBy.realtimeEnd.toString().equals(value))
            result = OrderBy.realtimeEnd;
        else if (OrderBy.realtimeStart.toString().equals(value))
            result = OrderBy.realtimeStart;
        else if (OrderBy.searchRank.toString().equals(value))
            result = OrderBy.searchRank;
        else if (OrderBy.seasonalAdjustment.toString().equals(value))
            result = OrderBy.seasonalAdjustment;
        else if (OrderBy.seriesId.toString().equals(value))
            result = OrderBy.seriesId;
        else if (OrderBy.releaseId.toString().equals(value))
            result = OrderBy.releaseId;
        else if (OrderBy.title.toString().equals(value))
            result = OrderBy.title;
        else if (OrderBy.units.toString().equals(value))
            result = OrderBy.units;
        else if (OrderBy.sourceId.toString().equals(value))
            result = OrderBy.sourceId;
        else if (OrderBy.releaseDate.toString().equals(value))
            result = OrderBy.releaseDate;
        else if (OrderBy.vintageDate.toString().equals(value))
            result = OrderBy.vintageDate;
        else if (OrderBy.seriesCount.toString().equals(value))
            result = OrderBy.seriesCount;
        else
            throw new IllegalArgumentException(
                "The value " + value + " cannot be converted into an " +
                "instance of this enumeration (see the OrderBy enumeration " +
                "as this value is likely missing; the converter will also" +
                "likely need additional logic added as well).");

        return result;
    }
}
