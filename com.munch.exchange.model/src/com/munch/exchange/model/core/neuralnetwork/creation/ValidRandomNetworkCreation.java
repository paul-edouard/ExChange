package com.munch.exchange.model.core.neuralnetwork.creation;

import java.util.Random;

import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class ValidRandomNetworkCreation extends BooleanArrayUniformCreation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	
	public ValidRandomNetworkCreation(int dim, int numberOfInputNeurons) {
		super(dim);
		
		//this.numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		this.numberOfInputNeurons=numberOfInputNeurons;
	}
	
	@Override
	public boolean[] create(Random r) {
		if(oldResults!=null && !oldResults.isEmpty()){
			return oldResults.pollLast();
		} 
		
		return createValidRandomNetwork(this.n,numberOfInputNeurons, r);
		
	}
	
	public static boolean[] createValidRandomNetwork(int dim, int numberOfInputNeurons, Random r){
		int numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		
		boolean[] cons=null;
		while(true){
			cons=createRandomBooleanArray(dim,r);
			NetworkArchitecture arch = new NetworkArchitecture(
					numberOfInputNeurons, numberOfInnerNeurons, cons);
			if(arch.isValid()){
				//System.out.println(arch);
				break;
			}
		}
		
		return cons;
	}
	
	public static boolean[] createRandomBooleanArray(int dim,Random r){
		boolean[] bs;
	    int i;

	    i = dim;
	    bs = new boolean[i];

	    for (; (--i) >= 0;) {
	      bs[i] = r.nextBoolean();
	    }

	    return bs;
	}
	
	
	public static void main(String[] args){
		Random r=new Random();
		int numberOfInputNeurons=15;
		int numberOfInnerNeurons=15;
		int dim=NetworkArchitecture.calculateActivatedConnectionsSize(numberOfInputNeurons, numberOfInnerNeurons);
		
		createValidRandomNetwork(dim,numberOfInputNeurons,r);
	}

}
