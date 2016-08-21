package com.munch.exchange.model.core.ib.chart;

import com.munch.exchange.model.core.ib.neural.NeuralIndicatorInput;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-08-21T13:28:40.662+0200")
@StaticMetamodel(IbChartIndicator.class)
public class IbChartIndicator_ {
	public static volatile SingularAttribute<IbChartIndicator, Integer> id;
	public static volatile SingularAttribute<IbChartIndicator, NeuralIndicatorInput> neuralIndicatorInput;
	public static volatile SingularAttribute<IbChartIndicator, String> name;
	public static volatile SingularAttribute<IbChartIndicator, String> note;
	public static volatile SingularAttribute<IbChartIndicator, String> icon;
	public static volatile SingularAttribute<IbChartIndicator, Boolean> isActivated;
	public static volatile ListAttribute<IbChartIndicator, IbChartParameter> parameters;
	public static volatile ListAttribute<IbChartIndicator, IbChartSerie> series;
	public static volatile SingularAttribute<IbChartIndicator, IbChartIndicatorGroup> group;
}
