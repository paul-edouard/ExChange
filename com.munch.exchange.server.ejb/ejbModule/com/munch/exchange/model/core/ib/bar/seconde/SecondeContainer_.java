package com.munch.exchange.model.core.ib.bar.seconde;

import com.munch.exchange.model.core.ib.IbContract;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-27T21:35:13.479+0200")
@StaticMetamodel(SecondeContainer.class)
public class SecondeContainer_ {
	public static volatile SingularAttribute<SecondeContainer, Long> id;
	public static volatile SingularAttribute<SecondeContainer, IbContract> contract;
	public static volatile ListAttribute<SecondeContainer, SecondeAskBar> secondeAskBars;
	public static volatile SingularAttribute<SecondeContainer, Long> lastShortTermAskBarTime;
	public static volatile SingularAttribute<SecondeContainer, Boolean> longTermAskBarLoadingFinished;
	public static volatile ListAttribute<SecondeContainer, SecondeBidBar> secondeBidBars;
	public static volatile SingularAttribute<SecondeContainer, Long> lastShortTermBidBarTime;
	public static volatile SingularAttribute<SecondeContainer, Boolean> longTermBidBarLoadingFinished;
	public static volatile ListAttribute<SecondeContainer, SecondeMidPointBar> secondeMidPointBars;
	public static volatile SingularAttribute<SecondeContainer, Long> lastShortTermMidPointBarTime;
	public static volatile SingularAttribute<SecondeContainer, Boolean> longTermMidPointBarLoadingFinished;
	public static volatile ListAttribute<SecondeContainer, SecondeTradesBar> secondeTradesBars;
	public static volatile SingularAttribute<SecondeContainer, Long> lastShortTermTradesBarTime;
	public static volatile SingularAttribute<SecondeContainer, Boolean> longTermTradesBarLoadingFinished;
}
