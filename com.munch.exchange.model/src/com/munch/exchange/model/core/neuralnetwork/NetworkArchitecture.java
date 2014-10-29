package com.munch.exchange.model.core.neuralnetwork;

import java.io.File;
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

import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.xml.XmlParameterElement;

public class NetworkArchitecture extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(NetworkArchitecture.class);
	
	
	private static String NETWORK_SAVE_PATH;
	
	static final String FIELD_NumberOfInnerNeurons="NumberOfInnerNeurons";
	static final String FIELD_NumberOfInputNeurons="NumberOfInputNeurons";
	static final String FIELD_ActivatedConnections="ActivatedConnections";
	
	static final String FIELD_MaxNumberOfSavedNetworks="MaxNumberOfSavedNetworks";
	static final String FIELD_Networks="Networks";
	static final String FIELD_Network="Network";
	static final String FIELD_NetworkLabel="NetworkLabel";
	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	private boolean[][] actConsMatrix;
	private boolean[] actConsArray;
	
	private HashMap<Integer, Neuron> neuronMap;
	private List<Layer> layers;
	
	private int maxNumberOfSavedNetworks=50;
	private NeuralNetwork network=new NeuralNetwork();
	
	private OptimizationResults optResults=new OptimizationResults();
	
	
	public NetworkArchitecture(){}
	
	public NetworkArchitecture(int numberOfInputNeurons,int numberOfInnerNeurons,boolean[] cons ){
		this.numberOfInputNeurons=numberOfInputNeurons;
		this.numberOfInnerNeurons=numberOfInnerNeurons;
		
		int activatedConnectionsSize=calculateActivatedConnectionsSize(numberOfInputNeurons, numberOfInnerNeurons);
		//logger.info("Needed Size: "+activatedConnectionsSize);
		//logger.info("Input Size: "+cons.length);
		if(cons.length==activatedConnectionsSize){
			this.actConsArray=cons;
		}
		
		
		addNewNeuralNetwork();
		
	}
	
	
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
		
		addNewNeuralNetwork();
		
	}
	
	public void addNewNeuralNetwork(){
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
		
		//networks.clear();
		this.network=network;
	}
	
	/**
	 * test the validity of the network
	 * @return
	 */
	public boolean isValid(){
		
		Layer[] layers=  this.network.getLayers();
		for(int i=1;i<layers.length;i++){
			Layer layer=layers[i];
			
			//No neuron
			if(layer.getNeuronsCount()==0)
				return false;
			
			//Test if the size of previous layers is bigger
			if(layer.getNeuronsCount()>layers[i-1].getNeuronsCount())
				return false;
			
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron neuron=layer.getNeuronAt(j);
				
				//Test if all the inner neurons has at least one input
				if(!neuron.hasInputConnections())
					return false;
				
				//Test if the inner neuron has at least one output connection
				if(i!=layers.length-1 && neuron.getOutConnections().length==0){
					return false;
				}
				
				
			}
			
		}
		
		
		return true;
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
	
	public static int calculateNbOfInnerNeurons(int activatedConnectionsSize,int numberOfInputNeurons){
		
		int i = numberOfInputNeurons;
		int l = activatedConnectionsSize;
		
		double a=1;
		double b=2*i+1;
		double c=2*(i-l);
		
		double delta=b*b-4*a*c;
		if(delta<0)return 0;
		
		int x= (int) ((-b+Math.sqrt(delta))/(2*a));
		return x;
		
	}
	
	
	//****************************************
	//***      GETTER AND SETTER          ****
	//****************************************
	
	public String getId(){
		return network.getLabel();
	}
	
	
	
	public int getNumberOfInnerNeurons() {
		return numberOfInnerNeurons;
	}

	public NeuralNetwork getNetwork() {
		return network;
	}

	public OptimizationResults getOptResults() {
		return optResults;
	}

	public void setOptResuts(OptimizationResults optResuts) {
		this.optResults = optResuts;
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
	
	
	public boolean[] getActConsArray() {
		return actConsArray;
	}


	public void setActConsArray(boolean[] actConsArray) {
		this.actConsArray = actConsArray;
	}
	
	
	
	//****************************************
	//***             XML                 ****
	//****************************************
	
	
	public static void setNetworkSavePath(String networkSavePath) {
		NETWORK_SAVE_PATH=networkSavePath;
		logger.info("NETWORK_SAVE_PATH: "+NETWORK_SAVE_PATH);
	}

	private String actConsToString(){
		String ret="";
		for(int i=0;i<actConsArray.length;i++){
			if(i==actConsArray.length-1){
				ret+=String.valueOf(actConsArray[i]);
			}
			else{
				ret+=String.valueOf(actConsArray[i])+",";
			}
		}
		
		return ret;
	}
	
	private void actConsFromString(String input){
		
		String[] tockens=input.split(",");
		actConsArray=new boolean[tockens.length];
		for(int i=0;i<tockens.length;i++){
			actConsArray[i]=Boolean.valueOf(tockens[i]);
		}
		
	}
	
	
	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setNumberOfInnerNeurons(Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfInnerNeurons)));
		this.setNumberOfInputNeurons(Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfInputNeurons)));
		actConsFromString(rootElement.getAttribute(FIELD_ActivatedConnections));
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_Network)){
			String networkLabel=childElement.getAttribute(FIELD_NetworkLabel);
			
			NeuralNetwork nnet=NeuralNetwork.createFromFile(NETWORK_SAVE_PATH+File.separator+networkLabel+".nnet");
			OptimizationResults res=OptimizationResults.createFromFile(NETWORK_SAVE_PATH+File.separator+networkLabel+".ores");
			
			
			this.network=nnet;
			this.optResults=res;
			
		}
		/*
		else if(childElement.getTagName().equals(optResults.getTagName())){
			optResults.init(childElement);
		}*/
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_NumberOfInnerNeurons,String.valueOf(this.getNumberOfInnerNeurons()));
		rootElement.setAttribute(FIELD_NumberOfInputNeurons,String.valueOf(this.getNumberOfInputNeurons()));
		rootElement.setAttribute(FIELD_ActivatedConnections,actConsToString());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void appendChild(Element rootElement, Document doc) {
		
		//for(NeuralNetwork network:this.networks){
			Element e=doc.createElement(FIELD_Network);
			e.setAttribute(FIELD_NetworkLabel,String.valueOf(network.getLabel()));
			//Save the network
			//logger.info("Save the network: "+NETWORK_SAVE_PATH+File.separator+network.getLabel()+".nnet");
			network.save(NETWORK_SAVE_PATH+File.separator+network.getLabel()+".nnet");
			optResults.save(NETWORK_SAVE_PATH+File.separator+network.getLabel()+".ores");
			
			rootElement.appendChild(e);
			
			
			
			
		//}
			
			
			//rootElement.appendChild(optResults.toDomElement(doc));
	}

}
