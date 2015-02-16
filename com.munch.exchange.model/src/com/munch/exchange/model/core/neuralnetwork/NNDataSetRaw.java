package com.munch.exchange.model.core.neuralnetwork;

import java.util.Arrays;
import java.util.Calendar;

import org.neuroph.core.data.DataSetRow;

public class NNDataSetRaw extends DataSetRow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6214692576119056254L;

	private double[] diff;
	private double[] startVal;
	private double[] endVal;
	private Calendar date;
	
	public NNDataSetRaw(double[] input, double[] desiredOutput, double[] diff, double[] startVal, double[] endVal,Calendar date) {
		super(input, desiredOutput);
		this.diff=diff;
		this.startVal=startVal;
		this.endVal=endVal;
		this.date=date;
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
	
	

	public Calendar getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "NNDataSetRaw [diff=" + Arrays.toString(diff) + ", startVal="
				+ Arrays.toString(startVal) + ", endVal="
				+ Arrays.toString(endVal) + "]";
	}
	
	
	
	
	
	
}
