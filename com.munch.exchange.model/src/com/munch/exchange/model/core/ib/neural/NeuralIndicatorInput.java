package com.munch.exchange.model.core.ib.neural;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;


@Entity
public class NeuralIndicatorInput extends NeuralInput{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7890490253355577308L;
	
	@Enumerated(EnumType.STRING)
	private BarSize size;
	
	@Enumerated(EnumType.STRING)
	private WhatToShow type;
	
	@OneToOne
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	@OneToOne
	@JoinColumn(name="BAR_CONTAINER_ID")
	private IbBarContainer barContainer;
	
	@OneToOne(mappedBy="neuralIndicatorInput",cascade=CascadeType.ALL)
	private IbChartIndicator indicator;	
	
	
	public NeuralIndicatorInput() {
		super();
	}

	@Override
	public NeuralInput copy() {
		NeuralIndicatorInput cp=new NeuralIndicatorInput();
		this.copyData(this, cp);
		
		cp.size=this.size;
		cp.type=this.type;
		cp.contract=this.contract.copy();
		cp.barContainer=this.barContainer.copy();
		cp.indicator=this.indicator.copy();
		cp.indicator.getSeries().clear();
		cp.indicator.setNeuralIndicatorInput(cp);
		
		return cp;
	}
	
	@Override
	public void load() {
//		System.out.println("Neural input name: "+this.getName());
		this.getBarContainer();
		this.getContract();
		this.getIndicator();
		this.getIndicator().getParameters().size();
		this.getIndicator().getSeries().size();
		
	}
	
	public void computeValues(List<IbBar> bars, boolean resetComponentRanges){
		
		if (values == null || times == null || values.length == 0
				|| times.length == 0) {
			indicator.createSeries();
			indicator.compute(bars);

			IbChartSerie serie = indicator.getChartSerie(this.getName());

			values = new double[serie.getPoints().size()];
			times = new double[serie.getPoints().size()];

			int i = 0;
			for (IbChartPoint point : serie.getPoints()) {
				values[i] = point.getValue();
				times[i] = point.getTime();
				i++;
			}

			
		}
		
		for(NeuralInputComponent component:this.getComponents()){
			component.computeValues();
			if(resetComponentRanges)
				component.computeRanges();
		}
		
		indicator.getSeries().clear();
		values=null;
		times=null;
		
		
	}
	
	public String getCollectedBarKey(){
		return this.getBarContainer().getId()+"_"+this.getSize().toString();
	}
	
	@Override
	public void computeAdaptedData(double[] referencedTimes) {
		for(NeuralInputComponent component : this.getComponents()){
			int j=0;
			LinkedList<Double> adaptedValues=new LinkedList<Double>();
			LinkedList<Double> adaptedTimes=new LinkedList<Double>();
			
			for(int i=0;i<referencedTimes.length;i++){
				//Continue as long as some values are found!
				if(component.getTimes()[j]>referencedTimes[i])
					continue;
				
				//Search the last guilty value
				while( (j+1)<component.getTimes().length &&
						component.getTimes()[j+1] <= referencedTimes[i]){
					j++;
				}
				
				adaptedTimes.add(referencedTimes[i]);
				adaptedValues.add(component.getValues()[j]);
			}
			
			component.setAdaptedValues(adaptedValues);
			component.setAdaptedtimes(adaptedTimes);
			
		}
	}
	
	
	
//	#######################
//	##   GETTER & SETTER ##
//	#######################
	
	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}
	
	public WhatToShow getType() {
		return type;
	}

	public void setType(WhatToShow type) {
		this.type = type;
	}

	public IbContract getContract() {
		return contract;
	}

	public void setContract(IbContract contract) {
		this.contract = contract;
	}

	public IbChartIndicator getIndicator() {
		return indicator;
	}

	public void setIndicator(IbChartIndicator indicator) {
		this.indicator = indicator;
	}
	
	public IbBarContainer getBarContainer() {
		return barContainer;
	}

	public void setBarContainer(IbBarContainer barContainer) {
		this.barContainer = barContainer;
	}

	

	
}
