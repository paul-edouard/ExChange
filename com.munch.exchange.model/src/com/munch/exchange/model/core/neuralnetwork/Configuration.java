package com.munch.exchange.model.core.neuralnetwork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.data.norm.RangeNormalizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeriesCategory;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeriesGroup;
import com.munch.exchange.model.core.neuralnetwork.training.TrainingBlock;
import com.munch.exchange.model.core.neuralnetwork.training.TrainingBlocks;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class Configuration extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(Configuration.class);
	
	static final String FIELD_Period="Period";
	static final String FIELD_DayOfWeekActivated="DayOfWeekActivated";
	static final String FIELD_DayOfWeek="Day of Week";
	static final String FIELD_Name="Name";
	static final String FIELD_Id="Id";
	static final String FIELD_LastUpdate="LastUpdate";
	//static final String FIELD_AllTimeSeries="AllTimeSeries";
	//static final String FIELD_AllTimeSeries="TimeSeries";
	static final String FIELD_OutputPointList="OutputPointList";
	static final String FIELD_LastInputPointDate="LastInputPointDate";
	static final String FIELD_OptLearnParam="OptLearnParam";
	public static final String FIELD_IsDirty="IsDirty";
	
	private PeriodType period=PeriodType.DAY;
	private boolean dayOfWeekActivated=false;
	private String Name="New Neural Network Configuration";
	private String id;
	//private String localSavePath="";
	
	private Calendar lastUpdate=Calendar.getInstance();
	
	
	private Stock parent;
	
	//Training Data
	private DataSet trainingSet=null;
	private double[] lastInput=null;
	private ValuePointList outputPointList=new ValuePointList();
	private TrainingBlocks trainingBlocks=new TrainingBlocks();
	
	//Time Series
	//private LinkedList<TimeSeries> allTimeSeries=new LinkedList<TimeSeries>();
	private Calendar lastInputPointDate=null;
	private TimeSeriesGroup rootTimeSeriesGroup=null;
	
	//Optimization Parameters
	private AlgorithmParameters<double[]> optLearnParam=new AlgorithmParameters<double[]>("Optimization_learn_parameters");
	private AlgorithmParameters<boolean[]> optArchitectureParam=new AlgorithmParameters<boolean[]>("Optimization_architecture_parameters");
	private LearnParameters learnParam=new LearnParameters("Neural_Network_learn_parameters");
	
	//Regularization Parameters
	private AlgorithmParameters<double[]> regOptParam=new AlgorithmParameters<double[]>("Regularization_optimization_parameters");
	private LearnParameters regTrainParam=new LearnParameters("Regularization_train_parameters");
	private RegularizationParameters regBasicParam=new RegularizationParameters("Regularization_basic_parameters");
	
	//Architectures
	private int maxNumberOfSavedAchitectures=200;
	private LinkedList<NetworkArchitecture> networkArchitectures=new LinkedList<NetworkArchitecture>();
	private HashMap<Integer, OptimizationResults> netArchiOptResultMap=new HashMap<Integer, OptimizationResults>();
	
	//Save the dirty
	private boolean isDirty=false;
	
	
	public Configuration(){
		
		id=UUID.randomUUID().toString();
		
		AlgorithmParameters.setDefaultBooleansParameters(optArchitectureParam);
		AlgorithmParameters.setDefaultDoublesParameters(optLearnParam);
		LearnParameters.setDefaultLearnParameters(learnParam);
		
		RegularizationParameters.setDefaultParameters(regBasicParam);
		AlgorithmParameters.setDefaultDoublesParameters(regOptParam);
		LearnParameters.setDefaultLearnParameters(regTrainParam);
		
	}
	
	
	
	public synchronized OptimizationResults getOptResults(int dimension){
		if(!netArchiOptResultMap.containsKey(dimension)){
			netArchiOptResultMap.put(dimension, new OptimizationResults() );
		}
		return netArchiOptResultMap.get(dimension);
	}
	
	
	
	public LinkedList<String> getInputNeuronNames(){
		
		LinkedList<String> names=new LinkedList<String>();
		if(dayOfWeekActivated){
			names.add(FIELD_DayOfWeek);
		}
		for(TimeSeries series:this.getRootTimeSeriesGroup().getAllTimeSeries()){
			names.addAll(series.getInputNeuronNames());
		}
		
		return names;
	}

	
	public int getNumberOfInputNeurons(){
		return getInputNeuronNames().size();
	}
	
	
	
	/*
	 * create the boolean like output array and the output diff array 
	 */
	private double[][] createOutputArrays(int maxNumberOfValues){
		double[] outputArray=new double[maxNumberOfValues];
		double[] outputDiffArray=new double[maxNumberOfValues];
		double[] outputStartArray=new double[maxNumberOfValues];
		double[] outputEndArray=new double[maxNumberOfValues];
		
		//double[][] output=new double[2][maxNumberOfValues];
		
		double[] tt=outputPointList.toDoubleArray();
		String[] metaDataArray=outputPointList.toStringArray();
		
		//logger.info("Output double: "+Arrays.toString(tt));
		//logger.info("Output diff: "+Arrays.toString(diffArray));
		
		int diff=outputPointList.toDoubleArray().length-maxNumberOfValues;
		for(int i=tt.length-1;i>=0;i--){
			if(i-diff<0)break;
			outputArray[i-diff]=tt[i];
			String[] mdatas=metaDataArray[i].split(";");
			if(mdatas.length!=3)continue;
			
			outputDiffArray[i-diff]=Double.valueOf(mdatas[0]);
			outputStartArray[i-diff]=Double.valueOf(mdatas[1]);
			outputEndArray[i-diff]=Double.valueOf(mdatas[2]);
		}
		
		//output[0]=outputArray;
		//output[1]=outputDiffArray;
		
		double[][] output={outputArray,outputDiffArray,outputStartArray,outputEndArray};
		
		return output;
		
	}
	
	
	
	//********************************
	//  Training Data & Last Input
	//********************************
	
	public static final String ROOT_DATA_SET="root";
	
	
	private boolean resultsCalculationNeeded=false;
	
	/**
	 * create the Training Data and the last input array
	 */
	private synchronized void createTrainingData(){
		
		if(!areAllTimeSeriesAvailable()){
			logger.info("Create Training Data ERROR: the time series are not available");
			return;
		}
		
		//Create the input arrays
		LinkedList<double[]> doubleArrayList=new LinkedList<double[]>();
		LinkedList<TimeSeries> sortedTimeSeries=this.rootTimeSeriesGroup.getAllTimeSeries();
		if(dayOfWeekActivated){
			createDayOfWeekSeries(sortedTimeSeries);
		}
		for(TimeSeries series:sortedTimeSeries){
			logger.info("Serie "+series.getName()+", number of inputs: "+series.getInputValues().size());
			//TODO Series zero
			LinkedList<double[]> d_array_list=series.transformSeriesToDoubleArrayList(lastInputPointDate);
			doubleArrayList.addAll(d_array_list);
		}
		
		if(sortedTimeSeries.size()==0){
			logger.info("Create Training Data ERROR: the time series are not available");
			return;
		}
		
		//All Dates
		LinkedList<Calendar> dates=sortedTimeSeries.getFirst().getAllDatesFrom(lastInputPointDate);
		
		//int len=Math.min(doubleArrayList.get(0).length,500);
		if(doubleArrayList.size()==0)return;
		int len=doubleArrayList.get(0).length;
		
		//Create the output array
		double[][] outputs=createOutputArrays(len-1);
		
		//Create the Training set
		trainingSet = new DataSet(doubleArrayList.size()+1, 1);
		trainingSet.setLabel(ROOT_DATA_SET);
		//this.getInputNeuronNames().toArray();
		
		//double[] outputdiffFactor=new double[len-1];
		for(int i=0;i<len;i++){
			
			
			double[] input=new double[doubleArrayList.size()+1];
			for(int j=0;j<doubleArrayList.size();j++){
				
				input[j]=doubleArrayList.get(j)[i];
			}
			//Set the bias input
			input[input.length-1]=1;
			
			double[] output=null;
			double[] diff=null;
			double[] startVal=null;
			double[] endVal=null;
			
			if(i==len-1){
				output=new double[]{0};
				diff=new double[]{0};
				startVal=new double[]{0};
				endVal=new double[]{0};
			}
			else{
				output=new double[]{outputs[0][i]};
				diff=new double[]{outputs[1][i]};
				startVal=new double[]{outputs[2][i]};
				endVal=new double[]{outputs[3][i]};
			}
			
			//if(i<len-1)
			//	outputdiffFactor[i]=outputs[1][i];
			
			NNDataSetRaw raw=new NNDataSetRaw(input, output, diff, startVal, endVal,dates.get(i));
			
			trainingSet.addRow(raw);
		}
		
		//logger.info("Diff: "+Arrays.toString(outputdiffFactor));
		
		//Normalize the training set
		//trainingSet.normalize(new RangeNormalizer(-1, 1));
		
		//Save the last input
		DataSetRow raw=trainingSet.getRowAt(len-1);
		lastInput=raw.getInput();
		trainingSet.removeRowAt(len-1);
		
		//Set the DiffFactor of the
		this.learnParam.setArrays(outputs[0],outputs[1],outputs[2],outputs[3]);
		this.regTrainParam.setArrays(outputs[0],outputs[1],outputs[2],outputs[3]);
		
		fireTrainingDataSetChanged();
		
		//return trainingSet;
	}
	
	private void createDayOfWeekSeries(LinkedList<TimeSeries> sortedTimeSeries){
		TimeSeries dayOfWeekSerie=new TimeSeries(FIELD_DayOfWeek/*, TimeSeriesCategory.RATE*/);
		dayOfWeekSerie.setNumberOfPastValues(1);
		for(ValuePoint point:sortedTimeSeries.getFirst().getInputValues()){
			double dayofWeek=(double)point.getDate().get(Calendar.DAY_OF_WEEK);
			dayOfWeekSerie.getInputValues().add(new ValuePoint(point.getDate(), dayofWeek));
		}
		dayOfWeekSerie.resetMinMaxValues(true);
		sortedTimeSeries.addFirst(dayOfWeekSerie);
	}
	
	public void resetTrainingData(){
		trainingSet=null;
		lastInput=null;
	}
	
	public DataSet getDataSet() {
		if(trainingSet==null)createTrainingData();
		return trainingSet;
	}
	
	public double[] getLastInput() {
		if(lastInput==null || lastInput.length==0)
			createTrainingData();
		return lastInput;
	}
	
	
	public TrainingBlocks getTrainingBlocks() {
		return trainingBlocks;
	}
	
	public DataSet getTrainingDataSet(){
		DataSet allData=this.getDataSet();
		if(trainingBlocks.getBlocks().isEmpty())return allData;
		
		//Create the training set
		DataSet trainSet=new DataSet(allData.getInputSize(), allData.getOutputSize());
		
		for(TrainingBlock block : trainingBlocks.getBlocks()){
			if(!block.isTraining())continue;
			
			for(int i=block.getStart();i<=block.getEnd();i++){
				trainSet.addRow(allData.getRowAt(i));
			}
		
		}
		return trainSet;
		
	}
	
	public DataSet getValidateDataSet(){
		if(trainingBlocks.getBlocks().isEmpty())return null;
		
		DataSet allData=this.getDataSet();
		//Create the training set
		DataSet valSet=new DataSet(allData.getInputSize(), allData.getOutputSize());
				
		for(TrainingBlock block : trainingBlocks.getBlocks()){
			if(block.isTraining())continue;
					
			for(int i=block.getStart();i<=block.getEnd();i++){
				valSet.addRow(allData.getRowAt(i));
			}
				
		}
		return valSet;
	}
	
	
	public synchronized boolean isResultsCalculationNeeded() {
		return resultsCalculationNeeded;
	}

	public void setResultsCalculationNeeded(boolean resultsCalculationNeeded) {
		this.resultsCalculationNeeded = resultsCalculationNeeded;
	}
	
	//*************************
	// Time Series
	//*************************

	public Configuration createCopy(){
		Configuration copy=new Configuration();
		copy.rootTimeSeriesGroup=this.rootTimeSeriesGroup.createCopy();
		//copy.allTimeSeries=this.createCopyOfTimeSeries();
		copy.Name=this.Name;
		copy.parent=this.parent;
		copy.dayOfWeekActivated=this.dayOfWeekActivated;
		
		//TODO copy the rest of attributes
		
		return copy;
	}
	
	/**
	 * Check if all times series are correctly loaded
	 * @return
	 */
	public boolean areAllTimeSeriesAvailable(){
		if(this.rootTimeSeriesGroup==null)return false;
		
		for(TimeSeries series:this.rootTimeSeriesGroup.getAllTimeSeries()){
			if(series.getInputValues().isEmpty())return false;
		}
		
		return true;
	}
	
	/*
	private LinkedList<TimeSeries> createCopyOfTimeSeries(){
		LinkedList<TimeSeries> copy=new LinkedList<TimeSeries>();
		for(TimeSeries series:this.allTimeSeries){
			copy.add(series.createCopy());
		}
		
		return copy;
	}
	*/
	
	public void copyTimeSeriesFrom(Configuration config){
		//this.allTimeSeries.clear();
		/*
		for(TimeSeries series:config.allTimeSeries){
			this.allTimeSeries.add(series.createCopy());
		}
		*/
		this.rootTimeSeriesGroup=config.rootTimeSeriesGroup.createCopy();
		this.dayOfWeekActivated=config.dayOfWeekActivated;
	}
	
	public TimeSeriesGroup getRootTimeSeriesGroup() {
		if(rootTimeSeriesGroup==null && this.getParent()!=null)
			rootTimeSeriesGroup=TimeSeriesGroup.createRoot(this.getParent());
		return rootTimeSeriesGroup;
	}
	
	
	
	//*************************
	// Architecture
	//*************************
	


	public synchronized NetworkArchitecture searchArchitecture(String id){
		for(NetworkArchitecture architecture :networkArchitectures){
			if(architecture.getId().equals(id))return architecture;
		}
		return null;
	}
	
	public synchronized NetworkArchitecture searchArchitecture(boolean[] actConsArray, String localSavePath){
		return searchArchitecture(actConsArray,false,localSavePath);
	}
	
	/**
	 * Knowing the connections array this method will search the corresponding architecture
	 * if no architecture was found a new one will be build
	 * 
	 * @param actConsArray
	 * @return
	 */
	public synchronized NetworkArchitecture searchArchitecture(boolean[] actConsArray,boolean loggerOn, String localSavePath){
		
		NetworkArchitecture searched=null;
		
		if(loggerOn)
			logger.info("Number of archi: "+networkArchitectures.size());
		
		
		//Search the architecture in the already created ones
		for(NetworkArchitecture architecture :networkArchitectures){
			if(architecture.getActConsArray().length==actConsArray.length){
				boolean isEqual=true;
				for(int i=0;i<actConsArray.length;i++){
					if(architecture.getActConsArray()[i]!=actConsArray[i]){
						isEqual=false;break;
					}
				}
				if(isEqual){
					//logger.info("Architecture found!");
					if(loggerOn)
						logger.info("Architecture found!");
					searched=architecture;
					return searched;
				}
				
			}
		}
		
		//Create the architecture
		if(searched==null){
			int numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(
					actConsArray.length, this.getNumberOfInputNeurons());
			
			searched=new NetworkArchitecture(getInputNeuronNames(),numberOfInnerNeurons,actConsArray,localSavePath);
			
			//Test the Network validity
			if(searched.isValid()){
				//Add the architecture in the list
				this.setDirty(true);
				searched.setParent(this);
				addNetworkArchitecture(searched);
				return searched;
			}
			else{
				if(loggerOn)
					logger.info("Archi no valid");
			}
		}
		
		
		return null;
	}
	
	private synchronized void addNetworkArchitecture(NetworkArchitecture architecture){
		networkArchitectures.add(architecture);
	}
	
	@SuppressWarnings("rawtypes")
	public NetworkArchitecture searchBestNetworkArchitecture(){
		double error=Double.POSITIVE_INFINITY;
		//NeuralNetwork nn=null;
		NetworkArchitecture best=null;
		for(NetworkArchitecture archi:networkArchitectures){
			ResultEntity ent=archi.getBestResultEntity();
			if(ent==null)continue;
			if(ent.getValue()<error){
				
				best=archi;
				//logger.info("Neuron size: "+nn.getWeights().length);
				//nn.setWeights(ent.getDoubleArray());
				error=ent.getValue();
			}
		}
		
		return best;
	}
	
	public LinkedList<NetworkArchitecture> searchNetworkArchitectures(int dimension){
		LinkedList<NetworkArchitecture> l=new LinkedList<NetworkArchitecture>();
		for(NetworkArchitecture archi:this.networkArchitectures){
			if(archi.getDimension()==dimension)
				l.add(archi);
		}
		
		return l;
	}
	
	
	//****************************************
	//***      GETTER AND SETTER          ****
	//****************************************
	
	
	
	
	
	
	public Type getOptimizationResultType(){
		switch (period) {
		case DAY:
			return Type.NEURAL_NETWORK_OUTPUT_DAY;
		case HOUR:
			return Type.NEURAL_NETWORK_OUTPUT_HOUR;
		case MINUTE:
			return Type.NEURAL_NETWORK_OUTPUT_MINUTE;
		case SECONDE:
			return Type.NEURAL_NETWORK_OUTPUT_SECONDE;

		default:
			return Type.NEURAL_NETWORK_OUTPUT_DAY;
		}
	}
	
	public AlgorithmParameters<double[]> getRegOptParam() {
		return regOptParam;
	}



	public void setRegOptParam(AlgorithmParameters<double[]> regOptParam) {
	this.regOptParam = regOptParam;}
	



	public LearnParameters getRegTrainParam() {
		return regTrainParam;
	}



	public void setRegTrainParam(LearnParameters regTrainParam) {
	this.regTrainParam = regTrainParam;}
	



	public RegularizationParameters getRegBasicParam() {
		return regBasicParam;
	}



	public void setRegBasicParam(RegularizationParameters regBasicParam) {
	this.regBasicParam = regBasicParam;}
	



	public Calendar getLastInputPointDate() {
		return lastInputPointDate;
	}

	public void setLastInputPointDate(Calendar lastInputPointDate) {
		if(this.lastInputPointDate==null){
			this.lastInputPointDate=lastInputPointDate;return;
		}
	
		if(this.lastInputPointDate.getTimeInMillis()<lastInputPointDate.getTimeInMillis()){
			this.lastInputPointDate=lastInputPointDate;return;
		}
	
	}
	
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
	changes.firePropertyChange(FIELD_IsDirty, this.isDirty, this.isDirty = isDirty);}
	

	public AlgorithmParameters<boolean[]> getOptArchitectureParam() {
		return optArchitectureParam;
	}

	public LearnParameters getLearnParam() {
		return learnParam;
	}

	public void setOptArchitectureParam(AlgorithmParameters<boolean[]> optArchitectureParam) {
	this.optArchitectureParam = optArchitectureParam;
	}
	
	public void setLearnParam(LearnParameters learnParam) {
		
		this.learnParam = learnParam;
		
	}
	
	public AlgorithmParameters<double[]> getOptLearnParam() {
		return optLearnParam;
	}

	public void setOptLearnParam(AlgorithmParameters<double[]> optLearnParam) {
	changes.firePropertyChange(FIELD_OptLearnParam, this.optLearnParam, this.optLearnParam = optLearnParam);}
	

	public ValuePointList getOutputPointList() {
		return outputPointList;
	}

	public void setOutputPointList(ValuePointList outputPointList) {
	changes.firePropertyChange(FIELD_OutputPointList, this.outputPointList, this.outputPointList = outputPointList);}
	

	public boolean isDayOfWeekActivated() {
		return dayOfWeekActivated;
	}

	public void setDayOfWeekActivated(boolean dayOfWeekActivated) {
		this.dayOfWeekActivated=dayOfWeekActivated;
	}
	

	public PeriodType getPeriod() {
		return period;
	}

	public void setPeriod(PeriodType period) {
	changes.firePropertyChange(FIELD_Period, this.period, this.period = period);}
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		changes.firePropertyChange(FIELD_Name, this.Name, this.Name = name);
	}

	public Calendar getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Calendar lastUpdate) {
	changes.firePropertyChange(FIELD_LastUpdate, this.lastUpdate, this.lastUpdate = lastUpdate);}
	
	/*
	public LinkedList<TimeSeries> getAllTimeSeries() {
		return allTimeSeries;
	}
	*/
	/*
	public LinkedList<TimeSeries> getSortedTimeSeries(){
		LinkedList<TimeSeries> list=new LinkedList<TimeSeries>();
		for(TimeSeries series:this.getTimeSeriesFromCategory(TimeSeriesCategory.RATE)){
			list.add(series);
		}
		for(TimeSeries series:this.getTimeSeriesFromCategory(TimeSeriesCategory.FINANCIAL)){
			list.add(series);
		}
		for(TimeSeries series:this.getTimeSeriesFromCategory(TimeSeriesCategory.TARGET_OUTPUT)){
			list.add(series);
		}
		return list;
	}
	*/
	
	/*
	public LinkedList<TimeSeries> getTimeSeriesFromCategory(TimeSeriesCategory category){
		LinkedList<TimeSeries> list=new LinkedList<TimeSeries>();
		for(TimeSeries series:allTimeSeries){
			if(series.getCategory()!=category)continue;
			list.add(series);
		}
		
		return list;
	}
	*/
	
	/*
	public void addTimeSeries(TimeSeries series, boolean fireTimeSeriesChanged){
		if(fireTimeSeriesChanged){
		series.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if(arg0.getPropertyName().equals(TimeSeries.FIELD_NumberOfPastValues)){
					fireTimeSeriesChanged();
				}
				
			}
		});
		}
		allTimeSeries.add(series);
		if(fireTimeSeriesChanged){
			fireTimeSeriesChanged();
		}
	}
	*/
	
	/*
	public void addTimeSeries(TimeSeries series){
		addTimeSeries(series,false);
	}
	*/

	/*
	public void removeTimeSeries(TimeSeries series){
		//series.removePropertyChangeListener(l);
		allTimeSeries.remove(series);
		fireTimeSeriesChanged();
	}
	*/
	
	public int getNumberOfTimeSeries(){
		return this.rootTimeSeriesGroup.getAllTimeSeries().size();
		
		//if(this.allTimeSeries==null)return 0;
		//return this.allTimeSeries.size();
	}
	
	
	/*
	public void setAllTimeSeries(LinkedList<TimeSeries> allTimeSeries) {
	changes.firePropertyChange(FIELD_AllTimeSeries, this.allTimeSeries, this.allTimeSeries = allTimeSeries);}
	*/
	
	public LinkedList<NetworkArchitecture> getNetworkArchitectures() {
		return networkArchitectures;
	}
	
	public synchronized LinkedList<NetworkArchitecture> getNetworkArchitecturesCopy(){
		LinkedList<NetworkArchitecture> copy=new LinkedList<NetworkArchitecture>();
		copy.addAll(networkArchitectures);
		
		return copy;
	}
	
	
	
	public int[] getMinMaxInnerNeurons(){
		int numberOfInputNeurons=this.getNumberOfInputNeurons();
		int[] minMaxInputN={numberOfInputNeurons,numberOfInputNeurons};
		
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MaxDimension)){
			minMaxInputN[1]=optArchitectureParam.getIntegerParam(AlgorithmParameters.MaxDimension);
		}
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MinDimension)){
			minMaxInputN[0]=optArchitectureParam.getIntegerParam(AlgorithmParameters.MinDimension);
		}
		
		int[] minMaxInnerN={NetworkArchitecture.calculateNbOfInnerNeurons(minMaxInputN[0], numberOfInputNeurons),
							NetworkArchitecture.calculateNbOfInnerNeurons(minMaxInputN[1], numberOfInputNeurons)};
		
		return minMaxInnerN;
		
	}
	
	


	public void setMinMaxInnerNeurons(int[] minMaxInnerN ){
		if(minMaxInnerN.length!=2)return;
		
		int numberOfInputNeurons=this.getNumberOfInputNeurons();
		//Change the Opt Architecture Parameters
		optArchitectureParam.setParam(AlgorithmParameters.MaxDimension,NetworkArchitecture.calculateActivatedConnectionsSize(numberOfInputNeurons, minMaxInnerN[1]) );
		optArchitectureParam.setParam(AlgorithmParameters.MinDimension, NetworkArchitecture.calculateActivatedConnectionsSize(numberOfInputNeurons,minMaxInnerN[0]));
	}
	
	public Stock getParent() {
		return parent;
	}

	public void setParent(Stock parent) {
		this.parent = parent;
	}
	
	public String getId() {
		return id;
	}

	//****************************************
	//***             XML                 ****
	//****************************************

	

	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.id=rootElement.getAttribute(FIELD_Id);
		this.setLastUpdate(DateTool.StringToDate(rootElement.getAttribute(FIELD_LastUpdate)));
		
		this.setPeriod(PeriodType.fromString((rootElement.getAttribute(FIELD_Period))));
		this.setDayOfWeekActivated(rootElement.getAttribute(FIELD_DayOfWeekActivated).equals("true"));
		
		//logger.info("Is Activated: "+this.isDayOfWeekActivated()+", XML value: "+rootElement.getAttribute(FIELD_DayOfWeekActivated);
		
		//allTimeSeries.clear();
		networkArchitectures.clear();
		netArchiOptResultMap.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		//TimeSeries ent=new TimeSeries();
		NetworkArchitecture arch=new NetworkArchitecture();
		OptimizationResults results=new OptimizationResults();
		TimeSeriesGroup group=new TimeSeriesGroup(null, "root", false);
		
		if(childElement.getTagName().equals(group.getTagName())){
			rootTimeSeriesGroup=group;
			rootTimeSeriesGroup.init(childElement);
		}
		
		//Weigth optimization parameters
		else if(childElement.getTagName().equals(optLearnParam.getTagName())){
			optLearnParam.init(childElement);
		}
		else if(childElement.getTagName().equals(learnParam.getTagName())){
			learnParam.init(childElement);
		}
		else if(childElement.getTagName().equals(optArchitectureParam.getTagName())){
			optArchitectureParam.init(childElement);
		}
		
		//Regularization parameters
		else if(childElement.getTagName().equals(regBasicParam.getTagName())){
			regBasicParam.init(childElement);
		}
		else if(childElement.getTagName().equals(regOptParam.getTagName())){
			regOptParam.init(childElement);
		}
		else if(childElement.getTagName().equals(regTrainParam.getTagName())){
			regTrainParam.init(childElement);
		}
		
		
		else if(childElement.getTagName().equals(trainingBlocks.getTagName())){
			trainingBlocks.init(childElement);
		}
		else if(childElement.getTagName().equals(arch.getTagName())){
			arch.init(childElement);
			arch.setParent(this);
			networkArchitectures.add(arch);
		}
		else if(childElement.getTagName().equals(results.getTagName())){
			results.init(childElement);
			if(results.getBestResult()!=null)
				netArchiOptResultMap.put(results.getBestResult().getGenome().size(), results);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_Id,this.getId());
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_LastUpdate,DateTool.dateToString( this.getLastUpdate()));
		
		rootElement.setAttribute(FIELD_Period,PeriodType.toString(this.getPeriod()));
		rootElement.setAttribute(FIELD_DayOfWeekActivated,String.valueOf(this.isDayOfWeekActivated()));
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		rootElement.appendChild(optLearnParam.toDomElement(doc));
		rootElement.appendChild(learnParam.toDomElement(doc));
		rootElement.appendChild(optArchitectureParam.toDomElement(doc));
		
		rootElement.appendChild(regBasicParam.toDomElement(doc));
		rootElement.appendChild(regOptParam.toDomElement(doc));
		rootElement.appendChild(regTrainParam.toDomElement(doc));
		
		rootElement.appendChild(trainingBlocks.toDomElement(doc));
		rootElement.appendChild(rootTimeSeriesGroup.toDomElement(doc));
		
		for(NetworkArchitecture ent:networkArchitectures){
			rootElement.appendChild(ent.toDomElement(doc));
		}
		
		for(OptimizationResults results:this.netArchiOptResultMap.values()){
			rootElement.appendChild(results.toDomElement(doc));
		}
		
		
	}

	
	// ****************************************
	// ***           LISTENER              ****
	// ****************************************

	public class ConfigurationEvent extends java.util.EventObject {
		
		private static final long serialVersionUID = -5656579093114367740L;

		// here's the constructor
		public ConfigurationEvent(Object source) {
			super(source);
		}
	}

	public interface ConfigurationListener {
		public void trainingDataSetChanged(EventObject e);
		public void timeSeriesChanged(EventObject e);
	}

	private List<ConfigurationListener> _listeners = new ArrayList<ConfigurationListener>();

	public synchronized void addEventListener(ConfigurationListener listener) {
		_listeners.add(listener);
	}

	public synchronized void removeEventListener(ConfigurationListener listener) {
		_listeners.remove(listener);
	}

	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void fireTrainingDataSetChanged() {
		ConfigurationEvent event = new ConfigurationEvent(this);
		Iterator<ConfigurationListener> i = _listeners.iterator();
		while (i.hasNext()) {
			((ConfigurationListener) i.next())
					.trainingDataSetChanged(event);
		}
	}
	
	
	public synchronized void fireTimeSeriesChanged() {
		ConfigurationEvent event = new ConfigurationEvent(this);
		Iterator<ConfigurationListener> i = _listeners.iterator();
		while (i.hasNext()) {
			((ConfigurationListener) i.next())
					.timeSeriesChanged(event);
		}
		
		LinkedList<String> inputNeurons=getInputNeuronNames();
		
		
		if(networkArchitectures.isEmpty())return;
		
		//logger.info("Time Series Changed!!!");
		//Save the old Max Min Dimension
		NetworkArchitecture f_a=networkArchitectures.getFirst();
		int numberOfInputNeurons=f_a.getNumberOfInputNeurons();
		
		int maxDim=numberOfInputNeurons;
		int minDim=numberOfInputNeurons;
		
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MaxDimension)){
			maxDim=optArchitectureParam.getIntegerParam(AlgorithmParameters.MaxDimension);
		}
		if(optArchitectureParam.hasParamKey(AlgorithmParameters.MinDimension)){
			minDim=optArchitectureParam.getIntegerParam(AlgorithmParameters.MinDimension);
		}
		
		
		int oldMax=NetworkArchitecture.calculateNbOfInnerNeurons(maxDim, numberOfInputNeurons);
		int oldMin=NetworkArchitecture.calculateNbOfInnerNeurons(minDim, numberOfInputNeurons);
		
		for(NetworkArchitecture archi:networkArchitectures){
			//logger.info("Adapt network for archi:"+archi.getId());
			archi.adaptNetwork(inputNeurons);
		}
		
		f_a=networkArchitectures.getFirst();
		numberOfInputNeurons=f_a.getNumberOfInputNeurons();
		
		//Change the Opt Architecture Parameters
		optArchitectureParam.setParam(AlgorithmParameters.MaxDimension,NetworkArchitecture.calculateActivatedConnectionsSize(numberOfInputNeurons, oldMax) );
		optArchitectureParam.setParam(AlgorithmParameters.MinDimension, NetworkArchitecture.calculateActivatedConnectionsSize(numberOfInputNeurons,oldMin));
		
		
		logger.info("All Archi adapted!!!");
		
	}
	
	
	
	
	
}
