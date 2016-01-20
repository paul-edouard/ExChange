package com.munch.exchange.model.core.ib.neural;

import java.util.Calendar;
import java.util.LinkedList;

import javax.persistence.Entity;

@Entity
public class NeuralDayOfWeekInput extends NeuralInput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7757591059039294324L;
	
	

	public NeuralDayOfWeekInput() {
		super();
	}
	
	public void addTo(NeuralConfiguration configuration){
		this.setName("Day of Week");
		this.addDirectComponent();
		
		this.getComponents().get(0).setLowerRange(0);
		this.getComponents().get(0).setUpperRange(4);
		
		configuration.getNeuralInputs().add(this);
		this.setNeuralConfiguration(configuration);
	}

	@Override
	public NeuralInput copy() {
		NeuralDayOfWeekInput cp=new NeuralDayOfWeekInput();
		this.copyData(this, cp);
		return cp;
	}

	@Override
	public void load() {
	}

	@Override
	public void computeAdaptedData(double[] referencedTimes) {
		
		NeuralInputComponent component=this.getComponents().get(0);
		
		double[] adaptedValues=new double[referencedTimes.length];
		double[] adaptedTimes=new double[referencedTimes.length];
		
		Calendar date=Calendar.getInstance();
		date.setFirstDayOfWeek(Calendar.SUNDAY);
		
		for(int i=0;i<referencedTimes.length;i++){
			date.setTimeInMillis((long)referencedTimes[i]);
			adaptedValues[i]=date.get(Calendar.DAY_OF_WEEK);
			adaptedTimes[i]=referencedTimes[i];
		}
		
		component.setAdaptedValues(adaptedValues);
		component.setAdaptedtimes(adaptedTimes);
		
	}

}
