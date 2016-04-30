package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationRamp;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.Equilateral;
import org.encog.ml.MLContext;
import org.encog.ml.MLInput;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.population.Population;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.munch.exchange.model.core.encog.CalculateNovelty;
import com.munch.exchange.model.core.encog.NoveltySearchGenome;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.IbBar;


@Entity
@DiscriminatorColumn(name="ARCHI_TYPE")
public class NeuralArchitecture implements Serializable, Copyable<NeuralArchitecture>, CalculateNovelty{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2002340365998939571L;
	
	private static final int NumberOfInternNeurons = 3;
	
	
	
	
	public static enum  ArchitectureType {
		FeedFoward, Elman, Jordan, Neat, HyperNeat, NoveltySearchNeat;
		
		public NeuralNetworkPattern getPattern(){
			if(this == FeedFoward || this == Neat || this == HyperNeat || this==NoveltySearchNeat){
				return new FeedForwardPattern();
			}
			else if(this == Elman){
				return new ElmanPattern();
			}
			else if(this == Jordan){
				return new JordanPattern();
			}
			else{
				return null;
			}
		}
	}
	
	public static enum Position{
		LONG,NEUTRAL,SHORT;
		
		public static int getInt(Position position){
			if(position==LONG){
				return 0;
			}
			else if(position==NEUTRAL){
				return 1;
			}
			else{
				return 2;
			}
		}
		
		public static int getSignal(Position position){
			if(position==LONG){
				return 1;
			}
			else if(position==NEUTRAL){
				return 0;
			}
			else{
				return -1;
			}
		}
		
		
		public static Position getPosition(int intPos){
			if(intPos==0)
				return LONG;
			else if(intPos==2){
				return SHORT;
			}
			else{
				return NEUTRAL;
			}
		}
	}
	
	public static enum Activation{
		TANH,Linear,LOG,Ramp;
		
		public ActivationFunction getFunc(){
			if(this == TANH){
				return new ActivationTANH();
			}
			else if(this == Linear){
				return new ActivationLinear();
			}
			else if(this == LOG){
				return new ActivationLOG();
			}
			else if(this == Ramp){
				return new ActivationRamp();
			}
			else{
				return new ActivationTANH();
			}
		}
	}
	
	public static enum TrainingMethod{
		GENETIC_ALGORITHM, SIMULATED_ANNEALING;
	}
	

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
 	protected int id;
	
	protected String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	protected NeuralConfiguration neuralConfiguration;

	@OneToMany(mappedBy="neuralArchitecture",cascade=CascadeType.ALL)
	protected List<NeuralNetwork> neuralNetworks=new LinkedList<NeuralNetwork>();
	
	@Enumerated(EnumType.STRING)
	protected ArchitectureType type=ArchitectureType.FeedFoward;
	
	protected String hiddenLayerDescription="";
	
	protected long volume=10;
	
	protected double blockProfitLimit=10000.0;
	
	protected double tradeProfitLimit=500.0;
	
	
	
	@Enumerated(EnumType.STRING)
	protected Activation activation=Activation.TANH;
	
	@Transient
	protected Equilateral equilateralOutput;
	
	@Transient
	protected NormalizedField normalizedTotalProfitLimit;
	
	@Transient
	protected NormalizedField normalizedTradeProfitLimit;
	
	@Transient
	protected NeuralInputComponent[] components;
	
	
	
	public NeuralArchitecture() {
		super();
	}

	@Override
	public NeuralArchitecture copy() {
		NeuralArchitecture cp=new NeuralArchitecture();
		
		cp.id=this.id;
		cp.name=this.name;
//		cp.neuralConfiguration=this.neuralConfiguration;
		cp.type=this.type;
		cp.blockProfitLimit=this.blockProfitLimit;
		cp.tradeProfitLimit=this.tradeProfitLimit;
		cp.volume=this.volume;
		cp.hiddenLayerDescription=this.hiddenLayerDescription;
		cp.activation=this.activation;
		
		cp.neuralNetworks=new LinkedList<NeuralNetwork>();
		for(NeuralNetwork network:this.neuralNetworks){
			NeuralNetwork network_cp=network.copy();
			network_cp.setNeuralArchitecture(cp);
			cp.neuralNetworks.add(network_cp);
		}
		
		return cp;
	}
	
	/**
	 * try to prepare the scoring 
	 * 
	 * @return
	 */
	public boolean prepareScoring(double high, double low){
		
		if(neuralConfiguration.getContract().allowShortPosition()){
			equilateralOutput=new Equilateral(3, high, low);
		}
		else{
			equilateralOutput=new Equilateral(2, high, low);
		}
		
		
		normalizedTotalProfitLimit=new NormalizedField(NormalizationAction.Normalize,
				this.getName()+" Total Profit Limit",
				blockProfitLimit, -blockProfitLimit, high*0.9, low*0.9);
		
		normalizedTradeProfitLimit=new NormalizedField(NormalizationAction.Normalize,
				this.getName()+" Trade Profit Limit",
				blockProfitLimit, -blockProfitLimit, high*0.9, low*0.9);
		
		
//		Create the Component Array
		int nbOfComponents=0;
		for (NeuralInput input : neuralConfiguration.getNeuralInputs()) {
			nbOfComponents+=input.getComponents().size();
		}
		
		components=new NeuralInputComponent[nbOfComponents];
		int i=0;
		for (NeuralInput input : neuralConfiguration.getNeuralInputs()) {
			for(NeuralInputComponent component:input.getComponents()){
				components[i]=component;
				components[i].createNormalizedValues(high*0.9, low*0.9);
				i++;
			}
		}
		
		return false;
	}
	
	public Substrate createHyperNeatSubstrat(){
		Substrate result = new Substrate(3);
		
		double maxInputSize=0;
		for (NeuralInput input : neuralConfiguration.getNeuralInputs()) {
			if(input.getComponents().size()>maxInputSize)
				maxInputSize=input.getComponents().size();
		}
		
		double colTick = -  2.0 / maxInputSize;
		double rawTick = -  2.0 / (neuralConfiguration.getNeuralInputs().size()+3);
		
		double colOrig = -1.0 + (colTick / 2.0);
		double rawOrig = -1.0 + (rawTick / 2.0);
		
		
//		Create the input neurons
		for (int row = 0; row < neuralConfiguration.getNeuralInputs().size(); row++) {
			NeuralInput input=neuralConfiguration.getNeuralInputs().get(row);
			for (int col = 0; col < input.getComponents().size(); col++) {
//				NeuralInputComponent component=input.getComponents().get(col);
				SubstrateNode inputNode = result.createInputNode();
				inputNode.getLocation()[0] = -1;
				inputNode.getLocation()[1] = colOrig + (row * colTick);
				inputNode.getLocation()[2] = rawOrig + (col * rawTick);
			}
		}
		
		
//		Create the inputs for the intern neurons (Current Trade Profit, Current Block Profit, current risk )
		int size=neuralConfiguration.getNeuralInputs().size();
		for(int i=0;i<NumberOfInternNeurons;i++){
			SubstrateNode inputNode = result.createInputNode();
			inputNode.getLocation()[0] = -1;
			inputNode.getLocation()[1] = colOrig ;
			inputNode.getLocation()[2] = rawOrig + ((size+i) * rawTick);
		}
		
//		create the output neurons
		int nbOfOutputs=getNumberOfOutputNeurons(1,0);
		double rawOutputTick = -  2.0 / nbOfOutputs;
		double rawOutputOrig= -1.0 + (rawOutputTick / 2.0);
		
		for(int orow = 0; orow <nbOfOutputs;orow++){
			SubstrateNode outputNode = result.createOutputNode();
			outputNode.getLocation()[0] = 1;
			outputNode.getLocation()[1] = rawOutputOrig + (orow * rawOutputTick);
			outputNode.getLocation()[2] =0;
			
			// link this output node to every input node
			for (SubstrateNode inputNode : result.getInputNodes()) {
				result.createLink(inputNode, outputNode);
			}
		}
		
		
		
		return result;
	}
	
	
	@Override
 	public double calculateScore(MLMethod method) {
		
		NeuralNetworkRating profitAndRiskTotal=calculateProfitAndRiskOfBlocks(
				neuralConfiguration.getTrainingBlocks(), method);
		
		return profitAndRiskTotal.getScore();
		
	}
	
	@Override
	public boolean shouldMinimize() {
		return false;
	}

	@Override
	public boolean requireSingleThreaded() {
//		return true;
		return false;
	}
	
	@Override
	public void calculateNovelty(NoveltySearchGenome n_genome,
			List<NoveltySearchGenome> allGenomes, int nbOfNearestNeighbor) {
		
//		System.out.println("Calculate novelty");
		LinkedList<NoveltySearchGenome> nearestNeighbors=extractNeighbors(n_genome, allGenomes, nbOfNearestNeighbor);
		
		double novelty=0;
		for(NoveltySearchGenome neighbor:nearestNeighbors){
			novelty+=neighbor.getRelativeDistance();
		}
		novelty/=nearestNeighbors.size();
	
		n_genome.setNovelty(novelty);
		
		
		System.out.println("Genome: novelty="+n_genome.getNovelty()+", behavior="+n_genome.getBehavior());
		
//		Now the score is really set equals to the novelty
		n_genome.setScore(novelty);
		n_genome.setAdjustedScore(novelty);
	}
	
	private LinkedList<NoveltySearchGenome> extractNeighbors(NoveltySearchGenome n_genome, 
			List<NoveltySearchGenome> allGenomes,  int nbOfNearestNeighbor){
		
		LinkedList<NoveltySearchGenome> nearestNeighbors=new LinkedList<NoveltySearchGenome>();
		
		for(NoveltySearchGenome genome:allGenomes){
			if(genome==n_genome)continue;
			
//			double relativeDistance=Math.abs(genome.getBehavior()-n_genome.getBehavior());
			double relDist=genome.getRating().calculateRelativDistance(n_genome.getRating());
//			System.out.println("Relativ distance: "+relDist);
			genome.setRelativeDistance(relDist);
			
			
			if(nearestNeighbors.isEmpty()){
				nearestNeighbors.add(genome);
				continue;
			}
			
//			The current relative distance is lower than the lowest one of the current neighbors
			if(nearestNeighbors.size()==nbOfNearestNeighbor &&
					nearestNeighbors.getLast().getRelativeDistance() < genome.getRelativeDistance()){
				continue;
			}
			
			int i=0;
			boolean isInserted=false;
			for(NoveltySearchGenome neighbor:nearestNeighbors){
				if(genome.getRelativeDistance() < neighbor.getRelativeDistance()){
					nearestNeighbors.add(i, genome);
					isInserted=true;
					break;
				}
				i++;
			}
			
			if(!isInserted)
				nearestNeighbors.add(genome);
			
			if(nearestNeighbors.size()>nbOfNearestNeighbor)
				nearestNeighbors.removeLast();
			
		}
		
		return nearestNeighbors;
		
	}
	
	
	
	

	@Override
	public NeuralNetworkRating calculateBehavior(MLMethod method) {
		// TODO Auto-generated method stub
		NeuralNetworkRating profitAndRiskTotal=calculateProfitAndRiskOfBlocks(
				neuralConfiguration.getTrainingBlocks(), method);
		return profitAndRiskTotal;

	}
	
	
	public  BasicNetwork createNetwork(){
		
		
//		Get the number of inputs
		int nbOfInputNeurons=0;
		for (NeuralInput input : neuralConfiguration.getNeuralInputs()) {
			nbOfInputNeurons+=input.getComponents().size();
		}
		
//		Block Profit
		nbOfInputNeurons++;
//		Block Risk
		nbOfInputNeurons++;
//		Trade Profit
		nbOfInputNeurons++;
		
		

		
		int[] hiddenLayers=hiddenLayerDescriptionToIntArray();
		
		//Create the pattern
		NeuralNetworkPattern pattern=type.getPattern();
		if(pattern==null)return null;
		
		//Set the pattern
		pattern.setInputNeurons(nbOfInputNeurons);
		for(int i=0;i<hiddenLayers.length;i++){
			pattern.addHiddenLayer(hiddenLayers[i]);
		}
		pattern.setOutputNeurons(getNumberOfOutputNeurons(1,-1));
		pattern.setActivationFunction(activation.getFunc());
		
		BasicNetwork network = (BasicNetwork)pattern.generate();
		network.reset();
		
		return network;
	}
	
	private int getNumberOfOutputNeurons(double high, double low){
//		Get the number of output
		if(neuralConfiguration.getContract().allowShortPosition()){
			equilateralOutput=new Equilateral(3, high, low);
		}
		else{
			equilateralOutput=new Equilateral(2, high, low);
		}
		int nbOfOutputNeurons=encode(Position.NEUTRAL).length;
		
		return nbOfOutputNeurons;
	}
	
	
	private int[] hiddenLayerDescriptionToIntArray(){
		if(hiddenLayerDescription == null|| hiddenLayerDescription.isEmpty())
			return new int[0];
		
		String[] hidddenLayersDes=hiddenLayerDescription.split(",");
		int[] hiddenLayers=new int[hidddenLayersDes.length];
		for(int i=0;i<hidddenLayersDes.length;i++){
			hiddenLayers[i]=Integer.valueOf(hidddenLayersDes[i]);
		}
		
		return hiddenLayers;
	}
	
	
	private double[] encode(Position pos){
		if(equilateralOutput==null)return null;
		
		return equilateralOutput.encode(Position.getInt(pos));
	}
	
	private Position decode(double[] activations){
//		System.out.println("Activation: "+Arrays.toString(activations));
		
		if(equilateralOutput==null)return Position.NEUTRAL;
		
		return Position.getPosition(equilateralOutput.decode(activations));
		
	}
	
	
	
	/**
	 * Calculate the profit of the bar block. The most of time this are the bar of a week or of a day
	 * 
	 * @param block
	 * @param method
	 * @param nbOfInput
	 * @return
	 */
	private NeuralNetworkRating calculateProfitAndRiskOfBlock(LinkedList<ExBar> block,MLMethod method, int nbOfInput){
		
		
//		System.out.println("New Block: "+block.size());
		
		NeuralNetworkRating profitAndRisk=new NeuralNetworkRating();
		profitAndRisk.setMethod(method);
		if(block==null || block.isEmpty() )return profitAndRisk;
		
		if(method instanceof MLContext){
			((MLContext)method).clearContext();
		}
		
		Position lastPosition=Position.NEUTRAL;
		ExBar previewBar=block.getFirst();
		ExBar lastBar=block.getLast();
		profitAndRisk.newPosition(previewBar.getTimeInMs(),lastPosition);
		
		long[] relTraindingPeriod=neuralConfiguration.getContract().
				getRelativeTraidingPeriod(previewBar.getTimeInMs());
				
		int i=0;
//		int nbOfPosition=0;
		for(ExBar bar:block){
			
//			#####################################################				
//			####  1. Check if the bar should be calculated   ####
//			#####################################################	
//			Jump first bar
			if(i==0){i++;continue;}
			
			long time=bar.getTimeInMs();
			if(!neuralConfiguration.getAdpatedTimesMap().containsKey(time))continue;
//			#####################################################		
			
			
//			#################################################################
//			####  2. Update the profit if the current position is open   ####
//			#################################################################
			double previewPrice=previewBar.getClose();
			double price=bar.getClose();
			
//			Add the profit and calculate the risk
			if(lastPosition!=Position.NEUTRAL){
				double diffProfit=Position.getSignal(lastPosition)*(price-previewPrice)*volume;
				profitAndRisk.updateProfit(diffProfit);
			}
			
//			Close the open position before living
			if(time>=relTraindingPeriod[1] || bar==lastBar){
				if(lastPosition!=Position.NEUTRAL){
//					Sold the last position
					IbCommission com=this.getNeuralConfiguration().getContract().getCommission();
					if(com!=null){
						double profitDiff=-com.calculate(volume, price);
						profitAndRisk.updateProfit(profitDiff);
					}
					
					profitAndRisk.newPosition(time,Position.NEUTRAL);
//					System.out.println("Sold last Position Profit: "+profitAndRisk.getProfit()+" last Position: "+lastPosition.toString());
					
				}
				break;
			}
//			#################################################################
			
			
//			#######################################################			
//			####  3. Create the new data raw for the network   ####
//			#######################################################
			int daptedValueIndex=neuralConfiguration.getAdpatedTimesMap().get(time);
			
//			Create the input data raw
			MLData input = new BasicMLData(nbOfInput);
			for(int j=0;j<components.length;j++){
				input.setData(j, components[j].getNormalizedAdaptedValueAt(daptedValueIndex));
			}
//			Add the Current Trade Profit
			input.setData(components.length,normalizedTradeProfitLimit.normalize(profitAndRisk.getTradeProfit()));
//			Add the Current Block Profit
			input.setData(components.length+1,normalizedTotalProfitLimit.normalize(profitAndRisk.getProfit()));
//			Add the current risk profit
			input.setData(components.length+2,normalizedTotalProfitLimit.normalize(profitAndRisk.getRisk()));
//			#######################################################	
			
			
//			#######################################################			
//			####  4. Calculate the new position                ####
//			#######################################################
//			Compute the raw
			MLData output = ((MLRegression)method).compute(input);
//			Test if the stock exchange is open otherwise the position will be ignore
			if(relTraindingPeriod[0]>time)continue;
			Position position=decode(output.getData());
//			i++;
//			
//			if(i%500==0){
//			System.out.println("Adapted value index: "+daptedValueIndex+", Input: "+Arrays.toString(input.getData())+", output: "+Arrays.toString(output.getData())+", Position: "+position.toString());
//			}
//			System.out.println("Profit: "+profitAndRisk.getProfit()+"Position: "+position.toString());
//			#######################################################
			
			
//			#######################################################			
//			####  5. Calculate the commission                  ####
//			#######################################################
			
//			Modification of the position, the commission will reduce the profit
			if(position!=lastPosition){
				
				double diffSignal=Position.getSignal(position)-Position.getSignal(lastPosition);
				double absDiffSignal=Math.abs(diffSignal);
			
				IbCommission com=this.getNeuralConfiguration().getContract().getCommission();
				if(com!=null){
					double profitDiff=-absDiffSignal*com.calculate(volume, price);
					profitAndRisk.updateProfit(profitDiff);
				}
				
//				System.out.println("Adapted value index: "+daptedValueIndex+", Input: "+Arrays.toString(input.getData())+", output: "+Arrays.toString(output.getData())+", Position: "+position.toString());
//				System.out.println("Modification Position Profit: "+profitAndRisk.getProfit()+"Position: "+position.toString());
				
//				Reset the trade profit
				profitAndRisk.resetTradeProfit();
				profitAndRisk.newPosition(time,position);
//				nbOfPosition++;
			}
			
	
			lastPosition=position;
			previewBar=bar;
		}
		
		
//		System.out.println("Nb of eval: "+i+", nb. of positions: "+nbOfPosition);
		
		
		return profitAndRisk;
		
	}
	
	public NeuralNetworkRating calculateProfitAndRiskOfBlocks(LinkedList<LinkedList<ExBar>> blocks,MLMethod method){
		
		
		int nbOfInputs=((MLInput) method).getInputCount();
		
		NeuralNetworkRating profitAndRiskTotal=new NeuralNetworkRating();
		profitAndRiskTotal.setMethod(method);
		
		for(LinkedList<ExBar> block:blocks){
			
//			System.out.println("Calculate block!");
			
			NeuralNetworkRating profitAndRiskOfBlock=calculateProfitAndRiskOfBlock(block, method, nbOfInputs);
			
//			Calculate the block Id
			ExBar bar=(ExBar)block.get(0);

			switch (neuralConfiguration.getSplitStrategy()) {
			case WEEK:
				Calendar sunday=IbBar.getLastSundayOfDate(bar.getTimeInMs());
				profitAndRiskOfBlock.setId(sunday.getTimeInMillis());
				
			case DAY:
				Calendar day=IbBar.getCurrentDayOf(bar.getTimeInMs());
				profitAndRiskOfBlock.setId(day.getTimeInMillis());
			}
			
//			Save the block rating
			profitAndRiskTotal.addChildren(profitAndRiskOfBlock);
			
		}
		
		//Calculate the score
		double mean=0;
		for(NeuralNetworkRating child:profitAndRiskTotal.getChildren()){
			mean+=child.getProfit();
		}
		mean/=profitAndRiskTotal.getChildren().size();
		
		double varianz=0;
		for(NeuralNetworkRating child:profitAndRiskTotal.getChildren()){
			double diff=child.getProfit()-mean;
			varianz+=diff*diff;
		}
		varianz/=profitAndRiskTotal.getChildren().size();
		
//		double score=(profitAndRiskTotal.getProfit()/profitAndRiskTotal.getChildren().size()-Math.sqrt(varianz)/2);
		
		double score=profitAndRiskTotal.getProfit()/profitAndRiskTotal.getChildren().size();
		
		
		profitAndRiskTotal.setScore(score);
		
		
		return profitAndRiskTotal;
		
	}
	
		
	public void addNeuralNetwork(BasicNetwork basicNetwork){
		NeuralNetwork network=new NeuralNetwork();
		network.setNetwork(basicNetwork);
		network.setNeuralArchitecture(this);
		this.neuralNetworks.add(network);
	}
	
	public void addNeuralNetwork(Population population){
		NeuralNetwork network=new NeuralNetwork();
		network.setNEATPopulation(population);
		network.setNeuralArchitecture(this);
		this.neuralNetworks.add(network);
	}
	
	public void addNeuralNetwork(Population population,String populationName,
			Population paretoPopulation, String paretoName){
		NeuralNetwork network=new NeuralNetwork();
		network.setNEATPopulation(population);
		network.setNetworkName(populationName);
		
		network.setParetoPopulation(paretoPopulation);
		network.setParetoName(paretoName);
		
		network.setNeuralArchitecture(this);
		this.neuralNetworks.add(network);
	}
	
	
	/**
	 * Evaluation of all the networks this could be done in different threads
	 */
	public void evaluateProfitAndRiskOfAllNetworks(){
		prepareScoring(1,-1);
		
		for(NeuralNetwork network:this.neuralNetworks){
			
			
			if(this.getType()==ArchitectureType.Neat ||
					this.getType()==ArchitectureType.NoveltySearchNeat || 
					this.getType()==ArchitectureType.HyperNeat){

				
				network.evaluateNEATPopulation(5);
				network.evaluateParetoPopulation();
			
			}
			
		
			else{
				MLMethod method=null;
				method=network.getNetwork();
				
//				Evaluate the Training data set
				NeuralNetworkRating trainingRating=calculateProfitAndRiskOfBlocks(
						neuralConfiguration.getTrainingBlocks(), method);
				trainingRating.setName("Training");
				network.setTrainingRating(trainingRating);
				
				
//				Evaluate the back testing data set
				NeuralNetworkRating backTestingRating=calculateProfitAndRiskOfBlocks(
						neuralConfiguration.getBackTestingBlocks(), method);
				backTestingRating.setName("Back Testing");
				network.setBackTestingRating(backTestingRating);
				
				
			}
			

			
			
		}
	}
	
	
//	#######################
//	##   GETTER & SETTER ##
//	#######################

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Activation getActivation() {
		return activation;
	}

	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	public NeuralConfiguration getNeuralConfiguration() {
		return neuralConfiguration;
	}

	public void setNeuralConfiguration(NeuralConfiguration neuralConfiguration) {
		this.neuralConfiguration = neuralConfiguration;
	}

	public List<NeuralNetwork> getNeuralNetworks() {
		return neuralNetworks;
	}

	public void setNeuralNetworks(List<NeuralNetwork> neuralNetworks) {
		this.neuralNetworks = neuralNetworks;
	}

	public ArchitectureType getType() {
		return type;
	}

	public void setType(ArchitectureType type) {
		this.type = type;
	}

	
	public String getHiddenLayerDescription() {
		return hiddenLayerDescription;
	}

	public void setHiddenLayerDescription(String hiddenLayerDescription) {
		this.hiddenLayerDescription = hiddenLayerDescription;
	}

	public Equilateral getEquilateralOutput() {
		return equilateralOutput;
	}

	public void setEquilateralOutput(Equilateral equilateralOutput) {
		this.equilateralOutput = equilateralOutput;
	}
	
	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public double getBlockProfitLimit() {
		return blockProfitLimit;
	}

	public void setBlockProfitLimit(double totalProfitLimit) {
		this.blockProfitLimit = totalProfitLimit;
	}

	public double getTradeProfitLimit() {
		return tradeProfitLimit;
	}

	public void setTradeProfitLimit(double traideProfitLimit) {
		this.tradeProfitLimit = traideProfitLimit;
	}
	


	public static void main(String args[])
	{
		
		Equilateral equilateralOutput=new Equilateral(3, 1, 0);
		double[] encodeLong=equilateralOutput.encode(Position.getInt(Position.LONG));
		System.out.println("Encode LONG: "+Arrays.toString(encodeLong));
		
		double[] encodeNeutral=equilateralOutput.encode(Position.getInt(Position.NEUTRAL));
		System.out.println("Encode NEUTRAL: "+Arrays.toString(encodeNeutral));
		
		double[] encodeShort=equilateralOutput.encode(Position.getInt(Position.SHORT));
		System.out.println("Encode SHORT: "+Arrays.toString(encodeShort));
		
		double[] testDecode={0.3,0.8};
		Position position=Position.getPosition(equilateralOutput.decode(testDecode));
		System.out.println("Decoden "+Arrays.toString(testDecode)+": "+position.toString());
		
	}

	
	
	
	

}
