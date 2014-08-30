package com.munch.exchange.model.core.neuralnetwork;

import java.util.LinkedList;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class NetworkArchitecture extends XmlParameterElement {
	
	public static String NETWORK_SAVE_PATH;
	
	static final String FIELD_NumberOfInnerNeurons="NumberOfInnerNeurons";
	static final String FIELD_NumberOfInputNeurons="NumberOfInputNeurons";
	static final String FIELD_ActivatedConnections="ActivatedConnections";
	
	static final String FIELD_MaxNumberOfSavedNetworks="MaxNumberOfSavedNetworks";
	static final String FIELD_Networks="Networks";
	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	private LinkedList<Boolean> activatedConnections=new LinkedList<Boolean>();
	
	//private boolean[][] inputInnerActCons;
	//private boolean[][] InnerInnerActCons;
	//private boolean[] inputOutputActCons;
	//private boolean[] InnerOutputActCons;
	private boolean[][] actCons;
	
	private int maxNumberOfSavedNetworks=50;
	private LinkedList<org.neuroph.core.NeuralNetwork> networks=new LinkedList<org.neuroph.core.NeuralNetwork>();
	
	
	private void initActConsMatrix(){
		int numberOfNeurons=numberOfInputNeurons+numberOfInnerNeurons+1;
		actCons=new boolean[numberOfNeurons][numberOfNeurons];
		
		int nbOfUsedValues=0;
		
		//Input / Inner neuron connections
		for(int i=0;i<numberOfInputNeurons;i++){
			for(int j=0;j<numberOfInnerNeurons;j++){
				actCons[i+numberOfInputNeurons][j]=activatedConnections.get(nbOfUsedValues);
				nbOfUsedValues++;
			}
		}
		
		//Inner / Inner neurons connections
		for(int i=0;i<numberOfInnerNeurons-1;i++){
			for(int j=i+1;j<numberOfInnerNeurons;j++){
				actCons[i+numberOfInputNeurons][j+numberOfInputNeurons]=activatedConnections.get(nbOfUsedValues);
				nbOfUsedValues++;
			}
		}
		
		//Input / Output neurons connections
		for(int i=0;i<numberOfInputNeurons;i++){
			actCons[i][numberOfInputNeurons+numberOfInnerNeurons]=activatedConnections.get(nbOfUsedValues);
			nbOfUsedValues++;
		}
		
		//Inner / Output neurons connections
		for(int i=0;i<numberOfInnerNeurons;i++){
			actCons[i+numberOfInputNeurons][numberOfInputNeurons+numberOfInnerNeurons]=activatedConnections.get(nbOfUsedValues);
			nbOfUsedValues++;
		}
		
	}
	
	private int calculateActivatedConnectionsSize(){
		int a = numberOfInputNeurons;
		int n = numberOfInnerNeurons;
		
		return a+n+n*a+n*(n-1)/2;
	}
	
	
	
	public int getNumberOfInnerNeurons() {
		return numberOfInnerNeurons;
	}

	public void setNumberOfInnerNeurons(int numberOfInnerNeurons) {
	changes.firePropertyChange(FIELD_NumberOfInnerNeurons, this.numberOfInnerNeurons, this.numberOfInnerNeurons = numberOfInnerNeurons);}
	

	public int getNumberOfInputNeurons() {
		return numberOfInputNeurons;
	}

	public void setNumberOfInputNeurons(int numberOfInputNeurons) {
	changes.firePropertyChange(FIELD_NumberOfInputNeurons, this.numberOfInputNeurons, this.numberOfInputNeurons = numberOfInputNeurons);}
	

	public int getMaxNumberOfSavedNetworks() {
		return maxNumberOfSavedNetworks;
	}

	public void setMaxNumberOfSavedNetworks(int maxNumberOfSavedNetworks) {
	changes.firePropertyChange(FIELD_MaxNumberOfSavedNetworks, this.maxNumberOfSavedNetworks, this.maxNumberOfSavedNetworks = maxNumberOfSavedNetworks);}
	
	

	public LinkedList<org.neuroph.core.NeuralNetwork> getNetworks() {
		return networks;
	}

	public void setNetworks(LinkedList<org.neuroph.core.NeuralNetwork> networks) {
	changes.firePropertyChange(FIELD_Networks, this.networks, this.networks = networks);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub
		
	}

}
