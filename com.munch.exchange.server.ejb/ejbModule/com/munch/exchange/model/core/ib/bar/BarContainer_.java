package com.munch.exchange.model.core.ib.bar;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-05-01T15:17:24.990+0200")
@StaticMetamodel(BarContainer.class)
public class BarContainer_ {
	public static volatile SingularAttribute<BarContainer, Long> id;
	public static volatile SingularAttribute<BarContainer, IbContract> contract;
	public static volatile SingularAttribute<BarContainer, IbChartIndicatorGroup> indicatorGroup;
	public static volatile SingularAttribute<BarContainer, WhatToShow> type;
}
