package org.neuroph.nnet.learning.financial;

import org.neuroph.nnet.learning.MomentumBackpropagation;

public class FinancialMomentumBackpropagation extends MomentumBackpropagation implements FinancialLearning{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6208855103863070082L;
	
	
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
