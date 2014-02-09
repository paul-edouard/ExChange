package com.coherentlogic.fred.client.core.domain;

/**
 * An enumeration that indicates an output type.
 *
 * @see <a href="http://alfred.stlouisfed.org/help/downloaddata#outputformats">
 * outputformats</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum OutputType {

    observationsByRealTimePeriod (1),
    observationsByVintageDateAllObservations (2),
    observationsByVintageDateNewAndRevisedObservationsOnly (3),
    observationsInitialReleaseOnly (4);

    private final int value;

    OutputType (int value) {
        this.value = value;
    }

    @Override
    public String toString () {
        return "" + value;
    }
}
