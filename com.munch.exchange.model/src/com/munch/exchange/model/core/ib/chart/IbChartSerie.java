package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.munch.exchange.model.core.chart.ChartIndicator;

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
	private int validAtPosition=0;
	private boolean isMain=false;
	private boolean isActivated=false;
	
	@ElementCollection
	private List<Integer> color;
	private RendererType rendererType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="INDICATOR_ID")
	private IbChartIndicator indicator;
	

}
