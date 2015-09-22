package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class IbChartIndicatorGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6625769451330452530L;
	public static final String ROOT="ROOT";
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;
	
	@Transient
	private boolean isDirty=false;
	
	@OneToMany(mappedBy="group",cascade=CascadeType.ALL)
	private List<IbChartIndicator> indicators;
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<IbChartIndicatorGroup> children;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PARENT_ID")
	private IbChartIndicatorGroup parent;
	
	
	public IbChartIndicatorGroup(IbChartIndicatorGroup parent,String name){
		this.parent=parent;
		this.name=name;
		
		if(this.parent!=null)
			this.parent.getChildren().add(this);
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

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		if(this.isDirty){
			if(parent!=null)
				parent.setDirty(isDirty);
		}
		else{
			for(IbChartIndicatorGroup child:children)
				child.setDirty(isDirty);
		}
	}

	public List<IbChartIndicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<IbChartIndicator> indicators) {
		this.indicators = indicators;
	}

	public List<IbChartIndicatorGroup> getChildren() {
		return children;
	}

	public void setChildren(List<IbChartIndicatorGroup> children) {
		this.children = children;
	}

	public IbChartIndicatorGroup getParent() {
		return parent;
	}

	public void setParent(IbChartIndicatorGroup parent) {
		this.parent = parent;
	}
	
}
