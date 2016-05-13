package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.List;

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
import com.munch.exchange.model.core.ib.ComparableAttributes;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;


@Entity
public class IbChartParameter implements Serializable,Copyable<IbChartParameter>,ComparableAttributes<IbChartParameter>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5309044394144277259L;
	
	public enum ParameterType {DOUBLE, INTEGER, STRING, NONE, LIST;}
	
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
	private String list="";
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="INDICATOR_ID")
	private IbChartIndicator parent;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="OPTIMIZED_PARAM_ID")
	private IbChartSignalOptimizedParameters optimizedParameters;
	
	public IbChartParameter(){
		
	}
	
	public IbChartParameter(IbChartIndicator parent,String name,String val, String[] values){
		
		this.list="";
		for(int i=0;i<values.length;i++){
			if(values[i]==val){
				this.currentValue=i;
			}
			this.list+=values[i]+";";
		}
		if(this.list.length()>0)
			this.list.substring(0, this.list.length()-1);
		
		this.defaultValue=this.currentValue;
		this._maxValue=values.length-1;
		this._minValue=0;
		this.type=ParameterType.LIST;
		this.name=name;
		this.parent=parent;
		
		this.scalarFactor=0;
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
		c.list=this.list;
	
		return c;
	}
	
	public void resetDefault(){
		this.setValue(this.defaultValue);
	}
	
	
	@Override
	public boolean identical(IbChartParameter other) {
		if (Double.doubleToLongBits(_maxValue) != Double
				.doubleToLongBits(other._maxValue))
			return false;
		if (Double.doubleToLongBits(_minValue) != Double
				.doubleToLongBits(other._minValue))
			return false;
		if (Double.doubleToLongBits(currentValue) != Double
				.doubleToLongBits(other.currentValue))
			return false;
		if (Double.doubleToLongBits(defaultValue) != Double
				.doubleToLongBits(other.defaultValue))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		
		if (scalarFactor != other.scalarFactor)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	
	public boolean hasSameValueAs(IbChartParameter other){
		if(other==null)return false;
		if(this.type!=other.type)return false;
		
		switch (type) {
		case DOUBLE:
			return this.currentValue==other.currentValue;
		case INTEGER:
			return this.currentValue==other.currentValue;
		case STRING:
			//TODO at the moment no supported
			return false;
		case LIST:
			return this.list==other.list && this.currentValue==other.currentValue;
		default:
			return false;
		}
		
	}
	
	
	public void copyValue(IbChartParameter other){
		if(other==null)return ;
		if(this.type!=other.type)return ;
		
		switch (type) {
		case DOUBLE:
			this.currentValue=other.currentValue;
			break;
		case INTEGER:
			this.currentValue=other.currentValue;
			break;
		case STRING:
			//TODO at the moment no supported
			break;
		case LIST:
			this.list=other.list;
			this.currentValue=other.currentValue;
			break;
		default:
			break;
		}
	}

	@Override
 	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IbChartParameter other = (IbChartParameter) obj;
		if (id != other.id)
			return false;
		return true;
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
	
	public String getStringValue() {
		int index= (int) currentValue;
		String[] values=this.list.split(";");
		if(index<values.length)return values[index];
		
		return "";
	}
	

	public void setValue(double value) {
		this.currentValue = value;
		if(parent!=null)
			parent.setDirty(true);
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
	
	


	public IbChartSignalOptimizedParameters getOptimizedParameters() {
		return optimizedParameters;
	}


	public void setOptimizedParameters(
			IbChartSignalOptimizedParameters optimizedParameters) {
		this.optimizedParameters = optimizedParameters;
	}


	public static boolean areAllValuesEqual(List<IbChartParameter> list1, List<IbChartParameter> list2){
		if(list1.size()!=list2.size())return false;
		
		for(int i=0;i<list1.size();i++){
			if(!list1.get(i).hasSameValueAs(list2.get(i)))
				return false;
		}
		
		
		return true;
	}
	
	public static void copyValuesOnly(List<IbChartParameter> input, List<IbChartParameter> target){
		if(input.size()!=target.size())return ;
		
		for(int i=0;i<input.size();i++){
			if(target.get(i).getType()!=input.get(i).getType())return;
		}
		
		for(int i=0;i<input.size();i++){
			target.get(i).copyValue(input.get(i));
		}
		
		
	}


	
	
	
	
	
}
