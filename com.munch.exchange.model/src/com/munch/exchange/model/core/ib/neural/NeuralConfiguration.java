package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;

@Entity
public class NeuralConfiguration implements Serializable, Copyable<NeuralConfiguration>,Comparable<NeuralConfiguration>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8390687932325619962L;
	
	public static enum ReferenceData {
		MID_POINT, BID_AND_ASK;
	}
	
	public static enum SplitStrategy {
		WEEK, DAY;
	}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	private String name;
	
	private long creationDate;
	
//	Input
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralInput> neuralInputs=new LinkedList<NeuralInput>();
	
	/**
	 * Map used in order to save the collected bars
	 */
	@Transient
	private HashMap<String ,List<IbBar>> neuralInputsBarsCollector=new HashMap<String ,List<IbBar>>();
	
//	Training Data
	
	@Enumerated(EnumType.STRING)
	private BarSize size=BarSize._1_min;
	
	private int percentOfTrainingData=60;
	
	@Enumerated(EnumType.STRING)
	private ReferenceData referenceData=ReferenceData.MID_POINT;
	
	@Enumerated(EnumType.STRING)
	private SplitStrategy splitStrategy=SplitStrategy.WEEK;
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralTrainingElement> neuralTrainingElements=new LinkedList<NeuralTrainingElement>();
	
	@Transient
	private List<IbBar> allMidPointBars=new LinkedList<IbBar>();
	
	@Transient
	private List<IbBar> allAskBars=new LinkedList<IbBar>();
	
	@Transient
	private List<IbBar> allBidBars=new LinkedList<IbBar>();
	
	@Transient
	private LinkedList<LinkedList<IbBar>> allBlocks=new LinkedList<LinkedList<IbBar>>();
	
	@Transient
	private LinkedList<LinkedList<IbBar>> trainingBlocks=new LinkedList<LinkedList<IbBar>>();
	
	@Transient
	private LinkedList<LinkedList<IbBar>> backTestingBlocks=new LinkedList<LinkedList<IbBar>>();
	
	@Transient
	private HashMap<Long, Integer> adpatedTimesMap=new HashMap<Long, Integer>();
	
	
//	Architectures
	
	/**
	 * This are the architectures that create and use for training, once trained some will be copied and passed to the isolated ones
	 */
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralArchitecture> neuralArchitectures=new LinkedList<NeuralArchitecture>();
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralArchitecture> isolatedArchitectures=new LinkedList<NeuralArchitecture>();
	
	
 	public NeuralConfiguration() {
		super();
	}



	@Override
	public NeuralConfiguration copy() {
		NeuralConfiguration c=new NeuralConfiguration();
		
		c.id=id;
		
		c.name=name;
		c.creationDate=creationDate;
		c.contract=contract.copy();
		
		/*
		c.neuralInputs.clear();
		for(NeuralInput input:neuralInputs){
			c.neuralInputs.add(input.copy());
		}
		*/
		
		c.percentOfTrainingData=percentOfTrainingData;
		c.referenceData=referenceData;
		c.splitStrategy=splitStrategy;
		c.neuralTrainingElements=new LinkedList<NeuralTrainingElement>();
		for(NeuralTrainingElement element:neuralTrainingElements){
			NeuralTrainingElement e_cp=element.copy();
			e_cp.setNeuralConfiguration(c);
			c.neuralTrainingElements.add(e_cp);
		}
		
		
		
		return c;
	}

	@Override
	public int compareTo(NeuralConfiguration o) {
		long diff=this.creationDate-o.creationDate;
		if(diff>0)return 1;
		else if(diff==0)return 0;
		return -1;
	}
	
	public boolean isResetMinMaxNeeded(){
		for(NeuralInput input:neuralInputs){
			if(!(input instanceof NeuralIndicatorInput))continue;
			
			for(NeuralInputComponent component:input.getComponents()){
				if(component.getLowerRange()==0 && component.getUpperRange()==0)
					return true;
			}
			
		}
		
		return false;
	}
	
	public void computeAllNeuralIndicatorInputs(boolean resetComponentRanges) {

		for (NeuralInput input : neuralInputs) {
			if (!(input instanceof NeuralIndicatorInput))
				continue;

			NeuralIndicatorInput nii = (NeuralIndicatorInput) input;

			// Compute the neural indicator values and reset the ranges of the
			// components
			List<IbBar> bars = this.getNeuralInputsBarsCollector().get(
					nii.getCollectedBarKey());

			nii.computeValues(bars, resetComponentRanges);

		}

	}
	
	public void computeAllNeuralIndicatorInputs(){
		computeAllNeuralIndicatorInputs(isResetMinMaxNeeded());
	}
	
	public void clearAllCollectedBars(){
		allMidPointBars.clear();
		allAskBars.clear();
		allBidBars.clear();
	}
	
	private List<IbBar> getReferenceBars(){
		switch (referenceData) {
		case MID_POINT:
			return allMidPointBars;
		case BID_AND_ASK:
//			TODO here make a combination of the 
			return allAskBars;
		default:
			return allMidPointBars;
		}
	}
	
	public void splitReferenceData(){
		
		
		trainingBlocks.clear();
		backTestingBlocks.clear();
		
		allBlocks=new LinkedList<LinkedList<IbBar>>();
		
		
		LinkedList<LinkedList<IbBar>> allBlocksTemp=null;
		
		switch (this.getSplitStrategy()) {
		case WEEK:
			allBlocksTemp=IbBar.splitBarListInWeekBlocks(this.getReferenceBars());
			break;
		case DAY:
			allBlocksTemp=IbBar.splitBarListInDayBlocks(this.getReferenceBars());
			break;
		}
		
		//Search the block with the maximum of values
		int maxSize=Integer.MIN_VALUE;
		for(LinkedList<IbBar> block:allBlocksTemp){
			if(block.size()>maxSize)
				maxSize=block.size();
		}
//		System.out.println("Maximum block size: "+maxSize);
		
		//Remove the blocks that contains only the half of the data
		for(LinkedList<IbBar> block:allBlocksTemp){
			if(block.size()<maxSize/2){
				System.out.println("NeuralConfiguration-> Block with size: "+block.size() +" will be ignored!");
				continue;
			}
			allBlocks.add(block);
		}
		allBlocksTemp.clear();
		
		
		if (neuralTrainingElements.isEmpty()) {
			for(LinkedList<IbBar> block:allBlocks){
				backTestingBlocks.add(block);
			}
			
			// Creation of the training blocks
			trainingBlocks = IbBar.collectPercentageOfBlocks(backTestingBlocks,
					this.getPercentOfTrainingData());
			// neuralTrainingElements.clear();

			for (LinkedList<IbBar> block : trainingBlocks) {
				
				String key=null;
				
				switch (this.getSplitStrategy()) {
				case WEEK:
					Calendar sunday = IbBar.getLastSundayOfDate(block.get(0)
							.getTimeInMs());
					key = String.valueOf(sunday.getTimeInMillis());
					break;
					
				case DAY:
					Calendar day = IbBar.getCurrentDayOf(block.get(0)
							.getTimeInMs());
					key = String.valueOf(day.getTimeInMillis());
					break;
				}
				
				if(key!=null){
					NeuralTrainingElement element=new NeuralTrainingElement(key);
					element.setNeuralConfiguration(this);
					neuralTrainingElements.add(element);
				}
				
				
			}
		} else {
			for(LinkedList<IbBar> block:allBlocks){
				
				String key=null;
				
				switch (this.getSplitStrategy()) {
				case WEEK:
					Calendar sunday = IbBar.getLastSundayOfDate(block.get(0)
							.getTimeInMs());
					key = String.valueOf(sunday.getTimeInMillis());
					break;
				case DAY:
					Calendar day = IbBar.getCurrentDayOf(block.get(0)
							.getTimeInMs());
					key = String.valueOf(day.getTimeInMillis());
					break;
				}
				
				if(key==null)continue;
				
				boolean isTrainingElement=false;
				for(NeuralTrainingElement element:neuralTrainingElements){
					if(element.getName().equals(key)){
						isTrainingElement=true;break;
					}
				}
				
				
				if(isTrainingElement){
					trainingBlocks.add(block);
				}
				else{
					backTestingBlocks.add(block);
				}
			}
			

		}
		
		
	}
	
	public void computeAdaptedDataOfEachComponents(){
		
		long[] referencedLongTimes=IbBar.getTimeArray(this.getReferenceBars());
		double[] referencedTimes=new double[referencedLongTimes.length];
		for(int i=0;i<referencedLongTimes.length;i++){
			referencedTimes[i]=referencedLongTimes[i];
		}
		
		
		
//		System.out.println("Nb of referenced times: "+referencedTimes.length);
		
		
//		Compute the Adapted Values
		for (NeuralInput input : neuralInputs) {
			input.computeAdaptedData(referencedTimes);
		}
		
//		reduce the value components values to their minimum
		int minLength=Integer.MAX_VALUE;
		for (NeuralInput input : neuralInputs) {
			for(NeuralInputComponent component: input.getComponents()){
//				System.out.println("Nb of adapted values: "+component.getAdaptedValues().length
//						+", last value: "+component.getAdaptedValues()[component.getAdaptedValues().length-1]);
				if(component.getAdaptedValues().length<minLength)
					minLength=component.getAdaptedValues().length;
			}
		}
		
		double[] tmpAdaptedTimes=Arrays.copyOfRange(referencedTimes,referencedTimes.length-minLength ,referencedTimes.length);
		adpatedTimesMap.clear();
		for(int i=0;i<tmpAdaptedTimes.length;i++){
			adpatedTimesMap.put((long)tmpAdaptedTimes[i], i);
		}
		
		
		for (NeuralInput input : neuralInputs) {
			for(NeuralInputComponent component: input.getComponents()){
				int dataLength=component.getAdaptedtimes().length;
				if(dataLength==minLength)continue;
				
				double[] reducedAdaptedValues=Arrays.copyOfRange(component.getAdaptedValues(),dataLength-minLength ,dataLength);
				double[] reducedAdaptedTimes=Arrays.copyOfRange(component.getAdaptedtimes(),dataLength-minLength ,dataLength);
				
				
				component.setAdaptedtimes(reducedAdaptedTimes);
				component.setAdaptedValues(reducedAdaptedValues);
				
//				double[] adaptedValues=Arrays.copyOfRange(component.getAdaptedValues(), 1000, 1050);
//				System.out.println("Adapted Values: "+Arrays.toString(adaptedValues));
				
			}
		}
		
//		System.out.println("Min Length: "+minLength);
//		
//		for (NeuralInput input : neuralInputs) {
//			for(NeuralInputComponent component: input.getComponents()){
//				System.out.println("Nb of adapted values after adaption: "+component.getAdaptedValues().length
//						+", last value: "+component.getAdaptedValues()[component.getAdaptedValues().length-1]);
//			}
//		}
		
		
		
		
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

	public IbContract getContract() {
		return contract;
	}

	public void setContract(IbContract contract) {
		this.contract = contract;
	}

	public List<NeuralInput> getNeuralInputs() {
		return neuralInputs;
	}

	public void setNeuralInputs(List<NeuralInput> neuralInputs) {
		this.neuralInputs = neuralInputs;
		for(NeuralInput neuralInput:this.neuralInputs){
			neuralInput.setNeuralConfiguration(this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	
	public int getPercentOfTrainingData() {
		return percentOfTrainingData;
	}

	public void setPercentOfTrainingData(int percentOfTrainingData) {
		this.percentOfTrainingData = percentOfTrainingData;
	}

	public ReferenceData getReferenceData() {
		return referenceData;
	}

	public void setReferenceData(ReferenceData referenceData) {
		this.referenceData = referenceData;
	}

	public SplitStrategy getSplitStrategy() {
		return splitStrategy;
	}

	public void setSplitStrategy(SplitStrategy splitStrategy) {
		this.splitStrategy = splitStrategy;
	}

	public List<NeuralTrainingElement> getNeuralTrainingElements() {
		return neuralTrainingElements;
	}

	public void setNeuralTrainingElements(
			List<NeuralTrainingElement> neuralTrainingElements) {
		this.neuralTrainingElements = neuralTrainingElements;
		for(NeuralTrainingElement elt:this.neuralTrainingElements){
			elt.setNeuralConfiguration(this);
		}
	}
	
	public List<IbBar> getAllMidPointBars() {
		return allMidPointBars;
	}
	
	public void setAllMidPointBars(List<IbBar> allMidPointBars) {
		this.allMidPointBars = allMidPointBars;
	}
	
	public List<IbBar> getAllAskBars() {
		return allAskBars;
	}
	
	public void setAllAskBars(List<IbBar> allAskBars) {
		this.allAskBars = allAskBars;
	}

	public List<IbBar> getAllBidBars() {
		return allBidBars;
	}
	
	public void setAllBidBars(List<IbBar> allBidBars) {
		this.allBidBars = allBidBars;
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}
	
	public HashMap<String, List<IbBar>> getNeuralInputsBarsCollector() {
		return neuralInputsBarsCollector;
	}
	
	public void clearNeuralInputsBarsCollector(){
		for(String key:neuralInputsBarsCollector.keySet()){
			neuralInputsBarsCollector.get(key).clear();
		}
		neuralInputsBarsCollector.clear();
	}
	
	

	public void setNeuralInputsBarsCollector(
			HashMap<String, List<IbBar>> neuralInputsBarsCollector) {
		this.neuralInputsBarsCollector = neuralInputsBarsCollector;
	}

	public LinkedList<LinkedList<IbBar>> getTrainingBlocks() {
		return trainingBlocks;
	}

	public void setTrainingBlocks(LinkedList<LinkedList<IbBar>> trainingBlocks) {
		this.trainingBlocks = trainingBlocks;
	}

	public LinkedList<LinkedList<IbBar>> getBackTestingBlocks() {
		return backTestingBlocks;
	}

	public void setBackTestingBlocks(LinkedList<LinkedList<IbBar>> backTestingBlocks) {
		this.backTestingBlocks = backTestingBlocks;
	}
	
	public LinkedList<LinkedList<IbBar>> getAllBlocks() {
		if(allBlocks==null){
			allBlocks=new LinkedList<LinkedList<IbBar>>();
		}
		return allBlocks;
	}

	public void setAllBlocks(LinkedList<LinkedList<IbBar>> allBlocks) {
		this.allBlocks = allBlocks;
	}

	public HashMap<Long, Integer> getAdpatedTimesMap() {
		return adpatedTimesMap;
	}
	



	public List<NeuralArchitecture> getNeuralArchitectures() {
		return neuralArchitectures;
	}
	



	public void setNeuralArchitectures(List<NeuralArchitecture> neuralArchitectures) {
		this.neuralArchitectures = neuralArchitectures;
		for(NeuralArchitecture archi:this.neuralArchitectures){
			archi.setNeuralConfiguration(this);
		}
	}


	
	
}
