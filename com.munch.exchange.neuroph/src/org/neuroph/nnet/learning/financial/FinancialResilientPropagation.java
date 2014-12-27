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
	public void setDiffFactorArray(double[] diffFactorArray) {
		this.diffFactorArray=diffFactorArray;
		
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		this.setErrorFunction(new FinancialMeanSquaredError(getTrainingSet().size(),diffFactorArray) );
		
	}

	
	
	
	
	
	

}
