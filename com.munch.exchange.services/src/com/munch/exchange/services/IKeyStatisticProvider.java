package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;

public interface IKeyStatisticProvider {
	
	
	boolean load(Stock stock);
		
	
	boolean update(Stock stock);

}
