package com.munch.exchange.model.core.ib.bar.minute;

import com.munch.exchange.model.core.ib.IbContract;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-10T21:31:44.941+0200")
@StaticMetamodel(MinuteContainer.class)
public class MinuteContainer_ {
	public static volatile SingularAttribute<MinuteContainer, Long> id;
	public static volatile SingularAttribute<MinuteContainer, IbContract> contract;
	public static volatile ListAttribute<MinuteContainer, MinuteAskBar> minuteAskBars;
	public static volatile ListAttribute<MinuteContainer, MinuteBidBar> minuteBidBars;
	public static volatile ListAttribute<MinuteContainer, MinuteMidPointBar> minuteMidPointBars;
	public static volatile ListAttribute<MinuteContainer, MinuteTradesBar> minuteTradesBars;
}
