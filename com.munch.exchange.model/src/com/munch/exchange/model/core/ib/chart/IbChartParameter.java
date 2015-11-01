package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.Copyable;


@Entity
public class IbChartParameter implements Serializable,Copyable<IbChartParameter>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5309044394144277259L;
	
	public enum ParameterType {DOUBLE, INTEGER, STRING, NONE;}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private ParameterType type;
	
	
	private String name;
	
	private double currentValue=0;
	private double defaultValue=0;
	private double _minValue=0;
	private double _maxValue=0;
	private int scalarFactor=0;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="INDICATOR_ID")
	private IbChartIndicator parent;
	
	public IbChartParameter(){
		
	}
	
	
	public IbChartParameter(IbChartIndicator parent,String name,ParameterType type,  double val, double minValue, double maxValue, int  scalarFac){
		this.currentValue=val;
		this.defaultValue=val;
		this._maxValue=maxValue;
		this._minValue=minValue;
		this.type=type;
		this.name=name;
		this.parent=parent;
		
		this.scalarFactor=scalarFac;
	}
	
	@Override
	public IbChartParameter copy() {
		IbChartParameter c=new IbChartParameter();
		
		c.id=this.id;
		
		c.type=this.type;
		c.name=this.name;
		c.currentValue=this.currentValue;
		c.defaultValue=this.defaultValue;
		c._minValue=this._minValue;
		c._maxValue=this._maxValue;
		c.scalarFactor=this.scalarFactor;
	
		return c;
	}
	
	public void resetDefault(){
		this.setValue(this.defaultValue);
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return currentValue;
	}
	
	public int getIntegerValue() {
		return (int) currentValue;
	}

	public void setValue(double value) {
		this.currentValue = value;
	}

	public double getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(double defaultValue) {
		this.defaultValue = defaultValue;
	}

	public double getMinValue() {
		return _minValue;
	}

	public void setMinValue(double minValue) {
		this._minValue = minValue;
	}

	public double getMaxValue() {
		return _maxValue;
	}

	public void setMaxValue(double maxValue) {
		this._maxValue = maxValue;
	}

	public int getScalarFactor() {
		return scalarFactor;
	}

	public void setScalarFactor(int scalarFactor) {
		this.scalarFactor = scalarFactor;
	}

	public IbChartIndicator getIndicator() {
		return parent;
	}

	public void setIndicator(IbChartIndicator indicator) {
		this.parent = indicator;
	}


	public IbChartIndicator getParent() {
		return parent;
	}


	public void setParent(IbChartIndicator parent) {
		this.parent = parent;
	}


	
	
	
	
	
}
