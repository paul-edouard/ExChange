package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public class EstimationPoint extends DatePoint {
	
static final String FIELD_Value="value";
	
	
	private float value=0;
	

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		changes.firePropertyChange(FIELD_Value, this.value, this.value = value);
		//this.value = value;
	}

	
	@Override
	public String toString() {
		return "Dividend [date=" + DateTool.dateToString(getDate()) + ", value=" + value + "]";
	}

	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	protected void initAttribute(Element Root){
		this.setValue(Float.valueOf(Root.getAttribute(FIELD_Value)));
		
		super.initAttribute(Root);
	}

	
	protected void setAttribute(Element e){
		e.setAttribute(FIELD_Value,String.valueOf(this.getValue()));
		
		super.setAttribute(e);
	}

}
