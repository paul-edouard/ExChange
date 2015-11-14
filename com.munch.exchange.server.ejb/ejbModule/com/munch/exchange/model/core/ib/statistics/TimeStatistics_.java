package com.munch.exchange.model.core.ib.statistics;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-14T16:28:22.208+0100")
@StaticMetamodel(TimeStatistics.class)
public class TimeStatistics_ {
	public static volatile SingularAttribute<TimeStatistics, Integer> id;
	public static volatile SingularAttribute<TimeStatistics, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<TimeStatistics, Long> averageTimeHoldingWinningTradesVersusLosingTrades;
	public static volatile SingularAttribute<TimeStatistics, Long> longestTotalEquityDrawdown;
	public static volatile SingularAttribute<TimeStatistics, Long> averageHoldingTime;
	public static volatile SingularAttribute<TimeStatistics, Long> maximumMonthlyTotalEquityDrawndown;
}
