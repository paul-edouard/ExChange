package com.munch.exchange.model.core.ib.chart;

import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-29T21:46:14.332+0200")
@StaticMetamodel(IbChartParameter.class)
public class IbChartParameter_ {
	public static volatile SingularAttribute<IbChartParameter, Integer> id;
	public static volatile SingularAttribute<IbChartParameter, ParameterType> type;
	public static volatile SingularAttribute<IbChartParameter, String> name;
	public static volatile SingularAttribute<IbChartParameter, Double> defaultValue;
	public static volatile SingularAttribute<IbChartParameter, Integer> scalarFactor;
	public static volatile SingularAttribute<IbChartParameter, IbChartIndicator> parent;
	public static volatile SingularAttribute<IbChartParameter, Double> currentValue;
	public static volatile SingularAttribute<IbChartParameter, Double> _minValue;
	public static volatile SingularAttribute<IbChartParameter, Double> _maxValue;
}
