package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;

public interface INeuralNetworkProvider {
	
	
	boolean load(Stock stock);
	boolean save(Stock stock);
		
	
	void createAllInputPoints(Stock stock);
	
	ValuePointList calculateMaxProfitOutputList(Stock stock);
	ValuePointList calculateMaxProfitOutputList(Stock stock,double penalty);
}
