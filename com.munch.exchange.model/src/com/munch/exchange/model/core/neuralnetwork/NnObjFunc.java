package com.munch.exchange.model.core.neuralnetwork;


import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.neuroph.core.NeuralNetwork;

public class NnObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 8646250762543992809L;
	private static Logger logger = Logger.getLogger(NnObjFunc.class);
	
	private NeuralNetwork network;
	
	public NnObjFunc(NeuralNetwork network){
		this.network=network;
	}
	
	@Override
	public double compute(double[] x, Random r) {
		network.setWeights(x);
		network.calculate();
		return network.getOutput()[0];
	}

}
