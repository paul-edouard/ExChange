package com.munch.exchange.model.core.ib.chart.signals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.SecType;
import com.ibm.icu.util.Calendar;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.ShapeType;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;

@Entity
public abstract class IbChartSignal extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7782487495338138639L;
	
	public static final String PROFIT="PROFIT";
	public static final String RISK="RISK";
	public static final String BUY_LONG_SIGNAL="BUY LONG";
	public static final String SELL_LONG_SIGNAL="SELL LONG";
	public static final String BUY_SHORT_SIGNAL="BUY SHORT";
	public static final String SELL_SHORT_SIGNAL="SELL SHORT";
	public static final String SIGNAL="SIGNAL";
	
	
	//Optimization variables
	
	@Transient
	private LinkedList<ExBar> optimizationBars;
	
	@Transient
	private LinkedList<LinkedList<ExBar>> optimizationBlocks;
	
	@Transient
	private boolean batch=false;
	
	@Transient
	private String algorithmName;
	
	@Transient
	private int numberOfEvaluations=0;
	
	@Transient
	private int numberOfSeeds=0;
	
	@Transient
	private String barSize;
	
	@Transient
	private long timeCounter;
	
	@Transient
	private double totalProfit;
	
	@Transient
	private double maxRisk;
	
	
	
	
	
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	protected List<IbChartSignalOptimizedParameters> optimizedSet=new LinkedList<IbChartSignalOptimizedParameters>();
	
	
	
	@Transient
	private IbCommission commission;
	
	@Transient
	private IbContract contract;
	
	
	@OneToOne(mappedBy="chartSignal",cascade=CascadeType.ALL)
	private PerformanceMetrics performanceMetrics;
	
	private long volume;
	
	public IbChartSignal() {
		super();
		volume=10;
	}


	public IbChartSignal(IbChartIndicatorGroup group) {
		super(group);
		volume=10;
		//initProblem();
	}

	@Override
	public void copyData(IbChartIndicator in) {
		if(in instanceof IbChartSignal){
			IbChartSignal in_s=(IbChartSignal)in;
			this.volume=in_s.volume;
			this.contract=in_s.getContract();
			this.optimizationBlocks=in_s.optimizationBlocks;
			
			this.optimizedSet=new LinkedList<IbChartSignalOptimizedParameters>();
			for(IbChartSignalOptimizedParameters parameters:in_s.optimizedSet){
				IbChartSignalOptimizedParameters copyParameters=parameters.copy();
				copyParameters.setParent(this);
				this.optimizedSet.add(copyParameters);
			}
			
		}
		
		super.copyData(in);
	}
	
	@Override
	public boolean identical(IbChartIndicator other) {
		if(other instanceof IbChartSignal){
			IbChartSignal other_s=(IbChartSignal)other;
			if (volume != other_s.volume)
				return false;
			
		}
		return super.identical(other);
		
	}
	
	
	
	
	public List<ExBar> getOptimizationBars() {
		return optimizationBars;
	}


	public void setOptimizationBars(LinkedList<ExBar> optimizationBars) {
		this.optimizationBars = optimizationBars;
	}


	@Override
	public void createSeries() {
		int[] colorS=new int[3];
		colorS[0]=10;
		colorS[1]=10;
		colorS[2]=50;
		IbChartSerie signal=new IbChartSerie(this,this.getName()+" "+SIGNAL,RendererType.SECOND,true,false,colorS);
		this.series.add(signal);
		
		int[] color=new int[3];
		color[0]=0;
		color[1]=0;
		color[2]=250;
		IbChartSerie profit=new IbChartSerie(this,this.getName()+" "+PROFIT,RendererType.PROFIT,false,true,color);
		this.series.add(profit);
		
		int[] colorR=new int[3];
		color[0]=250;
		color[1]=0;
		color[2]=0;
		IbChartSerie risk=new IbChartSerie(this,this.getName()+" "+RISK,RendererType.RISK,false,false,colorR);
		this.series.add(risk);
		
		int[] colorBUY=new int[3];
		colorBUY[0]=0;
		colorBUY[1]=250;
		colorBUY[2]=0;
		IbChartSerie buyLong=new IbChartSerie(this,this.getName()+" "+BUY_LONG_SIGNAL,RendererType.MAIN,false,true,colorBUY, ShapeType.UP_TRIANGLE);
		this.series.add(buyLong);
		IbChartSerie buyShort=new IbChartSerie(this,this.getName()+" "+BUY_SHORT_SIGNAL,RendererType.MAIN,false,true,colorBUY, ShapeType.DOWN_TRIANGLE);
		this.series.add(buyShort);
		
		int[] colorSELL=new int[3];
		colorSELL[0]=250;
		colorSELL[1]=0;
		colorSELL[2]=0;
		IbChartSerie sellLong=new IbChartSerie(this,this.getName()+" "+SELL_LONG_SIGNAL,RendererType.MAIN,false,true,colorSELL, ShapeType.DOWN_TRIANGLE);
		this.series.add(sellLong);
		IbChartSerie sellShort=new IbChartSerie(this,this.getName()+" "+SELL_SHORT_SIGNAL,RendererType.MAIN,false,true,colorSELL, ShapeType.UP_TRIANGLE);
		this.series.add(sellShort);
		
	}
	
	public abstract void computeSignalPointFromBarBlock(List<ExBar> bars, boolean reset);
	
	
	public  LinkedList<LinkedList<ExBar>> createBlocks(List<ExBar> bars){
		if(!batch){
			if(optimizationBlocks!=null)
				optimizationBlocks.clear();
			optimizationBlocks=null;
		}
		else{
			if(optimizationBlocks!=null){
				return optimizationBlocks;
			}
		}
		
		
		LinkedList<LinkedList<ExBar>> blocks=BarUtils.splitBarListInDayBlocks(bars);
		
		//Save the blocks in the batch modus only
		if(batch)
			setOptimizationBlocks(blocks);
		
		return blocks;
	}
	
	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		//If reset clear all series
		if(reset){
			for(IbChartSerie serie:this.series){
				serie.clearPoints();
			}
		}
		
		if(bars==null || bars.size()==0)return;
		
		
//		Split the received bars in blocks
//		startTimeCounter();
		LinkedList<LinkedList<ExBar>> blocks=createBlocks(bars);
//		stopTimeCounter("Split the received bars in blocks");
		
//		startTimeCounter();
		int i=0;
		for(List<ExBar> block:blocks){
//			System.out.println("Compute Block: "+(i++)+", Size: "+block.size());
			
			//Calculate the signal of the isolated block
			computeSignalPointFromBarBlock(block, reset);
			
			if(!batch && block==blocks.getLast())
				break;
			
			//Set the last signal to neutral in order to avoid wrong long position
			if(this.getSignalSerie().getPoints().size()>0)
				this.getSignalSerie().getPoints().get(this.getSignalSerie().getPoints().size()-1).setValue(this.getNeutralSignal());
		}
//		stopTimeCounter("Calculate the signal of the isolated blocks");
		
		//Return if the list is empty just in case of the problems with empty data
		if(this.getSignalSerie().getPoints().isEmpty())return;
		
		//Clean the Signal Series close the empty block with 0
//		startTimeCounter();
//		long interval=bars.get(0).getIntervallInSec();
		cleanSignalSerie();
//		stopTimeCounter("Clean the Signal Series close the empty block with 0");
		
		//Create the Signal Map
//		startTimeCounter();
		HashMap<Long, IbChartPoint> signalMap=new HashMap<Long, IbChartPoint>();
		for(IbChartPoint point:this.getSignalSerie().getPoints()){
			signalMap.put(point.getTime(), point);
		}
//		stopTimeCounter("Create the Signal Map");
		
		//Create the Profit Serie
//		startTimeCounter();
		createProfitAndRiskSeries(bars, reset, signalMap, this.volume);
//		stopTimeCounter("Create the Profit Serie");
		
		//update the performance metrics
//		startTimeCounter();
		if(reset && !batch){
			if(performanceMetrics==null)
				performanceMetrics=new PerformanceMetrics();
			performanceMetrics.calculateMetricsForSignal(bars, signalMap,this.getCommission(),volume);
		}
//		stopTimeCounter("update the performance metrics");
		
	}
	
	private void createProfitAndRiskSeries(List<ExBar> bars, boolean reset, HashMap<Long, IbChartPoint> signalMap, long volume){
		
//		System.out.println("Nb. Of bars: "+bars.size());
		
		//Creation & Initialization of the variables
		ExBar previewBar=bars.get(0);
		//System.out.println("Bar: "+previewBar.getTime());
		double previewSignal=signalMap.get(previewBar.getTimeInMs()).getValue();
		double profit=0.0;
		double risk=0.0;
		double maxProfit=0.0;
		
		//The list for the no batch modus
		long[] times=new long[0];
		double[] profits=new double[0];
		double[] risks=new double[0];
		
		if(!batch){
			times=new long[bars.size()];
			profits=new double[bars.size()];
			risks=new double[bars.size()];
			
			//Add the first chart point
			times[0]=previewBar.getTime();
			profits[0]=profit;
			risks[0]=risk;
		}
		else{
			maxRisk=0;
			totalProfit=0;
		}
		
		int i=0;
		for(ExBar bar:bars){
			if(i==0){i++;continue;}
			
			long time=bar.getTimeInMs();
			
			double signal=signalMap.get(time).getValue();
			double diffSignal=signal-previewSignal;
			double absDiffSignal=Math.abs(diffSignal);
			
			double previewPrice=previewBar.getClose();
			double price=bar.getClose();
			
			//Add the profit
			if(Math.abs(previewSignal)>0){
				//update the profit
				profit+=previewSignal*(price-previewPrice)*volume;
				
				//Calculate the risk
				if(profit>maxProfit)
					maxProfit=profit;
				
				risk=profit-maxProfit;
			}
			
			
			//Modification of the position
			if(signal!=previewSignal){
				
				// Calculate Commission
				IbCommission com=this.getCommission();
				if(com!=null){
					profit-=absDiffSignal*com.calculate(volume, price);
				}
				
				
				// Update the Buy and Sell Series
				if (!batch) {
					if (signal > 0) {
						this.getBuyLongSerie().addPoint(time, price);
					} else if (signal < 0) {
						this.getBuyShortSerie().addPoint(time, price);
					} else {
						if (previewPrice > 0) {
							this.getSellLongSerie().addPoint(time, price);
						} else {
							this.getSellShortSerie().addPoint(time, price);
						}
					}
				}
				
			}
			
			
			//Save the current state in the list in order to create the Series later on
			if(!batch){
				times[i]=bar.getTimeInMs();
				profits[i]=profit;
				risks[i]=risk;
				i++;
			}
			else if(maxRisk<-risk){
					maxRisk=-risk;
			}
			
			previewSignal=signal;
			previewBar=bar;
		}
		
		if(!batch){
			if(reset ){
				this.getProfitSerie().setPointValues(times, profits);
				this.getRiskSerie().setPointValues(times, risks);
			}
			else{
				this.getProfitSerie().addNewPointsOnly(times, profits);
				this.getRiskSerie().addNewPointsOnly(times, risks);
			}
		}else{
			totalProfit=profit;
		}
		
	}
	
	
	private void startTimeCounter(){
		timeCounter=Calendar.getInstance().getTimeInMillis();
	}
	
	private void stopTimeCounter(String message){
		long stopTime=Calendar.getInstance().getTimeInMillis();
		long timeDiff=stopTime-timeCounter;
		
		System.out.println("Stop time counter: "+message+", "+timeDiff + "ms");
		
	}
	
	
	/**
	 * this function clean the interval without values
	 * 
	 * @param interval
	 */
	private void cleanSignalSerie(){
		if(this.getSignalSerie().getPoints().size()==0)
			return;
		
		/*
		List<IbChartPoint> points=this.getSignalSerie().getPoints();
		
		long timePoint=points.get(0).getTime();
		long lastTimePoint=points.get(points.size()-1).getTime();
		
		//Fill the empty intervals with the neutral value
		long intervalInMs=interval*1000;
		while(timePoint<lastTimePoint){
			timePoint+=intervalInMs;
			if(this.getSignalSerie().containsPoint(timePoint))continue;
			
			this.getSignalSerie().addPoint(timePoint, this.getNeutralSignal());
			
		}
		this.getSignalSerie().sortPoints();
		*/
		//Clean signal Serie for contract that allow only long position
		IbContract contract= getContract();
		if(contract==null)return;
		if(contract.allowShortPosition())return;
		
		for(IbChartPoint point:this.getSignalSerie().getPoints()){
			if(point.getValue()<this.getNeutralSignal()){
				point.setValue(this.getNeutralSignal());
			}
		}
		
		
	}
	
	
	protected abstract int getValidAtPosition();
	
	protected double getNeutralSignal(){
		return 0.0;
	}

	public IbChartSerie getSignalSerie(){
		return this.getChartSerie(this.getName()+" "+SIGNAL);
	}
	
	public IbChartSerie getProfitSerie(){
		return this.getChartSerie(this.getName()+" "+PROFIT);
	}
	
	public IbChartSerie getRiskSerie(){
		return this.getChartSerie(this.getName()+" "+RISK);
	}
	
	public IbChartSerie getBuyLongSerie(){
		return this.getChartSerie(this.getName()+" "+BUY_LONG_SIGNAL);
	}
	
	public IbChartSerie getSellLongSerie(){
		return this.getChartSerie(this.getName()+" "+SELL_LONG_SIGNAL);
	}
	
	public IbChartSerie getBuyShortSerie(){
		return this.getChartSerie(this.getName()+" "+BUY_SHORT_SIGNAL);
	}
	
	public IbChartSerie getSellShortSerie(){
		return this.getChartSerie(this.getName()+" "+SELL_SHORT_SIGNAL);
	}
	
	
	public PerformanceMetrics getPerformanceMetrics() {
		return performanceMetrics;
	}
	
	public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) {
		this.performanceMetrics = performanceMetrics;
	}


	public long getVolume() {
		return volume;
	}


	public void setVolume(long volume) {
		if(volume!=this.volume){
			this.volume = volume;
			this.setDirty(true);
		}
	}


	public void setCommission(IbCommission commission) {
		this.commission = commission;
	}


	private IbCommission getCommission() {
		//Try to find the commission from the current contract
		if(commission==null){
				IbContract contract= getContract();
				if(contract!=null && contract.getCommission()!=null){
					commission=contract.getCommission();
				}
			
		}
		return commission;
	}
	
	private IbContract getContract(){
		if(contract==null){
			if(this.getGroup()==null)return null;
			IbChartIndicatorGroup rootGroup=this.getGroup().getRoot();
			if(rootGroup!=null && rootGroup.getContainer()!=null)
				contract=rootGroup.getContainer().getContract();
		}
		
		return contract;
	}
	
	


	public String getAlgorithmName() {
		return algorithmName;
	}


	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}


	public int getNumberOfEvaluations() {
		return numberOfEvaluations;
	}


	public void setNumberOfEvaluations(int numberOfEvaluations) {
		this.numberOfEvaluations = numberOfEvaluations;
	}
	


	public void setBatch(boolean batch) {
		this.batch = batch;
	}


	
	
	public boolean isBatch() {
		return batch;
	}


	public double getTotalProfit() {
		return totalProfit;
	}


	public double getMaxRisk() {
		return maxRisk;
	}


	public int getNumberOfSeeds() {
		return numberOfSeeds;
	}


	public void setNumberOfSeeds(int numberOfSeeds) {
		this.numberOfSeeds = numberOfSeeds;
	}


	
	
	public synchronized void setOptimizationBlocks(
			LinkedList<LinkedList<ExBar>> optimizationBlocks) {
		this.optimizationBlocks = optimizationBlocks;
		setDirty(true);
	}


	public String getBarSize() {
		return barSize;
	}


	public void setBarSize(String barSize) {
		this.barSize = barSize;
	}


	public List<IbChartSignalOptimizedParameters> getOptimizedSet() {
		if(this.barSize!=null && !this.barSize.isEmpty())
			return getOptimizedSet(this.barSize);
		return optimizedSet;
	}
	
	public List<IbChartSignalOptimizedParameters> getAllOptimizedSet(){
		return optimizedSet;
	}
	
	public List<IbChartSignalOptimizedParameters> getOptimizedSet(String barSize){
		return getOptimizedSet(BarUtils.getBarSizeFromString(barSize));
		
	}
	
	public List<IbChartSignalOptimizedParameters> getOptimizedSet(BarSize size){
		List<IbChartSignalOptimizedParameters> parametersSet=new LinkedList<IbChartSignalOptimizedParameters>();
		for(IbChartSignalOptimizedParameters parameters:optimizedSet){
			if(parameters.getSize()==size)
				parametersSet.add(parameters);
		}
		return parametersSet;
	}
	
	protected void setOptimizedSet(List<IbChartSignalOptimizedParameters> optimizedSet) {
		this.optimizedSet = optimizedSet;
	}
	
	public void addOptimizedParameters(IbChartSignalOptimizedParameters optimizedParameters){
		this.optimizedSet.add(optimizedParameters);
		optimizedParameters.setParent(this);
	}
	
	public void removeOptimizedParameters(IbChartSignalOptimizedParameters optimizedParameters){
		this.optimizedSet.remove(optimizedParameters);
	}
	
	public void removeAllOptimizedParameters(){
		this.optimizedSet.clear();
	}
	


}
