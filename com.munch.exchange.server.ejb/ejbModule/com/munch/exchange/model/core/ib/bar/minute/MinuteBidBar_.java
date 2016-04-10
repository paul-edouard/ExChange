package com.munch.exchange.model.core.ib.bar.minute;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-10T21:32:01.214+0200")
@StaticMetamodel(MinuteBidBar.class)
public class MinuteBidBar_ {
	public static volatile SingularAttribute<MinuteBidBar, Long> id;
	public static volatile SingularAttribute<MinuteBidBar, MinuteContainer> container;
	public static volatile SingularAttribute<MinuteBidBar, Long> time;
	public static volatile SingularAttribute<MinuteBidBar, Double> high;
	public static volatile SingularAttribute<MinuteBidBar, Double> low;
	public static volatile SingularAttribute<MinuteBidBar, Double> open;
	public static volatile SingularAttribute<MinuteBidBar, Double> close;
	public static volatile SingularAttribute<MinuteBidBar, Double> wap;
	public static volatile SingularAttribute<MinuteBidBar, Long> volume;
	public static volatile SingularAttribute<MinuteBidBar, Integer> count;
}
