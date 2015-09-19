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


@Entity
public class IbChartParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5309044394144277259L;
	
	public enum ParameterType {DOUBLE, INTEGER, STRING, NONE;}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private ParameterType ptype;
	
	
	private String name;
	
	private double currentValue=0;
	private double defaultValue=0;
	private double _minValue=0;
	private double _maxValue=0;
	private int scalarFactor=0;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="INDICATOR_ID")
	private IbChartIndicator parent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParameterType getType() {
		return ptype;
	}

	public void setType(ParameterType type) {
		this.ptype = type;
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
	
	
	
	
}
