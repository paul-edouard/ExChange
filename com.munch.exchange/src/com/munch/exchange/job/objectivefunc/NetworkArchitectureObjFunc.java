package com.munch.exchange.job.objectivefunc;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.goataa.impl.OptimizationModule;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;

import com.munch.exchange.job.Optimizer;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class NetworkArchitectureObjFunc<X> extends OptimizationModule implements
		IObjectiveFunction<boolean[]> {
			
					
	/**
	 * 
	 */
	private static final long serialVersionUID = 8630727938986919169L;
	
	
	private static Logger logger = Logger.getLogger(NetworkArchitectureObjFunc.class);
	
	private Configuration configuration;
	
	private IEventBroker eventBroker;
	private List<Individual<double[], X>>  solutions;

	ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm;
	
	StepLimitPropChange<X> term;
	
	@Override
	public double compute(boolean[] x, Random r) {
		
		NetworkArchitecture architecture=configuration.searchArchitecture(x);
		
		
		// TODO Auto-generated method stub
		return 0;
	}

}
