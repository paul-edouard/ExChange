package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;

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
	private HashMap<String ,List<ExBar>> neuralInputsBarsCollector=new HashMap<String ,List<ExBar>>();
	
//	Training Data
	
	@Enumerated(EnumType.STRING)
	private BarType barType=BarType.TIME;
	
	@Enumerated(EnumType.STRING)
	private BarSize size=BarSize._1_min;
	
	private double barRange = 0;
	
	private int percentOfTrainingData=60;
	
	private double minProfitLimit = 5.0;
	
	private long volume = 20000;
	
	@Enumerated(EnumType.STRING)
	private ReferenceData referenceData=ReferenceData.MID_POINT;
	
	@Enumerated(EnumType.STRING)
	private SplitStrategy splitStrategy=SplitStrategy.WEEK;
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralTrainingElement> neuralTrainingElements=new LinkedList<NeuralTrainingElement>();
	
	@Transient
	private List<ExBar> allMidPointBars=new LinkedList<ExBar>();
	
	@Transient
	private List<ExBar> allAskBars=new LinkedList<ExBar>();
	
	@Transient
	private HashMap<Long, ExBar> askBarsMap=new HashMap<Long, ExBar>();
	
	@Transient
	private List<ExBar> allBidBars=new LinkedList<ExBar>();
	
	@Transient
	private HashMap<Long, ExBar> bidBarsMap=new HashMap<Long, ExBar>();
	
	@Transient
	private LinkedList<LinkedList<ExBar>> allBlocks=new LinkedList<LinkedList<ExBar>>();
	
	@Transient
	private LinkedList<LinkedList<ExBar>> trainingBlocks=new LinkedList<LinkedList<ExBar>>();
	
	@Transient
	private LinkedList<LinkedList<ExBar>> backTestingBlocks=new LinkedList<LinkedList<ExBar>>();
	
	@Transient
	private HashMap<Long, Integer> adpatedTimesMap=new HashMap<Long, Integer>();
	
	
//	Architectures
	
	/**
	 * This are the architectures that create and use for training, once trained some will be copied and passed to the isolated ones
	 */
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralArchitecture> neuralArchitectures=new LinkedList<NeuralArchitecture>();
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<IsolatedNeuralArchitecture> isolatedArchitectures=new LinkedList<IsolatedNeuralArchitecture>();
	
	
 	public NeuralConfiguration() {
		super();
	}



	@Override
	public NeuralConfiguration copy() {
		NeuralConfiguration c=new NeuralConfiguration();
		
		c.id=id;
		
		c.name=name;
		c.creationDate=creationDate;
		c.barType = barType;
		c.size = size;
		c.barRange = barRange;
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
		
		c.minProfitLimit=minProfitLimit;
		c.volume=volume;
		
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
			List<ExBar> bars = this.getNeuralInputsBarsCollector().get(
					nii.getCollectedBarKey());

			nii.computeValues(bars, resetComponentRanges);

		}

	}
	
	public void computeAllNeuralIndicatorInputs(){
		computeAllNeuralIndicatorInputs(isResetMinMaxNeeded());
	}
	
	public ExBar getAskBar(long time){
		if(this.askBarsMap.containsKey(time))
			return this.askBarsMap.get(time);
		
		return null;
	}
	
	public ExBar getBidBar(long time){
		if(this.bidBarsMap.containsKey(time))
			return this.bidBarsMap.get(time);
		
		return null;
	}
	
	
	public void clearAllCollectedBars(){
		allMidPointBars.clear();
		allAskBars.clear();
		allBidBars.clear();
	}
	
	private List<ExBar> getReferenceBars(){
		
		return allMidPointBars;
		
//		switch (referenceData) {
//		case MID_POINT:
//			return allMidPointBars;
//		case BID_AND_ASK:
////			TODO here make a combination of the 
//			return allAskBars;
//		default:
//			return allMidPointBars;
//		}
	}
	
	
	public void synchronizedReceivedBars(){
		
		System.out.println("Nb of mid point bars: "+allMidPointBars.size());
		System.out.println("Nb of bid bars: "+allBidBars.size());
		System.out.println("Nb of ask bars: "+allAskBars.size());
		
		
		LinkedList<ExBar> synMidPointBars=new LinkedList<ExBar>();
		LinkedList<ExBar> synBidBars=new LinkedList<ExBar>();
		LinkedList<ExBar> synAskBars=new LinkedList<ExBar>();
		
//		ListIterator<ExBar> midPointItr = allMidPointBars.listIterator();
		ListIterator<ExBar> bidItr = allBidBars.listIterator();
		ListIterator<ExBar> askItr = allAskBars.listIterator();
		
//		ExBar mpBar=midPointItr.next();
		ExBar bBar=bidItr.next();
		ExBar aBar=askItr.next();
		
		for(ExBar bar:allMidPointBars){
			if(bar.getTime() < bBar.getTime())continue;
			if(bar.getTime() < aBar.getTime())continue;
			
			while(bidItr.hasNext() && bar.getTime() > bBar.getTime()){
				bBar=bidItr.next();
			}
			
			while(askItr.hasNext() && bar.getTime() > aBar.getTime()){
				aBar=askItr.next();
			}
			
			if(bar.getTime()==bBar.getTime() && bar.getTime()==aBar.getTime()){
				synMidPointBars.add(bar);
				synBidBars.add(bBar);
				synAskBars.add(aBar);
			}
			
		}
		
		allMidPointBars=synMidPointBars;
		allBidBars=synBidBars;
		allAskBars=synAskBars;
		
		System.out.println("Nb of mid point bars: "+allMidPointBars.size());
		System.out.println("Nb of bid bars: "+allBidBars.size());
		System.out.println("Nb of ask bars: "+allAskBars.size());
		
//		Fill the bar maps
		bidBarsMap.clear();
		for(ExBar bar : allBidBars){
			bidBarsMap.put(bar.getTime(), bar);
		}
		
		askBarsMap.clear();
		for(ExBar bar : allAskBars){
			askBarsMap.put(bar.getTime(), bar);
		}
		
	}
	
	
	public void splitReferenceData(){
		
//		TODO Split the Reference Data
		
		trainingBlocks.clear();
		backTestingBlocks.clear();
		
		allBlocks=new LinkedList<LinkedList<ExBar>>();
		
//		LinkedList<LinkedList<ExBar>> allBlocksTemp=BarUtils.splitBarListInDayBlocks(this.getReferenceBars());
		
		LinkedList<LinkedList<ExBar>> allBlocksTemp=null;
		
		switch (this.getSplitStrategy()) {
		case WEEK:
			allBlocksTemp=BarUtils.splitBarListInWeekBlocks(this.getReferenceBars());
			break;
		case DAY:
			allBlocksTemp=BarUtils.splitBarListInDayBlocks(this.getReferenceBars());
			break;
		}
		
		//Search the block with the maximum of values
		long maxSize=Integer.MIN_VALUE;
		for(LinkedList<ExBar> block:allBlocksTemp){
			System.out.println("NeuralConfiguration-> Block with size: "+block.size()+
					", start: "+BarUtils.format(block.getFirst().getTimeInMs())+
					", end: "+BarUtils.format(block.getLast().getTimeInMs()));
			
			long size = block.getLast().getTimeInMs() - block.getFirst().getTimeInMs();
			
			if(size>maxSize)
				maxSize = size;
		}
//		System.out.println("Maximum block size: "+maxSize);
		
		//Remove the blocks that contains only the half of the data
		for(LinkedList<ExBar> block:allBlocksTemp){
			long size = block.getLast().getTimeInMs() - block.getFirst().getTimeInMs();
			
			if(size<maxSize/2){
				System.out.println("NeuralConfiguration-> Block with size: "+block.size() +" will be ignored!");
				continue;
			}
			allBlocks.add(block);
		}
		allBlocksTemp.clear();
		
		
		if (neuralTrainingElements.isEmpty()) {
			for(LinkedList<ExBar> block:allBlocks){
				backTestingBlocks.add(block);
			}
			
			// Creation of the training blocks
			trainingBlocks = BarUtils.collectPercentageOfBlocks(backTestingBlocks,
					this.getPercentOfTrainingData());
			// neuralTrainingElements.clear();

			for (LinkedList<ExBar> block : trainingBlocks) {
				Calendar day =BarUtils.getCurrentDayOf(block.get(0)
							.getTimeInMs());
				day=BarUtils.addOneDayTo(day);
				
				String key = String.valueOf(day.getTimeInMillis());
				
//				switch (this.getSplitStrategy()) {
//				case WEEK:
//					Calendar sunday = IbBar.getLastSundayOfDate(block.get(0)
//							.getTimeInMs());
//					key = String.valueOf(sunday.getTimeInMillis());
//					break;
//					
//				case DAY:
//					Calendar day = IbBar.getCurrentDayOf(block.get(0)
//							.getTimeInMs());
//					key = String.valueOf(day.getTimeInMillis());
//					break;
//				}
				
				if(key!=null){
					NeuralTrainingElement element=new NeuralTrainingElement(key);
					element.setNeuralConfiguration(this);
					neuralTrainingElements.add(element);
				}
				
				
			}
		} else {
			for(LinkedList<ExBar> block:allBlocks){
				Calendar day =BarUtils.getCurrentDayOf(block.get(0)
						.getTimeInMs());
				day=BarUtils.addOneDayTo(day);
			
				String key = String.valueOf(day.getTimeInMillis());
			
//				String key=null;
//				
//				switch (this.getSplitStrategy()) {
//				case WEEK:
//					Calendar sunday = IbBar.getLastSundayOfDate(block.get(0)
//							.getTimeInMs());
//					key = String.valueOf(sunday.getTimeInMillis());
//					break;
//				case DAY:
//					Calendar day = IbBar.getCurrentDayOf(block.get(0)
//							.getTimeInMs());
//					key = String.valueOf(day.getTimeInMillis());
//					break;
//				}
				
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
		
		long[] referencedLongTimes=BarUtils.getTimeArray(this.getReferenceBars());
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
	
	public List<ExBar> getAllMidPointBars() {
		return allMidPointBars;
	}
	
	public void setAllMidPointBars(List<ExBar> allMidPointBars) {
		this.allMidPointBars = allMidPointBars;
	}
	
	public List<ExBar> getAllAskBars() {
		return allAskBars;
	}
	
	public void setAllAskBars(List<ExBar> allAskBars) {
		this.allAskBars = allAskBars;
	}

	public List<ExBar> getAllBidBars() {
		return allBidBars;
	}
	
	public void setAllBidBars(List<ExBar> allBidBars) {
		this.allBidBars = allBidBars;
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}
	
	public HashMap<String, List<ExBar>> getNeuralInputsBarsCollector() {
		return neuralInputsBarsCollector;
	}
	
	public void clearNeuralInputsBarsCollector(){
		for(String key:neuralInputsBarsCollector.keySet()){
			neuralInputsBarsCollector.get(key).clear();
		}
		neuralInputsBarsCollector.clear();
	}
	
	

	public void setNeuralInputsBarsCollector(
			HashMap<String, List<ExBar>> neuralInputsBarsCollector) {
		this.neuralInputsBarsCollector = neuralInputsBarsCollector;
	}

	public LinkedList<LinkedList<ExBar>> getTrainingBlocks() {
		return trainingBlocks;
	}

	public void setTrainingBlocks(LinkedList<LinkedList<ExBar>> trainingBlocks) {
		this.trainingBlocks = trainingBlocks;
	}

	public LinkedList<LinkedList<ExBar>> getBackTestingBlocks() {
		return backTestingBlocks;
	}

	public void setBackTestingBlocks(LinkedList<LinkedList<ExBar>> backTestingBlocks) {
		this.backTestingBlocks = backTestingBlocks;
	}
	
	public LinkedList<LinkedList<ExBar>> getAllBlocks() {
		if(allBlocks==null){
			allBlocks=new LinkedList<LinkedList<ExBar>>();
		}
		return allBlocks;
	}

	public void setAllBlocks(LinkedList<LinkedList<ExBar>> allBlocks) {
		this.allBlocks = allBlocks;
	}

	public HashMap<Long, Integer> getAdpatedTimesMap() {
		return adpatedTimesMap;
	}
	

	public BarType getBarType() {
		return barType;
	}


	public void setBarType(BarType barType) {
		this.barType = barType;
	}


	public double getRange() {
		return barRange;
	}


	public void setRange(double range) {
		this.barRange = range;
	}

	public List<NeuralArchitecture> getNeuralArchitectures() {
		return neuralArchitectures;
	}
	
	public List<IsolatedNeuralArchitecture> getIsolatedArchitectures() {
		return isolatedArchitectures;
	}

	public void setIsolatedArchitectures(
			List<IsolatedNeuralArchitecture> isolatedArchitectures) {
		this.isolatedArchitectures = isolatedArchitectures;
		for(IsolatedNeuralArchitecture archi:this.isolatedArchitectures){
			archi.setParent(this);
		}
	}

	public void setNeuralArchitectures(List<NeuralArchitecture> neuralArchitectures) {
		this.neuralArchitectures = neuralArchitectures;
		for(NeuralArchitecture archi:this.neuralArchitectures){
			archi.setNeuralConfiguration(this);
		}
	}



	
	
	public double getMinProfitLimit() {
		return minProfitLimit;
	}



	public void setMinProfitLimit(double minProfitLimit) {
		this.minProfitLimit = minProfitLimit;
	}



	public long getVolume() {
		return volume;
	}



	public void setVolume(long volume) {
		this.volume = volume;
	}


	
	
}
