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

import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartSerie.RendererType;


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
	
	private int color_R;
	private int color_G;
	private int color_B;
	
	@Enumerated(EnumType.STRING)
	private RendererType rendererType;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="INDICATOR_ID")
	private IbChartIndicator indicator;
	
	public IbChartSerie(){
		
	}
	
	public IbChartSerie(IbChartIndicator parent,String name,RendererType type,boolean isMain, boolean isActivated,int[] color ){
		this.name=name;
		
		this.isMain=isMain;
		this.isActivated=isActivated;
		this.color_R=color[0];
		this.color_G=color[1];
		this.color_B=color[2];
		this.rendererType=type;
		
		this.indicator=parent;
	
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

	public RendererType getRendererType() {
		return rendererType;
	}

	public void setRendererType(RendererType rendererType) {
		this.rendererType = rendererType;
	}
	
	public IbChartIndicator getIndicator() {
		return indicator;
	}

	public void setIndicator(IbChartIndicator indicator) {
		this.indicator = indicator;
	}
	

	public int getColor_R() {
		return color_R;
	}
	

	public void setColor_R(int color_R) {
		this.color_R = color_R;
	}
	

	public int getColor_G() {
		return color_G;
	}
	

	public void setColor_G(int color_G) {
		this.color_G = color_G;
	}
	

	public int getColor_B() {
		return color_B;
	}
	

	public void setColor_B(int color_B) {
		this.color_B = color_B;
	}
	
	
	

}
