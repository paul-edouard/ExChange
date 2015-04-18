package com.munch.exchange.model.core.neuralnetwork;

import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.impl.utils.Constants;
import org.goataa.spec.IObjectiveFunction;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.learning.error.ErrorFunction;
import org.neuroph.nnet.learning.financial.FinancialProfitError;

import com.munch.exchange.model.core.optimization.ResultEntity;

public class NnRegularizationObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {

			
	private static final long serialVersionUID = 8646250762543992809L;
	private static Logger logger = Logger.getLogger(NnObjFunc.class);
			
	private NetworkArchitecture architecture;
	private NeuralNetwork network;
	private DataSet testSet;
	
	private ErrorFunction errorFunction;
			
	
	public NnRegularizationObjFunc(NetworkArchitecture architecture, DataSet testSet/*,double maxProfit, double penalty*/){
		//this.network=architecture.getNetwork();
		this.testSet=testSet;
		this.architecture=architecture;
		network=this.architecture.getCopyOfFaMeNetwork();
		
	}
	
	@Override
	public double compute(double[] x, Random r) {
		 double error=calculateError(x,r);
	        
	        //Save the results
	        ResultEntity ent=new ResultEntity(x,error);
	        ent.setParam(ResultEntity.GENERATED_FROM, "Optimization");
	        ent.setId(ent.getId()+", Optimization");
	        if( ent.getDoubleArray().length==0){
	        	logger.info("X input:" + Arrays.toString(x));
	        }
	        
			architecture.addRegularizationResultEntity(ent);
			//logger.info("Algorithm Best: "+ent);
			if(Double.isNaN(error) || Double.isInfinite(error)){
				logger.info("Archi: "+architecture);
				logger.info("Error: "+error);
				return Constants.WORST_FITNESS;
			}
			
			return error;
	}
	
	
	public double calculateError(double[] w, Random r){
		
		NetworkArchitecture.setNewRandomValueOfFaMeNeurons(network);
		
		network.setWeights(w);
		
		double[][] outputs=NetworkArchitecture.calculateOutputs(network,testSet);
		
		double[] output=outputs[0];
		double[] desiredOutput=outputs[1];
		double[] outputdiff=outputs[2];
		double[] startVal=outputs[3];
		double[] endVal=outputs[4];
		
		
		//Create the error function
		errorFunction=new FinancialProfitError(outputdiff.length,desiredOutput, outputdiff,
				startVal,endVal);
		
		//Calculate the error
        for (int i = 0; i < output.length; i++) {
        	double[] outputError={desiredOutput[i] - output[i]};
        	errorFunction.addOutputError(outputError);
            //outputError[i] = (desiredOutput[i] - output[i])*outputdiff[i];
        }
        double error=errorFunction.getTotalError();
        
        return error;
	}
	

}
