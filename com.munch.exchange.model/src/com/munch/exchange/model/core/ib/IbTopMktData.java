package com.munch.exchange.model.core.ib;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class IbTopMktData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5627050802382737686L;
	
	public static final String CONTRACT_ID="CONTRACT_ID";
	
	public static final String BID="BID";
	public static final String ASK="ASK";
	public static final String LAST="LAST";
	public static final String LAST_TIME="LAST_TIME";
	public static final String BID_SIZE="BID_SIZE";
	public static final String ASK_SIZE="ASK_SIZE";
	public static final String CLOSE="CLOSE";
	public static final String VOLUME="VOLUME";
	public static final String FROZEN="FROZEN";
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	private int contractId;
	
	private double bid;
	private double ask;
	private double last;
	private long lastTime;
	private int bidSize;
	private int askSize;
	private double close;
	private int volume;
	private boolean frozen;
	
	public IbTopMktData(IbContract contract){
		contractId=contract.getId();
	}
	
	
	public  void setValue(String field, String value){
		if(field.equals(BID)){
			this.setBid(Double.parseDouble(value));
		}
		else if(field.equals(ASK)){
			this.setAsk(Double.parseDouble(value));
		}
		else if(field.equals(LAST)){
			this.setLast(Double.parseDouble(value));
		}
		else if(field.equals(LAST_TIME)){
			this.setLastTime(Long.parseLong(value));
		}
		else if(field.equals(BID_SIZE)){
			this.setBidSize(Integer.parseInt(value));
		}
		else if(field.equals(ASK_SIZE)){
			this.setAskSize(Integer.parseInt(value));
		}
		else if(field.equals(CLOSE)){
			this.setClose(Double.parseDouble(value));
		}
		else if(field.equals(VOLUME)){
			this.setVolume(Integer.parseInt(value));
		}
		else if(field.equals(FROZEN)){
			this.setFrozen(Boolean.parseBoolean(value));
		}	
	}
	

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		changes.firePropertyChange(BID, this.bid,this.bid = bid);
	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		changes.firePropertyChange(ASK, this.ask,this.ask = ask);
	}

	public double getLast() {
		return last;
	}

	public void setLast(double last) {
		changes.firePropertyChange(LAST, this.last,this.last = last);
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		changes.firePropertyChange(LAST_TIME, this.lastTime,this.lastTime = lastTime);
	}

	public int getBidSize() {
		return bidSize;
	}

	public void setBidSize(int bidSize) {
		changes.firePropertyChange(BID_SIZE, this.bidSize,this.bidSize = bidSize);
	}

	public int getAskSize() {
		return askSize;
	}

	public void setAskSize(int askSize) {
		changes.firePropertyChange(ASK_SIZE, this.askSize,this.askSize = askSize);
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		changes.firePropertyChange(CLOSE, this.close,this.close = close);
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		changes.firePropertyChange(VOLUME, this.volume,this.volume = volume);
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		changes.firePropertyChange(FROZEN, this.frozen,this.frozen = frozen);
	}

	public int getContractId() {
		return contractId;
	}
	
	
	@Override
	public String toString() {
		return "ExTopMktData [" + "contractId="
				+ contractId + ", bid=" + bid + ", ask=" + ask + ", last="
				+ last + ", lastTime=" + lastTime + ", bidSize=" + bidSize
				+ ", askSize=" + askSize + ", close=" + close + ", volume="
				+ volume + ", frozen=" + frozen + "]";
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}
	

}
