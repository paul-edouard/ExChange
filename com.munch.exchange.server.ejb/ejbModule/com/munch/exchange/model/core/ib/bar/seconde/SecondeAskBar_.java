package com.munch.exchange.model.core.ib.bar.seconde;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-10T21:32:26.416+0200")
@StaticMetamodel(SecondeAskBar.class)
public class SecondeAskBar_ {
	public static volatile SingularAttribute<SecondeAskBar, Long> id;
	public static volatile SingularAttribute<SecondeAskBar, SecondeContainer> container;
	public static volatile SingularAttribute<SecondeAskBar, Long> time;
	public static volatile SingularAttribute<SecondeAskBar, Double> high;
	public static volatile SingularAttribute<SecondeAskBar, Double> low;
	public static volatile SingularAttribute<SecondeAskBar, Double> open;
	public static volatile SingularAttribute<SecondeAskBar, Double> close;
	public static volatile SingularAttribute<SecondeAskBar, Double> wap;
	public static volatile SingularAttribute<SecondeAskBar, Long> volume;
	public static volatile SingularAttribute<SecondeAskBar, Integer> count;
}
