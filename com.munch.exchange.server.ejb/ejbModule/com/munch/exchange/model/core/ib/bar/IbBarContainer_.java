package com.munch.exchange.model.core.ib.bar;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-10T21:13:09.548+0200")
@StaticMetamodel(IbBarContainer.class)
public class IbBarContainer_ extends IbBar_ {
	public static volatile SingularAttribute<IbBarContainer, IbContract> contract;
	public static volatile ListAttribute<IbBarContainer, IbBar> allBars;
	public static volatile SingularAttribute<IbBarContainer, IbChartIndicatorGroup> indicatorGroup;
}
