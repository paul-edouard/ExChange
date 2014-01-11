package com.munch.exchange.model.core.analystestimation;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public class EstimationPoint extends DatePoint {
	
static final String FIELD_Value="value";
	
	
	private float value=Float.NaN;
	
	
	public EstimationPoint(){
		super();
	}
	
	public EstimationPoint(Calendar date,float value){
		this.date=date;
		this.value=value;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		changes.firePropertyChange(FIELD_Value, this.value, this.value = value);
		//this.value = value;
	}

	
	@Override
	public String toString() {
		return "[date=" + DateTool.dateToString(getDate()) + "; value=" + value+"]" ;
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
