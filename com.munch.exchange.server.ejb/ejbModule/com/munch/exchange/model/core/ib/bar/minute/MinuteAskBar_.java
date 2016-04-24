package com.munch.exchange.model.core.ib.bar.minute;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-21T20:12:08.552+0200")
@StaticMetamodel(MinuteAskBar.class)
public class MinuteAskBar_ {
	public static volatile SingularAttribute<MinuteAskBar, MinuteContainer> container;
	public static volatile SingularAttribute<MinuteAskBar, Long> time;
	public static volatile SingularAttribute<MinuteAskBar, Double> high;
	public static volatile SingularAttribute<MinuteAskBar, Double> low;
	public static volatile SingularAttribute<MinuteAskBar, Double> open;
	public static volatile SingularAttribute<MinuteAskBar, Double> close;
	public static volatile SingularAttribute<MinuteAskBar, Double> wap;
	public static volatile SingularAttribute<MinuteAskBar, Long> volume;
	public static volatile SingularAttribute<MinuteAskBar, Integer> count;
}
