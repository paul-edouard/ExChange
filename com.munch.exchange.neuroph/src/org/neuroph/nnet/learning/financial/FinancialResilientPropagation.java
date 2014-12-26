package org.neuroph.nnet.learning.financial;

import org.neuroph.nnet.learning.ResilientPropagation;

public class FinancialResilientPropagation extends ResilientPropagation
		implements FinancialLearning {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5024635739582291189L;

	private double[] diffFactorArray=null;
	
	@Override
	protected double[] calculateOutputError(double[] desiredOutput,
			double[] output) {
		// TODO Auto-generated method stub
		if(diffFactorArray==null || diffFactorArray.length!=desiredOutput.length){
			return super.calculateOutputError(desiredOutput, output);
		}
		
		double[] outputError = new double[desiredOutput.length];
	        
	    for (int i = 0; i < output.length; i++) {
	            outputError[i] = (desiredOutput[i] - output[i])*diffFactorArray[i];
	     }
	        
	    return outputError;
		
	}

	@Override
	public void setDiffFactorArray(double[] diffFactorArray) {
		this.diffFactorArray=diffFactorArray;
	}

}
