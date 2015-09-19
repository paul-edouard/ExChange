package com.munch.exchange.model.core.ib;

import com.ib.controller.Types.Right;
import com.ib.controller.Types.SecIdType;
import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-19T21:24:10.397+0200")
@StaticMetamodel(IbContract.class)
public class IbContract_ {
	public static volatile SingularAttribute<IbContract, Integer> id;
	public static volatile ListAttribute<IbContract, IbBarContainer> bars;
	public static volatile SingularAttribute<IbContract, Integer> conId;
	public static volatile SingularAttribute<IbContract, String> symbol;
	public static volatile SingularAttribute<IbContract, SecType> secType;
	public static volatile SingularAttribute<IbContract, String> expiry;
	public static volatile SingularAttribute<IbContract, Double> strike;
	public static volatile SingularAttribute<IbContract, Right> m_right;
	public static volatile SingularAttribute<IbContract, String> multiplier;
	public static volatile SingularAttribute<IbContract, String> exchange;
	public static volatile SingularAttribute<IbContract, String> currency;
	public static volatile SingularAttribute<IbContract, String> localSymbol;
	public static volatile SingularAttribute<IbContract, String> tradingClass;
	public static volatile SingularAttribute<IbContract, String> primaryExch;
	public static volatile SingularAttribute<IbContract, SecIdType> secIdType;
	public static volatile SingularAttribute<IbContract, String> secId;
	public static volatile SingularAttribute<IbContract, String> marketName;
	public static volatile SingularAttribute<IbContract, Double> minTick;
	public static volatile SingularAttribute<IbContract, Integer> priceMagnifier;
	public static volatile SingularAttribute<IbContract, String> orderTypes;
	public static volatile SingularAttribute<IbContract, String> validExchanges;
	public static volatile SingularAttribute<IbContract, Integer> underConid;
	public static volatile SingularAttribute<IbContract, String> longName;
	public static volatile SingularAttribute<IbContract, String> contractMonth;
	public static volatile SingularAttribute<IbContract, String> industry;
	public static volatile SingularAttribute<IbContract, String> category;
	public static volatile SingularAttribute<IbContract, String> subcategory;
	public static volatile SingularAttribute<IbContract, String> timeZoneId;
	public static volatile SingularAttribute<IbContract, String> tradingHours;
	public static volatile SingularAttribute<IbContract, String> liquidHours;
	public static volatile SingularAttribute<IbContract, String> evRule;
	public static volatile SingularAttribute<IbContract, Double> evMultiplier;
	public static volatile ListAttribute<IbContract, IbTagValue> secIdList;
	public static volatile SingularAttribute<IbContract, String> cusip;
	public static volatile SingularAttribute<IbContract, String> ratings;
	public static volatile SingularAttribute<IbContract, String> descAppend;
	public static volatile SingularAttribute<IbContract, String> bondType;
	public static volatile SingularAttribute<IbContract, String> couponType;
	public static volatile SingularAttribute<IbContract, Boolean> callable;
	public static volatile SingularAttribute<IbContract, Boolean> putable;
	public static volatile SingularAttribute<IbContract, Double> coupon;
	public static volatile SingularAttribute<IbContract, Boolean> convertible;
	public static volatile SingularAttribute<IbContract, String> maturity;
	public static volatile SingularAttribute<IbContract, String> issueDate;
	public static volatile SingularAttribute<IbContract, String> nextOptionDate;
	public static volatile SingularAttribute<IbContract, String> nextOptionType;
	public static volatile SingularAttribute<IbContract, Boolean> nextOptionPartial;
	public static volatile SingularAttribute<IbContract, String> notes;
}
