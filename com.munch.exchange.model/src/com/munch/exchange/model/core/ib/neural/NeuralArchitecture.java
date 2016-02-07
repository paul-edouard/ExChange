package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
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
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;


@Entity
public class NeuralArchitecture implements Serializable, Copyable<NeuralArchitecture>, CalculateScore{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2002340365998939571L;
	
	private class ProfitAndRisk{
		private double profit;
		private double risk;
		
		private double maxProfit;
		private double maxRisk;
		
		private double tradeProfit;
		
		void ProfitAndRisk(){
			profit=0;
			risk=0;
			maxProfit=0;
			maxRisk=0;
			tradeProfit=0.0;
		}
		
		public void updateProfit(double diff){
			updateProfitOnly(diff);
			tradeProfit+=diff;
			
//			Update the maxProfit
			if(profit>maxProfit)
				maxProfit=profit;
			
//			Calculate the risk
			risk=profit-maxProfit;
			
//			Update the maxRisk
			if(maxRisk<-risk){
				maxRisk=-risk;
			}
		}
		
		public void updateProfitOnly(double diff){
			profit+=diff;
		}
		
		public void resetTradeProfit(){
			tradeProfit=0;
		}

		public double getProfit() {
			return profit;
		}

		public double getRisk() {
			return risk;
		}


		public double getMaxProfit() {
			return maxProfit;
		}

		

		public double getMaxRisk() {
			return maxRisk;
		}

		public double getTradeProfit() {
			return tradeProfit;
		}

		public void setRisk(double risk) {
			this.risk = risk;
		}
		
		

		
		
		
		
	}
	
	
	public static enum  ArchitectureType {
		FeedFoward, Elman, Jordan, Neat;
		
		public NeuralNetworkPattern getPattern(){
			if(this == FeedFoward){
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
 	private int id;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	private NeuralConfiguration neuralConfiguration;

	@OneToMany(mappedBy="neuralArchitecture",cascade=CascadeType.ALL)
	private List<NeuralNetwork> neuralNetworks=new LinkedList<NeuralNetwork>();
	
	@Enumerated(EnumType.STRING)
	private ArchitectureType type=ArchitectureType.FeedFoward;
	
	private String hiddenLayerDescription="";
	
	private long volume=10;
	
	private double blockProfitLimit=10000.0;
	
	private double tradeProfitLimit=500.0;
	
	
	@Enumerated(EnumType.STRING)
	private Activation activation=Activation.TANH;
	
	@Transient
	private Equilateral equilateralOutput;
	
	@Transient
	private NormalizedField normalizedTotalProfitLimit;
	
	@Transient
	private NormalizedField normalizedTradeProfitLimit;
	
	@Transient
	private NeuralInputComponent[] components;
	
	
	
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
	public boolean prepareScoring(){
		
		normalizedTotalProfitLimit=new NormalizedField(NormalizationAction.Normalize,
				this.getName()+" Total Profit Limit",
				-blockProfitLimit, blockProfitLimit, -0.9, 0.9);
		
		normalizedTradeProfitLimit=new NormalizedField(NormalizationAction.Normalize,
				this.getName()+" Trade Profit Limit",
				-blockProfitLimit, blockProfitLimit, -0.9, 0.9);
		
		
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
				components[i].createNormalizedValues();
				i++;
			}
		}
		
		return false;
	}
	
	
	@Override
	public double calculateScore(MLMethod method) {
		BasicNetwork basicNetwork=(BasicNetwork) method;
		
		ProfitAndRisk profitAndRiskTotal=calculateProfitAndRiskOfBlocks(
				neuralConfiguration.getTrainingBlocks(), basicNetwork);
		
//		Loop over the training Blocks
//		double totalProfit=0.0;
//		double maxRisk=0.0;
//		for(LinkedList<IbBar> block:neuralConfiguration.getTrainingBlocks()){
//			
//			ProfitAndRisk profitAndRisk=calculateProfitAndRiskOfBlock(block, basicNetwork);
//			
//			totalProfit+=profitAndRisk.getProfit();
//			if(maxRisk<profitAndRisk.getMaxRisk())
//				maxRisk=profitAndRisk.getMaxRisk();
//			
//		}
		
		
//		System.out.println("Profit: "+totalProfit);
//		System.out.println("Risk: "+maxRisk);
//		
//		System.out.println("Score: "+(totalProfit-maxRisk));
		
//		return totalProfit-maxRisk;
		
		
		return profitAndRiskTotal.getProfit();
		
	}
	
	@Override
	public boolean shouldMinimize() {
		return false;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
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
				
		
		
//		Get the number of output
		if(neuralConfiguration.getContract().allowShortPosition()){
			equilateralOutput=new Equilateral(3, -1, 1);
		}
		else{
			equilateralOutput=new Equilateral(2, -1, 1);
		}
		int nbOfOutputNeurons=encode(Position.NEUTRAL).length;
		
		int[] hiddenLayers=hiddenLayerDescriptionToIntArray();
		
		//Create the pattern
		NeuralNetworkPattern pattern=type.getPattern();
		if(pattern==null)return null;
		
		//Set the pattern
		pattern.setInputNeurons(nbOfInputNeurons);
		for(int i=0;i<hiddenLayers.length;i++){
			pattern.addHiddenLayer(hiddenLayers[i]);
		}
		pattern.setOutputNeurons(nbOfOutputNeurons);
		pattern.setActivationFunction(activation.getFunc());
		
		BasicNetwork network = (BasicNetwork)pattern.generate();
		network.reset();
		
		return network;
	}
	
	private int[] hiddenLayerDescriptionToIntArray(){
		if(hiddenLayerDescription == null|| hiddenLayerDescription.isEmpty())
			return null;
		
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
		if(equilateralOutput==null)return Position.NEUTRAL;
		
		return Position.getPosition(equilateralOutput.decode(activations));
		
	}
	
	
	
	private ProfitAndRisk calculateProfitAndRiskOfBlock(LinkedList<IbBar> block,BasicNetwork basicNetwork){
		
		ProfitAndRisk profitAndRisk=new ProfitAndRisk();
		if(block==null || block.isEmpty())return profitAndRisk;
		
		basicNetwork.clearContext();
		
		Position lastPosition=Position.NEUTRAL;
		IbBar previewBar=block.get(0);
		
		
		long[] relTraindingPeriod=neuralConfiguration.getContract().
				getRelativeTraidingPeriod(previewBar.getTimeInMs());
				
		int i=0;
		for(IbBar bar:block){
			
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
			if(time>=relTraindingPeriod[1]){
				if(lastPosition!=Position.NEUTRAL){
//					Sold the last position
					IbCommission com=this.getNeuralConfiguration().getContract().getCommission();
					if(com!=null){
						double profitDiff=-com.calculate(volume, price);
						profitAndRisk.updateProfit(profitDiff);
					}
				}
				break;
			}
//			#################################################################
			
			
//			#######################################################			
//			####  3. Create the new data raw for the network   ####
//			#######################################################
			int daptedValueIndex=neuralConfiguration.getAdpatedTimesMap().get(time);
			
//			Create the input data raw
			MLData input = new BasicMLData(basicNetwork.getInputCount());
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
			MLData output = basicNetwork.compute(input);
//			Test if the stock exchange is open otherwise the position will be ignore
			if(relTraindingPeriod[0]>time)continue;
			
			Position position=decode(output.getData());
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
//				Reset the trade profit
				profitAndRisk.resetTradeProfit();
			}
			
	
			lastPosition=position;
			previewBar=bar;
		}
		
		
		return profitAndRisk;
		
	}
	
	private ProfitAndRisk calculateProfitAndRiskOfBlocks(LinkedList<LinkedList<IbBar>> blocks,BasicNetwork basicNetwork ){
		ProfitAndRisk profitAndRiskTotal=new ProfitAndRisk();
		
		for(LinkedList<IbBar> block:blocks){
			
			ProfitAndRisk profitAndRiskOfBlock=calculateProfitAndRiskOfBlock(block, basicNetwork);
			
			profitAndRiskTotal.updateProfitOnly(profitAndRiskOfBlock.getProfit());
			
			if(profitAndRiskTotal.getRisk()<profitAndRiskOfBlock.getMaxRisk())
				profitAndRiskTotal.setRisk(profitAndRiskOfBlock.getMaxRisk());
			
		}
		
		return profitAndRiskTotal;
		
	}
	
	
		
	public void addNeuralNetwork(BasicNetwork basicNetwork){
		NeuralNetwork network=new NeuralNetwork();
		network.setNetwork(basicNetwork);
		network.setNeuralArchitecture(this);
		this.neuralNetworks.add(network);
	}
	
	
	public void evaluateProfitAndRiskOfAllNetworks(){
		prepareScoring();
		
		for(NeuralNetwork network:this.neuralNetworks){
			
//			Evaluate the Training data set
			ProfitAndRisk profitAndRiskTraining=calculateProfitAndRiskOfBlocks(
					neuralConfiguration.getTrainingBlocks(), network.getNetwork());
			network.setScore(profitAndRiskTraining.getProfit());
			network.setTrainingProfit(profitAndRiskTraining.getProfit());
			network.setTrainingRisk(profitAndRiskTraining.getRisk());
			
//			Evaluate the back testing data set
			ProfitAndRisk profitAndRiskBackTesting=calculateProfitAndRiskOfBlocks(
					neuralConfiguration.getBackTestingBlocks(), network.getNetwork());
			network.setBackTestingProfit(profitAndRiskBackTesting.getProfit());
			network.setBackTestingRisk(profitAndRiskBackTesting.getRisk());
			
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
		
		Equilateral equilateralOutput=new Equilateral(3, -1, 1);
		double[] encodeLong=equilateralOutput.encode(Position.getInt(Position.LONG));
		System.out.println("Encode LONG: "+Arrays.toString(encodeLong));
		
		double[] encodeNeutral=equilateralOutput.encode(Position.getInt(Position.NEUTRAL));
		System.out.println("Encode NEUTRAL: "+Arrays.toString(encodeNeutral));
		
		double[] encodeShort=equilateralOutput.encode(Position.getInt(Position.SHORT));
		System.out.println("Encode SHORT: "+Arrays.toString(encodeShort));
		
		double[] testDecode={-0.7,-0.7};
		Position position=Position.getPosition(equilateralOutput.decode(testDecode));
		System.out.println("Decoden "+Arrays.toString(testDecode)+": "+position.toString());
		
	}
	
	
	

}
