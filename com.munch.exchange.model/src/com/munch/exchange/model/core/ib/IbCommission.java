package com.munch.exchange.model.core.ib;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Entity
public class IbCommission implements Serializable, Copyable<IbCommission>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -671846356367038814L;
	
	public static enum CommissionCategory {
		None, StocksETFsWarrants, Options, FuturesAndFOPs, US_SSFs_EFPs, Forex, Metals, Bonds, CFDs, MutualFunds;
	}
	
	public static enum CommissionType {
		None, Fixed, Tiered;
	}
	
	public static enum Currency {
		None, USD, EUR, CAD, JPY, NOK, SEK;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private CommissionCategory commissionCategory=CommissionCategory.None;
	
	@Enumerated(EnumType.STRING)
	private CommissionType commissionType=CommissionType.None;
	
	@Enumerated(EnumType.STRING)
	private Currency currency=Currency.None;
	
	
	private double fixed=-1;
	private boolean fixed_isPercentOfTradeValue=false;
	
	private double minPerOrder=-1;
	private boolean minPerOrder_isPercentOfTradeValue=false;
	
	private double maxPerOrder=-1;
	private boolean maxPerOrder_isPercentOfTradeValue=false;
	
	private double monthlyTradeAmount=-1;
	private double commissions=-1;
	
	private double contractVolume=-1;
	
	@OneToOne
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	public IbCommission(IbContract contract) {
		super();
		this.contract = contract;
		this.contract.setCommission(this);
	}


	/**
	 * Constructor
	 */
	public IbCommission() {
		super();
	}
	
	@Override
	public IbCommission copy() {
		IbCommission c=new IbCommission();
		
		c.id=id;
		
		c.commissionCategory=commissionCategory;
		c.commissionType=commissionType;
		c.currency=currency;
		
		c.fixed=fixed;
		c.fixed_isPercentOfTradeValue=fixed_isPercentOfTradeValue;
		c.minPerOrder=minPerOrder;
		c.minPerOrder_isPercentOfTradeValue=minPerOrder_isPercentOfTradeValue;
		c.maxPerOrder=maxPerOrder;
		c.maxPerOrder_isPercentOfTradeValue=maxPerOrder_isPercentOfTradeValue;
		
		c.monthlyTradeAmount=monthlyTradeAmount;
		c.commissions=commissions;
		c.contractVolume=contractVolume;
		
		c.contract=contract;
		
		return c;
	}
	
	
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CommissionCategory getCommissionCategory() {
		return commissionCategory;
	}

	public void setCommissionCategory(CommissionCategory commissionCategory) {
		this.commissionCategory = commissionCategory;
	}

	public CommissionType getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(CommissionType commissionType) {
		this.commissionType = commissionType;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public double getFixed() {
		return fixed;
	}

	public void setFixed(double fixed) {
		this.fixed = fixed;
	}

	public boolean isFixedPercentOfTradeValue() {
		return fixed_isPercentOfTradeValue;
	}

	public void setFixedPercentOfTradeValue(boolean fixed_isPercentOfTradeValue) {
		this.fixed_isPercentOfTradeValue = fixed_isPercentOfTradeValue;
	}

	public double getMinPerOrder() {
		return minPerOrder;
	}

	public void setMinPerOrder(double minPerOrder) {
		this.minPerOrder = minPerOrder;
	}

	public boolean isMinPerOrderPercentOfTradeValue() {
		return minPerOrder_isPercentOfTradeValue;
	}

	public void setMinPerOrderPercentOfTradeValue(
			boolean minPerOrder_isPercentOfTradeValue) {
		this.minPerOrder_isPercentOfTradeValue = minPerOrder_isPercentOfTradeValue;
	}

	public double getMaxPerOrder() {
		return maxPerOrder;
	}

	public void setMaxPerOrder(double maxPerOrder) {
		this.maxPerOrder = maxPerOrder;
	}

	public boolean isMaxPerOrderPercentOfTradeValue() {
		return maxPerOrder_isPercentOfTradeValue;
	}

	public void setMaxPerOrderPercentOfTradeValue(
			boolean maxPerOrder_isPercentOfTradeValue) {
		this.maxPerOrder_isPercentOfTradeValue = maxPerOrder_isPercentOfTradeValue;
	}

	public double getMonthlyTradeAmount() {
		return monthlyTradeAmount;
	}

	public void setMonthlyTradeAmount(double monthlyTradeAmount) {
		this.monthlyTradeAmount = monthlyTradeAmount;
	}

	public double getCommissions() {
		return commissions;
	}

	public void setCommissions(double commissions) {
		this.commissions = commissions;
	}

	public double getContractVolume() {
		return contractVolume;
	}

	public void setContractVolume(double contractVolume) {
		this.contractVolume = contractVolume;
	}

	public IbContract getContract() {
		return contract;
	}

	public void setContract(IbContract contract) {
		this.contract = contract;
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
		IbCommission other = (IbCommission) obj;
		if (id != other.id)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "IbCommission [id=" + id + ", commissionCategory="
				+ commissionCategory + ", commissionType=" + commissionType
				+ ", currency=" + currency + ", fixed=" + fixed
				+ ", fixed_isPercentOfTradeValue="
				+ fixed_isPercentOfTradeValue + ", minPerOrder=" + minPerOrder
				+ ", minPerOrder_isPercentOfTradeValue="
				+ minPerOrder_isPercentOfTradeValue + ", maxPerOrder="
				+ maxPerOrder + ", maxPerOrder_isPercentOfTradeValue="
				+ maxPerOrder_isPercentOfTradeValue + ", monthlyTradeAmount="
				+ monthlyTradeAmount + ", commissions=" + commissions
				+ ", contractVolume=" + contractVolume + "]";
	}


	
	
	
	
	
	

}
