package com.munch.exchange.model.core.ib.chart.signals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;

@Entity
public abstract class IbChartSignal extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7782487495338138639L;
	
	public static final String PROFIT="PROFIT";
	public static final String RISK="RISK";
	public static final String BUY_SIGNAL="BUY";
	public static final String SELL_SIGNAL="SELL";
	public static final String SIGNAL="SIGNAL";
	
	@Transient
	private IbChartSignalProblem problem;
	
	@Transient
	private IbCommission commission;
	
	
	@OneToOne(mappedBy="chartSignal",cascade=CascadeType.ALL)
	private PerformanceMetrics performanceMetrics;
	
	private long volume;
	
	public IbChartSignal() {
		super();
		volume=10;
		initProblem();
	}


	public IbChartSignal(IbChartIndicatorGroup group) {
		super(group);
		volume=10;
		initProblem();
	}


	private void initProblem(){
		problem=new IbChartSignalProblem(this);
	}
	

	@Override
	public void createSeries() {
		int[] colorS=new int[3];
		colorS[0]=10;
		colorS[1]=250;
		colorS[2]=50;
		IbChartSerie signal=new IbChartSerie(this,this.getName()+" "+SIGNAL,RendererType.SECOND,true,false,colorS);
		this.series.add(signal);
		
		int[] color=new int[3];
		color[0]=10;
		color[1]=250;
		color[2]=10;
		IbChartSerie profit=new IbChartSerie(this,this.getName()+" "+PROFIT,RendererType.SECOND,false,true,color);
		this.series.add(profit);
		
		int[] colorR=new int[3];
		color[0]=250;
		color[1]=10;
		color[2]=10;
		IbChartSerie risk=new IbChartSerie(this,this.getName()+" "+RISK,RendererType.SECOND,false,false,colorR);
		this.series.add(risk);
		
		int[] colorBUY=new int[3];
		colorBUY[0]=0;
		colorBUY[1]=250;
		colorBUY[2]=0;
		IbChartSerie buy=new IbChartSerie(this,this.getName()+" "+BUY_SIGNAL,RendererType.MAIN,false,true,colorBUY);
		this.series.add(buy);
		
		int[] colorSELL=new int[3];
		colorSELL[0]=250;
		colorSELL[1]=0;
		colorSELL[2]=0;
		IbChartSerie sell=new IbChartSerie(this,this.getName()+" "+SELL_SIGNAL,RendererType.MAIN,false,true,colorSELL);
		this.series.add(sell);
		
	}
	
	public abstract void computeSignalPointFromBarBlock(List<IbBar> bars, boolean reset);
	
	@Override
	protected void computeSeriesPointValues(List<IbBar> bars, boolean reset) {
		//If reset clear all series
		if(reset){
			for(IbChartSerie serie:this.series){
				serie.getPoints().clear();
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
			if(timeDiff>interval && block.size()>=this.getValidAtPosition()){
				computeSignalPointFromBarBlock(bars, reset);
				block=new LinkedList<IbBar>();
			}
			block.add(currentBar);
			lastBar=currentBar;
		}
		
		if(block.size()>=this.getValidAtPosition()){
			computeSignalPointFromBarBlock(bars, reset);
		}
		
		//Clean the Signal Series close the empty block with 0
		cleanSignalSerie(interval);
		
		//Create the Signal Map
		HashMap<Long, IbChartPoint> signalMap=new HashMap<Long, IbChartPoint>();
		for(IbChartPoint point:this.getSignalSerie().getPoints())
			signalMap.put(point.getTime(), point);
		
		//Create the Bar Map
		HashMap<Long, IbBar> barMap=new HashMap<Long, IbBar>();
		for(IbBar bar:bars)
			barMap.put(bar.getTime(), bar);
		
		//Create the Profit Serie
		createProfitAndRiskSeries(bars, reset, signalMap, this.volume);
		
		//TODO update the performance metrics
		
	}
	
	private void createProfitAndRiskSeries(List<IbBar> bars, boolean reset, HashMap<Long, IbChartPoint> signalMap, long volume){
		
		IbBar previewBar=bars.get(0);
		double previewSignal=signalMap.get(previewBar.getTime()).getValue();
		double profit=0.0;
		double risk=0.0;
		double maxCapital=0.0;
		
		//Add the first chart point
		long[] times=new long[bars.size()];
		double[] profits=new double[bars.size()];
		double[] risks=new double[bars.size()];
		
		times[0]=previewBar.getTime();
		profits[0]=profit;
		
		for(int i=1;i<bars.size();i++){
			IbBar bar=bars.get(i);
			long time=bar.getTime();
			if(!signalMap.containsKey(time))continue;
			
			double signal=signalMap.get(time).getValue();
			double previewPrice=previewBar.getClose();
			double capital=bar.getClose()*volume;
			
			//Modification of position
			if(signal!=previewSignal){
				// Calculate Commission
				IbCommission com=this.getCommission();
				if(com!=null){
					profit-=com.calculate(volume, bar.getOpen());
				}
				previewPrice=bar.getOpen();
				capital=bar.getOpen()*volume;
				if(signal>0){
					this.getBuySerie().addPoint(time, bar.getOpen());
				}
				else{
					this.getSellSerie().addPoint(time, bar.getOpen());
				}
			}
			
			//Signal is long
			if(signal>0){
				profit+=(bar.getClose()-previewPrice)*volume;
				if(capital>maxCapital){
					maxCapital=capital;
					risk=0.0;
				}
				else{
					risk=capital-maxCapital;
				}
			}
			
			times[i]=bar.getTime();
			profits[i]=profit;
			risks[i]=risk;
			
			previewSignal=signal;
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
		IbChartPoint oldPoint=this.getSignalSerie().getPoints().get(0);
		List<IbChartPoint> pointsToAdd=new LinkedList<IbChartPoint>();
		for(int i=1;i<this.getSignalSerie().getPoints().size();i++){
			IbChartPoint point=this.getSignalSerie().getPoints().get(i);
			if(point.getValue()!=oldPoint.getValue()){
				if(point.getTime()>oldPoint.getTime()+interval){
					IbChartPoint nPoint=null;
					if(point.getValue()>0){
						nPoint=new IbChartPoint(point.getTime()-interval, oldPoint.getValue());
					}
					else{
						nPoint=new IbChartPoint(oldPoint.getTime()+interval, point.getValue());
					}
					pointsToAdd.add(nPoint);
				}
			}
			
			oldPoint=point;
		}
		
		this.getSignalSerie().insertPoints(pointsToAdd);
		
	}
	
	
	protected abstract int getValidAtPosition();


	public IbChartSerie getSignalSerie(){
		return this.getChartSerie(this.getName()+" "+SIGNAL);
	}
	
	public IbChartSerie getProfitSerie(){
		return this.getChartSerie(this.getName()+" "+PROFIT);
	}
	
	public IbChartSerie getRiskSerie(){
		return this.getChartSerie(this.getName()+" "+RISK);
	}
	
	public IbChartSerie getBuySerie(){
		return this.getChartSerie(this.getName()+" "+BUY_SIGNAL);
	}
	
	public IbChartSerie getSellSerie(){
		return this.getChartSerie(this.getName()+" "+SELL_SIGNAL);
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
			IbChartIndicatorGroup rootGroup=this.getGroup().getRoot();
			if(rootGroup!=null && rootGroup.getContainer()!=null){
				IbContract contract=rootGroup.getContainer().getContract();
				if(contract!=null && contract.getCommission()!=null){
					commission=contract.getCommission();
				}
			}
		}
		return commission;
	}


	
	
}
