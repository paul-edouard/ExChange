package com.munch.exchange.model.core.ib.bar;

public enum BarType {
	
	TIME,RANGE;
	
	public static String[] toStringArray(){
		String[] array= new String[2];
		array[0] = TIME.name();
		array[1] = RANGE.name();
		return array;
	}
	
	public static BarType fromString(String name){
		if(name.equals(RANGE.name()))
			return RANGE;
		else return TIME;
	}
	

}
