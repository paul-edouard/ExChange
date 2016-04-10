package com.munch.exchange.model.core.ib.bar.seconde;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-10T21:32:42.858+0200")
@StaticMetamodel(SecondeTradesBar.class)
public class SecondeTradesBar_ {
	public static volatile SingularAttribute<SecondeTradesBar, Long> id;
	public static volatile SingularAttribute<SecondeTradesBar, SecondeContainer> container;
	public static volatile SingularAttribute<SecondeTradesBar, Long> time;
	public static volatile SingularAttribute<SecondeTradesBar, Double> high;
	public static volatile SingularAttribute<SecondeTradesBar, Double> low;
	public static volatile SingularAttribute<SecondeTradesBar, Double> open;
	public static volatile SingularAttribute<SecondeTradesBar, Double> close;
	public static volatile SingularAttribute<SecondeTradesBar, Double> wap;
	public static volatile SingularAttribute<SecondeTradesBar, Long> volume;
	public static volatile SingularAttribute<SecondeTradesBar, Integer> count;
}
