package com.munch.exchange.model.core.neuralnetwork;


import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.impl.utils.Constants;
import org.goataa.spec.IObjectiveFunction;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import com.munch.exchange.model.core.optimization.ResultEntity;

public class NnObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 8646250762543992809L;
	private static Logger logger = Logger.getLogger(NnObjFunc.class);
	
	private NetworkArchitecture architecture;
	private NeuralNetwork network;
	private DataSet testSet;
	private double maxProfit;
	private double penalty;
	
	public NnObjFunc(NetworkArchitecture architecture, DataSet testSet/*,double maxProfit, double penalty*/){
		this.network=architecture.getNetwork();
		this.testSet=testSet;
		this.architecture=architecture;
		this.maxProfit=maxProfit;
		this.penalty=penalty;
	}
	
	@Override
	public double compute(double[] x, Random r) {
		
		//logger.info("Computing: "+Arrays.toString(x));
		
		network.setWeights(x);
		//logger.info("X input:" + Arrays.toString(x));
		
		double[] output = new double[testSet.getRows().size()];
		double[] desiredOutput = new double[testSet.getRows().size()];
		
		double[] outputError = new double[testSet.getRows().size()];
		double[] outputdiff=new double[testSet.getRows().size()];
		
		//Calculate the output for all the test data
		int pos=0;
		for(DataSetRow testSetRow : testSet.getRows()) {
			if(testSetRow.getInput().length!=network.getInputsCount()){
				logger.info("Size error: Test Row Size: "+testSetRow.getInput().length+", Network input: "+network.getInputsCount());
				continue;
			}
			 network.setInput(testSetRow.getInput());
			 network.calculate();
	         double[] networkOutput = network.getOutput();
	         
	         output[pos]=networkOutput[0];
	         desiredOutput[pos]=testSetRow.getDesiredOutput()[0];
	         
	         if(testSetRow instanceof NNDataSetRaw){
	        	 NNDataSetRaw row=(NNDataSetRaw) testSetRow;
	        	 outputdiff[pos]=row.getDiff()[0];
	         }
	         
	         pos++;
	          
	       }
		
		//Calculate the error
        for (int i = 0; i < output.length; i++) {
            outputError[i] = (desiredOutput[i] - output[i])*outputdiff[i];
        }
        
        //Calculate the output error Square Sum:
        double outputErrorSqrSum = 0;
        for (double error : outputError) {
            outputErrorSqrSum += (error * error) * 0.5; // a;so multiply with 1/trainingSetSize  1/2n * (...)
        }
        
        double error=outputErrorSqrSum/testSet.size();
        
        //Save the results
        ResultEntity ent=new ResultEntity(x,error);
        if( ent.getDoubleArray().length==0){
        	logger.info("X input:" + Arrays.toString(x));
        }
        
		architecture.addResultEntity(ent);
		//logger.info("Algorithm Best: "+ent);
		if(Double.isNaN(error) || Double.isInfinite(error)){
			logger.info("Archi: "+architecture);
			logger.info("Error: "+error);
			return Constants.WORST_FITNESS;
		}
		
		return error;
	}
	
	
	public double calError(double[] x){
		
		network.setWeights(x);
		//logger.info("X input:" + Arrays.toString(x));
		
		double[] output = new double[testSet.getRows().size()];
		double[] desiredOutput = new double[testSet.getRows().size()];
		
		double[] outputError = new double[testSet.getRows().size()];
		double[] outputdiff=new double[testSet.getRows().size()];
		
		//Calculate the output for all the test data
		int pos=0;
		for(DataSetRow testSetRow : testSet.getRows()) {
			if(testSetRow.getInput().length!=network.getInputsCount()){
				logger.info("Size error: Test Row Size: "+testSetRow.getInput().length+", Network input: "+network.getInputsCount());
				continue;
			}
			 network.setInput(testSetRow.getInput());
			 network.calculate();
	         double[] networkOutput = network.getOutput();
	         
	         output[pos]=networkOutput[0];
	         desiredOutput[pos]=testSetRow.getDesiredOutput()[0];
	         
	         if(testSetRow instanceof NNDataSetRaw){
	        	 NNDataSetRaw row=(NNDataSetRaw) testSetRow;
	        	 outputdiff[pos]=row.getDiff()[0];
	         }
	         
	         pos++;
	          
	       }
		
		
		//logger.info("DesiredOutput: "+Arrays.toString(desiredOutput));
		//logger.info("Output       : "+Arrays.toString(output));
		//logger.info("Outputdiff   : "+Arrays.toString(outputdiff));
		
		
		
		//Calculate the error
        for (int i = 0; i < output.length; i++) {
        	outputError[i] = (desiredOutput[i] - output[i]);
        }
        
        //logger.info("outputError  : "+Arrays.toString(outputError));
        
        for (int i = 0; i < output.length; i++) {
        	//outputError[i] = (desiredOutput[i] - output[i]);
            outputError[i] = (desiredOutput[i] - output[i])*outputdiff[i];
        }
        
        //Calculate the output error Square Sum:
        double outputErrorSqrSum = 0;
        for (double error : outputError) {
            outputErrorSqrSum += (error * error) * 0.5; // a;so multiply with 1/trainingSetSize  1/2n * (...)
        }
        
        logger.info("outputErrorSqrSum  : "+outputErrorSqrSum);
        
        double error=outputErrorSqrSum/testSet.size();
        
        logger.info("error  : "+error);
        
        //Save the results
        ResultEntity ent=new ResultEntity(x,error);
        if( ent.getDoubleArray().length==0){
        	logger.info("X input:" + Arrays.toString(x));
        }
        
		architecture.addResultEntity(ent);
		//logger.info("Algorithm Best: "+ent);
		if(Double.isNaN(error) || Double.isInfinite(error)){
			logger.info("Archi: "+architecture);
			logger.info("Error: "+error);
			return Constants.WORST_FITNESS;
		}
		
		return error;
		
	}
	
	
	
	

}
