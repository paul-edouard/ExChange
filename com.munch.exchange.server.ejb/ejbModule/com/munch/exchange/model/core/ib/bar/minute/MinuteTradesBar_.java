package com.munch.exchange.model.core.ib.bar.minute;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-25T21:11:34.047+0200")
@StaticMetamodel(MinuteTradesBar.class)
public class MinuteTradesBar_ {
	public static volatile SingularAttribute<MinuteTradesBar, MinuteContainer> container;
	public static volatile SingularAttribute<MinuteTradesBar, Long> time;
	public static volatile SingularAttribute<MinuteTradesBar, Long> containerId;
	public static volatile SingularAttribute<MinuteTradesBar, Double> high;
	public static volatile SingularAttribute<MinuteTradesBar, Double> low;
	public static volatile SingularAttribute<MinuteTradesBar, Double> open;
	public static volatile SingularAttribute<MinuteTradesBar, Double> close;
	public static volatile SingularAttribute<MinuteTradesBar, Double> wap;
	public static volatile SingularAttribute<MinuteTradesBar, Long> volume;
	public static volatile SingularAttribute<MinuteTradesBar, Integer> count;
}
