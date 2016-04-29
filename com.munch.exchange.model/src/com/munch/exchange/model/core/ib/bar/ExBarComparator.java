package com.munch.exchange.model.core.ib.bar;

import java.util.Comparator;

public class ExBarComparator implements Comparator<ExBar> {

	@Override
	public int compare(ExBar arg0, ExBar arg1) {
		if(arg0.getTime() > arg1.getTime())
			return 1;
		else if(arg0.getTime() < arg1.getTime()){
			return -1;
		}
		
		return 0;
	}

}
