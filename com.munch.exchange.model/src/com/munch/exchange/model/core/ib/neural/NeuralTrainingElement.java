package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.munch.exchange.model.core.ib.Copyable;

@Entity
public class NeuralTrainingElement implements Serializable, Copyable<NeuralTrainingElement>,Comparable<NeuralTrainingElement>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2576227407709736361L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	private NeuralConfiguration neuralConfiguration;
	
	private String name;
	
	
	public NeuralTrainingElement() {
		super();
	}
	
	public NeuralTrainingElement(String name) {
		super();
		this.name=name;
	}

	@Override
	public int compareTo(NeuralTrainingElement o) {
		return this.name.compareTo(o.name);
	}

	@Override
	public NeuralTrainingElement copy() {
		NeuralTrainingElement cp=new NeuralTrainingElement();
		cp.id=this.id;
		cp.name=this.name;
		cp.neuralConfiguration=this.neuralConfiguration;
		
		return cp;
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

	public NeuralConfiguration getNeuralConfiguration() {
		return neuralConfiguration;
	}

	public void setNeuralConfiguration(NeuralConfiguration neuralConfiguration) {
		this.neuralConfiguration = neuralConfiguration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
