package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartParameter;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.ib.ComparableAttributes;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;

@Entity
@Inheritance
@DiscriminatorColumn(name="CHART_TYPE")
public abstract class IbChartIndicator implements Serializable,Copyable<IbChartIndicator>,ComparableAttributes<IbChartIndicator>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5013784544015564679L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	protected String name;
	private boolean isActivated=false;
	
	@Transient
	private boolean isDirty=false;
	
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	protected List<IbChartParameter> parameters=new LinkedList<IbChartParameter>();
	
	
	@OneToMany(mappedBy="indicator",cascade=CascadeType.ALL)
	protected List<IbChartSerie> series=new LinkedList<IbChartSerie>();
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="GROUP_ID")
	private IbChartIndicatorGroup group;
	
	public IbChartIndicator(IbChartIndicatorGroup group) {
		super();
		
		setGroup(group);
		initName();
		createSeries();
		createParameters();
		
	}
	
	public IbChartIndicator() {
		super();
		
		initName();
		createSeries();
		createParameters();
		
	}
	
	public void copyData(IbChartIndicator in){
		this.id=in.id;
		this.name=in.name;
		this.isActivated=in.isActivated;
		this.isDirty=in.isDirty;
		
		parameters=new LinkedList<IbChartParameter>();
		
		for(IbChartParameter param:in.parameters){
			IbChartParameter c_p=param.copy();
			c_p.setParent(this);
			parameters.add(c_p);
		}
		
		series=new LinkedList<IbChartSerie>();
		for(IbChartSerie serie:in.series){
			IbChartSerie c_s=serie.copy();
			c_s.setIndicator(this);
			series.add(c_s);
		}
	}
	
	
	public void resetDefault(){
		for(IbChartParameter p:parameters){
			p.resetDefault();
		}
	}
	
	
	public boolean containsSerie(IbChartSerie serie){
		for(IbChartSerie s:series){
			if(s.getId()==serie.getId())return true;
		}
		return false;
	}
	
	
	@Override
	public boolean identical(IbChartIndicator other) {
		if (id != other.id)
			return false;
		if (isActivated != other.isActivated)
			return false;
		if (isDirty != other.isDirty)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		}
		else if (parameters.size()!=other.parameters.size())
			return false;
		else{
			for(IbChartParameter param:parameters){
				IbChartParameter c_param=other.getChartParameter(param.getName());
				if(c_param==null)return false;
				if(!param.identical(c_param))return false;
			}
		}
		if (series == null) {
			if (other.series != null)
				return false;
		}
		else if (series.size()!=other.series.size())
			return false;
		else {
			for(IbChartSerie serie:series){
				IbChartSerie c_serie=other.getChartSerie(serie.getName());
				if(c_serie==null)return false;
				if(!serie.identical(c_serie))return false;
			}
		}
		return true;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IbChartIndicator other = (IbChartIndicator) obj;
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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
		
	}
	
	public void fireActivationChanged(){
		this.getGroup().getRoot().fireIndicatorActivationChanged(this);
	}


	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}


	public List<IbChartParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<IbChartParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void fireParametersChanged(){
		this.getGroup().getRoot().fireIndicatorParameterChanged(this);
	}


	public List<IbChartSerie> getSeries() {
		return series;
	}

	public void setSeries(List<IbChartSerie> series) {
		this.series = series;
	}


	public IbChartIndicatorGroup getGroup() {
		return group;
	}


	public void setGroup(IbChartIndicatorGroup group) {
		this.group=group;
		if(this.group!=null)
		this.group.getIndicators().add(this);

	}
	
	
	public IbChartParameter getChartParameter(String paramName){
		for(IbChartParameter param:parameters){
			if(param.getName().equals(paramName)){
				return param;
			}
		}
		return null;
	}
	
	public IbChartSerie getChartSerie(String serieName){
		for(IbChartSerie serie:series){
			if(serie.getName().equals(serieName)){
				return serie;
			}
		}
		return null;
	}
	
	public IbChartSerie getMainChartSerie(){
		for(IbChartSerie serie:series){
			if(serie.isMain())return serie;
		}
		return null;
	}
	
	
	public abstract void initName();
	
	public abstract void createSeries();
	
	public abstract void createParameters();
	
	public abstract void compute(List<IbBar> bars);
	
	public abstract void computeLast(List<IbBar> bars);
	
	protected double[] barsToDoubleArray(List<IbBar> bars,DataType dataType){
		return barsToDoubleArray(bars, dataType, bars.size());
	}
	
	protected double[] barsToDoubleArray(List<IbBar> bars,DataType dataType,int numberOfValues){
		int min=Math.min(bars.size(), numberOfValues);
		int last=bars.size()-min;
		double[] array=new double[min];
		for(int i=bars.size()-1;i>=last;i--){
			array[i-last]=bars.get(i).getData(dataType);
		}
		return array;
	}
	
	protected long[] getTimeArray(List<IbBar> bars,int numberOfValues){
		int min=Math.min(bars.size(), numberOfValues);
		int last=bars.size()-min;
		long[] array=new long[min];
		for(int i=bars.size()-1;i>=last;i--){
			array[i-last]=bars.get(i).getTimeInMs();
		}
		return array;
	}
	
	protected long[] getTimeArray(List<IbBar> bars){
		return getTimeArray(bars, bars.size());
	}

	
}
