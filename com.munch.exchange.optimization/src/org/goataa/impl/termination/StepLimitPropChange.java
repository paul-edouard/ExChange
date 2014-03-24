package org.goataa.impl.termination;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StepLimitPropChange extends StepLimit {
	
	public static final String FIELD_STEP = "STEP";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	public StepLimitPropChange(int steps) {
		super(steps);
		// TODO Auto-generated constructor stub
	}
	
	
	

	@Override
	public boolean terminationCriterion() {
		boolean isTerminated=super.terminationCriterion();
		changes.firePropertyChange(FIELD_STEP, this.remaining+1, this.remaining);
		return isTerminated;
	}




	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
