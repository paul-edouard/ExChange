package org.neuroph.nnet.learning.financial;

import java.io.Serializable;

import org.neuroph.core.learning.error.ErrorFunction;

import com.munch.exchange.utils.ProfitUtils;


public class FinancialProfitError implements ErrorFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9108253958530545173L;
	
	//private transient double totalErrorSum;
	//private double buySellLimit=0.5;
	private transient double n;
	private transient double[] output = null;
	private transient double[] desiredOutput = null;
	private transient double[] diffFactorArray = null;
	private transient double[] changes = null;
	//private transient double[] startVal = null;
	//private transient double[] endVal = null;
	
	private double targetProfit;
	
	private int pos=0;
	
	
	public FinancialProfitError(double n,double[] desiredOutput, double[] diffFactorArray,
			double[] startVal, double[] endVal) {
		this.n = n;
		this.diffFactorArray = diffFactorArray;
		this.changes=new double[startVal.length+1];
		for(int i=0;i<startVal.length;i++){
			this.changes[i]=startVal[i];
		}
		this.changes[startVal.length]=endVal[startVal.length-1];
		
		this.desiredOutput=desiredOutput;
		this.output=new double[this.desiredOutput.length];
		
		targetProfit=ProfitUtils.calculate(this.desiredOutput, this.changes);
		//System.out.println("Target Profit: "+targetProfit);
		
	}
	
	@Override
	public double getTotalError() {
		double totalError=targetProfit-ProfitUtils.calculate(this.output, this.changes);
		return totalError;
	}

	@Override
	public void addOutputError(double[] outputError) {
		output[pos]=desiredOutput[pos]-outputError[0];
		/*
		double outputErrorSum = 0;
		for (int i = 0; i < outputError.length; i++) {
			double output=desiredOutput[pos]-outputError[i];
			// for (double error : outputError) {
			//double error = 1;
			
			if(Math.abs(outputError[i])>=buySellLimit){
				outputErrorSum += Math.pow(diffFactorArray[pos], 2) ;
			}
			
			 
		}
		
		this.totalErrorSum += outputErrorSum;
		*/
		this.pos++;

	}

	@Override
	public void reset() {
		//totalErrorSum = 0;
		pos=0;

	}

}
