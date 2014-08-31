package com.munch.exchange.model.core.neuralnetwork;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.transfer.Linear;
import org.neuroph.nnet.comp.neuron.InputNeuron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.NeuralNetworkFactory;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.NeuronFactory;
import org.neuroph.util.NeuronProperties;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.NguyenWidrowRandomizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class NetworkArchitecture extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(NetworkArchitecture.class);
	
	
	public static String NETWORK_SAVE_PATH;
	
	static final String FIELD_NumberOfInnerNeurons="NumberOfInnerNeurons";
	static final String FIELD_NumberOfInputNeurons="NumberOfInputNeurons";
	static final String FIELD_ActivatedConnections="ActivatedConnections";
	
	static final String FIELD_MaxNumberOfSavedNetworks="MaxNumberOfSavedNetworks";
	static final String FIELD_Networks="Networks";
	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	private boolean[][] actConsMatrix;
	private boolean[] actConsArray;
	
	private HashMap<Integer, Neuron> neuronMap;
	private List<Layer> layers;
	
	private int maxNumberOfSavedNetworks=50;
	private LinkedList<org.neuroph.core.NeuralNetwork> networks=new LinkedList<org.neuroph.core.NeuralNetwork>();
	
	
	public NetworkArchitecture(int numberOfInputNeurons,int numberOfInnerNeurons,double[] cons ){
		
		this.numberOfInputNeurons=numberOfInputNeurons;
		this.numberOfInnerNeurons=numberOfInnerNeurons;
		
		int activatedConnectionsSize=calculateActivatedConnectionsSize(numberOfInputNeurons, numberOfInnerNeurons);
		if(cons.length==activatedConnectionsSize){
			actConsArray=new boolean[cons.length];
			for(int i=0;i<cons.length;i++){
				if(cons[i]>0.5){
					actConsArray[i]=true;
				}
				else{
					actConsArray[i]=false;
				}
			}
		}
		
		//Creation of the neurons
		createNeuronMap();
		
		//Creation of the connection matrix
		convertActConsArrayToMatrix();
		
		//Creation of the neuron connections
		createNeuronConnection();
		
		//Creation of the layers
		createLayers();
		
		//Creation of the first network
		createNetwork();
		
	}
	
	
	private void createNeuronMap(){
		neuronMap=new HashMap<Integer, Neuron>();
		
		// create input neurons
		NeuronProperties inputNeuronProperties = new NeuronProperties(
			InputNeuron.class, Linear.class);
		for(int i=0;i<numberOfInputNeurons;i++){
			Neuron neuron = NeuronFactory.createNeuron(inputNeuronProperties);
			neuron.setLabel(UUID.randomUUID().toString());
			neuronMap.put(i, neuron);
		}
		
		//Create the inner neurons
		NeuronProperties neuronProperties = new NeuronProperties();
        neuronProperties.setProperty("useBias", false);
        neuronProperties.setProperty("transferFunction", TransferFunctionType.SIGMOID);
        for(int i=numberOfInputNeurons;i<numberOfInnerNeurons+numberOfInputNeurons;i++){
        	Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
			neuronMap.put(i, neuron);
		}
        
        //Create the output neuron
        Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
        neuronMap.put(numberOfInnerNeurons+numberOfInputNeurons, neuron);
		
	}
	
	private void convertActConsArrayToMatrix(){
		int numberOfNeurons=numberOfInputNeurons+numberOfInnerNeurons+1;
		actConsMatrix=new boolean[numberOfNeurons][numberOfNeurons];
		for(int i=0;i<numberOfNeurons;i++){
			for(int j=0;j<numberOfNeurons;j++){
				actConsMatrix[i][j]=false;
			}
		}
		
		int nbOfUsedValues=0;
		
		//Input / Inner neuron connections
		for(int i=0;i<numberOfInputNeurons;i++){
			for(int j=0;j<numberOfInnerNeurons;j++){
				actConsMatrix[i][j+numberOfInputNeurons]=actConsArray[nbOfUsedValues];
				nbOfUsedValues++;
			}
		}
		
		//Inner / Inner neurons connections
		for(int i=0;i<numberOfInnerNeurons-1;i++){
			for(int j=i+1;j<numberOfInnerNeurons;j++){
				actConsMatrix[i+numberOfInputNeurons][j+numberOfInputNeurons]=actConsArray[nbOfUsedValues];
				nbOfUsedValues++;
			}
		}
		
		//Input / Output neurons connections
		for(int i=0;i<numberOfInputNeurons;i++){
			actConsMatrix[i][numberOfInputNeurons+numberOfInnerNeurons]=actConsArray[nbOfUsedValues];
			nbOfUsedValues++;
		}
		
		//Inner / Output neurons connections
		for(int i=0;i<numberOfInnerNeurons;i++){
			actConsMatrix[i+numberOfInputNeurons][numberOfInputNeurons+numberOfInnerNeurons]=actConsArray[nbOfUsedValues];
			nbOfUsedValues++;
		}
		
	}
	
	private void createNeuronConnection(){
		
		for(int i=0;i<=numberOfInputNeurons+numberOfInnerNeurons;i++){
			for(int j=i+1;j<=numberOfInputNeurons+numberOfInnerNeurons;j++){
				if(actConsMatrix[i][j]){
					ConnectionFactory.createConnection(neuronMap.get(i), neuronMap.get(j));
				}
			}
		}
		
	}
	
	
	private void createLayers(){
		
		layers=new LinkedList<Layer>();
		
		//Create the input layer
		Layer inputLayer=new Layer();
		for(int i=0;i<numberOfInputNeurons;i++){
			inputLayer.addNeuron(neuronMap.get(i));
		}
		layers.add(inputLayer);
		
		//List<Integer> layerSizes=new LinkedList<Integer>();
		//layerSizes.add(numberOfInputNeurons);
		
		int numberOfFalseInLine=0;
		while(numberOfFalseInLine<=numberOfInnerNeurons){
			Layer layer=new Layer();
			
			for(int i=numberOfInputNeurons;i<numberOfInnerNeurons+numberOfInputNeurons;i++){
				int currentNumberOfFalse=0;
				for (int j = numberOfInputNeurons; j < numberOfInnerNeurons+numberOfInputNeurons; j++) {
					if(actConsMatrix[i][j]==false){
						currentNumberOfFalse++;
					}
					else{
						break;
					}
				}
				
				if(currentNumberOfFalse==numberOfFalseInLine){
					layer.addNeuron(neuronMap.get(i));
				}
			}
			
			if(layer.getNeuronsCount()>0)
				layers.add(layer);
			
			numberOfFalseInLine++;
		}
		
		
		//Create the output layer
		Layer outputLayer=new Layer();
		outputLayer.addNeuron(neuronMap.get(numberOfInputNeurons+numberOfInnerNeurons));
		layers.add(outputLayer);
		
	}
	
	
	private void createNetwork(){
		
		//Creation
		NeuralNetwork <BackPropagation> network=new NeuralNetwork <BackPropagation>();
		network.setLabel(UUID.randomUUID().toString());
		
		// set network type
		network.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);
		
		//Add the layers
		for(Layer layer:layers)
			network.addLayer(layer);
		
		// set input and output cells for network
		NeuralNetworkFactory.setDefaultIO(network);
		
		// set learning rule
		network.setLearningRule(new MomentumBackpropagation());
		
		//initialization of the weigths
		network.randomizeWeights(new NguyenWidrowRandomizer(-0.7, 0.7));
		
		networks.clear();
		networks.add(network);
	}
	
	private void convertActConsMatrixToArray(){
		
		actConsArray=new boolean[calculateActivatedConnectionsSize(numberOfInputNeurons,numberOfInnerNeurons)];
		for(int i=0;i<actConsArray.length;i++){
			actConsArray[i]=false;
		}
		
		int nbOfUsedValues = 0;

		// Input / Inner neuron connections
		for (int i = 0; i < numberOfInputNeurons; i++) {
			for (int j = 0; j < numberOfInnerNeurons; j++) {
				actConsArray[nbOfUsedValues] = actConsMatrix[i
						+ numberOfInputNeurons][j];
				nbOfUsedValues++;
			}
		}

		// Inner / Inner neurons connections
		for (int i = 0; i < numberOfInnerNeurons - 1; i++) {
			for (int j = i + 1; j < numberOfInnerNeurons; j++) {
				actConsArray[nbOfUsedValues] = actConsMatrix[i
						+ numberOfInputNeurons][j + numberOfInputNeurons];
				nbOfUsedValues++;
			}
		}

		// Input / Output neurons connections
		for (int i = 0; i < numberOfInputNeurons; i++) {
			actConsArray[nbOfUsedValues] = actConsMatrix[i][numberOfInputNeurons
					+ numberOfInnerNeurons];
			nbOfUsedValues++;
		}

		// Inner / Output neurons connections
		for (int i = 0; i < numberOfInnerNeurons; i++) {
			actConsArray[nbOfUsedValues] = actConsMatrix[i
					+ numberOfInputNeurons][numberOfInputNeurons
					+ numberOfInnerNeurons];
			nbOfUsedValues++;
		}
		
	}
	
	
	
	public static int calculateActivatedConnectionsSize(int numberOfInputNeurons,int numberOfInnerNeurons){
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
