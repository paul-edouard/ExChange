package com.munch.exchange.model.core.ib.chart.signals;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator_;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-12-12T21:23:59.278+0100")
@StaticMetamodel(IbChartSignal.class)
public class IbChartSignal_ extends IbChartIndicator_ {
	public static volatile SingularAttribute<IbChartSignal, String> algorithmName;
	public static volatile SingularAttribute<IbChartSignal, Integer> numberOfEvaluations;
	public static volatile SingularAttribute<IbChartSignal, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<IbChartSignal, Long> volume;
}
