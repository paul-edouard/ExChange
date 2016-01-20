package com.munch.exchange.model.core.ib.neural;

import java.util.Calendar;

import javax.persistence.Entity;

@Entity
public class NeuralDayPositionInput extends NeuralInput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1343662813911282479L;
	
	

	public NeuralDayPositionInput() {
		super();
	}
	
	public void addTo(NeuralConfiguration configuration){
		this.setName("Day Position");
		this.addDirectComponent();
		
		this.getComponents().get(0).setLowerRange(0);
		this.getComponents().get(0).setUpperRange(24.0*60.0*60.0);
		
		configuration.getNeuralInputs().add(this);
		this.setNeuralConfiguration(configuration);
	}

	@Override
	public NeuralInput copy() {
		NeuralDayPositionInput cp=new NeuralDayPositionInput();
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
		Calendar day=Calendar.getInstance();
		
		for(int i=0;i<referencedTimes.length;i++){
			date.setTimeInMillis((long)referencedTimes[i]);
			day.setTimeInMillis((long)referencedTimes[i]);
			day.set(Calendar.HOUR_OF_DAY, 0);
			day.set(Calendar.MINUTE, 0);
			day.set(Calendar.SECOND, 0);
			day.set(Calendar.MILLISECOND, 0);
			
			adaptedValues[i]=(date.getTimeInMillis()-day.getTimeInMillis())/1000;
			adaptedTimes[i]=referencedTimes[i];
		}
		
		component.setAdaptedValues(adaptedValues);
		component.setAdaptedtimes(adaptedTimes);
		
	}

}
