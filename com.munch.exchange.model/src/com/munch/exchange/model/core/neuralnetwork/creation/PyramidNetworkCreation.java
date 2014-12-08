package com.munch.exchange.model.core.neuralnetwork.creation;

import java.util.Random;

import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;
import org.neuroph.core.Layer;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class PyramidNetworkCreation extends
		BooleanArrayUniformCreation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	
	public PyramidNetworkCreation(int dim, int numberOfInputNeurons ) {
		super(dim);
		
		this.numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		this.numberOfInputNeurons=numberOfInputNeurons;
	}
	

	@Override
	public boolean[] create(Random r) {
		if(oldResults!=null && !oldResults.isEmpty()){
			return oldResults.pollLast();
		} 
		
		return createPyramidNetwork(numberOfInnerNeurons,numberOfInputNeurons, r);
		
	}
	
	public static boolean[] createPyramidNetwork(int numberOfInnerNeurons,int numberOfInputNeurons,Random r){
		
		boolean hasOneNeuron=true;
		boolean[] cons=null;
		
		while (hasOneNeuron) {
			hasOneNeuron = false;
			double alpha = r.nextDouble();

			cons = NetworkArchitecture.createFullStraigthFowardNetwork(
					numberOfInputNeurons, numberOfInnerNeurons, alpha);

			NetworkArchitecture arch = new NetworkArchitecture(
					numberOfInputNeurons, numberOfInnerNeurons, cons);
			Layer[] layers = arch.getNetwork().getLayers();
			for (int i = 0; i < layers.length - 1; i++) {
				if (layers[i].getNeuronsCount() == 1) {
					hasOneNeuron = true;
				}
			}
		}
		
		return cons;
	}
	
	
	
	public static void main(String[] args){
		Random r=new Random();
		boolean hasOneNeuron=true;
		NetworkArchitecture arch=null;
		
		while(hasOneNeuron){
			hasOneNeuron=false;
		double alpha=r.nextDouble();
		
		System.out.println("alpha"+alpha);
		
		boolean[] cons=NetworkArchitecture.createFullStraigthFowardNetwork(
				10, 8, alpha);
	
	
		arch=new NetworkArchitecture(10, 8, cons);
		
		
		Layer[] layers=arch.getNetwork().getLayers();
		
		for(int i=0;i<layers.length-1;i++){
			if(layers[i].getNeuronsCount()==1){
				System.out.println("Layer "+i+" has only one neuron!");
				hasOneNeuron=true;
			}
		}
		}
		
		
		System.out.println(arch);
		System.out.println("Is Valid "+arch.isValid());
		
	}

}
