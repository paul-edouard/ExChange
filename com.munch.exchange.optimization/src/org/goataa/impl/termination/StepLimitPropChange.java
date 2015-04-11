package org.goataa.impl.termination;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

import org.goataa.impl.algorithms.es.ESIndividual;
import org.goataa.impl.utils.Individual;

public class StepLimitPropChange<G,X> extends StepLimit {
	
	public static final String FIELD_STEP = "STEP";
	public static final String FIELD_BEST = "BEST";
	public static final String FIELD_POP = "POP";
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Individual<G, X> best;
	
	private ESIndividual<X>[] pop;
	
	private boolean isCancel=false;
	
	public StepLimitPropChange(int steps) {
		super(steps);
	}
	
	public void cancel(){
		isCancel=true;
	}
	

	public Individual<G, X> getBest() {
		return best;
	}


	public void setBest(Individual<G, X> best) {
		Individual<G, X> localBest=new Individual<G, X>();
		localBest.assign(best);
		
	
		changes.firePropertyChange(FIELD_BEST, this.best,
				this.best = localBest);
	}
	
	
	public ESIndividual<X>[] getPop() {
		return pop;
	}

	public void setPop(ESIndividual<X>[] pop){
		changes.firePropertyChange(FIELD_POP, this.pop,
				this.pop = pop);
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
