package com.munch.exchange.model.core.ib.chart;

import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-04T19:18:54.438+0100")
@StaticMetamodel(IbChartIndicatorGroup.class)
public class IbChartIndicatorGroup_ {
	public static volatile SingularAttribute<IbChartIndicatorGroup, Integer> id;
	public static volatile SingularAttribute<IbChartIndicatorGroup, String> name;
	public static volatile ListAttribute<IbChartIndicatorGroup, IbChartIndicator> indicators;
	public static volatile ListAttribute<IbChartIndicatorGroup, IbChartIndicatorGroup> children;
	public static volatile SingularAttribute<IbChartIndicatorGroup, IbChartIndicatorGroup> parent;
	public static volatile SingularAttribute<IbChartIndicatorGroup, IbBarContainer> container;
}
