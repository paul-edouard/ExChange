package com.munch.exchange.model.core.ib.statistics;

import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-14T17:21:21.166+0100")
@StaticMetamodel(PerformanceMetrics.class)
public class PerformanceMetrics_ {
	public static volatile SingularAttribute<PerformanceMetrics, Integer> id;
	public static volatile SingularAttribute<PerformanceMetrics, TradeStatistics> tradeStatistics;
	public static volatile SingularAttribute<PerformanceMetrics, TimeStatistics> timeStatistics;
	public static volatile SingularAttribute<PerformanceMetrics, StabilityStatistics> stabilityStatistics;
	public static volatile SingularAttribute<PerformanceMetrics, RevenueStatistics> revenueStatistics;
	public static volatile SingularAttribute<PerformanceMetrics, IbChartSignal> chartSignal;
}
