package com.munch.exchange.model.core.neuralnetwork;

import java.util.Random;

import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;

public class FullyStraigthFowardNetworkCreation extends
		BooleanArrayUniformCreation {
	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	
	public FullyStraigthFowardNetworkCreation(int dim, int numberOfInputNeurons ) {
		super(dim);
		
		this.numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		this.numberOfInputNeurons=numberOfInputNeurons;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 9213393203484470534L;

	@Override
	public boolean[] create(Random r) {
		if(oldResults!=null && !oldResults.isEmpty()){
			return oldResults.pollLast();
		} 
		
		//if(r.nextDouble()>0.5){
			double alpha=r.nextDouble();
			//double reduceFactor=alpha+(1-alpha)*(1/numberOfInputNeurons);
			
			return NetworkArchitecture.createFullStraigthFowardNetwork(
					numberOfInputNeurons, numberOfInnerNeurons, alpha);
		/*
		}
		else{
			boolean[] bs;
		    int i;

		    i = this.n;
		    bs = new boolean[i];

		    for (; (--i) >= 0;) {
		      bs[i] = r.nextBoolean();
		    }

		    return bs;
		}
		*/
	}
	
	
	
	

}
