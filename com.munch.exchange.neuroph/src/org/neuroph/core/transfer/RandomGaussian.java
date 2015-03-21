package org.neuroph.core.transfer;

import java.io.Serializable;
import java.util.Random;

import org.neuroph.util.Properties;

/** 
Generate pseudo-random floating point values, with an 
approximately Gaussian (normal) distribution.

Many physical measurements have an approximately Gaussian 
distribution; this provides a way of simulating such values. 
*/

public class RandomGaussian extends TransferFunction implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */		
	private static final long serialVersionUID = 1L;
	
	/**
	 * The mean of the gaussian probability function
	 */	
	private double mean = 1.0d;
	private double varianz = 1.0d;
	
	private double randomGaussian = 1.0d;
	
	private Random fRandom = new Random();
	
	public RandomGaussian(){
		init();
	}
	
	public RandomGaussian(Properties properties){
		try {
			this.mean = (Double)properties.getProperty("transferFunction.mean");
			this.varianz = (Double)properties.getProperty("transferFunction.varianz");
		} catch (NullPointerException e) {
			// if properties are not set just leave default values
		} catch (NumberFormatException e) {
			System.err.println("Invalid transfer function properties! Using default values.");
		}
		
		init();
	}
	  
	      
	private void init(){
		  randomGaussian=mean + fRandom.nextGaussian() * varianz;
	}
	  


	@Override
	public double getOutput(double net) {
		return randomGaussian*net;
	}
	
	@Override
	public double getDerivative(double net) {
		return randomGaussian;
	}

}
