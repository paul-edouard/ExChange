package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.LinkedList;
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
import javax.persistence.Transient;

import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.neural.NeuralInputComponent.ComponentType;

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
	private List<NeuralInputComponent> components=new LinkedList<NeuralInputComponent>();
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	private NeuralConfiguration neuralConfiguration;
	
	private String name;
	
	@Transient
	protected double[] values;
	
	@Transient
	protected double[] times;
	
	
	public NeuralInput() {
		super();
	}
		
	protected void copyData(NeuralInput input, NeuralInput target){
		target.id=input.id;
		target.components=new LinkedList<NeuralInputComponent>();
		for(NeuralInputComponent component:input.components){
			NeuralInputComponent component_copy=component.copy();
			component_copy.setNeuralInput(target);
			target.components.add(component_copy);
		}
		
		target.neuralConfiguration=input.neuralConfiguration.copy();
		target.name=input.name;
	}
	
	public void addDirectComponent(){
		
		NeuralInputComponent inputComponent=new NeuralInputComponent();
		inputComponent.setNeuralInput(this);
		inputComponent.setComponentType(ComponentType.DIRECT);
		inputComponent.setOffset(0);
		inputComponent.setPeriod(1);
		
		components.add(inputComponent);
		
	}
	
	public abstract void load();
	
	public abstract void computeAdaptedData(double[] referencedTimes);
	
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
	
	public double[] getValues() {
		return values;
	}
	
	public void setValues(double[] values) {
		this.values = values;
	}
	
	public double[] getTimes() {
		return times;
	}
	
	public void setTimes(double[] times) {
		this.times = times;
	}
	
	
}
