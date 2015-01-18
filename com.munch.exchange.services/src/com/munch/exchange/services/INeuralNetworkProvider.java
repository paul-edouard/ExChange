package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;

public interface INeuralNetworkProvider {
	
	
	boolean load(Stock stock);
	boolean save(Stock stock);
	
	boolean loadConfiguration(Stock stock);
	boolean saveConfiguration(Stock stock);
	boolean deleteConfiguration(Stock stock);
	
	
	
	String getNetworkArchitecturesLocalSavePath(Stock stock);
	
	
	/**
	 * create all value point from the defined time series
	 * the output value point list will be also created
	 * 
	 * @param stock
	 */
	void createAllValuePoints(Configuration configuration, boolean forceCreation);
	
}
