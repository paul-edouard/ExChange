package com.munch.exchange.model.core.ib;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.Length;

import com.ib.client.TagValue;
import com.ib.controller.NewContract;
import com.ib.controller.NewContractDetails;
import com.ib.controller.Types.Right;
import com.ib.controller.Types.SecIdType;
import com.ib.controller.Types.SecType;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;


@Entity
@NamedQuery(name="IbContract.getAll",query="SELECT s FROM IbContract s")
public class IbContract implements Serializable,Copyable<IbContract>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6597196075952688514L;
	
	public static enum TradingPeriod {
		DAILY, WEEKLY, NONE;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToMany(mappedBy="contract",cascade=CascadeType.ALL)
	private List<IbBarContainer> bars;
	
	@OneToOne(mappedBy="contract",cascade=CascadeType.ALL)
	private IbCommission commission;
	
	/*
	@OneToOne(mappedBy="contract",cascade=CascadeType.ALL)
	private NeuralIndicatorInput neuralIndicatorInput;
	*/
	
	@OneToMany(mappedBy="contract",cascade=CascadeType.ALL)
	private List<NeuralConfiguration> neuralConfigurations=new LinkedList<NeuralConfiguration>();
	
	private long startTradeTimeInMs=0;
	private long endTradeTimeInMs=0;
	
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
	
	///////////////////////////////
	//From NewContract Details
	//////////////////////////////
	private String marketName;
	private double minTick;
	private int priceMagnifier;
	
	@Length(max=1000)
	private String orderTypes;
	private String validExchanges;
	private int underConid;
	private String longName;
	private String contractMonth;
	private String industry;
	private String category;
	private String subcategory;
	private String timeZoneId;
	private String tradingHours;
	private String liquidHours;
	private String evRule;
	private double evMultiplier;
	
	@OneToMany(mappedBy="owner")
	private List<IbTagValue> secIdList;
	
	// BOND values
	private String cusip;
	private String ratings;
	private String descAppend;
	private String bondType;
	private String couponType;
	private boolean callable = false;
	private boolean putable = false;
	private double coupon = 0;
	private boolean convertible = false;
	private String maturity;
	private String issueDate;
	private String nextOptionDate;
	private String nextOptionType;
	private boolean nextOptionPartial = false;
	private String notes;
	
	
	public IbContract(){}
	
	public IbContract(NewContractDetails newContractDetails){
		
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
		secIdType=newContractDetails.contract().secIdType();
		secId=newContractDetails.contract().secId();
		
		marketName=newContractDetails.marketName();
		minTick=newContractDetails.minTick();
		priceMagnifier=newContractDetails.PripeMagnifier();
		orderTypes=newContractDetails.orderTypes();
		validExchanges=newContractDetails.validExchanges();
		underConid=newContractDetails.underConid();
		longName=newContractDetails.longName();
		contractMonth=newContractDetails.contractMonth();
		industry=newContractDetails.industry();
		category=newContractDetails.category();
		subcategory=newContractDetails.subcategory();
		timeZoneId=newContractDetails.timeZoneId();
		tradingHours=newContractDetails.tradingHours();
		liquidHours=newContractDetails.liquidHours();
		evRule=newContractDetails.evRule();
		evMultiplier=newContractDetails.evMultiplier();
		
		secIdList=new LinkedList<IbTagValue>();
		if(newContractDetails.secIdList()!=null){
		for (TagValue tagValue : newContractDetails.secIdList()) {
			secIdList.add(new IbTagValue(tagValue));
		}
		}
		
		// BOND values
		cusip=newContractDetails.cusip();
		ratings = newContractDetails.ratings();
		descAppend = newContractDetails.descAppend();
		bondType = newContractDetails.bondType();
		couponType = newContractDetails.couponType();
		callable = newContractDetails.callable();
		putable = newContractDetails.putable();
		coupon = newContractDetails.coupon();
		convertible = newContractDetails.convertible();
		maturity =newContractDetails.maturity();
		issueDate =newContractDetails.issueDate();
		nextOptionDate = newContractDetails.nextOptionDate();
		nextOptionType = newContractDetails.nextOptionType();
		nextOptionPartial =newContractDetails.nextOptionPartial();
		notes=newContractDetails.notes();
		
		
	}
	
	
	public boolean compareWith(IbContract contract){
		if(conId!=contract.getConId())return false;
		if(!symbol.equals(contract.getSymbol()))return false;
		if(!exchange.equals(contract.getExchange()))return false;
		if(!currency.equals(contract.getCurrency()))return false;
		if(!localSymbol.equals(contract.getLocalSymbol()))return false;
		if(secType!=contract.getSecType())return false;
		
		return true;
	}
	
	public NewContract getNewContract(){
		NewContract n_contract=new NewContract();
		
		n_contract.conid(this.conId);
		n_contract.symbol(this.symbol);
		//if(this.secType!=SecType.STK)
		n_contract.secType(this.secType);
		n_contract.expiry(this.expiry);
		n_contract.strike(this.strike);
		n_contract.right(this.m_right);
		n_contract.multiplier(this.multiplier);
		n_contract.exchange(this.exchange);
		n_contract.currency(this.currency);
		n_contract.localSymbol(this.localSymbol);
		//if(this.secType!=SecType.STK)
		n_contract.tradingClass(this.tradingClass);
		n_contract.primaryExch(this.primaryExch);
		n_contract.secIdType(this.secIdType);
		n_contract.secId(this.secId);
		//Missing Delta, ComboLegs
		return n_contract;
		
	}
	
	
	@Override
	public IbContract copy() {
		IbContract c=new IbContract();
		
		c.id=this.id;
		
		c.startTradeTimeInMs=this.startTradeTimeInMs;
		c.endTradeTimeInMs=this.endTradeTimeInMs;
		
		
		
		c.conId=this.conId;
		c.symbol=this.symbol;
		
		c.secType=this.secType;
		c.expiry=this.expiry;
		c.strike=this.strike;
		
		c.m_right=this.m_right;
		c.multiplier=this.multiplier;
		c.exchange=this.exchange;
		
		c.currency=this.currency;
		c.localSymbol=this.localSymbol;
		c.tradingClass=this.tradingClass;
		c.primaryExch=this.primaryExch;
		
		c.secIdType=this.secIdType;
		c.secId=this.secId;
		
		///////////////////////////////
		//From NewContract Details
		//////////////////////////////
		
		c.marketName=this.marketName;
		c.minTick=this.minTick;
		c.priceMagnifier=this.priceMagnifier;
		
		c.orderTypes=this.orderTypes;
		c.validExchanges=this.validExchanges;
		c.underConid=this.underConid;
		c.longName=this.longName;
		c.contractMonth=this.contractMonth;
		c.industry=this.industry;
		c.category=this.category;
		c.subcategory=this.subcategory;
		c.timeZoneId=this.timeZoneId;
		c.tradingHours=this.tradingHours;
		c.liquidHours=this.liquidHours;
		c.evRule=this.evRule;
		c.evMultiplier=this.evMultiplier;
		
		c.secIdList=new LinkedList<IbTagValue>();
		for(IbTagValue ibTagValue:this.secIdList){
			IbTagValue v_c=ibTagValue.copy();
			v_c.setOwner(c);
			c.secIdList.add(v_c);
		}
		
		c.cusip=this.cusip;
		c.ratings=this.ratings;
		c.descAppend=this.descAppend;
		c.bondType=this.bondType;
		c.couponType=this.couponType;
		c.callable=this.callable;
		c.putable=this.putable;
		c.coupon=this.coupon;
		c.convertible=this.convertible;
		c.maturity=this.maturity;
		c.issueDate=this.issueDate;
		c.nextOptionDate=this.nextOptionDate;
		c.nextOptionType=this.nextOptionType;
		c.nextOptionPartial=this.nextOptionPartial;
		c.notes=this.notes;
		
		return c;
	}
	
	
	public boolean allowShortPosition(){
		switch (secType) {
			case STK:return false;
			case CASH:return true;
			case IND:return false;
		}
		
		return true;
	}
	
	public TradingPeriod getTraidingPeriod(){
		switch (secType) {
			case STK:return TradingPeriod.DAILY;
			case CASH:return TradingPeriod.WEEKLY;
			case IND:return TradingPeriod.NONE;
		}
		
		return TradingPeriod.NONE;
	}
	
	public long[] getRelativeTraidingPeriod(long dateInMs){
		long[] startEndTime = new long[2];
		
		if(this.getTraidingPeriod()==TradingPeriod.WEEKLY){
			Calendar startDate=Calendar.getInstance();
			startDate.setTimeInMillis(dateInMs);
			
			startDate.set(Calendar.MILLISECOND, 0);
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			
			if(startDate.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				startDate.add(Calendar.DAY_OF_WEEK, +1);
			}
			
			while(startDate.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){
				startDate.add(Calendar.DAY_OF_WEEK, -1);
			}
			startEndTime[0]=startDate.getTimeInMillis();
			
			startDate.add(Calendar.DAY_OF_WEEK, +5);
			startEndTime[1]=startDate.getTimeInMillis();
		}
		else if(this.getTraidingPeriod()==TradingPeriod.DAILY){
			Calendar date=Calendar.getInstance();
			date.setTimeInMillis(dateInMs);
			date.set(Calendar.MILLISECOND, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.HOUR_OF_DAY, 0);
			
			long dayStart=date.getTimeInMillis();
			
			date.add(Calendar.MILLISECOND,(int) startTradeTimeInMs);
			startEndTime[0]=date.getTimeInMillis();
			
			date.setTimeInMillis(dayStart);
			date.add(Calendar.MILLISECOND,(int) endTradeTimeInMs);
			startEndTime[1]=date.getTimeInMillis();
			
		}
		
		return startEndTime;
	}
	
	
	
	
 	public int getId() {
		return id;
	}
	public void setId(int id) {
	this.id = id;}
	
	public List<IbBarContainer> getBars() {
		return bars;
	}
	public void setBars(List<IbBarContainer> bars) {
		this.bars = bars;
	}

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
	

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public double getMinTick() {
		return minTick;
	}

	public void setMinTick(double minTick) {
		this.minTick = minTick;
	}

	public int getPriceMagnifier() {
		return priceMagnifier;
	}

	public void setPriceMagnifier(int priceMagnifier) {
		this.priceMagnifier = priceMagnifier;
	}

	public String getOrderTypes() {
		return orderTypes;
	}

	public void setOrderTypes(String orderTypes) {
		this.orderTypes = orderTypes;
	}

	public String getValidExchanges() {
		return validExchanges;
	}

	public void setValidExchanges(String validExchanges) {
		this.validExchanges = validExchanges;
	}

	public int getUnderConid() {
		return underConid;
	}

	public void setUnderConid(int underConid) {
		this.underConid = underConid;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getContractMonth() {
		return contractMonth;
	}

	public void setContractMonth(String contractMonth) {
		this.contractMonth = contractMonth;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getTradingHours() {
		return tradingHours;
	}

	public void setTradingHours(String tradingHours) {
		this.tradingHours = tradingHours;
	}

	public String getLiquidHours() {
		return liquidHours;
	}

	public void setLiquidHours(String liquidHours) {
		this.liquidHours = liquidHours;
	}

	public String getEvRule() {
		return evRule;
	}

	public void setEvRule(String evRule) {
		this.evRule = evRule;
	}

	public double getEvMultiplier() {
		return evMultiplier;
	}

	public void setEvMultiplier(double evMultiplier) {
		this.evMultiplier = evMultiplier;
	}

	public List<IbTagValue> getSecIdList() {
		return secIdList;
	}

	public void setSecIdList(Vector<IbTagValue> secIdList) {
		this.secIdList = secIdList;
	}

	public String getCusip() {
		return cusip;
	}

	public void setCusip(String cusip) {
		this.cusip = cusip;
	}

	public String getRatings() {
		return ratings;
	}

	public void setRatings(String ratings) {
		this.ratings = ratings;
	}

	public String getDescAppend() {
		return descAppend;
	}

	public void setDescAppend(String descAppend) {
		this.descAppend = descAppend;
	}

	public String getBondType() {
		return bondType;
	}

	public void setBondType(String bondType) {
		this.bondType = bondType;
	}

	public String getCouponType() {
		return couponType;
	}

	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

	public boolean isCallable() {
		return callable;
	}

	public void setCallable(boolean callable) {
		this.callable = callable;
	}

	public boolean isPutable() {
		return putable;
	}

	public void setPutable(boolean putable) {
		this.putable = putable;
	}

	public double getCoupon() {
		return coupon;
	}

	public void setCoupon(double coupon) {
		this.coupon = coupon;
	}

	public boolean isConvertible() {
		return convertible;
	}

	public void setConvertible(boolean convertible) {
		this.convertible = convertible;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getNextOptionDate() {
		return nextOptionDate;
	}

	public void setNextOptionDate(String nextOptionDate) {
		this.nextOptionDate = nextOptionDate;
	}

	public String getNextOptionType() {
		return nextOptionType;
	}

	public void setNextOptionType(String nextOptionType) {
		this.nextOptionType = nextOptionType;
	}

	public boolean isNextOptionPartial() {
		return nextOptionPartial;
	}

	public void setNextOptionPartial(boolean nextOptionPartial) {
		this.nextOptionPartial = nextOptionPartial;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	

	public List<NeuralConfiguration> getNeuralConfigurations() {
		return neuralConfigurations;
	}
	
	public void setNeuralConfigurations(
			List<NeuralConfiguration> neuralConfigurations) {
		this.neuralConfigurations = neuralConfigurations;
	}

	public IbCommission getCommission() {
		if(commission==null)
			commission=new IbCommission(this);
		return commission;
	}

	public void setCommission(IbCommission commission) {
		this.commission = commission;
	}
	
	
	

	public long getStartTradeTimeInMs() {
		return startTradeTimeInMs;
	}

	public void setStartTradeTimeInMs(long startTradeTime) {
		this.startTradeTimeInMs = startTradeTime;
	}

	public long getEndTradeTimeInMs() {
		return endTradeTimeInMs;
	}

	public void setEndTradeTimeInMs(long endTradeTime) {
		this.endTradeTimeInMs = endTradeTime;
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
		IbContract other = (IbContract) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExContract [id=" + id + ", conId=" + conId + ", symbol="
				+ symbol + ", secType=" + secType + ", expiry=" + expiry
				+ ", strike=" + strike + ", m_right=" + m_right
				+ ", multiplier=" + multiplier + ", exchange=" + exchange
				+ ", currency=" + currency + ", localSymbol=" + localSymbol
				+ ", tradingClass=" + tradingClass + ", primaryExch="
				+ primaryExch + ", secIdType=" + secIdType + ", secId=" + secId
				+ ", marketName=" + marketName + ", minTick=" + minTick
				+ ", priceMagnifier=" + priceMagnifier + ", orderTypes="
				+ orderTypes + ", validExchanges=" + validExchanges
				+ ", underConid=" + underConid + ", longName=" + longName
				+ ", contractMonth=" + contractMonth + ", industry=" + industry
				+ ", category=" + category + ", subcategory=" + subcategory
				+ ", timeZoneId=" + timeZoneId + ", tradingHours="
				+ tradingHours + ", liquidHours=" + liquidHours + ", evRule="
				+ evRule + ", evMultiplier=" + evMultiplier + /*", m_secIdList="
				+ secIdList +*/ ", cusip=" + cusip + ", ratings=" + ratings
				+ ", descAppend=" + descAppend + ", bondType=" + bondType
				+ ", couponType=" + couponType + ", callable=" + callable
				+ ", putable=" + putable + ", coupon=" + coupon
				+ ", convertible=" + convertible + ", maturity=" + maturity
				+ ", issueDate=" + issueDate + ", nextOptionDate="
				+ nextOptionDate + ", nextOptionType=" + nextOptionType
				+ ", nextOptionPartial=" + nextOptionPartial + ", notes="
				+ notes + "]";
	}
	
	
	//######################################
  	//##              STATIC              ##
  	//######################################
	public static List<IbBarContainer> getAllAvailableIbBarContainers(IbContract contract){
		List<IbBarContainer> Allbars=new LinkedList<IbBarContainer>();
		//STOCK
		if(contract.getSecType()==SecType.STK ){
			Allbars.add(new IbBarContainer(contract,WhatToShow.MIDPOINT));
			Allbars.add(new IbBarContainer(contract,WhatToShow.ASK));
			Allbars.add(new IbBarContainer(contract,WhatToShow.BID));
			Allbars.add(new IbBarContainer(contract,WhatToShow.TRADES));
			
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.HISTORICAL_VOLATILITY));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.OPTION_IMPLIED_VOLATILITY));
		}
		else if(contract.getSecType()==SecType.CASH || 
				contract.getSecType()==SecType.CMDTY){
			Allbars.add(new IbBarContainer(contract,WhatToShow.MIDPOINT));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.ASK));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.BID));
		}
		else if(contract.getSecType()==SecType.OPT ||
				contract.getSecType()==SecType.FUT){
			Allbars.add(new IbBarContainer(contract,WhatToShow.MIDPOINT));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.ASK));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.BID));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.TRADES));
		}
		//INDICE
		else if(contract.getSecType()==SecType.IND){
			Allbars.add(new IbBarContainer(contract,WhatToShow.TRADES));
			
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.HISTORICAL_VOLATILITY));
			//Allbars.add(new IbBarContainer(exContract,WhatToShow.OPTION_IMPLIED_VOLATILITY));
		}
		
		return Allbars;
		
	}

	
	
	
	
}
