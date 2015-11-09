package com.munch.exchange.model.core.ib;

import com.munch.exchange.model.core.ib.IbCommission.CommissionCategory;
import com.munch.exchange.model.core.ib.IbCommission.CommissionType;
import com.munch.exchange.model.core.ib.IbCommission.Currency;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-09T14:57:25.161+0100")
@StaticMetamodel(IbCommission.class)
public class IbCommission_ {
	public static volatile SingularAttribute<IbCommission, Integer> id;
	public static volatile SingularAttribute<IbCommission, CommissionCategory> commissionCategory;
	public static volatile SingularAttribute<IbCommission, CommissionType> commissionType;
	public static volatile SingularAttribute<IbCommission, Currency> currency;
	public static volatile SingularAttribute<IbCommission, Double> fixed;
	public static volatile SingularAttribute<IbCommission, Boolean> fixed_isPercentOfTradeValue;
	public static volatile SingularAttribute<IbCommission, Double> minPerOrder;
	public static volatile SingularAttribute<IbCommission, Boolean> minPerOrder_isPercentOfTradeValue;
	public static volatile SingularAttribute<IbCommission, Double> maxPerOrder;
	public static volatile SingularAttribute<IbCommission, Boolean> maxPerOrder_isPercentOfTradeValue;
	public static volatile SingularAttribute<IbCommission, Double> monthlyTradeAmount;
	public static volatile SingularAttribute<IbCommission, Double> commissions;
	public static volatile SingularAttribute<IbCommission, Double> contractVolume;
	public static volatile SingularAttribute<IbCommission, IbContract> contract;
}
