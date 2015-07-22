package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
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
import com.munch.exchange.model.core.ib.ExContract;

@Entity
@Inheritance
@DiscriminatorColumn(name="BAR_TYPE")
public abstract  class ExBar implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private WhatToShow type;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	private ExBar parent;
	
	@Enumerated(EnumType.STRING)
	private BarSize size;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROOT_ID")
	private ExContractBars root;
	
	@OneToMany(mappedBy="parent")
	private List<ExBar> childBars;
	
	
	private  long time;
	private  double high;
	private  double low;
	private  double open;
	private  double close;
	private  double wap;
	private  long volume;
	private  int count;
	
	public ExBar(){
		
	}
	
	public ExBar( long time, double high, double low, double open, double close, double wap, long volume, int count) {
		this.time = time;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.wap = wap;
		this.volume = volume;
		this.count = count;
	}
	
	public ExBar(Bar bar){
		this.time = bar.time();
		this.high = bar.high();
		this.low = bar.low();
		this.open = bar.open();
		this.close = bar.close();
		this.wap = bar.wap();
		this.volume = bar.volume();
		this.count = bar.count();
	}
	
	

	public List<ExBar> getChildBars() {
		return childBars;
	}

	public void setChildBars(List<ExBar> childBars) {
		this.childBars = childBars;
	}

	public ExContractBars getRoot() {
		return root;
	}

	public void setRoot(ExContractBars root) {
		this.root = root;
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}

	public WhatToShow getType() {	return type;}

	public void setType(WhatToShow type) {this.type = type;}

	public int getId() {return id;}

	public void setId(int id) {this.id = id;}

	public ExBar getParent() {return parent;}

	public void setParent(ExBar parent) {this.parent = parent;}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
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
	}
	
	/** Format for query. */
	public static String format( long ms) {
		return FORMAT.format( new Date( ms) );
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
		ExBar other = (ExBar) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExBar [id=" + id + ", whatToShow=" + type + ", time="
				+ time + ", high=" + high + ", low=" + low + ", open=" + open
				+ ", close=" + close + ", wap=" + wap + ", volume=" + volume
				+ ", count=" + count + "]";
	}
	
	
	

}
