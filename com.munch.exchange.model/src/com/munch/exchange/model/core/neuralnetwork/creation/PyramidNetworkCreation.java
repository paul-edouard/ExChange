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
	private String localSavePath;
	
	
	public PyramidNetworkCreation(int dim, int numberOfInputNeurons , String localSavePath) {
		super(dim);
		
		this.numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		this.numberOfInputNeurons=numberOfInputNeurons;
		this.localSavePath=localSavePath;
	}
	

	@Override
	public boolean[] create(Random r) {
		if(oldResults!=null && !oldResults.isEmpty()){
			return oldResults.pollLast();
		} 
		
		return createPyramidNetwork(numberOfInnerNeurons,numberOfInputNeurons, r,localSavePath);
		
	}
	public static int MAX_LOOPS=200;
	public static boolean[] createPyramidNetwork(int numberOfInnerNeurons,int numberOfInputNeurons,Random r,String localSavePath ){
		
		boolean[] cons=null;
		int loop=0;
		while (true) {
			boolean hasOneNeuron = false;
			
			double alpha = r.nextDouble();
			//alpha=0.5*alpha+0.5;
			cons = NetworkArchitecture.createFullStraigthFowardNetwork(
					numberOfInputNeurons, numberOfInnerNeurons, alpha);

			NetworkArchitecture arch = new NetworkArchitecture(
					numberOfInputNeurons, numberOfInnerNeurons, cons,localSavePath);
			Layer[] layers = arch.getNetwork().getLayers();
			for (int i = 0; i < layers.length - 1; i++) {
				if (layers[i].getNeuronsCount() == 1) {
					hasOneNeuron = true;
				}
			}
			if(!hasOneNeuron && arch.isValid())break;
			
			loop++;
			if(loop>MAX_LOOPS)break;
			
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
		alpha=0.5*alpha+0.5;
		System.out.println("alpha"+alpha);
		
		boolean[] cons=NetworkArchitecture.createFullStraigthFowardNetwork(
				9, 7, alpha);
	
	
		arch=new NetworkArchitecture(9, 7, cons,"");
		
		
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
