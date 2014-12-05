package com.munch.exchange.model.core.neuralnetwork;

import org.neuroph.core.data.DataSetRow;

public class NNDataSetRaw extends DataSetRow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6214692576119056254L;

	private double[] diff;

	public NNDataSetRaw(double[] input, double[] desiredOutput, double[] diff) {
		super(input, desiredOutput);
		this.diff=diff;
	}

	public double[] getDiff() {
		return diff;
	}
	
	
	
	
	
	
}
