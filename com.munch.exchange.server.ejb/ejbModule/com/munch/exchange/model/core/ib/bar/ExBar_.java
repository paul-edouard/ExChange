package com.munch.exchange.model.core.ib.bar;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-20T19:42:12.237+0200")
@StaticMetamodel(ExBar.class)
public class ExBar_ {
	public static volatile SingularAttribute<ExBar, Integer> id;
	public static volatile SingularAttribute<ExBar, WhatToShow> type;
	public static volatile SingularAttribute<ExBar, ExBar> parent;
	public static volatile SingularAttribute<ExBar, BarSize> size;
	public static volatile SingularAttribute<ExBar, Long> time;
	public static volatile SingularAttribute<ExBar, Double> high;
	public static volatile SingularAttribute<ExBar, Double> low;
	public static volatile SingularAttribute<ExBar, Double> open;
	public static volatile SingularAttribute<ExBar, Double> close;
	public static volatile SingularAttribute<ExBar, Double> wap;
	public static volatile SingularAttribute<ExBar, Long> volume;
	public static volatile SingularAttribute<ExBar, Integer> count;
	public static volatile SingularAttribute<ExBar, ExContractBars> root;
}
