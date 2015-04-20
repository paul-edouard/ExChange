package com.munch.exchange.model.core.neuralnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.ejml.example.QRExampleSimple;
import org.ejml.simple.SimpleMatrix;
import org.goataa.impl.searchSpaces.trees.math.real.basic.Constant;
import org.goataa.impl.utils.Constants;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.input.WeightedSum;
import org.neuroph.core.transfer.Linear;
import org.neuroph.core.transfer.RandomGaussian;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.model.xml.XmlParameterElement;
import com.munch.exchange.utils.ProfitUtils;

public class NetworkArchitecture extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(NetworkArchitecture.class);
	
	
	//private static String NETWORK_SAVE_PATH;
	
	static final String FIELD_NumberOfInnerNeurons="NumberOfInnerNeurons";
	static final String FIELD_NumberOfInputNeurons="NumberOfInputNeurons";
	static final String FIELD_ActivatedConnections="ActivatedConnections";
	
	static final String FIELD_MaxNumberOfSavedNetworks="MaxNumberOfSavedNetworks";
	static final String FIELD_Networks="Networks";
	static final String FIELD_Network="Network";
	static final String FIELD_NetworkLabel="NetworkLabel";
	static final String FIELD_BestValue="BestValue";
	
	private String networkLabel;
	private String localSavePath="";
	
	private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	
	private boolean[][] actConsMatrix;
	private boolean[] actConsArray;
	
	
	private int maxNumberOfSavedNetworks=50;
	
	
	
	//#########################################
	//##     Optimization & Training         ##
	//#########################################
	
	static final String FIELD_NumberOfTraining="NumberOfTraining";
	static final String FIELD_LastTraining="LastTraining";
	static final String FIELD_BestTrainingRate="BestTrainingRate";
	static final String FIELD_MiddleTrainingRate="MiddleTrainingRate";
	
	private int numberOfTraining=0;
	private Calendar lastTraining=Calendar.getInstance();
	private double bestTrainingRate=Double.NEGATIVE_INFINITY;
	private double middleTrainingRate=0;
	private double beforeTrainingBestValue=Constants.WORST_FITNESS;
	
	static final String FIELD_NumberOfOptimization="NumberOfOptimization";
	static final String FIELD_LastOptimization="LastOptimization";
	static final String FIELD_BestOptimizationRate="BestOptimizationRate";
	static final String FIELD_MiddleOptimizationRate="MiddleOptimizationRate";
	
	private int numberOfOptimization=0;
	private Calendar lastOptimization=Calendar.getInstance();
	private double bestOptimizationRate=Double.NEGATIVE_INFINITY;
	private double middleOptimzationRate=0;
	private double beforeOptimizationBestValue=Constants.WORST_FITNESS;
	
	//private double bestValue=Constants.WORST_FITNESS;
	
	static final String FIELD_BestResultEntity="BestResultEntity";
	private ResultEntity bestResultEntity=null;
	private ResultEntity selectedResultEntity=null;
	
	
	private NeuralNetwork network=new NeuralNetwork();
	private OptimizationResults optResults=new OptimizationResults();
	
	private Configuration parent=null;
	
	//*************************
	// CONSTRUCTORS
	//*************************

	/**
	 * Default constructor
	 */
	public NetworkArchitecture(){
		this.setSaveParameters(false);
	}
	
	public NetworkArchitecture(int numberOfInputNeurons,int numberOfInnerNeurons,boolean[] cons ,String localSavepath ){
		this.setSaveParameters(false);
		
		LinkedList<String> inputNeuronsLabels=new LinkedList<String>();
		for(int i=0;i<numberOfInputNeurons;i++){
			inputNeuronsLabels.add(String.valueOf(i));
		}
		
		this.numberOfInputNeurons=inputNeuronsLabels.size();
		this.numberOfInnerNeurons=numberOfInnerNeurons;
		//this.neuronsLabels.addAll(inputNeuronsLabels);
		
		int activatedConnectionsSize=calculateActivatedConnectionsSize(numberOfInputNeurons, numberOfInnerNeurons);
		if(cons.length==activatedConnectionsSize){
			this.actConsArray=cons;
		}
		
		addNewNeuralNetwork(inputNeuronsLabels);
		networkLabel=this.network.getLabel();
		this.localSavePath=localSavepath;
		resultLoaded=true;
			
	}
	
	
	public NetworkArchitecture(LinkedList<String> inputNeuronsLabels,int numberOfInnerNeurons,boolean[] cons,String localSavepath ){
		this.setSaveParameters(false);
		
		this.numberOfInputNeurons=inputNeuronsLabels.size();
		this.numberOfInnerNeurons=numberOfInnerNeurons;
		//this.neuronsLabels.addAll(inputNeuronsLabels);
		
		int activatedConnectionsSize=calculateActivatedConnectionsSize(numberOfInputNeurons, numberOfInnerNeurons);
		if(cons.length==activatedConnectionsSize){
			this.actConsArray=cons;
		}
		
		
		addNewNeuralNetwork(inputNeuronsLabels);
		networkLabel=this.network.getLabel();
		this.localSavePath=localSavepath;
		resultLoaded=true;
		
	}
	
	//**************************************
	// Neural Network Creation & Functions
	//**************************************
	
	/**
	 *  Create the neural network corresponding to the input data
	 */
	public void addNewNeuralNetwork(LinkedList<String> inputNeuronsLabels){
		//Creation of the neurons
		LinkedList<Neuron> neurons=createNeurons(inputNeuronsLabels);
				
		//Creation of the connection matrix
		convertActConsArrayToMatrix();
				
		//Creation of the neuron connections
		createNeuronConnection(neurons);
				
		//Creation of the layers
		LinkedList<Layer> layers=createLayers(neurons);
				
		//Creation of the first network
		createNetwork(layers);
	}
	
	private NeuronProperties getInputNeuronProperties(){
		return new NeuronProperties(
				InputNeuron.class, Linear.class);
	}
	
	private NeuronProperties getNeuronProperties(){
		NeuronProperties neuronProperties = new NeuronProperties();
		neuronProperties.setProperty("useBias", false);
	    neuronProperties.setProperty("transferFunction", TransferFunctionType.TANH);
	    return neuronProperties;
	}
	
	
	private LinkedList<Neuron> createNeurons(LinkedList<String> neuronsLabels){
		LinkedList<Neuron> neurons=new LinkedList< Neuron>();
		
		// create input neurons
		NeuronProperties inputNeuronProperties = getInputNeuronProperties();
		for(int i=0;i<numberOfInputNeurons;i++){
			Neuron neuron = NeuronFactory.createNeuron(inputNeuronProperties);
			neuron.setLabel(neuronsLabels.get(i));
			neurons.add( neuron);
		}
		
		//Create the inner neurons
		NeuronProperties neuronProperties = getNeuronProperties();
	      
        for(int i=numberOfInputNeurons;i<numberOfInnerNeurons+numberOfInputNeurons;i++){
        	Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
        	neuron.setLabel(UUID.randomUUID().toString());
        	//neuronsLabels.add(neuron.getLabel());
        	neurons.add( neuron);
		}
        
        //Create the output neuron
        Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
        neuron.setLabel(UUID.randomUUID().toString());
    	//neuronsLabels.add(neuron.getLabel());
        neurons.add( neuron);
        
        //Add the bias neuron
      	Neuron bias_neuron = NeuronFactory.createNeuron(inputNeuronProperties);
      	bias_neuron.setLabel("Bias");
      	neurons.add( bias_neuron);
		
        return neurons;
	}
	
	private void createNeuronConnection(LinkedList<Neuron> neurons){
		
		for(int i=0;i<=numberOfInputNeurons+numberOfInnerNeurons;i++){
			for(int j=i+1;j<=numberOfInputNeurons+numberOfInnerNeurons;j++){
				if(actConsMatrix[i][j]){
					ConnectionFactory.createConnection(neurons.get(i), neurons.get(j));
				}
			}
		}
		
		//Create the bias neuron connections
		Neuron bias_neuron=neurons.getLast();
		if(bias_neuron==null)return;
		for(int i=numberOfInputNeurons;i<neurons.size()-1;i++){
			ConnectionFactory.createConnection(bias_neuron, neurons.get(i));
		}
		
	}
	
	private LinkedList<Layer> createLayers(LinkedList<Neuron> neurons){
		
		LinkedList<Layer> layers=new LinkedList<Layer>();
		
		//Create the input layer
		Layer inputLayer=new Layer();
		for(int i=0;i<numberOfInputNeurons;i++){
			inputLayer.addNeuron(neurons.get(i));
		}
		//Add the bias neuron the input layer
		if(neurons.getLast()!=null)
			inputLayer.addNeuron(neurons.getLast());
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
					layer.addNeuron(neurons.get(i));
				}
			}
			
			if(layer.getNeuronsCount()>0)
				layers.add(layer);
			
			numberOfFalseInLine++;
		}
		
		
		//Create the output layer
		Layer outputLayer=new Layer();
		outputLayer.addNeuron(neurons.get(numberOfInputNeurons+numberOfInnerNeurons));
		layers.add(outputLayer);
		
		return layers;
		
	}
	
	private void createNetwork( LinkedList<Layer> layers){
		
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
		
		this.network=network;
	}
	
	/**
	 * test the validity of the network
	 * @return
	 */
	public boolean isValid(){
		
		if(this.network.getWeights().length==0)return false;
		
		Layer[] layers=  this.network.getLayers();
		for(int i=1;i<layers.length-1;i++){
			Layer layer=layers[i];
			
			//less than one neuron
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
	
	private void convertActConsMatrixToArray(){
		
		actConsArray=convertActConsMatrixToArray(actConsMatrix,numberOfInputNeurons, numberOfInnerNeurons);
		
		/*
		actConsArray=new boolean[calculateActivatedConnectionsSize(numberOfInputNeurons,numberOfInnerNeurons)];
		for(int i=0;i<actConsArray.length;i++){
			actConsArray[i]=false;
		}
		
		int nbOfUsedValues = 0;

		// Input / Inner neuron connections
		for (int i = 0; i < numberOfInputNeurons; i++) {
			for (int j = 0; j < numberOfInnerNeurons; j++) {
				actConsArray[nbOfUsedValues] = actConsMatrix[i][j+numberOfInputNeurons];
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
		*/
		
		
	}
	
	/**
	 * reset the activated connections according to the neuron/neuron connections
	 */
	private void resetActCons(){
		
		LinkedList<Neuron> neurons=this.getNeuronsList();
		
		// Recreate the connection matrix
		int numberOfNeurons = numberOfInputNeurons + numberOfInnerNeurons + 1;
		actConsMatrix = new boolean[numberOfNeurons][numberOfNeurons];
		for (int i = 0; i < numberOfNeurons; i++) {
			for (int j = 0; j < numberOfNeurons; j++) {
				actConsMatrix[i][j] = neurons.get(i).hasOutputConnectionTo(
						neurons.get(j));
			}
		}

		// Reset the Connection Array
		convertActConsMatrixToArray();
	}

	
	public double calculateNetworkOutput(double[] input,double[] weigths ){
		if(!resultLoaded)this.loadResults();
		
		/*
		logger.info("Network weigths size: "+network.getWeights().length+", Number of results: "+this.getResultsEntities().size());
		logger.info("Best Results weigths size: "+weigths.length);
		for(ResultEntity ent: this.getResultsEntities()){
			logger.info("Ent weigths size: "+weigths.length+", Res: "+ent.getValue());
		}
		*/
		
		network.setWeights(weigths);
		network.setInput(input);
		network.calculate();
		double[] networkOutput = network.getOutput();
		if(networkOutput.length==1)return networkOutput[0];
		
		return Double.NaN;
	}
	
	public double calculateNetworkOutputFromBestResult(double[] input){
		if(this.getBestResultEntity()==null)
			return Double.NaN;
		
		return calculateNetworkOutput(input,this.getBestResultEntity().getDoubleArray());
	}
	
	/**
	 * calculate the network output of a complete set of data
	 * 
	 * @param dataSet
	 * @param weigths
	 * @return 3 arrays: network output, desired output, the diff factor, start values, end values
	 */
	public double[][] calculateNetworkOutputs(DataSet dataSet,double[] weigths){
		//return calculateOutputFromNetwork(dataSet,weigths,network);
		
		if(!resultLoaded)this.loadResults();
		
		network.setWeights(weigths);
		
		double[] output 		= new double[dataSet.getRows().size()];
		double[] desiredOutput 	= new double[dataSet.getRows().size()];
		double[] outputdiff		= new double[dataSet.getRows().size()];
		double[] startVal		= new double[dataSet.getRows().size()];
		double[] endVal			= new double[dataSet.getRows().size()];
		
		int pos=0;
		for(DataSetRow row : dataSet.getRows()) {
			if(row.getInput().length!=network.getInputsCount()){
				logger.info("Size error: Test Row Size: "+row.getInput().length+", Network input: "+network.getInputsCount());
				continue;
			}
			 network.setInput(row.getInput());
			 network.calculate();
	         double[] networkOutput = network.getOutput();
	         
	         output[pos]=networkOutput[0];
	         desiredOutput[pos]=row.getDesiredOutput()[0];
	         
	         if(row instanceof NNDataSetRaw){
	        	 NNDataSetRaw nn_row=(NNDataSetRaw) row;
	        	// logger.info("Row:"+nn_row);
	        	 outputdiff[pos]=nn_row.getDiff()[0];
	        	 startVal[pos]=nn_row.getStartVal()[0];
	        	 endVal[pos]=nn_row.getEndVal()[0];
	         }
	         
	         pos++;
			
		}
		
		double[][] outputs={output, desiredOutput, outputdiff, startVal, endVal};
		
		return outputs;
		
	}
	
	/*
	private double[][] calculateOutputFromNetwork(DataSet dataSet,double[] weigths,NeuralNetwork net){
		if(!resultLoaded)this.loadResults();
		
		net.setWeights(weigths);
		
		double[] output 		= new double[dataSet.getRows().size()];
		double[] desiredOutput 	= new double[dataSet.getRows().size()];
		double[] outputdiff		= new double[dataSet.getRows().size()];
		double[] startVal		= new double[dataSet.getRows().size()];
		double[] endVal			= new double[dataSet.getRows().size()];
		
		int pos=0;
		for(DataSetRow row : dataSet.getRows()) {
			if(row.getInput().length!=net.getInputsCount()){
				logger.info("Size error: Test Row Size: "+row.getInput().length+", Network input: "+net.getInputsCount());
				continue;
			}
			 net.setInput(row.getInput());
			 net.calculate();
	         double[] networkOutput = net.getOutput();
	         
	         output[pos]=networkOutput[0];
	         desiredOutput[pos]=row.getDesiredOutput()[0];
	         
	         if(row instanceof NNDataSetRaw){
	        	 NNDataSetRaw nn_row=(NNDataSetRaw) row;
	        	// logger.info("Row:"+nn_row);
	        	 outputdiff[pos]=nn_row.getDiff()[0];
	        	 startVal[pos]=nn_row.getStartVal()[0];
	        	 endVal[pos]=nn_row.getEndVal()[0];
	         }
	         
	         pos++;
			
		}
		
		double[][] outputs={output, desiredOutput, outputdiff, startVal, endVal};
		
		return outputs;
	}
	*/
	
	/**
	 * calculate the profit and add at the end of the output matrix
	 * 
	 * @param dataSet
	 * @param weigths
	 * @param penalty
	 * @return
	 */
	public double[][] calculateNetworkOutputsAndProfit(DataSet dataSet,double[] weigths, double penalty){
		
		double[][] outputs=calculateNetworkOutputs(dataSet,weigths);
		
		return calculateProfitFromOutput(outputs);
	}
	
	public static double[][] calculateProfitFromOutput(double[][] outputs){
		double[] output 		= outputs[0];
		double[] desiredOutput 	= outputs[1];
		double[] outputdiff		= outputs[2];
		double[] startVal		= outputs[3];
		double[] endVal			= outputs[4];
		
		double[] changes=new double[startVal.length+1];
		for(int i=0;i<startVal.length;i++){
			changes[i]=startVal[i];
		}
		changes[startVal.length]=endVal[endVal.length-1];
		
		double[] profit=ProfitUtils.calculateArray(output, changes);
		double[] desiredProfit=ProfitUtils.calculateArray(desiredOutput, changes);
		
		
		double[][] n_outputs={output, desiredOutput, outputdiff, startVal, endVal,profit,desiredProfit };
		
		return n_outputs;
	}
	
	
	public double[][] calculateNetworkOutputsAndProfitFromBestResult(DataSet dataSet,double penalty){
		if(this.getBestResultEntity()==null)
			return null;
		
		return calculateNetworkOutputsAndProfit(dataSet, this.getBestResultEntity().getDoubleArray(), penalty);
	}
	
	//*************************
	//Factored Mean Network
	//*************************
	
	private NeuralNetwork faMeNetwork=null;
	
	private OptimizationResults regularizationResults=new OptimizationResults();
	
	private boolean regularizationResultLoaded=false;
	
	
	private synchronized void loadRegularizationResults(){
		if(localSavePath.isEmpty()){
			logger.info("Error by loading Results: Local save path is empty!!");
			return;
		}
		File netFile=new File(localSavePath+File.separator+networkLabel+"_reg.nnet");
		if(!netFile.exists())return;
		
		this.faMeNetwork=NeuralNetwork.createFromFile(localSavePath+File.separator+networkLabel+"_reg.nnet");
		
		
		File resFile=new File(localSavePath+File.separator+networkLabel+"_reg.ores");
		if(!resFile.exists())return;
		
		setRegularizationResults(OptimizationResults.createFromFile(localSavePath+File.separator+networkLabel+"_reg.ores"));
		regularizationResultLoaded=true;
	}
	
	public void setRegularizationResults(OptimizationResults optResuts) {
		this.regularizationResults = optResuts;
		for(ResultEntity resultEntity:optResults.getResults()){
			resultEntity.setParentId(this.getId());
		}
		
	}
	
	public void saveRegularizationResults(){
		if(localSavePath.isEmpty()){
			logger.info("Error by saving results: Local save path is empty!!");
			return;
		}
		if(faMeNetwork==null)return;
		faMeNetwork.save(localSavePath + File.separator + networkLabel + "_reg.nnet");
		regularizationResults.save(localSavePath + File.separator + networkLabel + "_reg.ores");
	}
	
	
	public List<ResultEntity> getRegularizationResultsEntities(){
		if(!regularizationResultLoaded)this.loadRegularizationResults();
		return regularizationResults.getResults();
	}
	
	public void setRegularizationResultsEntities(LinkedList<ResultEntity> results){
		if(!regularizationResultLoaded)this.loadRegularizationResults();
		
		regularizationResults.setResults(results);
		
	}
	
	public void sortRegularizationResults(){
		regularizationResults.sort();
	}
	
	/**
	 * Add a result untity to the current list and save the best value
	 * @param ent
	 */
	public synchronized boolean addRegularizationResultEntity(ResultEntity ent){
		
		//Test if the entity is already present
		for(ResultEntity old_ent:this.getRegularizationResultsEntities()){
			if(old_ent.isGenomeEqualsTo(ent)){
				old_ent.setValue(ent.getValue());
				this.regularizationResults.sort();
				return false;
			}
		}
		
		
		ent.setParentId(this.getId());
		this.regularizationResults.addResult(ent);
		
		return true;
		
	}
	
	
	public boolean isFaMeNetworkCreated(){
		return faMeNetwork!=null;
	}
	
	public void resetFaMeNetwork(){
		faMeNetwork=null;
	}
	
	
	public NeuralNetwork getFaMeNetwork() {
		if(faMeNetwork==null)
			faMeNetwork=createFactoredMeanNetwork();
		return faMeNetwork;
	}
	
	public NeuralNetwork getCopyOfFaMeNetwork(){
		NeuralNetwork master=getFaMeNetwork();
		NeuralNetwork copy=createFactoredMeanNetwork();
		
		Double[] masterWeigths=master.getWeights();
		double[] weigths=new double[masterWeigths.length];
		for(int i=0;i<weigths.length;i++){
			weigths[i]=masterWeigths[i];
		}
		
		copy.setWeights(weigths);
		
		return copy;
	}
	
	public double[][] calculateFaMeNetworkOutputs(DataSet dataSet,double[] weigths){
		faMeNetwork.setWeights(weigths);
		return calculateFaMeNetworkOutputs(dataSet);
	}
	
	
	public static double[][] calculateOutputs(NeuralNetwork nn, DataSet dataSet){
		double[] output 		= new double[dataSet.getRows().size()];
		double[] desiredOutput 	= new double[dataSet.getRows().size()];
		double[] outputdiff		= new double[dataSet.getRows().size()];
		double[] startVal		= new double[dataSet.getRows().size()];
		double[] endVal			= new double[dataSet.getRows().size()];
		
		int pos=0;
		for(DataSetRow row : dataSet.getRows()) {
			if(row.getInput().length!=nn.getInputsCount()){
				logger.info("Size error: Test Row Size: "+row.getInput().length+", Network input: "+nn.getInputsCount());
				continue;
			}
			nn.setInput(row.getInput());
			nn.calculate();
			
	         double[] networkOutput = nn.getOutput();
	         
	         output[pos]=networkOutput[0];
	         desiredOutput[pos]=row.getDesiredOutput()[0];
	         
	    
	         
	         if(row instanceof NNDataSetRaw){
	        	 NNDataSetRaw nn_row=(NNDataSetRaw) row;
	        	// logger.info("Row:"+nn_row);
	        	 outputdiff[pos]=nn_row.getDiff()[0];
	        	 startVal[pos]=nn_row.getStartVal()[0];
	        	 endVal[pos]=nn_row.getEndVal()[0];
	         }
	         
	         pos++;
			
		}
		
		double[][] outputs={output, desiredOutput, outputdiff, startVal, endVal};
		
		return outputs;
	}
	
	
	public static double[][] calculateOutputsAndProfit(NeuralNetwork nn, DataSet dataSet){
		double[][] outputs=calculateOutputs(nn,dataSet);
		return calculateProfitFromOutput(outputs);
	}
	
	private double[][] calculateFaMeNetworkOutputs(DataSet dataSet){	
		return calculateOutputs(faMeNetwork, dataSet);
	}
	
	
	public double[][] calculateFaMeNetworkOutputsAndProfit(DataSet dataSet){
		return calculateOutputsAndProfit(faMeNetwork, dataSet);
	}
	
	
	public void setNewRandomValueOfFaMeNeurons(){
		setNewRandomValueOfFaMeNeurons(faMeNetwork);
	}
	
	public static void setNewRandomValueOfFaMeNeurons(NeuralNetwork network){
		if(network==null)return;
		for(int i=0;i<network.getLayersCount();i++){
			Layer layer=network.getLayerAt(i);
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				if(n.getTransferFunction() instanceof RandomGaussian){
					RandomGaussian func=(RandomGaussian)n.getTransferFunction();
					func.resetValue();
				}
			}
		}
	}
	
	public void setVarianzOfFaMeNeurons(double varianz){
		setVarianzOfFaMeNeurons(faMeNetwork,varianz);
	}
	
	
	public static void setVarianzOfFaMeNeurons(NeuralNetwork network,double varianz){
		if(network==null)return;
		for(int i=0;i<network.getLayersCount();i++){
			Layer layer=network.getLayerAt(i);
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				if(n.getTransferFunction() instanceof RandomGaussian){
					RandomGaussian func=(RandomGaussian)n.getTransferFunction();
					func.setVarianz(varianz);
					
				}
			}
		}
	}
	
	public static String plotValueOfFaMeNeurons(NeuralNetwork network){
		if(network==null)return "";
		
		String plot="[";
		
		for(int i=0;i<network.getLayersCount();i++){
			Layer layer=network.getLayerAt(i);
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				if(n.getTransferFunction() instanceof RandomGaussian){
					RandomGaussian func=(RandomGaussian)n.getTransferFunction();
					plot+=func.getValue()+", ";
					
				}
			}
		}
		
		return plot+"]";
		
	}
	
	
	public void checkFaMeNeuronsOutput(){
		if(faMeNetwork==null)return;
		for(int i=0;i<faMeNetwork.getLayersCount();i++){
			Layer layer=faMeNetwork.getLayerAt(i);
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				if(n.getTransferFunction() instanceof RandomGaussian){
					System.out.println("Gaussian Label: "+n.getLabel()+", Input: "+n.getNetInput()+", Neuron Output: "+n.getOutput()+ ", Layer: "+i);
				}
				else{
					System.out.println("Label: "+n.getLabel()+", Input: "+n.getNetInput()+", Neuron Output: "+n.getOutput()+ ", Layer: "+i);
				}
				Connection[] conns=n.getInputConnections();
				for(int k=0;k<conns.length;k++){
					Connection conn=conns[k];
					System.out.println("Conn From: "+conn.getFromNeuron().getLabel()+", with weight: "+conn.getWeight().getValue());
				}
				
			}
		}
	}
	
	public void checkFaMeLayerWeigth(){
		String output="";
		for(int i=0;i<faMeNetwork.getLayersCount();i++){
			Layer layer=faMeNetwork.getLayerAt(i);
			double weigth=0;
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				Connection[] conns=n.getInputConnections();
				for(int k=0;k<conns.length;k++){
					Connection conn=conns[k];
					weigth+=conn.getWeight().getValue()*conn.getWeight().getValue();
				}
			}
			output+="Layer_"+i+", total weigth="+weigth+", ";
		}
		
		logger.info(output);
		
	}
	
	public void checkNeuronsOutput(){
		if(network==null)return;
		for(int i=0;i<network.getLayersCount();i++){
			Layer layer=network.getLayerAt(i);
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				if(n.getTransferFunction() instanceof RandomGaussian){
					System.out.println("Gaussian Label: "+n.getLabel()+", Input: "+n.getNetInput()+", Neuron Output: "+n.getOutput()+ ", Layer: "+i);
					
				}
				else{
					System.out.println("Label: "+n.getLabel()+", Input: "+n.getNetInput()+", Neuron Output: "+n.getOutput()+ ", Layer: "+i);
				}
				
				Connection[] conns=n.getInputConnections();
				for(int k=0;k<conns.length;k++){
					Connection conn=conns[k];
					System.out.println("Conn From: "+conn.getFromNeuron().getLabel()+", with weight: "+conn.getWeight().getValue());
				}
				
				
			}
		}
	}

	public void setMeanValueOfFaMeNeurons(){
		if(faMeNetwork==null)return;
		for(int i=0;i<faMeNetwork.getLayersCount();i++){
			Layer layer=faMeNetwork.getLayerAt(i);
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron n=layer.getNeuronAt(j);
				if(n.getTransferFunction() instanceof RandomGaussian){
					RandomGaussian func=(RandomGaussian)n.getTransferFunction();
					func.resetToMean();
				}
			}
		}
	}
	
	private NeuronProperties getFaMeNeuronProperties(){
		NeuronProperties neuronProperties = new NeuronProperties();
		neuronProperties.setProperty("useBias", false);
	    neuronProperties.setProperty("transferFunction", TransferFunctionType.RANDOM_GAUSSIAN);
	    neuronProperties.setProperty("transferFunction.mean", 1.0d);
	    neuronProperties.setProperty("transferFunction.varianz", 0.05d);
	    return neuronProperties;
	}
	
	private NeuralNetwork createFactoredMeanNetwork(){
		
		if(!resultLoaded)this.loadResults();
		
		//logger.info("createFactoredMeanNetwork!");
		LinkedList<Layer> fema_layers=new LinkedList<Layer>();
		HashMap<String, Neuron> cpNeuronMap=new HashMap<String, Neuron>();
		HashMap<String, Neuron> faMeNeuronMap=new HashMap<String, Neuron>();
		//=======================
		//Load the network!
		//=======================
		this.getNetwork();
		if(network.getLayersCount()<2)return null;
		
		//=======================
		//Create the input Layer
		//=======================
		NeuronProperties inputNeuronProperties = getInputNeuronProperties();
		
		Layer in_layer=network.getLayerAt(0);
		Layer in_layer_cp=new Layer();
		for(int j=0;j<in_layer.getNeuronsCount()-1;j++){
			Neuron neuron = NeuronFactory.createNeuron(inputNeuronProperties);
			neuron.setLabel(in_layer.getNeuronAt(j).getLabel());
			in_layer_cp.addNeuron(neuron);
			cpNeuronMap.put(in_layer.getNeuronAt(j).getLabel(), neuron);
		}
		
		//Add the bias neuron
		Neuron bias_neuron=in_layer.getNeuronAt(in_layer.getNeuronsCount()-1);
      	Neuron bias_neuron_cp = NeuronFactory.createNeuron(inputNeuronProperties);
      	bias_neuron_cp.setLabel("Bias");
      	in_layer_cp.addNeuron( bias_neuron_cp);
		
      	fema_layers.add(in_layer_cp);
		Layer last_layer_cp=in_layer_cp;
		
		
		//=======================
		//Create the inner layers
		//=======================
		NeuronProperties neuronProperties = getNeuronProperties();
		NeuronProperties faMeNeuronProperties = getFaMeNeuronProperties();
		HashMap<String, Double> LayerConnectionMap=new HashMap<String, Double>();
		
		
		for(int i=1;i<network.getLayersCount();i++){
			Layer layer=network.getLayerAt(i);
			Layer p_layer=network.getLayerAt(i-1);
			
			int max_pre=p_layer.getNeuronsCount();
			if(i==1)max_pre=max_pre-1;
			
			//=======================
			//Create the Connection HasMap
			//=======================
			for(int k=0;k<max_pre;k++){
				Neuron fromNeuron=p_layer.getNeuronAt(k);
				Connection[] cons=fromNeuron.getOutConnections();
				for(int n=0;n<cons.length;n++){
					Neuron toNeuron=cons[n].getToNeuron();
					LayerConnectionMap.put(fromNeuron.getLabel()+"_"+toNeuron.getLabel(), cons[n].getWeight().getValue());
					//logger.info("Creation Key: "+fromNeuron.getLabel()+"_"+toNeuron.getLabel());
				}
			}
			
			
			
			//=======================
			//Decompose the origin neuron connections using the QR algorithm
			//=======================
			//SimpleMatrix QR=createTheQRMatrix(p_layer,layer,i);
			//QRExampleSimple decomp=new QRExampleSimple();
			//decomp.decompose(QR);
			
			//SimpleMatrix Q=decomp.getQ();
			//SimpleMatrix R=decomp.getR();
			
			//=======================
			//Creation of the neurons
			//=======================
			
			Layer fame_layer=new Layer();
			Layer layer_cp=new Layer();
			
			
			//=======================
			//Creation of the FaMe Layer
			//=======================
			
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron master=layer.getNeuronAt(j);
				
				Neuron fame_neuron = NeuronFactory.createNeuron(faMeNeuronProperties);
				fame_neuron.setLabel(master.getLabel()+"_fame");
				fame_layer.addNeuron(fame_neuron);
				faMeNeuronMap.put(master.getLabel(), fame_neuron);
				//Connection
				for(int k=0;k<max_pre;k++){
					Neuron from=last_layer_cp.getNeuronAt(k);
					//String conKey=from.getLabel()+"_"+master.getLabel();
					//logger.info("Key: "+conKey);
					//if(LayerConnectionMap.containsKey(conKey)){
					//	ConnectionFactory.createConnection(from, fame_neuron, LayerConnectionMap.get(conKey));
					//}
					//else{
					ConnectionFactory.createConnection(from, fame_neuron, 0);
					//}
					
				}
			}
			
			//=======================
			//Creation of the Target Layer
			//=======================
			
			for(int j=0;j<layer.getNeuronsCount();j++){
				Neuron master=layer.getNeuronAt(j);
				
				
				Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
				neuron.setLabel(master.getLabel());
				layer_cp.addNeuron(neuron);
				cpNeuronMap.put(master.getLabel(), neuron);
				//Connection
				for(int k=0;k<fame_layer.getNeuronsCount();k++){
					Neuron fromNeuron = fame_layer.getNeuronAt(k);
					if(k==j)
						ConnectionFactory.createConnection(fromNeuron, neuron, 1);
					else
						ConnectionFactory.createConnection(fromNeuron, neuron, 0);
				}
				
			}
			
			
			fema_layers.add(fame_layer);
			fema_layers.add(layer_cp);
			
			
			
			
			//createFactoredMeanNeuronsConnections(last_layer_cp, Q, fame_layer, R, layer_cp);
			
			//Creation of the bias neuron connections
			Connection[] conns=bias_neuron.getOutConnections();
			HashMap<String, Double> biasWeigthMap=new HashMap<>();
			for(int k=0;k<conns.length;k++){
				String key=conns[k].getToNeuron().getLabel();
				biasWeigthMap.put(key, conns[k].getWeight().getValue());
			}
			for(int k=0;k<layer_cp.getNeuronsCount();k++){
				Neuron toNeuron=layer_cp.getNeuronAt(k);
				ConnectionFactory.createConnection(bias_neuron_cp, toNeuron, biasWeigthMap.get(toNeuron.getLabel()));
			}
			
			last_layer_cp=layer_cp;
		}
		
		//=======================
		//Create the neuron connection
		//=======================
		for(String key_1:cpNeuronMap.keySet()){
			for(String key_2:faMeNeuronMap.keySet()){
				if(key_1==key_2)continue;
				
				String conKey=key_1+"_"+key_2;
				if(LayerConnectionMap.containsKey(conKey)){
					Neuron fromNeuron=cpNeuronMap.get(key_1);
					Neuron toNeuron=faMeNeuronMap.get(key_2);
					double value=LayerConnectionMap.get(conKey);
					Connection conn=toNeuron.getConnectionFrom(fromNeuron);
					if(conn==null)
						ConnectionFactory.createConnection(fromNeuron, toNeuron,value);
					else
						toNeuron.getConnectionFrom(fromNeuron).getWeight().setValue(value);
				}
				
			}
		}
		
		//Creation of the feMaNetwork
		NeuralNetwork <BackPropagation> f_network=new NeuralNetwork <BackPropagation>();
		f_network.setLabel(UUID.randomUUID().toString());
				
		// set network type
		f_network.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);
				
		//Add the layers
		for(Layer layer:fema_layers)
			f_network.addLayer(layer);
				
		// set input and output cells for network
		NeuralNetworkFactory.setDefaultIO(f_network);
				
		// set learning rule
		f_network.setLearningRule(new MomentumBackpropagation());
		f_network.setLabel(network.getLabel());
		
		return f_network;
		//faMeNetwork=network;
	}
	
	
	
	
	private void createFactoredMeanNeuronsConnections(Layer fame_input,SimpleMatrix Q,Layer fame_inner,
			SimpleMatrix R, Layer fame_target){
		
		for(int i=0;i<Q.numRows();i++){
			Neuron input=fame_input.getNeuronAt(i);
			for(int j=0;j<Q.numCols();j++){
				//Create connections
				double weigth=Q.get(i, j);
				if(weigth!=0){
					Neuron output=fame_inner.getNeuronAt(j);
					ConnectionFactory.createConnection(input, output, weigth);
				}
			}
		}
		
		for(int i=0;i<R.numRows();i++){
			Neuron input=fame_inner.getNeuronAt(i);
			for(int j=0;j<R.numCols();j++){
				//Create connections
				double weigth=R.get(i, j);
				if(weigth!=0){
					Neuron output=fame_target.getNeuronAt(j);
					ConnectionFactory.createConnection(input, output, weigth);
				}
			}
		}
		
	}
	
	private SimpleMatrix createTheQRMatrix(Layer input, Layer target, int layer_pos){
		SimpleMatrix qr=new SimpleMatrix(input.getNeuronsCount(), target.getNeuronsCount());
		int i_max=input.getNeuronsCount();
		//ignore the bias neuron
		if(layer_pos==1 && i_max>1){
			i_max=i_max-1;
		}
		for(int i=0;i<input.getNeuronsCount();i++){
			Neuron input_neuron=input.getNeuronAt(i);
			for(int j=0;j<target.getNeuronsCount();j++){
				Neuron target_neuron=target.getNeuronAt(j);
				if(input_neuron.hasOutputConnectionTo(target_neuron)){
					Connection conn=target_neuron.getConnectionFrom(input_neuron);
					qr.set(i, j, conn.getWeight().getValue());
				}
			}
		}
		
		
		return qr;
	}
	
	
	
	//*************************
	// ADAPTION to new input neurons
	//*************************
	
	/**
	 * Adapt the Network to the new inputs neurons without loosing the old best weights found so far
	 * 
	 * @param newInputNeuronsLabels
	 */
	public void adaptNetwork(LinkedList<String> newInputNeuronsLabels){
		if(!resultLoaded)this.loadResults();
		
		printState("Before Adaption");
		
		LinkedList<String> neuronsLabels=this.getNeuronLabels();
		
		//Find the input neurons to delete
		Set<String> toDeleteLabels=new HashSet<String>();
		for(String label:neuronsLabels){
			if(label.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}"))
				continue;
			else if(label.equals("Bias"))
				continue;
			else if(!newInputNeuronsLabels.contains(label) && newInputNeuronsLabels.size()>0){
				toDeleteLabels.add(label);
			}
		}
		if(toDeleteLabels.size()>0)
			removeInputNeurons(toDeleteLabels);
		
		//Find and Add the new neurons
		for(int i=0;i<newInputNeuronsLabels.size();i++){
			String label=newInputNeuronsLabels.get(i);
			if(!neuronsLabels.contains(label)){
				addInputNeuron(label,i);
			}
		}
		
		//this.saveResults();
		
		printState("After Adaption");
		
		
	}
	
	private void printState(String label){
		logger.info(label+"Archi: "+this.networkLabel+"Network input: "+this.network.getInputsCount()+", Weigths: "+network.getWeights().length+", Act Array: "+actConsArray.length);
		int a=0;
		for(ResultEntity ent:this.getResultsEntities()){
			a=ent.getDoubleArray().length;
		}
		logger.info("Results Size: "+a);
	}
	
	private void addInputNeuron(String label, int index){
		
		//neuronsLabels.add(index, label);
		numberOfInputNeurons++;
		
		//Create the new input neuron
		NeuronProperties inputNeuronProperties = new NeuronProperties(
					InputNeuron.class, Linear.class);
		Neuron neuron = NeuronFactory.createNeuron(inputNeuronProperties);
		neuron.setLabel(label);
		//neurons.add(index, neuron);
		Layer[] layers=this.network.getLayers();
		if(index==layers[0].getNeuronsCount())
			layers[0].addNeuron(neuron);
		else
			layers[0].addNeuron(index, neuron);
		
		
		this.network.setInputNeurons(layers[0].getNeurons());
		
		
		//Add zero connections to all neurons of the next layers
		for(Neuron in:layers[1].getNeurons()){
			ConnectionFactory.createConnection(neuron, in, 0);
		}
		
		
		//Save the weight position to add
		HashSet<Integer> weightIds = new HashSet<Integer>();
		int id = 0;
		for (Layer layer : layers) {
			for (Neuron n : layer.getNeurons())
				for (Connection conn : n.getInputConnections()) {
					if (conn.getFromNeuron().getLabel().equals(label)) {
						weightIds.add(id);
					}
					id++;
				}
		}
		
		//logger.info("Number of weigths: "+network.getWeights().length);
		
		// Reset all Old results
		for (ResultEntity ent : optResults.getResults()) {
			LinkedList<Object> r_genome = new LinkedList<Object>();
			int oldId=0;
			for (int k=0;k<id;k++) {
				if (weightIds.contains(k)) {
					r_genome.add(0.0d);
				}
				else{
					r_genome.add(ent.getGenome().get(oldId));
					oldId++;
				}
			}
			// Reset the reduce genome
			ent.setGenome(r_genome);
			
			//logger.info("Length of res genome: "+r_genome.size());
		}
		
		
		
		
		//Recreate the connection Matrix and Array
		resetActCons();
		
	}
	
	private void removeInputNeurons(Set<String> labels){
		for(String label:labels)
			removeInputNeuron(label);
	}
	
	private void removeInputNeuron(String label){
		
		int pos=this.getNeuronLabels().indexOf(label);
		if(pos<0)return;
		
		numberOfInputNeurons--;
		
		//Save the weight position to clean
		HashSet<Integer> weightIds = new HashSet<Integer>();
		Neuron neuronToDelete=null;
		
        int index=0;
        Layer[] layers=this.network.getLayers();
		for(Layer layer : layers) {
            for(Neuron neuron : layer.getNeurons()){
            	
            	if(neuron.getLabel().equals(label)){
            		neuronToDelete=neuron;
            	}
                for(Connection conn : neuron.getInputConnections()) {
                	//logger.info("From neuron: "+conn.getFromNeuron().getLabel());
                	if(conn.getFromNeuron().getLabel().equals(label)){
                		//neuronToDelete=conn.getFromNeuron();
                		weightIds.add(index);
                	}
                	index++;
                }
            }
        }
        
		//Remove the neuron all neuron connection
		//Neuron neuronToDelete=neurons.remove(pos);
		//neuronToDelete.removeAllOutputConnections();
		
		//Remove the neuron
		//logger.info("Input layer length: "+layers[0].getNeuronsCount());
		layers[0].removeNeuron(neuronToDelete);
		//logger.info("Input layer length: "+layers[0].getNeuronsCount());
		this.network.setInputNeurons(layers[0].getNeurons());
		
		
		printState("After deletion! ");
		
		
		
		//Reset all Old results
		for(ResultEntity ent:optResults.getResults()){
			LinkedList<Object> r_genome=new LinkedList<Object>();
			int i=0;
			for(Object obj:ent.getGenome()){
				if(!weightIds.contains(i)){
					r_genome.add(obj);
				}
				i++;
			}
			//Reset the reduce genome
			ent.setGenome(r_genome);
		}
		
		//Recreate the connection Matrix and Array
		resetActCons();
		
	}
	
	
	//*************************
	// STATISTIC
	//*************************
	
	/**
	 * prepare the Architecture before a optimization by saving the current best value
	 */
	public void prepareOptimizationStatistic(){
		if(!resultLoaded)this.loadResults();
		if(!hasResults())return;
		this.beforeOptimizationBestValue=this.optResults.getBestResult().getValue();
	}
	
	public void saveOptimizationStatistic(){
		if(!resultLoaded)this.loadResults();
		if(!hasResults())return;
		
		lastOptimization=Calendar.getInstance();
		numberOfOptimization++;	
		double newBest=this.optResults.getBestResult().getValue();
		double optRate=Double.NEGATIVE_INFINITY;
		if(beforeOptimizationBestValue!=Constants.WORST_FITNESS){
			optRate=(beforeOptimizationBestValue-newBest)/beforeOptimizationBestValue;
		}
		if(Double.isInfinite(optRate))return;
		bestOptimizationRate=Math.max(bestOptimizationRate, optRate);
		middleOptimzationRate=(middleOptimzationRate*(numberOfOptimization-1)+optRate)/(numberOfOptimization);
			
		
		
		//logger.info("Statistics:\n"+
		//			"Before: "+beforeOptimizationBestValue+", After: "+newBest);
	}
	
	public void prepareTrainingStatistic(ResultEntity ent){
		beforeTrainingBestValue=Constants.WORST_FITNESS;
		if(!resultLoaded)this.loadResults();
		if(!hasResults())return;
		beforeTrainingBestValue=ent.getValue();
	}
	
	public void saveTrainingStatistic(double networkError){
		if(!resultLoaded)this.loadResults();
		if(!hasResults())return;
		
		lastTraining=Calendar.getInstance();
		numberOfTraining++;
		
		double newBest=networkError;
		double optRate=Double.NEGATIVE_INFINITY;
		if(beforeTrainingBestValue!=Constants.WORST_FITNESS){
			optRate=(beforeTrainingBestValue-newBest)/beforeTrainingBestValue;
		}
		//logger.info("Best: "+bestTrainingRate+", local: "+optRate);
		if(Double.isInfinite(optRate))return;
		bestTrainingRate=Math.max(bestTrainingRate, optRate);
		middleTrainingRate=(middleTrainingRate*(numberOfTraining-1)+optRate)/(numberOfTraining);
		
		
	}
	
	
	//*************************
	// RESULTS
	//*************************
	
	private boolean resultLoaded=false;
	private boolean resultLocked=false;
	
	
	
	
	public ResultEntity getSelectedResultEntity() {
		return selectedResultEntity;
	}

	public void setSelectedResultEntity(ResultEntity selectedResultEntity) {
	this.selectedResultEntity = selectedResultEntity;
	}
	
	
	

	/**
	 * Add a result untity to the current list and save the best value
	 * @param ent
	 */
	public boolean addResultEntity(ResultEntity ent){
		
		//Test if the entity is already present
		for(ResultEntity old_ent:this.getResultsEntities()){
			if(old_ent.getValue()!=ent.getValue())continue;
			
			return false;
			
		}
		
		if(bestResultEntity==null || ent.getValue()<bestResultEntity.getValue())
			setBestResultEntity(ent);
		ent.setParentId(this.getId());
		this.optResults.addResult(ent);
		
		return true;
		
	}
	
	public void sortResults(){
		this.optResults.sort();
		bestResultEntity=this.optResults.getBestResult();
	}
	
	public ResultEntity getBestResultEntity(){
		if(bestResultEntity!=null)return bestResultEntity;
		
		return this.optResults.getBestResult();
	}
	
	public synchronized void clearResultsAndNetwork(boolean saveResults){
		if(resultLocked) return;
		if(saveResults)this.saveResults();
		this.network=null;
		this.optResults.getResults().clear();
		resultLoaded=false;
	}
	
	public boolean hasResults(){
		if(this.optResults==null)return false;
		if(this.optResults.getResults()==null)return false;
		return !this.optResults.getResults().isEmpty();
	}
	
	private synchronized void loadResults(){
		if(localSavePath.isEmpty()){
			logger.info("Error by loading Results: Local save path is empty!!");
			return;
		}
		this.network=NeuralNetwork.createFromFile(localSavePath+File.separator+networkLabel+".nnet");
		setOptResuts(OptimizationResults.createFromFile(localSavePath+File.separator+networkLabel+".ores"));
		resultLoaded=true;
	}
	
	private void saveResults(){
		if(localSavePath.isEmpty()){
			logger.info("Error by saving results: Local save path is empty!!");
			return;
		}
		if(network==null)return;
		network.save(localSavePath + File.separator + network.getLabel() + ".nnet");
		optResults.save(localSavePath + File.separator + network.getLabel() + ".ores");
	}
	
	/**
	 * set the lock status of the results, if the lock status is set to true then the results clear results function has no effect
	 * @param resultLocked
	 */
	public synchronized void lockResults(boolean resultLocked){
		this.resultLocked=resultLocked;
	}
	
	public List<ResultEntity> getResultsEntities(){
		if(!resultLoaded)this.loadResults();
		return optResults.getResults();
	}
	
	public int getResultPosition(ResultEntity ent){
		int pos=0;
		for(ResultEntity res:getResultsEntities()){
			if(res.getId().equals(ent.getId())){
				return pos;
			}
			pos++;
		}
		return 0;
	}
	
	public void setOptResuts(OptimizationResults optResuts) {
		this.optResults = optResuts;
		for(ResultEntity resultEntity:optResults.getResults()){
			resultEntity.setParentId(this.getId());
		}
		if(this.optResults!=null && this.optResults.getBestResult()!=null)
			setBestResultEntity(this.optResults.getBestResult());
	}
	
	public double getBestValue() {
		if(bestResultEntity==null)return Constants.WORST_FITNESS;
		return bestResultEntity.getValue();
	}
	
	private void setBestResultEntity(ResultEntity bestResultEntity) {
		changes.firePropertyChange(FIELD_BestResultEntity, this.bestResultEntity, this.bestResultEntity = bestResultEntity);
	}
	
	public boolean isResultLoaded() {
		return resultLoaded;
	}
	
	
	@Override
	public String toString() {
		/*
		return "NetworkArchitecture [actConsMatrix="
				+ Arrays.toString(actConsMatrix) + "]";
		*/
		String outputStr="Input neurons: "+numberOfInputNeurons;
		outputStr+="\nInput neurons: "+numberOfInputNeurons;
		outputStr+="\nInput neurons: "+numberOfInnerNeurons;
		
		/*
		int numberOfNeurons=numberOfInputNeurons+numberOfInnerNeurons+1;
		outputStr="\nMatrix:\n";
		for(int i=0;i<numberOfNeurons;i++){
			outputStr+="[";
			for(int j=0;j<numberOfNeurons;j++){
				if(j>0)outputStr+=", ";
				if(actConsMatrix[i][j])
					outputStr+="1";
				else
					outputStr+="0";
			}
			outputStr+="]\n";
		}
		outputStr+="\n";
		*/
		return outputStr;
	}
	
	//****************************************
	//***            STATIC               ****
	//****************************************
	
	
	public static boolean[] convertActConsMatrixToArray(boolean[][] actConsMatrix,int numberOfInputNeurons,int numberOfInnerNeurons){
		boolean[] actConsArray=new boolean[calculateActivatedConnectionsSize(numberOfInputNeurons,numberOfInnerNeurons)];
		for(int i=0;i<actConsArray.length;i++){
			actConsArray[i]=false;
		}
		
		int nbOfUsedValues = 0;

		// Input / Inner neuron connections
		for (int i = 0; i < numberOfInputNeurons; i++) {
			for (int j = 0; j < numberOfInnerNeurons; j++) {
				actConsArray[nbOfUsedValues] = actConsMatrix[i][j+numberOfInputNeurons];
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
		
		return actConsArray;
	}
	
	public static boolean[] createFullStraigthFowardNetwork(int numberOfInputNeurons,int numberOfInnerNeurons, double reduceFactor){
		
		
		//TODO CreateStraigtFowardNetwork
		//Set all to false
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
		int nextLayerSize=Math.max(1,Math.min((int) (Math.pow(numberOfInnerNeurons, reduceFactor)), numberOfInnerNeurons));
		int leftNeurons=numberOfInnerNeurons-nextLayerSize;
		//int nbOfUsedNeurons=numberOfInputNeurons+nextLayerSize;
		
		int loop=0;
		while(true){
			for(int i=layerStartId;i<layerStartId+layerSize;i++){
				for(int j=layerStartId+layerSize;j<layerStartId+layerSize+nextLayerSize;j++)
					actConsMatrix[i][j]=true;
			}
			
			layerStartId+=layerSize;
			layerSize=nextLayerSize;
			if(leftNeurons<=0)break;
			
			nextLayerSize=Math.max(1,Math.min((int) (0.5+Math.pow(leftNeurons, reduceFactor)), numberOfInnerNeurons));
			if(nextLayerSize>leftNeurons)
				nextLayerSize=leftNeurons;
			
			leftNeurons-=nextLayerSize;
			if(leftNeurons==1){
				nextLayerSize++;
				leftNeurons--;
			}
			
			loop++;
			if(loop>100)break;
			
			//nbOfUsedNeurons+=nextLayerSize;
		}
		
		//Create the last layer to output connection
		int lastLayerSizes=layerSize;
		for(int i=0;i<lastLayerSizes;i++){
			actConsMatrix[numberOfNeurons-2-i][numberOfNeurons-1]=true;
		}
		
		/*
		if(lastLayerSizes==1){
			System.out.println("Last layer is one: ");
		}
		*/
		
		/*
		System.out.println("Input neurons: "+numberOfInputNeurons);
		System.out.println("Inner neurons: "+numberOfInnerNeurons);
		System.out.println("Reduce Factor: "+reduceFactor);
		
		String outputStr="";
		for(int i=0;i<numberOfNeurons;i++){
			outputStr+="[";
			for(int j=0;j<numberOfNeurons;j++){
				if(j>0)outputStr+=", ";
				if(actConsMatrix[i][j])
					outputStr+="1";
				else
					outputStr+="0";
			}
			outputStr+="]\n";
		}
		outputStr+="\n";
		System.out.println(outputStr);
		*/
		
		return convertActConsMatrixToArray(actConsMatrix,numberOfInputNeurons, numberOfInnerNeurons);
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
	
	public int getSelfIndex(){
		if(this.parent==null)return 0;
		if(this.parent.getNetworkArchitectures()==null)return 0;
		
		int i=0;
		for(NetworkArchitecture archi:this.parent.getNetworkArchitectures()){
			if(archi.getId().equals(this.getId()))return i;
			i++;
		}
		
		return 0;
	}
	
	
	public int getDimension(){
		return this.actConsArray.length;
	}
	
	public void setLocalSavePath(String localSavePath) {
		this.localSavePath = localSavePath;
	}
	
	public Configuration getParent() {
		return parent;
	}

	public void setParent(Configuration parent) {
	this.parent = parent;
	}
	
	private LinkedList<Neuron> getNeuronsList(){
		if(!resultLoaded)this.loadResults();
		LinkedList<Neuron> neurons=new LinkedList< Neuron>();
		
		//Set the neuron Map
		for(Layer l:this.network.getLayers()){
			for(Neuron n:l.getNeurons()){
				neurons.add(n);
			}
		}
		return neurons;
	}
	
	private LinkedList<String> getNeuronLabels(){
		LinkedList<Neuron> neurons=getNeuronsList();
		LinkedList<String> labels=new LinkedList<String>();
		for(Neuron neuron:neurons)
			labels.add(neuron.getLabel());
		
		return labels;
	}
	
	public String getId(){
		return this.networkLabel;
	}
	
	public int getNumberOfInnerNeurons() {
		return numberOfInnerNeurons;
	}

	public NeuralNetwork getNetwork() {
		if(!resultLoaded)this.loadResults();
		return network;
	}

	/*public OptimizationResults getOptResults() {
		return optResults;
	}
	*/
	

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

	
	public int getNumberOfTraining() {
		return numberOfTraining;
	}

	public Calendar getLastTraining() {
		return lastTraining;
	}

	public double getBestTrainingRate() {
		return bestTrainingRate;
	}

	public double getMiddleTrainingRate() {
		return middleTrainingRate;
	}

	public int getNumberOfOptimization() {
		return numberOfOptimization;
	}

	public Calendar getLastOptimization() {
		return lastOptimization;
	}

	public double getBestOptimizationRate() {
		return bestOptimizationRate;
	}

	public double getMiddleOptimzationRate() {
		return middleOptimzationRate;
	}


	public void setActConsArray(boolean[] actConsArray) {
		this.actConsArray = actConsArray;
	}
	
	//****************************************
	//***             XML                 ****
	//****************************************
	
	/*
	public static void setNetworkSavePath(String networkSavePath) {
		NETWORK_SAVE_PATH=networkSavePath;
		logger.info("NETWORK_SAVE_PATH: "+NETWORK_SAVE_PATH);
	}
	*/

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
		
		this.numberOfTraining=Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfTraining));
		this.lastTraining.setTimeInMillis(Long.parseLong(rootElement.getAttribute(FIELD_LastTraining)));
		this.bestTrainingRate=(Double.parseDouble(rootElement.getAttribute(FIELD_BestTrainingRate)));
		this.middleTrainingRate=(Double.parseDouble(rootElement.getAttribute(FIELD_MiddleTrainingRate)));
		
		this.numberOfOptimization=Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfOptimization));
		this.lastOptimization.setTimeInMillis(Long.parseLong(rootElement.getAttribute(FIELD_LastOptimization)));
		this.bestOptimizationRate=(Double.parseDouble(rootElement.getAttribute(FIELD_BestOptimizationRate)));
		this.middleOptimzationRate=(Double.parseDouble(rootElement.getAttribute(FIELD_MiddleOptimizationRate)));
		
		this.networkLabel=rootElement.getAttribute(FIELD_NetworkLabel);
		this.setMaxNumberOfSavedNetworks(Integer.parseInt(rootElement.getAttribute(FIELD_MaxNumberOfSavedNetworks)));
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initChild(Element childElement) {
		
		/*
		if(childElement.getTagName().equals(FIELD_Network)){
			String networkLabel=childElement.getAttribute(FIELD_NetworkLabel);
			
			NeuralNetwork nnet=NeuralNetwork.createFromFile(NETWORK_SAVE_PATH+File.separator+networkLabel+".nnet");
			OptimizationResults res=OptimizationResults.createFromFile(NETWORK_SAVE_PATH+File.separator+networkLabel+".ores");
			
			
			this.network=nnet;
			
			setOptResuts(res);
			
		}*/
		ResultEntity ent=new ResultEntity();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			this.bestResultEntity=ent;
		}
	}
	
	
	
	

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_NumberOfInnerNeurons,String.valueOf(this.getNumberOfInnerNeurons()));
		rootElement.setAttribute(FIELD_NumberOfInputNeurons,String.valueOf(this.getNumberOfInputNeurons()));
		rootElement.setAttribute(FIELD_ActivatedConnections,actConsToString());
		
		rootElement.setAttribute(FIELD_NetworkLabel,networkLabel);
		rootElement.setAttribute(FIELD_MaxNumberOfSavedNetworks,String.valueOf(maxNumberOfSavedNetworks));
		
		rootElement.setAttribute(FIELD_NumberOfTraining,String.valueOf(numberOfTraining));
		rootElement.setAttribute(FIELD_LastTraining,String.valueOf(lastTraining.getTimeInMillis()));
		rootElement.setAttribute(FIELD_BestTrainingRate,String.valueOf(bestTrainingRate));
		rootElement.setAttribute(FIELD_MiddleTrainingRate,String.valueOf(middleTrainingRate));
		
		rootElement.setAttribute(FIELD_NumberOfOptimization,String.valueOf(numberOfOptimization));
		rootElement.setAttribute(FIELD_LastOptimization,String.valueOf(lastOptimization.getTimeInMillis()));
		rootElement.setAttribute(FIELD_BestOptimizationRate,String.valueOf(bestOptimizationRate));
		rootElement.setAttribute(FIELD_MiddleOptimizationRate,String.valueOf(middleOptimzationRate));
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void appendChild(Element rootElement, Document doc) {
		
		/*
		Element e = doc.createElement(FIELD_Network);
		e.setAttribute(FIELD_NetworkLabel, String.valueOf(network.getLabel()));
		// Save the network
		network.save(NETWORK_SAVE_PATH + File.separator + network.getLabel() + ".nnet");
		optResults.save(NETWORK_SAVE_PATH + File.separator + network.getLabel() + ".ores");
		rootElement.appendChild(e);
		*/
		saveResults();
		
		saveRegularizationResults();
		
		if(this.bestResultEntity!=null){
			rootElement.appendChild(this.bestResultEntity.toDomElement(doc));
		}
		

	}
	
	@Override
	protected void finalizeInitalization() {
		
		convertActConsArrayToMatrix();
	
	}
	
	
	 public static void main(String args[]) {
		 
		 NeuronProperties neuronProperties = new NeuronProperties();
	        neuronProperties.setProperty("useBias", false);
	        neuronProperties.setProperty("transferFunction", TransferFunctionType.TANH);
	       // neuronProperties.setProperty("transferFunction.slope", new Double(2));
	        neuronProperties.setProperty("inputFunction", WeightedSum.class);
	        Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
	        
	        
	        double[] a={-3,-2,-1,-0.5,0,0.5,1,2,3};
	        for(int i=0;i<a.length;i++){
	        	neuron.setInput(a[i]);
	 	        neuron.calculate();
	 	        System.out.println("Input: "+a[i]+", output: "+neuron.getOutput());
	        }
	        
	       
	        
		 
	 }
	
	
	
}
