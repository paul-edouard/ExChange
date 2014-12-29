package com.munch.exchange.model.core.neuralnetwork;

import org.neuroph.core.data.DataSetRow;

public class NNDataSetRaw extends DataSetRow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6214692576119056254L;

	private double[] diff;
	private double[] startVal;
	private double[] endVal;
	
	public NNDataSetRaw(double[] input, double[] desiredOutput, double[] diff, double[] startVal, double[] endVal) {
		super(input, desiredOutput);
		this.diff=diff;
		this.startVal=startVal;
		this.endVal=endVal;
	}

	public double[] getDiff() {
		return diff;
	}

	public double[] getStartVal() {
		return startVal;
	}

	public double[] getEndVal() {
		return endVal;
	}
	
	
	
	
	
	
}
