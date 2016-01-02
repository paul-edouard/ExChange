package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.munch.exchange.model.core.ib.Copyable;

@Entity
public class NeuralInputComponent implements Serializable, Copyable<NeuralInputComponent>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4382165162373283836L;
	
	
	public static enum ComponentType {
		DIRECT, DIFF, MEAN;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private ComponentType componentType=ComponentType.DIRECT;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="INPUT_ID")
	private NeuralInput neuralInput;
	
	
	private int offset=0;
	
	private int period=1;
	
	private double upperRange;
	
	private double lowerRange;
	
	
	

	public NeuralInputComponent() {
		super();
	}





	@Override
	public NeuralInputComponent copy() {
		NeuralInputComponent c=new NeuralInputComponent();
		
		c.id=id;
		
		c.neuralInput=neuralInput;
		c.offset=offset;
		c.period=period;
		
		c.upperRange=upperRange;
		c.lowerRange=lowerRange;
		
		return c;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NeuralInput getNeuralInput() {
		return neuralInput;
	}

	public void setNeuralInput(NeuralInput neuralInput) {
		this.neuralInput = neuralInput;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public double getUpperRange() {
		return upperRange;
	}

	public void setUpperRange(double upperRange) {
		this.upperRange = upperRange;
	}

	public double getLowerRange() {
		return lowerRange;
	}


	public void setLowerRange(double lowerRange) {
		this.lowerRange = lowerRange;
	}
	
	
	

}
