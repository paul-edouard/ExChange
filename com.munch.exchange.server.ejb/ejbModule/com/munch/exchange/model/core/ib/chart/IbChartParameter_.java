package com.munch.exchange.model.core.ib.chart;

import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-12-20T16:20:19.735+0100")
@StaticMetamodel(IbChartParameter.class)
public class IbChartParameter_ {
	public static volatile SingularAttribute<IbChartParameter, Integer> id;
	public static volatile SingularAttribute<IbChartParameter, ParameterType> type;
	public static volatile SingularAttribute<IbChartParameter, String> name;
	public static volatile SingularAttribute<IbChartParameter, Double> currentValue;
	public static volatile SingularAttribute<IbChartParameter, Double> defaultValue;
	public static volatile SingularAttribute<IbChartParameter, Double> _minValue;
	public static volatile SingularAttribute<IbChartParameter, Double> _maxValue;
	public static volatile SingularAttribute<IbChartParameter, Integer> scalarFactor;
	public static volatile SingularAttribute<IbChartParameter, IbChartIndicator> parent;
	public static volatile SingularAttribute<IbChartParameter, IbChartSignalOptimizedParameters> optimizedParameters;
}
