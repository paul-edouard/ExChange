package com.munch.exchange.model.core.ib.neural;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ib.controller.Types.BarSize;


@Entity
public class NeuralIndicatorInput extends NeuralInput{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7890490253355577308L;
	
	@Enumerated(EnumType.STRING)
	private BarSize size;
	
	private int ibBarContainerId;
	
	
	public NeuralIndicatorInput() {
		super();
	}

	@Override
	public NeuralInput copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}

	public int getIbBarContainerId() {
		return ibBarContainerId;
	}

	public void setIbBarContainerId(int ibBarContainerId) {
		this.ibBarContainerId = ibBarContainerId;
	}
	
	

}
