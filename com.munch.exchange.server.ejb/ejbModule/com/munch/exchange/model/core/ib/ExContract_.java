package com.munch.exchange.model.core.ib;

import com.ib.controller.Types.Right;
import com.ib.controller.Types.SecIdType;
import com.ib.controller.Types.SecType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-04T20:27:46.307+0200")
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
	public static volatile SingularAttribute<ExContract, String> m_marketName;
	public static volatile SingularAttribute<ExContract, Double> m_minTick;
	public static volatile SingularAttribute<ExContract, Integer> m_priceMagnifier;
	public static volatile SingularAttribute<ExContract, String> m_orderTypes;
	public static volatile SingularAttribute<ExContract, String> m_validExchanges;
	public static volatile SingularAttribute<ExContract, Integer> m_underConid;
	public static volatile SingularAttribute<ExContract, String> m_longName;
	public static volatile SingularAttribute<ExContract, String> m_contractMonth;
	public static volatile SingularAttribute<ExContract, String> m_industry;
	public static volatile SingularAttribute<ExContract, String> m_category;
	public static volatile SingularAttribute<ExContract, String> m_subcategory;
	public static volatile SingularAttribute<ExContract, String> m_timeZoneId;
	public static volatile SingularAttribute<ExContract, String> m_tradingHours;
	public static volatile SingularAttribute<ExContract, String> m_liquidHours;
	public static volatile SingularAttribute<ExContract, String> m_evRule;
	public static volatile SingularAttribute<ExContract, Double> m_evMultiplier;
}
