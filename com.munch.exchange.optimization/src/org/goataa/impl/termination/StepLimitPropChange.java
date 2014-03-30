package org.goataa.impl.termination;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.goataa.impl.utils.Individual;

public class StepLimitPropChange<X> extends StepLimit {
	
	public static final String FIELD_STEP = "STEP";
	public static final String FIELD_BEST = "BEST";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Individual<double[], X> best;
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	public StepLimitPropChange(int steps) {
		super(steps);
		// TODO Auto-generated constructor stub
	}
	

	public Individual<double[], X> getBest() {
		return best;
	}


	public void setBest(Individual<double[], X> best) {
		System.out.println("New Best: "+best);
		changes.firePropertyChange(FIELD_BEST, this.best,
				this.best = best);
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
