package com.munch.exchange.model.core.ib.chart;


public class IbChartPoint implements Comparable<IbChartPoint>{
	
	
	private long time;
	private double value;
	
	public IbChartPoint(long time, double value) {
		super();
		this.time = time;
		this.value = value;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (time ^ (time >>> 32));
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
		IbChartPoint other = (IbChartPoint) obj;
		if (time != other.time)
			return false;
		return true;
	}

	@Override
	public int compareTo(IbChartPoint o) {
		if( time < o.time )   
			 return -1;        
		 if( time >  o.time)  
			 return 1; 
		return 0;
	}
	
	
	
	

}
