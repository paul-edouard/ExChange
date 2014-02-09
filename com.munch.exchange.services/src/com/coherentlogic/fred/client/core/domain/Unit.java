package com.coherentlogic.fred.client.core.domain;

/**
 * A key that indicates a data value transformation.
 *
 * @see <a href="http://alfred.stlouisfed.org/help#growth_formulas">
 * growth_formulas</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum Unit {

    /**
     * Levels (No transformation)
     */
    lin,

    /**
     * Change
     */
    chg,

    /**
     * Change from Year Ago
     */
    ch1,

    /**
     * Percent Change
     */
    pch,

    /**
     * Percent Change from Year Ago
     */
    pc1,

    /**
     * Compounded Annual Rate of Change
     */
    pca,

    /**
     * Continuously Compounded Rate of Change
     */
    cch,

    /**
     * Continuously Compounded Annual Rate of Change
     */
    cca,

    /**
     * Natural Log
     */
    log;
}
