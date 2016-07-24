package com.munch.exchange.model.core.ib.chart.signals;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator_;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-07-23T14:35:58.018+0200")
@StaticMetamodel(IbChartSignal.class)
public class IbChartSignal_ extends IbChartIndicator_ {
	public static volatile ListAttribute<IbChartSignal, IbChartSignalOptimizedParameters> optimizedSet;
	public static volatile SingularAttribute<IbChartSignal, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<IbChartSignal, Long> volume;
}
