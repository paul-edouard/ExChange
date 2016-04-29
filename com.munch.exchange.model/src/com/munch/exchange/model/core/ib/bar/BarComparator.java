package com.munch.exchange.model.core.ib.bar;

import java.util.Comparator;

import com.ib.controller.Bar;

public class BarComparator implements Comparator<Bar> {

	@Override
	public int compare(Bar arg0, Bar arg1) {
		if(arg0.time() > arg1.time())
			return 1;
		else if(arg0.time() < arg1.time()){
			return -1;
		}
		
		return 0;
	}

}
