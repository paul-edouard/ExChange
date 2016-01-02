package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.munch.exchange.model.core.ib.Copyable;

@Entity
@Inheritance
@DiscriminatorColumn(name="INPUT_TYPE")
public abstract class NeuralInput implements Serializable, Copyable<NeuralInput>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8345519650468293792L;
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	@OneToMany(mappedBy="neuralInput",cascade=CascadeType.ALL)
	private List<NeuralInputComponent> components;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	private NeuralConfiguration neuralConfiguration;
	
	private String name;
	

	public NeuralInput() {
		super();
		// TODO Auto-generated constructor stub
	}

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

	public List<NeuralInputComponent> getComponents() {
		return components;
	}

	public void setComponents(List<NeuralInputComponent> components) {
		this.components = components;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
	
}
