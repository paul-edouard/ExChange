package com.munch.exchange.services.ejb.interfaces;

public interface IIBNeuralProvider extends NeuralBeanRemote {
	
	
	//Initialization
	void init();
			
	//Close the service
	void close();

}
