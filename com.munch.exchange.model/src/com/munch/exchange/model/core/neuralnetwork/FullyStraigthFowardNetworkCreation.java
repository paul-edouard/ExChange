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
		//while(true){
			double alpha=r.nextDouble();
			
			boolean[] cons=NetworkArchitecture.createFullStraigthFowardNetwork(
					numberOfInputNeurons, numberOfInnerNeurons, alpha);
		
		
			NetworkArchitecture arch=new NetworkArchitecture(numberOfInputNeurons, numberOfInnerNeurons, cons);
			
			//arch.get
			
			//if(arch.isValid())
			return cons;
		//}
		
		
	}
	
	
	

}
