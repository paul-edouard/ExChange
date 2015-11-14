package com.munch.exchange.model.core.ib.statistics;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-14T16:00:43.785+0100")
@StaticMetamodel(RevenueStatistics.class)
public class RevenueStatistics_ {
	public static volatile SingularAttribute<RevenueStatistics, Integer> id;
	public static volatile SingularAttribute<RevenueStatistics, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<RevenueStatistics, Double> ruturnRate;
	public static volatile SingularAttribute<RevenueStatistics, Double> drawdown;
	public static volatile SingularAttribute<RevenueStatistics, Double> maximumDrawdown;
	public static volatile SingularAttribute<RevenueStatistics, Double> averageDrawdown;
	public static volatile SingularAttribute<RevenueStatistics, Double> maximumClosedEquityDrawdown;
	public static volatile SingularAttribute<RevenueStatistics, Double> averageClosedEquityDrawdown;
	public static volatile SingularAttribute<RevenueStatistics, Double> grossProfit;
	public static volatile SingularAttribute<RevenueStatistics, Double> grossLoss;
	public static volatile SingularAttribute<RevenueStatistics, Double> netProfit;
	public static volatile SingularAttribute<RevenueStatistics, Double> averageProfit;
	public static volatile SingularAttribute<RevenueStatistics, Double> averageLoss;
	public static volatile SingularAttribute<RevenueStatistics, Double> profitFactor;
	public static volatile SingularAttribute<RevenueStatistics, Double> payoffRatio;
	public static volatile SingularAttribute<RevenueStatistics, Double> expectedPayoff;
}
