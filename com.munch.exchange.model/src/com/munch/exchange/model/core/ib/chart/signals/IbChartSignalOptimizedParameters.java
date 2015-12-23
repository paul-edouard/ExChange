package com.munch.exchange.model.core.ib.chart.signals;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;

@Entity
public class IbChartSignalOptimizedParameters implements Serializable,Copyable<IbChartSignalOptimizedParameters>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2403412044128190012L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private BarSize size;
	
	@OneToMany(mappedBy="optimizedParameters",cascade=CascadeType.ALL)
	private List<IbChartParameter> parameters=new LinkedList<IbChartParameter>();
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SIGNAL_ID")
	private IbChartSignal parent;

	public IbChartSignalOptimizedParameters() {
		super();
	}
	
	
	@Override
	public IbChartSignalOptimizedParameters copy() {
		
		IbChartSignalOptimizedParameters cp=new IbChartSignalOptimizedParameters();
		cp.id=this.id;
		cp.size=this.size;
		cp.parameters=new LinkedList<IbChartParameter>();
		
		for(IbChartParameter param:this.parameters){
			IbChartParameter c_p=param.copy();
			c_p.setOptimizedParameters(cp);
			cp.parameters.add(c_p);
		}
		
		return cp;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}

	public List<IbChartParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<IbChartParameter> parameters) {
		this.parameters = parameters;
	}

	public IbChartSignal getParent() {
		return parent;
	}

	public void setParent(IbChartSignal parent) {
		this.parent = parent;
	}

	
	
	
	
	
	
	

}
