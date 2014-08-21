package com.munch.exchange.model.analytic.indicator;

import java.util.LinkedList;

import com.munch.exchange.model.xml.Parameter;
import com.munch.exchange.model.xml.ParameterElement;

public abstract class Indicator<E> extends ParameterElement{
	
	
	public String getName(){
		return this.getClass().getSimpleName();
	}
	
	public abstract LinkedList<Parameter> getParameterList();
	
	public abstract E compute();
	
	

}
