package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;

public interface IAnalystEstimationProvider {
	
	
	//==================================
	//==     ANALYST ESTIMATION       ==
	//==================================
		
	
	boolean load(Stock stock);
		
	
	boolean update(Stock stock);

}
