package com.munch.exchange.model.core.financials;

import java.io.Serializable;
import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public abstract class FinancialPoint extends DatePoint {
	
	static final String FIELD_PeriodEnding="PeriodEnding";
	static final String FIELD_Type="type";
	
	
	protected Calendar PeriodEnding;
	
	protected Type type;
	
	public enum Type implements Serializable {
		ANNUAL(1), QUATERLY(2), NONE(0);
		private int val;

		private Type(int value) {
			this.val = value;
		}

		private void fromString(String value) {
			this.val = Integer.parseInt(value);
		}

		public int getValue() {
			return val;
		}

		public String toString() {
			return String.valueOf(val);
		}
		
		

	}

	public Calendar getPeriodEnding() {
		return PeriodEnding;
	}

	public void setPeriodEnding(Calendar periodEnding) {
		changes.firePropertyChange(FIELD_PeriodEnding, PeriodEnding, PeriodEnding = periodEnding);
		//PeriodEnding = periodEnding;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		changes.firePropertyChange(FIELD_Type, this.type, this.type = type);
		//this.type = type;
	}

	@Override
	protected void initAttribute(Element rootElement) {
		this.setPeriodEnding(DateTool.StringToDate(rootElement.getAttribute(FIELD_PeriodEnding)));
		this.type.fromString(rootElement.getAttribute(FIELD_Type));
		
		super.initAttribute(rootElement);
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_PeriodEnding, DateTool.dateToString( this.getPeriodEnding()));
		rootElement.setAttribute(FIELD_Type, this.type.toString());
		
		// TODO Auto-generated method stub
		super.setAttribute(rootElement);
	};
	
	
	

}
