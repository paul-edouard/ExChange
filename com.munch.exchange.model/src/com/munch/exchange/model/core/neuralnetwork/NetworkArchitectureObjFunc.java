package com.munch.exchange.model.core.neuralnetwork;

import java.util.Random;

import org.goataa.impl.OptimizationModule;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;

public class NetworkArchitectureObjFunc<X> extends OptimizationModule implements
		IObjectiveFunction<boolean[]> {
			
					
	/**
	 * 
	 */
	private static final long serialVersionUID = 8630727938986919169L;

	ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm;
	
	
	
	@Override
	public double compute(boolean[] x, Random r) {
		// TODO Auto-generated method stub
		return 0;
	}

}
