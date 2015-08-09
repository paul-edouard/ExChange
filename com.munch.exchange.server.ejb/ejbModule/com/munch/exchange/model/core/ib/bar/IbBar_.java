package com.munch.exchange.model.core.ib.bar;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-08-09T17:20:56.692+0200")
@StaticMetamodel(IbBar.class)
public class IbBar_ {
	public static volatile SingularAttribute<IbBar, Long> id;
	public static volatile SingularAttribute<IbBar, WhatToShow> type;
	public static volatile SingularAttribute<IbBar, IbBar> parent;
	public static volatile SingularAttribute<IbBar, BarSize> size;
	public static volatile SingularAttribute<IbBar, IbBarContainer> root;
	public static volatile ListAttribute<IbBar, IbBar> childBars;
	public static volatile SingularAttribute<IbBar, Long> time;
	public static volatile SingularAttribute<IbBar, Double> high;
	public static volatile SingularAttribute<IbBar, Double> low;
	public static volatile SingularAttribute<IbBar, Double> open;
	public static volatile SingularAttribute<IbBar, Double> close;
	public static volatile SingularAttribute<IbBar, Double> wap;
	public static volatile SingularAttribute<IbBar, Long> volume;
	public static volatile SingularAttribute<IbBar, Integer> count;
}
