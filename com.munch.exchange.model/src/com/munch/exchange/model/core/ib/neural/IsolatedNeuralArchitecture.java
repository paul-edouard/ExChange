package com.munch.exchange.model.core.ib.neural;

import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class IsolatedNeuralArchitecture extends NeuralArchitecture {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5348439200729124298L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	protected NeuralConfiguration parent;
	
	
	/**
	 * this is the account to set for the isolated architecture
	 * once set then buy and sell order will be automatically send to the server
	 */
	private String account="";
	
	
	public IsolatedNeuralArchitecture(NeuralArchitecture neuralArchitecture) {
		super();
		this.id=neuralArchitecture.id;
		this.name=neuralArchitecture.name;
		
		this.type=neuralArchitecture.type;
		this.blockProfitLimit=neuralArchitecture.blockProfitLimit;
		this.tradeProfitLimit=neuralArchitecture.tradeProfitLimit;
		this.volume=neuralArchitecture.volume;
		this.hiddenLayerDescription=neuralArchitecture.hiddenLayerDescription;
		this.activation=neuralArchitecture.activation;
		
		this.neuralNetworks=new LinkedList<NeuralNetwork>();
		for(NeuralNetwork network:neuralArchitecture.neuralNetworks){
			NeuralNetwork network_cp=network.copy();
			network_cp.setNeuralArchitecture(this);
			this.neuralNetworks.add(network_cp);
		}
	}
	
	public IsolatedNeuralArchitecture() {
		super();
		// TODO Auto-generated constructor stub
	}




	@Override
	public IsolatedNeuralArchitecture copy() {
		IsolatedNeuralArchitecture cp=new IsolatedNeuralArchitecture();
		
		cp.id=this.id;
		cp.name=this.name;
		
		cp.type=this.type;
		cp.blockProfitLimit=this.blockProfitLimit;
		cp.tradeProfitLimit=this.tradeProfitLimit;
		cp.volume=this.volume;
		cp.hiddenLayerDescription=this.hiddenLayerDescription;
		cp.activation=this.activation;
		
		cp.neuralNetworks=new LinkedList<NeuralNetwork>();
		for(NeuralNetwork network:this.neuralNetworks){
			NeuralNetwork network_cp=network.copy();
			network_cp.setNeuralArchitecture(cp);
			cp.neuralNetworks.add(network_cp);
		}
		
		cp.account=this.account;
		
		return cp;
	}
	
	
	
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public NeuralConfiguration getParent() {
		return parent;
	}

	public void setParent(NeuralConfiguration parent) {
		this.parent = parent;
	}
	
	
	

}
