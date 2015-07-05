package com.munch.exchange.model.core.ib;

import com.ib.controller.Types.Right;
import com.ib.controller.Types.SecIdType;
import com.ib.controller.Types.SecType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-05T13:33:49.024+0200")
@StaticMetamodel(ExContract.class)
public class ExContract_ {
	public static volatile SingularAttribute<ExContract, Integer> id;
	public static volatile SingularAttribute<ExContract, Integer> conId;
	public static volatile SingularAttribute<ExContract, String> symbol;
	public static volatile SingularAttribute<ExContract, SecType> secType;
	public static volatile SingularAttribute<ExContract, String> expiry;
	public static volatile SingularAttribute<ExContract, Double> strike;
	public static volatile SingularAttribute<ExContract, Right> m_right;
	public static volatile SingularAttribute<ExContract, String> multiplier;
	public static volatile SingularAttribute<ExContract, String> exchange;
	public static volatile SingularAttribute<ExContract, String> currency;
	public static volatile SingularAttribute<ExContract, String> localSymbol;
	public static volatile SingularAttribute<ExContract, String> tradingClass;
	public static volatile SingularAttribute<ExContract, String> primaryExch;
	public static volatile SingularAttribute<ExContract, SecIdType> secIdType;
	public static volatile SingularAttribute<ExContract, String> secId;
	public static volatile SingularAttribute<ExContract, String> marketName;
	public static volatile SingularAttribute<ExContract, Double> minTick;
	public static volatile SingularAttribute<ExContract, Integer> priceMagnifier;
	public static volatile SingularAttribute<ExContract, String> orderTypes;
	public static volatile SingularAttribute<ExContract, String> validExchanges;
	public static volatile SingularAttribute<ExContract, Integer> underConid;
	public static volatile SingularAttribute<ExContract, String> longName;
	public static volatile SingularAttribute<ExContract, String> contractMonth;
	public static volatile SingularAttribute<ExContract, String> industry;
	public static volatile SingularAttribute<ExContract, String> category;
	public static volatile SingularAttribute<ExContract, String> subcategory;
	public static volatile SingularAttribute<ExContract, String> timeZoneId;
	public static volatile SingularAttribute<ExContract, String> tradingHours;
	public static volatile SingularAttribute<ExContract, String> liquidHours;
	public static volatile SingularAttribute<ExContract, String> evRule;
	public static volatile SingularAttribute<ExContract, Double> evMultiplier;
	public static volatile ListAttribute<ExContract, ExTagValue> secIdList;
	public static volatile SingularAttribute<ExContract, String> cusip;
	public static volatile SingularAttribute<ExContract, String> ratings;
	public static volatile SingularAttribute<ExContract, String> descAppend;
	public static volatile SingularAttribute<ExContract, String> bondType;
	public static volatile SingularAttribute<ExContract, String> couponType;
	public static volatile SingularAttribute<ExContract, Boolean> callable;
	public static volatile SingularAttribute<ExContract, Boolean> putable;
	public static volatile SingularAttribute<ExContract, Double> coupon;
	public static volatile SingularAttribute<ExContract, Boolean> convertible;
	public static volatile SingularAttribute<ExContract, String> maturity;
	public static volatile SingularAttribute<ExContract, String> issueDate;
	public static volatile SingularAttribute<ExContract, String> nextOptionDate;
	public static volatile SingularAttribute<ExContract, String> nextOptionType;
	public static volatile SingularAttribute<ExContract, Boolean> nextOptionPartial;
	public static volatile SingularAttribute<ExContract, String> notes;
}
