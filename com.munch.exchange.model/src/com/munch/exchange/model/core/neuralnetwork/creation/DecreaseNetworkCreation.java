package com.munch.exchange.model.core.neuralnetwork.creation;

import java.util.Random;

import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;
import org.neuroph.core.Layer;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class DecreaseNetworkCreation extends BooleanArrayUniformCreation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8066670679276130817L;

	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	private String localSavePath;
	
	
	public DecreaseNetworkCreation(int dim, int numberOfInputNeurons , String localSavePath) {
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
		
		
		return searchNoneNullDecreaseNetwork(numberOfInnerNeurons,numberOfInputNeurons, r, localSavePath);
		
	}
	public static int MAX_LOOPS=200;
	
	public static boolean[] searchNoneNullDecreaseNetwork(int numberOfInnerNeurons,int numberOfInputNeurons,Random r, String localSavePath){
		boolean[] cons=null;
		int loop=0;
		while(true){
			
			boolean hasOneNeuron = false;
			
			cons=createDecreaseNetwork(numberOfInnerNeurons,numberOfInputNeurons,r);
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
	
	public static boolean[] createDecreaseNetwork(int numberOfInnerNeurons,int numberOfInputNeurons,Random r){
	
		int numberOfNeurons=numberOfInputNeurons+numberOfInnerNeurons+1;
		boolean[][] actConsMatrix=new boolean[numberOfNeurons][numberOfNeurons];
		for(int i=0;i<numberOfNeurons;i++){
			for(int j=0;j<numberOfNeurons;j++){
				actConsMatrix[i][j]=false;
			}
		}
		
		//Create the fowards blocks
		int layerStartId=0;
		int layerSize=numberOfInputNeurons;
		int nextLayerSize=numberOfInnerNeurons;
		if(numberOfInnerNeurons>2)
			nextLayerSize=r.nextInt(numberOfInnerNeurons-2)+2;
		int leftNeurons=numberOfInnerNeurons-nextLayerSize;
		
		int loop=0;
		while(true){
			for(int i=layerStartId;i<layerStartId+layerSize;i++){
				for(int j=layerStartId+layerSize;j<layerStartId+layerSize+nextLayerSize;j++)
					actConsMatrix[i][j]=true;
			}
			
			layerStartId+=layerSize;
			layerSize=nextLayerSize;
			if(leftNeurons<=0)break;
				
			if(leftNeurons>2)
				nextLayerSize=r.nextInt(leftNeurons-2)+2;
			else
				nextLayerSize=leftNeurons;
				
			if(nextLayerSize>leftNeurons)
				nextLayerSize=leftNeurons;
			
			leftNeurons-=nextLayerSize;
			if(leftNeurons==1){
				nextLayerSize++;
				leftNeurons--;
			}
			
			loop++;
			if(loop>100)break;
		}
		
		//Create the last layer to output connection
		int lastLayerSizes=layerSize;
		for(int i=0;i<lastLayerSizes;i++){
			actConsMatrix[numberOfNeurons-2-i][numberOfNeurons-1]=true;
		}
		
		
		
		return NetworkArchitecture.convertActConsMatrixToArray(actConsMatrix,numberOfInputNeurons, numberOfInnerNeurons);
		
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
