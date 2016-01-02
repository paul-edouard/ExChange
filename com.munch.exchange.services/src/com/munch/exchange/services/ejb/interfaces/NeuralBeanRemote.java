package com.munch.exchange.services.ejb.interfaces;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;



@Remote
public interface NeuralBeanRemote {

	
	//Neural Configuration
	public void update(NeuralConfiguration confiuration);
	
	
}
