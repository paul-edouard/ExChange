package com.munch.exchange.model.core.neuralnetwork.creation;

import java.util.Random;

import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class MixNetworkCreation extends BooleanArrayUniformCreation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -991443906400329333L;
	
	

	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	
	public MixNetworkCreation(int dim, int numberOfInputNeurons ) {
		super(dim);
		
		this.numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		this.numberOfInputNeurons=numberOfInputNeurons;
	}
	

	@Override
	public boolean[] create(Random r) {
		if(oldResults!=null && !oldResults.isEmpty()){
			return oldResults.pollLast();
		} 
		
		double alpha=r.nextDouble();
		
		if(alpha<1d/3d){
			return DecreaseNetworkCreation.searchNoneNullDecreaseNetwork(
					numberOfInnerNeurons, numberOfInputNeurons, r);
		}
		else if(alpha<2d/3d){
			return ValidRandomNetworkCreation.createValidRandomNetwork(
					this.n,numberOfInputNeurons, r);
		}
		else{
			return PyramidNetworkCreation.createPyramidNetwork(
					numberOfInnerNeurons, numberOfInputNeurons, r);
		}
		
	}
	
	
	
	
	

}
