package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.ib.controller.Bar;
import com.ib.controller.Formats;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;

@Entity
@Inheritance
@DiscriminatorColumn(name="BAR_TYPE")
public abstract  class IbBar implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Enumerated(EnumType.STRING)
	private WhatToShow type;
	
	/*
	@EmbeddedId
	private ExBarPK pk;
	*/
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	private IbBar parent;
	
	@Enumerated(EnumType.STRING)
	private BarSize size;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROOT_ID")
	private IbBarContainer root;
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<IbBar> childBars;
	
	
	private  long time;
	private  double high;
	private  double low;
	private  double open;
	private  double close;
	private  double wap;
	private  long volume;
	private  int count;
	
	public IbBar(){
		
	}
	
	public IbBar( long time, double high, double low, double open, double close, double wap, long volume, int count) {
		this.setTime( time  );
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.wap = wap;
		this.volume = volume;
		this.count = count;
	}
	
	public IbBar(Bar bar){
		this.init(bar);
	}
	
	public void init(Bar bar){
		this.setTime( bar.time());
		this.high = bar.high();
		this.low = bar.low();
		this.open = bar.open();
		this.close = bar.close();
		this.wap = bar.wap();
		this.volume = bar.volume();
		this.count = bar.count();
	}
	
	public void copyData(IbBar bar){
		this.setTime( bar.time);
		this.high = bar.high;
		this.low = bar.low;
		this.open = bar.open;
		this.close = bar.close;
		this.wap = bar.wap;
		this.volume = bar.volume;
		this.count = bar.count;
		
		this.size=bar.size;
		this.type=bar.type;
		this.id=bar.id;
	}
	
	
	public void setRootAndParent(IbBarContainer root,IbBar parent){
		setParent(parent);
		setRoot(root);
		setType(root.getType());
		
		//parent.getChildBars().add(this);
		//root.getAllBars().add(this);
		
	}
	
	public abstract long getIntervall();
	
	
	/*
	public ExBarPK getPk() {
		return pk;
	}

	public void setPk(ExBarPK pk) {
		this.pk = pk;
	}
	*/

	public List<IbBar> getChildBars() {
		if(childBars==null || childBars.isEmpty()){
			childBars=new LinkedList<IbBar>();
		}
		return childBars;
	}

	public void setChildBars(List<IbBar> childBars) {
		this.childBars = childBars;
	}

	public IbBarContainer getRoot() {
		return root;
	}

	public void setRoot(IbBarContainer root) {
		this.root = root;
		setType(root.getType());
		
		//root.getAllBars().add(this);
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}

	public WhatToShow getType() {
		return type;
		//return pk.getType();
	}

	public void setType(WhatToShow type) {
		this.type = type;
		//this.pk.setType(type);
	}
	
	
	public long getId() {return id;}

	public void setId(long id) {this.id = id;}
	
	public IbBar getParent() {return parent;}

	public void setParent(IbBar parent) {this.parent = parent;}

	public long getTime() {
		return this.time;
		//return this.pk.getTime();
	}
	
	public long getTimeInMs() {
		return this.time*1000;
		//return this.pk.getTime();
	}

	public void setTime(long time) {
		this.time = time;
		//this.pk.setTime(time);
	}
	
	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}
	
	public void setLow(double low) {
		this.low = low;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getWap() {
		return wap;
	}

	public void setWap(double wap) {
		this.wap = wap;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String formattedTime() {
		return Formats.fmtDate( this.time * 1000);
		//return Formats.fmtDate( this.pk.getTime() * 1000);
	}
	
	/** Format for query. */
	public static String format( long ms) {
		return FORMAT.format( new Date( ms) );
	}

	@Override
	public String toString() {
		return "ExBar [id=" + id + ", type=" + type + ", parent=" + parent
				+ ", size=" + size + ", root=" + root + ", time=" + time +", formated time="+this.formattedTime()+ ", high=" + high + ", low="
				+ low + ", open=" + open + ", close=" + close + ", wap=" + wap
				+ ", volume=" + volume + ", count=" + count + "]";
	}
	
	

	
	
	

}
