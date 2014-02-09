package com.coherentlogic.coherent.data.model.core.util;

/**
 * A helper class used to test when a {@link java.beans.PropertyChangeEvent} has
 * been fired from a {@link java.beans.PropertyChangeListener}.
 *
 * @todo Note that this class is shared with the FRED and WB Client projects for
 *  testing purposes and this class should ideally be in the test directory,
 *  however if we include it in the test directory in this project, projects
 *  that reference this will not see it since it will be removed when the
 *  project is built. 
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class Flag {
    private boolean value = false;

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
