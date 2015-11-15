package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.LinkedList;
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
import javax.persistence.Transient;

import com.munch.exchange.model.core.ib.ComparableAttributes;
import com.munch.exchange.model.core.ib.Copyable;



@Entity
public class IbChartSerie implements Serializable,Copyable<IbChartSerie>,ComparableAttributes<IbChartSerie>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2519428267199061398L;
	
	public enum RendererType { NONE, MAIN, SECOND, PERCENT, ERROR, DEVIATION, DEVIATION_PERCENT;}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;
	
	@Transient
	private List<IbChartPoint> points=new LinkedList<IbChartPoint>();
	
	
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
	
	
	@Override
	public IbChartSerie copy() {
		IbChartSerie c=new IbChartSerie();
		
		c.id=this.id;
		
		c.name=this.name;
		c.isMain=this.isMain;
		c.isActivated=this.isActivated;
		c.color_R=this.color_R;
		c.color_G=this.color_G;
		c.color_B=this.color_B;
		c.rendererType=this.rendererType;
		
		c.validAtPosition=this.validAtPosition;
		
		return c;
	}
	
	/* (non-Javadoc)
	 * @see com.munch.exchange.model.core.ib.ComparableAttributes#identical(java.lang.Object)
	 */
	@Override
	public boolean identical(IbChartSerie other) {
		//System.out.println("Test the color B");
		if (color_B != other.color_B)
			return false;
		//System.out.println("Test the color G");
		if (color_G != other.color_G)
			return false;
		//System.out.println("Test the color R");
		if (color_R != other.color_R)
			return false;
		//System.out.println("Test the id");
		if (id != other.id)
			return false;
		//System.out.println("Test the activation");
		if (isActivated != other.isActivated)
			return false;
		//System.out.println("Test the main");
		if (isMain != other.isMain)
			return false;
		//System.out.println("Test the name");
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		//System.out.println("Test the renderer");
		if (rendererType != other.rendererType)
			return false;
		//System.out.println("Test the valid at position");
		//if (validAtPosition != other.validAtPosition)
		//	return false;
		
		//System.out.println("Test finihed");
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
		IbChartSerie other = (IbChartSerie) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void setPointValues(long[] times,double[] values){
		if(values.length!=times.length)return;
		
		points.clear();
		for(int i=0;i<values.length;i++){
			points.add(new IbChartPoint(times[i], values[i]));
		}
	}
	
	public void addNewPointsOnly(long[] times,double[] values){
		if(values.length!=times.length)return;
		
		for(int i=0;i<values.length;i++){
			if(containsPoint(times[i]))continue;
			points.add(new IbChartPoint(times[i], values[i]));
		}
	}
	
	private boolean containsPoint(long time){
		
		if(points.size()>0){
			IbChartPoint point=points.get(points.size()-1);
			if(time>point.getTime())return false;
		}
		
		
		for(int i=points.size()-1;i>=0;i--){
			IbChartPoint point=points.get(i);
			if(time==point.getTime())return true;
		}
		
		return true;
	}
	
	public void addPoint(long time,double value){
		points.add(new IbChartPoint(time, value));
	}
	
	public boolean isEmpty(){
		return points.isEmpty();
	}
	
	
 	public List<IbChartPoint> getPoints() {
		return points;
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
	
	/*
	public void fireActivationChanged(){
		this.getIndicator().getGroup().getRoot().fireSerieActivationChanged(this);
	}
	*/

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
	
	/*
	public void fireColorChanged(){
		this.getIndicator().getGroup().getRoot().fireSerieColorChanged(this);
	}
	*/
	
	

}
