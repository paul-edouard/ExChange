package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
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
public class IbChartSerie implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2519428267199061398L;
	
	public enum RendererType { NONE, MAIN, SECOND, PERCENT, ERROR, DEVIATION, DEVIATION_PERCENT;}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;
	//private double[] values;
	//private ValuePointList valuePointList=new ValuePointList();
	private int validAtPosition;
	private boolean isMain;
	private boolean isActivated;
	
	//@ElementCollection
	//private List<Integer> color;
	
	@Enumerated(EnumType.STRING)
	private RendererType rendererType;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="INDICATOR_ID")
	private IbChartIndicator indicator;
	
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

	public int getValidAtPosition() {
		return validAtPosition;
	}

	public void setValidAtPosition(int validAtPosition) {
		this.validAtPosition = validAtPosition;
	}

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	
	/*
	public List<Integer> getColor() {
		return color;
	}

	public void setColor(List<Integer> color) {
		this.color = color;
	}
	*/

	public RendererType getRendererType() {
		return rendererType;
	}

	public void setRendererType(RendererType rendererType) {
		this.rendererType = rendererType;
	}
	
	/*
	public IbChartIndicator getIndicator() {
		return indicator;
	}

	public void setIndicator(IbChartIndicator indicator) {
		this.indicator = indicator;
	}
	*/
	
	

}
