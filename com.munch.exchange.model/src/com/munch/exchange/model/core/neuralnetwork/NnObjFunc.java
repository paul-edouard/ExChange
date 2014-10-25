package com.munch.exchange.model.core.neuralnetwork;


import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

public class NnObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 8646250762543992809L;
	private static Logger logger = Logger.getLogger(NnObjFunc.class);
	
	private NeuralNetwork network;
	private DataSet testSet;
	
	public NnObjFunc(NeuralNetwork network, DataSet testSet){
		this.network=network;
		this.testSet=testSet;
	}
	
	@Override
	public double compute(double[] x, Random r) {
		
		logger.info("Computing: "+Arrays.toString(x));
		
		network.setWeights(x);
		
		double[] output = new double[testSet.getRows().size()];
		double[] desiredOutput = new double[testSet.getRows().size()];
		
		double[] outputError = new double[testSet.getRows().size()];
		
		
		//Calculate the output for all the test data
		int pos=0;
		for(DataSetRow testSetRow : testSet.getRows()) {
			 network.setInput(testSetRow.getInput());
			 network.calculate();
	         double[] networkOutput = network.getOutput();
	         
	         output[pos]=networkOutput[0];
	         desiredOutput[pos]=testSetRow.getDesiredOutput()[0];
	         
	         pos++;
	          
	       }
		
		//Calculate the error
        for (int i = 0; i < output.length; i++) {
            outputError[i] = desiredOutput[i] - output[i];
        }
        
        //Calculate the output error Square Sum:
        double outputErrorSqrSum = 0;
        for (double error : outputError) {
            outputErrorSqrSum += (error * error) * 0.5; // a;so multiply with 1/trainingSetSize  1/2n * (...)
        }
        
        logger.info("Error: "+outputErrorSqrSum);
		
		return outputErrorSqrSum;
	}

}
