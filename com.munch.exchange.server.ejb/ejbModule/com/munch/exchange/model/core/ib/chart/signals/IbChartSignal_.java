package com.munch.exchange.model.core.ib.chart.signals;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator_;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-30T21:25:32.816+0100")
@StaticMetamodel(IbChartSignal.class)
public class IbChartSignal_ extends IbChartIndicator_ {
	public static volatile SingularAttribute<IbChartSignal, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<IbChartSignal, Long> volume;
	public static volatile SingularAttribute<IbChartSignal, String> algorithmName;
	public static volatile SingularAttribute<IbChartSignal, Integer> numberOfEvaluations;
}
