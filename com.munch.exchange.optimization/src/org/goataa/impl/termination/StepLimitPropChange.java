package org.goataa.impl.termination;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.goataa.impl.utils.Individual;

public class StepLimitPropChange<X> extends StepLimit {
	
	public static final String FIELD_STEP = "STEP";
	public static final String FIELD_BEST = "BEST";
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Individual<double[], X> best;
	
	private boolean isCancel=false;
	
	public StepLimitPropChange(int steps) {
		super(steps);
	}
	
	public void cancel(){
		isCancel=true;
	}
	

	public Individual<double[], X> getBest() {
		return best;
	}


	public void setBest(Individual<double[], X> best) {
		Individual<double[], X> localBest=new Individual<double[], X>();
		localBest.assign(best);
		
	
		changes.firePropertyChange(FIELD_BEST, this.best,
				this.best = localBest);
	}


	@Override
	public boolean terminationCriterion() {
		
		boolean isTerminated=super.terminationCriterion();
		changes.firePropertyChange(FIELD_STEP, this.remaining+1, this.remaining);
		
		if(isCancel)return true;
		return isTerminated;
	}


	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
