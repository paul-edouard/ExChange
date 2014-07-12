package com.munch.exchange.model.core.financials;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public abstract class FinancialPoint extends DatePoint {
	
	static final String FIELD_PeriodEnding="PeriodEnding";
	static final String FIELD_EffectiveDate="EffectiveDate";
	
	static final String FIELD_PeriodType="type";
	
	public static final String PeriodeTypeNone="none";
	public static final String PeriodeTypeAnnual="annual";
	public static final String PeriodeTypeQuaterly="quarterly";
	
	
	protected Calendar PeriodEnding;
	protected Calendar EffectiveDate;
	
	private String periodType=PeriodeTypeNone;
	
	
	public abstract String getValue(String fieldKey);
	public abstract void setValue(String fieldKey);
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		/*
		result = prime * result
				+ ((PeriodEnding == null) ? 0 : PeriodEnding.hashCode());
		*/
		result = prime * result + ((periodType == null) ? 0 : periodType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		/*
		if (!super.equals(obj)) {
			return false;
		}
		*/
		//System.out.println("InList: "+this);
		//System.out.println("CompareWith: "+obj+"\n");
		if (!(obj instanceof FinancialPoint)) {
			return false;
		}
		FinancialPoint other = (FinancialPoint) obj;
		if (PeriodEnding == null) {
			if (other.PeriodEnding != null) {
				return false;
			}
		} else if (!PeriodEnding.equals(other.PeriodEnding)) {
			return false;
		}
		if (!periodType.equals( other.periodType)) {
			return false;
		}
		return true;
	}
	
	
	

	@Override
	public int compareTo(DatePoint o) {
		if(o instanceof FinancialPoint){
		//	FinancialPoint p=(FinancialPoint) o;
			if(this.periodType.compareTo(((FinancialPoint) o).periodType)==-1)return -1;
			if(this.periodType.compareTo(((FinancialPoint) o).periodType)==1)return 1;
		}
		
		return super.compareTo(o);
	}

	public String LongValueToString(long l){
		if(l==Long.MIN_VALUE)return "-";
		else{
			return String.valueOf(l);
		}
	}

	public Calendar getPeriodEnding() {
		return PeriodEnding;
	}

	public void setPeriodEnding(Calendar periodEnding) {
		changes.firePropertyChange(FIELD_PeriodEnding, PeriodEnding, PeriodEnding = periodEnding);
		//PeriodEnding = periodEnding;
	}


	public Calendar getEffectiveDate() {
		if(EffectiveDate==null)return PeriodEnding;
		return EffectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		changes.firePropertyChange(FIELD_EffectiveDate, this.EffectiveDate,
				this.EffectiveDate = effectiveDate);
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		changes.firePropertyChange(FIELD_PeriodType, this.periodType, this.periodType = periodType);
		//this.periodType = periodType;
	}

	@Override
	protected void initAttribute(Element rootElement) {
		this.setPeriodEnding(DateTool.StringToDate(rootElement.getAttribute(FIELD_PeriodEnding)));
		this.setEffectiveDate(DateTool.StringToDate(rootElement.getAttribute(FIELD_EffectiveDate)));
		this.setPeriodType(rootElement.getAttribute(FIELD_PeriodType));
		
		super.initAttribute(rootElement);
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_PeriodEnding, DateTool.dateToString( this.getPeriodEnding()));
		rootElement.setAttribute(FIELD_EffectiveDate, DateTool.dateToString( this.getEffectiveDate()));
		rootElement.setAttribute(FIELD_PeriodType, this.getPeriodType());
		
		super.setAttribute(rootElement);
	};
	
	
	

}
