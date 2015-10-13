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
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;

@Entity
@Inheritance
@DiscriminatorColumn(name="CHART_TYPE")
public abstract class IbChartIndicator implements Serializable{
	
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
