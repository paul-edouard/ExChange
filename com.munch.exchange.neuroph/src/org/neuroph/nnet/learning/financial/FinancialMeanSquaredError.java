package org.neuroph.nnet.learning.financial;

import java.io.Serializable;
import java.util.Arrays;

import org.neuroph.core.learning.error.ErrorFunction;

public class FinancialMeanSquaredError implements ErrorFunction, Serializable {
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8950037418811374103L;

	private transient double totalSquaredErrorSum;
	private transient double n;
	private transient double[] diffFactorArray = null;
	private int pos=0;

	public FinancialMeanSquaredError(double n, double[] diffFactorArray) {
		this.n = n;
		this.diffFactorArray = diffFactorArray;
	}

	@Override
	public double getTotalError() {
		return totalSquaredErrorSum / n;
	}

	@Override
	public void addOutputError(double[] outputError) {
		
		
		
		double outputErrorSqrSum = 0;
		for (int i = 0; i < outputError.length; i++) {
			// for (double error : outputError) {
			double error = outputError[i];
			double errorScaled = error * diffFactorArray[pos];
			outputErrorSqrSum += (errorScaled * errorScaled) * 0.5; // a;so
																	// multiply
																	// with
																	// 1/trainingSetSize
																	// 1/2n *
																	// (...)
		}
		
		//System.out.println("outputErrorSqrSum  : "+outputErrorSqrSum);
		
		this.totalSquaredErrorSum += outputErrorSqrSum;
		this.pos++;
		//System.out.println("totalSquaredErrorSum  : "+totalSquaredErrorSum);

	}

	@Override
	public void reset() {
		totalSquaredErrorSum = 0;
		pos=0;
	}

}
