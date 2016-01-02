package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbContract;



@Entity
public class NeuralConfiguration implements Serializable, Copyable<NeuralConfiguration>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8390687932325619962L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	@OneToMany(mappedBy="neuralConfiguration",cascade=CascadeType.ALL)
	private List<NeuralInput> neuralInputs;
	
	
	private String name;
	
	private long creationDate;
	
	

	public NeuralConfiguration() {
		super();
	}



	@Override
	public NeuralConfiguration copy() {
		NeuralConfiguration c=new NeuralConfiguration();
		
		c.id=id;
		
		c.name=name;
		c.creationDate=creationDate;
		c.contract=contract;
		
		return c;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public IbContract getContract() {
		return contract;
	}



	public void setContract(IbContract contract) {
		this.contract = contract;
	}



	public List<NeuralInput> getNeuralInputs() {
		return neuralInputs;
	}



	public void setNeuralInputs(List<NeuralInput> neuralInputs) {
		this.neuralInputs = neuralInputs;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public long getCreationDate() {
		return creationDate;
	}



	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	
	

}
