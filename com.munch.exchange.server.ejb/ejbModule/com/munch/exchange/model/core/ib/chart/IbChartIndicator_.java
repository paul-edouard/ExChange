package com.munch.exchange.model.core.ib.chart;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-19T22:00:19.697+0200")
@StaticMetamodel(IbChartIndicator.class)
public class IbChartIndicator_ {
	public static volatile SingularAttribute<IbChartIndicator, Integer> id;
	public static volatile SingularAttribute<IbChartIndicator, String> name;
	public static volatile SingularAttribute<IbChartIndicator, Boolean> isActivated;
	public static volatile ListAttribute<IbChartIndicator, IbChartParameter> parameters;
	public static volatile SingularAttribute<IbChartIndicator, IbChartIndicatorGroup> group;
	public static volatile ListAttribute<IbChartIndicator, IbChartSerie> series;
}
