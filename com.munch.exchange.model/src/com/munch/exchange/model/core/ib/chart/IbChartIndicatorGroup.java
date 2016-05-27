package com.munch.exchange.model.core.ib.chart;

import java.io.Serializable;
import java.util.LinkedList;
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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.ComparableAttributes;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarType;

@Entity
public class IbChartIndicatorGroup implements Serializable, Copyable<IbChartIndicatorGroup>,
ComparableAttributes<IbChartIndicatorGroup>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6625769451330452530L;
	public static final String ROOT="ROOT";
	
	//@Transient
	//protected List<IbChartGroupChangeListener> listeners = new LinkedList<IbChartGroupChangeListener>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;
	
	@Transient
	private boolean isDirty=false;
	
	@Transient
	private BarSize barSize;
	
	@Transient
	private BarType barType = BarType.TIME;
	
	@Transient
	private double range = 0;
	
	@OneToMany(mappedBy="group",cascade=CascadeType.ALL)
	private List<IbChartIndicator> indicators;
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<IbChartIndicatorGroup> children;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PARENT_ID")
	private IbChartIndicatorGroup parent;
	
	@OneToOne
	@JoinColumn(name="CONTAINER_ID")
	private BarContainer container;
	
	public IbChartIndicatorGroup(){
		
	}
	
	public IbChartIndicatorGroup(IbChartIndicatorGroup parent,String name){
		this.parent=parent;
		this.name=name;
		
		if(this.parent!=null)
			this.parent.getChildren().add(this);
	}
	
	
	public IbChartIndicatorGroup copy(){
		IbChartIndicatorGroup group=new IbChartIndicatorGroup();
		group.id=this.id;
		group.name=this.name;
		group.isDirty=this.isDirty;
		group.container=this.container;
		
		group.children=new LinkedList<IbChartIndicatorGroup>();
		for(IbChartIndicatorGroup child:this.children){
			IbChartIndicatorGroup copyChild=child.copy();
			copyChild.setParent(group);
			group.children.add(copyChild);
		}
		
		group.indicators=new LinkedList<IbChartIndicator>();
		for(IbChartIndicator indicator:this.indicators){
			//System.out.println("Copy Indicator: "+indicator.getName());
			IbChartIndicator copyInd=indicator.copy();
			copyInd.setGroup(group);
			//group.indicators.add(copyInd);
		}
			
		return group;
	}
	
	public boolean containsIndicator(IbChartIndicator indicator){
		for(IbChartIndicatorGroup child:children){
			if(child.containsIndicator(indicator))return true;
		}
		
		for(IbChartIndicator ind:indicators){
			if(ind.getId()==indicator.getId())return true;
		}
		return false;
	}
	
	public boolean containsSerie(IbChartSerie serie){
		for(IbChartIndicatorGroup child:children){
			if(child.containsSerie(serie))return true;
		}
		
		for(IbChartIndicator ind:indicators){
			if(ind.containsSerie(serie))return true;
		}
		return false;
	}
	
	public IbChartIndicatorGroup getRoot(){
		IbChartIndicatorGroup g=this;
		while(!g.getName().equals(ROOT)){
			g=g.getParent();
		}
		return g;
		
	}
	
	public IbChartIndicator searchIndicator(String name){
		for(IbChartIndicator ind:indicators){
			if(ind.getName().equals(name))return ind;
		}
		return null;
	}
	
	public IbChartIndicator searchIndicator(int id){
		for(IbChartIndicatorGroup child:this.getChildren()){
			IbChartIndicator indicator=child.searchIndicator(id);
			if(indicator!=null)return indicator;
		}
		
		for(IbChartIndicator ind:indicators){
			if(ind.getId()==id)return ind;
		}
		return null;
	}
	
	public List<IbChartIndicator> getAllIndicators(){
		List<IbChartIndicator> allIndicators = new LinkedList<IbChartIndicator>();
		for(IbChartIndicatorGroup child:this.getChildren()){
			allIndicators.addAll(child.getAllIndicators());
		}
		allIndicators.addAll(indicators);
		return allIndicators;
	}
	
	public IbChartIndicatorGroup searchChild(String name){
		for(IbChartIndicatorGroup child:children){
			if(child.getName().equals(name))return child;
		}
		return null;
	}
	
	
	@Override
	public boolean identical(IbChartIndicatorGroup other) {
		if (children == null) {
			if (other.children != null)
				return false;
		}
		else if (children.size()!=other.children.size())
			return false;
		else{
			for(IbChartIndicatorGroup child:children){
				IbChartIndicatorGroup c_child=other.searchChild(child.getName());
				if(c_child==null)return false;
				if(!child.identical(c_child))return false;
			}
		}
		if (id != other.id)
			return false;
		if (indicators == null) {
			if (other.indicators != null)
				return false;
		}
		else if (indicators.size()!=other.indicators.size())
			return false;
		else{
			for(IbChartIndicator ind:indicators){
				IbChartIndicator c_ind=other.searchIndicator(ind.getName());
				//System.out.println("Test the indicator: "+ind.getName());
				if(c_ind==null){
					//System.out.println("Indicator no found! "+ind.getName());
					return false;
				}
				if(!ind.identical(c_ind)){
					//System.out.println("Indicator no equals! "+ind.getName());
					return false;
				}
			}
		}
		//if (isDirty != other.isDirty)
		//	return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IbChartIndicatorGroup other = (IbChartIndicatorGroup) obj;
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

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		if(this.isDirty==true){
			if(parent!=null)
				parent.setDirty(true);
		}
		/*
		else if(children!=null){
			for(IbChartIndicatorGroup child:children)
				child.setDirty(isDirty);
		}
		*/
	}

	public List<IbChartIndicator> getIndicators() {
		if(indicators==null)
			indicators=new LinkedList<IbChartIndicator>();
		return indicators;
	}

	public void setIndicators(List<IbChartIndicator> indicators) {
		this.indicators = indicators;
	}

	public List<IbChartIndicatorGroup> getChildren() {
		if(children==null)
			children=new LinkedList<IbChartIndicatorGroup>();
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


	public BarContainer getContainer() {
		return container;
	}

	public void setContainer(BarContainer container) {
		this.container = container;
	}

	public BarSize getBarSize() {
		return barSize;
	}

	public void setBarSize(BarSize barSize) {
		this.barSize = barSize;
	}

	public BarType getBarType() {
		return barType;
	}

	public void setBarType(BarType barType) {
		this.barType = barType;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}
	
	
	
	
	//Listener
	/*
	public void fireIndicatorActivationChanged(IbChartIndicator indicator){
		for(IbChartGroupChangeListener l:this.listeners){
			l.indicatorActivationChanged(indicator);
		}
	}
	
	public void fireIndicatorParameterChanged(IbChartIndicator indicator){
		for(IbChartGroupChangeListener l:this.listeners){
			l.indicatorParameterChanged(indicator);
		}
	}
	
	public void fireSerieActivationChanged(IbChartSerie serie){
		for(IbChartGroupChangeListener l:this.listeners){
			l.serieActivationChanged(serie);
		}
	}
	
	public void fireSerieColorChanged(IbChartSerie serie){
		for(IbChartGroupChangeListener l:this.listeners){
			l.serieColorChanged(serie);
		}
	}
	
	public void addListener(IbChartGroupChangeListener l) {
		if(listeners==null)
			listeners = new LinkedList<IbChartGroupChangeListener>();
		listeners.add(l);
	}

	public void removeListener(IbChartGroupChangeListener l) {
		if(listeners==null)
			listeners = new LinkedList<IbChartGroupChangeListener>();
		listeners.remove(l);
	}
	
	public void removeAllListeners() {
		if(listeners==null)
			listeners = new LinkedList<IbChartGroupChangeListener>();
		listeners.clear();
	}
*/
	
	
	
	
}
