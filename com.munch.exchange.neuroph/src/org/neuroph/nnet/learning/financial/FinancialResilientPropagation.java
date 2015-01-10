package org.neuroph.nnet.learning.financial;

import org.neuroph.nnet.learning.ResilientPropagation;

public class FinancialResilientPropagation extends ResilientPropagation
		implements FinancialLearning {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5024635739582291189L;
	
	private double[] desiredOutput		=	null;
	private double[] diffFactorArray	=	null;
	private double[] startVal			= 	null;
	private double[] endVal				= 	null;
	
	
	@Override
	public void setArrays(double[] desiredOutput, double[] diffFactorArray, double[] startVal, double[] endVal) {
		this.desiredOutput=desiredOutput;
		this.diffFactorArray=diffFactorArray;
		this.startVal=startVal;
		this.endVal=endVal;
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		this.setErrorFunction(new FinancialProfitError(getTrainingSet().size(),
				desiredOutput,diffFactorArray,startVal,endVal) );
		
	}

	
	
	
	
	
	

}
