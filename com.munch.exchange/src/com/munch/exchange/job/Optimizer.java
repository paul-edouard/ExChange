package com.munch.exchange.job;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.ISOOptimizationAlgorithm;

import com.munch.exchange.model.core.ExchangeRate;

public class Optimizer<X> extends Job {
	
	private static Logger logger = Logger.getLogger(Optimizer.class);
	
	@Inject
	ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm;
	
	@Inject
	StepLimitPropChange<X> term;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	public Optimizer(ExchangeRate rate ) {
		super("Optimization of "+rate.getFullName());
		setSystem(true);
		setPriority(SHORT);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if(term==null){
			logger.error("Termination Criterion is not initialized!");
			return Status.CANCEL_STATUS;
		}
			
		if(algorithm==null){
			logger.error("Algothm is not initialized!");
			return Status.CANCEL_STATUS;
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * to Stop the job in a safty way
	 * quoteLoader.cancel();
		quoteLoader.join();
		
		
		add the line 
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
	 */

}
