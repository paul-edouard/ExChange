package com.munch.exchange.job;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.ISOOptimizationAlgorithm;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;

public class Optimizer<X> extends Job {
	
	private static Logger logger = Logger.getLogger(Optimizer.class);

	private IEventBroker eventBroker;
	
	private ExchangeRate rate;
	private OptimizationInfo info;
	private List<Individual<double[], X>>  solutions;
	
	ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm;
	
	StepLimitPropChange<X> term;
	
	
	public Optimizer() {
		super("Optimizer");
		setSystem(true);
		setPriority(SHORT);
		
	}
	
	public void initOptimizationInfo(IEventBroker eventBroker,OptimizationType type,ExchangeRate rate,ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm, StepLimitPropChange<X> term ){
		
		this.algorithm=algorithm;
		this.term=term;
		this.rate=rate;
		this.eventBroker=eventBroker;
		
		this.info=new OptimizationInfo(this.rate,type,this.term.getMaxSteps());
	}
	

	public List<Individual<double[], X>> getSolutions() {
		return solutions;
	}
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		eventBroker.post(IEventConstant.OPTIMIZATION_STARTED,info);
		
		if(term==null){
			logger.error("Termination Criterion is not initialized!");
			eventBroker.send(IEventConstant.OPTIMIZATION_FINISHED,info);
			return Status.CANCEL_STATUS;
		}
			
		if(algorithm==null){
			logger.error("Algothm is not initialized!");
			eventBroker.send(IEventConstant.OPTIMIZATION_FINISHED,info);
			return Status.CANCEL_STATUS;
		}
		
		if(info==null){
			logger.error("Optimization Info is not initialized!");
			eventBroker.send(IEventConstant.OPTIMIZATION_FINISHED,info);
			return Status.CANCEL_STATUS;
		}
		
		
		//Create and add the listener
		TerminationPropertyChangeListener listener=new TerminationPropertyChangeListener(monitor);
		term.addPropertyChangeListener(listener);
		
		//Start the Optimization
		solutions=algorithm.call();
		
		//Remove the listener
		//term.removePropertyChangeListener(listener);
		eventBroker.send(IEventConstant.OPTIMIZATION_FINISHED,info);
		logger.info("Optimization finished!!");
		return Status.OK_STATUS;
	}
	
	
	
	private class TerminationPropertyChangeListener implements PropertyChangeListener{
		IProgressMonitor monitor;
		
		public TerminationPropertyChangeListener(IProgressMonitor monitor){
			this.monitor=monitor;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_BEST)){
				info.setBest((Individual<double[], X>) evt.getNewValue());
				eventBroker.send(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL,info);
			}
			else if(evt.getPropertyName().equals(StepLimitPropChange.FIELD_STEP)){
				int val=(int)evt.getNewValue();
				if(val%10==0){
					info.setStep((int)evt.getNewValue());
					eventBroker.post(IEventConstant.OPTIMIZATION_NEW_STEP,info);
				}
			}
			// Cancel called
			if (monitor.isCanceled()){
				term.cancel();
				eventBroker.send(IEventConstant.OPTIMIZATION_FINISHED,info);
			}
			
		}
		
		
	}
	
	/**
	 * to Stop the job in a safty way
	 * quoteLoader.cancel();
		quoteLoader.join();
		
		
		add the line 
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
	 */
	
	public enum OptimizationType { MOVING_AVERAGE, MACD, NONE};
	
	public static String OptimizationTypeToString(OptimizationType type){
		switch (type) {
		case MOVING_AVERAGE:
			return "MOVING_AVERAGE";
		case MACD:
			return "MACD";

		default:
			return "NONE";
		}
	}
	
	public static OptimizationType stringToOptimizationType(String type){
		if(type.equals("MOVING_AVERAGE"))
			return OptimizationType.MOVING_AVERAGE;
		else if(type.equals("MACD")){
			return OptimizationType.MACD;
		}
		
		return OptimizationType.NONE;
		
	}
	
	public class OptimizationInfo{
		
		private ExchangeRate rate;
		private OptimizationType type;
		private int step;
		private int maximum;
		private Individual<double[], X> best;
		
		public OptimizationInfo(ExchangeRate rate, OptimizationType type, int maximum) {
			super();
			this.rate = rate;
			this.type = type;
			this.step=maximum;
			this.maximum=maximum;
		}
		
		public int getStep() {
			return step;
		}
		public void setStep(int step) {
			this.step = step;
		}
		public Individual<double[], X> getBest() {
			return best;
		}
		public void setBest(Individual<double[], X> best) {
			this.best = best;
		}
		public int getMaximum() {
			return maximum;
		}
		public void setMaximum(int maximum) {
			this.maximum = maximum;
		}

		public ExchangeRate getRate() {
			return rate;
		}

		public OptimizationType getType() {
			return type;
		}
		
		
		
	}
	
	
}
