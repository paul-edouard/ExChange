package com.munch.exchange.model.core.ib.chart.signals;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.moeaframework.problem.AbstractProblem;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public abstract class IbChartSignal extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7782487495338138639L;
	
	public static final String PROFIT="PROFIT";
	public static final String RISK="RISK";
	public static final String BUY_AND_SELL="BUY AND SELL";
	
	@Transient
	private AbstractProblem problem;
	
	
	public IbChartSignal() {
		super();
		initProblem();
	}


	public IbChartSignal(IbChartIndicatorGroup group) {
		super(group);
		initProblem();
	}


	public abstract void initProblem();
	

	@Override
	public void createSeries() {
		int[] color=new int[3];
		color[0]=10;
		color[1]=250;
		color[2]=10;
		IbChartSerie profit=new IbChartSerie(this,this.getName()+" "+PROFIT,RendererType.SECOND,true,true,color);
		this.series.add(profit);
		
		int[] colorR=new int[3];
		color[0]=250;
		color[1]=10;
		color[2]=10;
		IbChartSerie risk=new IbChartSerie(this,this.getName()+" "+RISK,RendererType.SECOND,false,false,colorR);
		this.series.add(risk);
		
		int[] colorBS=new int[3];
		color[0]=10;
		color[1]=250;
		color[2]=50;
		IbChartSerie buyAndSell=new IbChartSerie(this,this.getName()+" "+BUY_AND_SELL,RendererType.MAIN,false,false,colorBS);
		this.series.add(buyAndSell);

	}
	
}
