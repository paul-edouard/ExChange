package com.munch.exchange.model.core.ib.bar.minute;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-10T21:32:06.703+0200")
@StaticMetamodel(MinuteMidPointBar.class)
public class MinuteMidPointBar_ {
	public static volatile SingularAttribute<MinuteMidPointBar, Long> id;
	public static volatile SingularAttribute<MinuteMidPointBar, MinuteContainer> container;
	public static volatile SingularAttribute<MinuteMidPointBar, Long> time;
	public static volatile SingularAttribute<MinuteMidPointBar, Double> high;
	public static volatile SingularAttribute<MinuteMidPointBar, Double> low;
	public static volatile SingularAttribute<MinuteMidPointBar, Double> open;
	public static volatile SingularAttribute<MinuteMidPointBar, Double> close;
	public static volatile SingularAttribute<MinuteMidPointBar, Double> wap;
	public static volatile SingularAttribute<MinuteMidPointBar, Long> volume;
	public static volatile SingularAttribute<MinuteMidPointBar, Integer> count;
}
