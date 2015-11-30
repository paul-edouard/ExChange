package com.munch.exchange.model.core.ib.chart.signals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.util.Vector;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.ShapeType;
import com.munch.exchange.model.core.ib.chart.trend.TrendLineProblem;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import com.sun.istack.internal.logging.Logger;

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
	
	
	@Transient
	private List<IbBar> optimizationBars;
	
	
	private String algorithmName;
	
	private int numberOfEvaluations;
	
	
	
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
		//initProblem();
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
	
	
	
	
	public List<IbBar> getOptimizationBars() {
		return optimizationBars;
	}


	public void setOptimizationBars(List<IbBar> optimizationBars) {
		this.optimizationBars = optimizationBars;
	}

	/*
	public void initProblem(List<IbBar> bars){
		
		
		optExecutor = new Executor()
		.withProblemClass(IbChartSignalProblem.class, this,bars)
		.withAlgorithm("NSGAII")
		//.usingAlgorithmFactory(factory)
		.withMaxEvaluations(1000)
		.distributeOnAllCores();
		
		
	}
	*/
	
	/*
	public void optimize(){
		NondominatedPopulation result =optExecutor.run();
		
		// print the results
		for (int i = 0; i < result.size(); i++) {
					Solution solution = result.get(i);
					double[] objectives = solution.getObjectives();
							
					// negate objectives to return them to their maximized form
					objectives = Vector.negate(objectives);
							
					System.out.println("Solution " + (i+1) + ":");
					System.out.println("    Profit: " + objectives[0]);
					System.out.println("    Risk:   " + objectives[1]);
					
		}
		
		
	}
	*/
	
	

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
		IbChartSerie sellShort=new IbChartSerie(this,this.getName()+" "+SELL_LONG_SIGNAL,RendererType.MAIN,false,true,colorSELL, ShapeType.UP_TRIANGLE);
		this.series.add(sellShort);
		
	}
	
	public abstract void computeSignalPointFromBarBlock(List<IbBar> bars, boolean reset);
	
	@Override
	protected void computeSeriesPointValues(List<IbBar> bars, boolean reset) {
		//If reset clear all series
		if(reset){
			for(IbChartSerie serie:this.series){
				serie.clearPoints();
			}
		}
		
		//Split the received bars in blocks
		if(bars==null || bars.size()==0)return;
		IbBar lastBar=bars.get(0);
		long interval=lastBar.getIntervallInSec();
		List<IbBar> block=new LinkedList<IbBar>();
		block.add(lastBar);
		
		for(int i=1;i<bars.size();i++){
			IbBar currentBar=bars.get(i);
			long timeDiff=currentBar.getTime()-lastBar.getTime();
			if(timeDiff > interval ){
				//Calculate the signal of the isolated block
				computeSignalPointFromBarBlock(block, reset);
				
				//Set the last signal to neutral in order to avoid wrong long position
				if(this.getSignalSerie().getPoints().size()>0)
					this.getSignalSerie().getPoints().get(this.getSignalSerie().getPoints().size()-1).setValue(this.getNeutralSignal());
				
				//Reset the block
				block=new LinkedList<IbBar>();
			}
			block.add(currentBar);
			lastBar=currentBar;
		}
		
		//Calculate the last block
		if(block.size()>=this.getValidAtPosition()){
			computeSignalPointFromBarBlock(block, reset);
		}
		
		//Return if the list is empty just in case of the problems with empty data
		if(this.getSignalSerie().getPoints().isEmpty())return;
		
		//Clean the Signal Series close the empty block with 0
		cleanSignalSerie(interval);
		
		//Create the Signal Map
		HashMap<Long, IbChartPoint> signalMap=new HashMap<Long, IbChartPoint>();
		for(IbChartPoint point:this.getSignalSerie().getPoints()){
			signalMap.put(point.getTime(), point);
		}
		
		//Create the Profit Serie
		createProfitAndRiskSeries(bars, reset, signalMap, this.volume);
		
		//update the performance metrics
		if(reset){
			if(performanceMetrics==null)
				performanceMetrics=new PerformanceMetrics();
			performanceMetrics.calculateMetricsForSignal(bars, signalMap,this.getCommission(),volume);
		}
		
	}
	
	private void createProfitAndRiskSeries(List<IbBar> bars, boolean reset, HashMap<Long, IbChartPoint> signalMap, long volume){
		
		IbBar previewBar=bars.get(0);
		//System.out.println("Bar: "+previewBar.getTime());
		double previewSignal=signalMap.get(previewBar.getTimeInMs()).getValue();
		double profit=0.0;
		double risk=0.0;
		double maxProfit=0.0;
		
		//Add the first chart point
		long[] times=new long[bars.size()];
		double[] profits=new double[bars.size()];
		double[] risks=new double[bars.size()];
		
		times[0]=previewBar.getTime();
		profits[0]=profit;
		risks[0]=risk;
		
		for(int i=1;i<bars.size();i++){
			IbBar bar=bars.get(i);
			long time=bar.getTimeInMs();
			if(!signalMap.containsKey(time))continue;
			
			double signal=signalMap.get(time).getValue();
			double previewPrice=previewBar.getClose();
			double price=bar.getClose();
			
			//Modification of the position
			if(signal!=previewSignal){
				double diffSignal=signal-previewSignal;
				double diffAbs=Math.abs(diffSignal);
				
				// Calculate Commission
				IbCommission com=this.getCommission();
				if(com!=null){
					profit-=diffAbs*com.calculate(volume, price);
				}
				
				//Update the Buy and Sell Series
				if(signal>0){
					if(diffAbs==1)
						previewPrice=price;
					this.getBuyLongSerie().addPoint(time, price);
				}
				else if(signal<0){
					if(diffAbs==1)
						previewPrice=price;
					this.getBuyShortSerie().addPoint(time, price);
				}
				else{
					if(previewPrice>0){
						this.getSellLongSerie().addPoint(time, price);
					}
					else{
						this.getSellShortSerie().addPoint(time, price);
					}
				}
			}
			
			//Signal is long
			if(signal>0 || signal!=previewSignal){
				//update the profit
				profit+=(price-previewPrice)*volume;
				
				//Calculate the risk
				if(profit>maxProfit)
					maxProfit=profit;
				
				risk=profit-maxProfit;
			}
			else if(signal<0 || signal!=previewSignal){
				//update the profit
				profit-=(price-previewPrice)*volume;
				
				//Calculate the risk
				if(profit>maxProfit)
					maxProfit=profit;
				
				risk=profit-maxProfit;
			}
			
			times[i]=bar.getTimeInMs();
			profits[i]=profit;
			risks[i]=risk;
			
			previewSignal=signal;
			previewBar=bar;
		}
		
		
		if(reset){
			this.getProfitSerie().setPointValues(times, profits);
			this.getRiskSerie().setPointValues(times, risks);
		}
		else{
			this.getProfitSerie().addNewPointsOnly(times, profits);
			this.getRiskSerie().addNewPointsOnly(times, risks);
		}
		
		
	}
	
	
	
	/**
	 * this function clean the interval without values
	 * 
	 * @param interval
	 */
	private void cleanSignalSerie(long interval){
		if(this.getSignalSerie().getPoints().size()==0)
			return;
		
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
		
		//Clean signal Serie for contract that allow only long position
		IbContract contract= getContract();
		if(contract==null)return;
		
		if(contract.getSecType()==SecType.STK){
			//Set negative signal (short) to neutral
			for(int i=0;i<this.getSignalSerie().getPoints().size();i++){
				IbChartPoint point=this.getSignalSerie().getPoints().get(i);
				if(point.getValue()<this.getNeutralSignal())
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


	
	
}
