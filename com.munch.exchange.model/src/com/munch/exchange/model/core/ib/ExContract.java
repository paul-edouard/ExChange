package com.munch.exchange.model.core.ib;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import com.ib.controller.NewContractDetails;
import com.ib.controller.Types.Right;
import com.ib.controller.Types.SecIdType;
import com.ib.controller.Types.SecType;


@Entity
@NamedQuery(name="ExContract.getAll",query="SELECT s FROM ExContract s")
public class ExContract implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6597196075952688514L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	private int    conId;
	private String symbol;
	
	@Enumerated(EnumType.STRING)
	private SecType secType;
	private String expiry;
	private double strike;
	
	@Enumerated(EnumType.STRING)
	private Right m_right;
	private String multiplier;
	private String exchange;

	private String currency;
	private String localSymbol;
	private String tradingClass;
	private String primaryExch;      // pick a non-aggregate (ie not the SMART exchange) exchange that the contract trades on.  DO NOT SET TO SMART.
	//private boolean includeExpired;  // can not be set to true for orders.
	
	@Enumerated(EnumType.STRING)
	private SecIdType secIdType;        // CUSIP;SEDOL;ISIN;RIC
	private String secId;
	
	public ExContract(){}
	
	public ExContract(NewContractDetails newContractDetails){
		
		conId=newContractDetails.contract().conid();
		symbol=newContractDetails.contract().symbol();
		secType=newContractDetails.contract().secType();
		expiry=newContractDetails.contract().expiry();
		m_right=newContractDetails.contract().right();
		multiplier=newContractDetails.contract().multiplier();
		exchange=newContractDetails.contract().exchange();
		currency=newContractDetails.contract().currency();
		localSymbol=newContractDetails.contract().localSymbol();
		tradingClass=newContractDetails.contract().tradingClass();
		primaryExch=newContractDetails.contract().primaryExch();
		//secIdType=newContractDetails.contract().secIdType();
		secId=newContractDetails.contract().secId();
		
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
	this.id = id;}
	
	public int getConId() {
		return conId;
	}
	public void setConId(int conId) {
	this.conId = conId;}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
	this.symbol = symbol;}
	
	
	
	public SecType getSecType() {
		return secType;
	}

	public void setSecType(SecType secType) {
		this.secType = secType;
	}
	

	public String getExpiry() {
		return expiry;
	}
	public void setExpiry(String expiry) {
	this.expiry = expiry;}
	
	public double getStrike() {
		return strike;
	}
	public void setStrike(double strike) {
	this.strike = strike;}
	

	public Right getM_right() {
		return m_right;
	}

	public void setM_right(Right m_right) {
		this.m_right = m_right;
	}

	public String getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(String multiplier) {
	this.multiplier = multiplier;}
	
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
	this.exchange = exchange;}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
	this.currency = currency;}
	
	public String getLocalSymbol() {
		return localSymbol;
	}
	public void setLocalSymbol(String localSymbol) {
	this.localSymbol = localSymbol;}
	
	public String getTradingClass() {
		return tradingClass;
	}
	public void setTradingClass(String tradingClass) {
	this.tradingClass = tradingClass;}
	
	public String getPrimaryExch() {
		return primaryExch;
	}
	public void setPrimaryExch(String primaryExch) {
	this.primaryExch = primaryExch;}
	
	
	public SecIdType getSecIdType() {
		return secIdType;
	}

	public void setSecIdType(SecIdType secIdType) {
		this.secIdType = secIdType;
	}
	

	public String getSecId() {
		return secId;
	}
	public void setSecId(String secId) {
	this.secId = secId;}
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
		ExContract other = (ExContract) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "ExContract [id=" + id + ", conId=" + conId + ", symbol="
				+ symbol + ", secType=" + secType + ", expiry=" + expiry
				+ ", strike=" + strike + ", right=" + m_right + ", multiplier="
				+ multiplier + ", exchange=" + exchange + ", currency="
				+ currency + ", localSymbol=" + localSymbol + ", tradingClass="
				+ tradingClass + ", primaryExch=" + primaryExch
				+ ", secIdType=" + secIdType + ", secId=" + secId + "]";
	}
	
	
	
	
	
	
}
