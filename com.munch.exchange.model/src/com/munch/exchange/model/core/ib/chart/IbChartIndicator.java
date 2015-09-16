package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Inheritance
@DiscriminatorColumn(name="BAR_TYPE")
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
	
	
	@OneToMany(mappedBy="indicator",cascade=CascadeType.ALL)
	private List<IbChartParameter> parameters;
	
	@OneToMany(mappedBy="indicator",cascade=CascadeType.ALL)
	private List<IbChartSerie> series;
	
	


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
	
	

}
