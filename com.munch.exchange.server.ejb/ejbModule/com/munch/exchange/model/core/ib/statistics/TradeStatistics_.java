package com.munch.exchange.model.core.ib.statistics;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-28T14:01:03.433+0100")
@StaticMetamodel(TradeStatistics.class)
public class TradeStatistics_ {
	public static volatile SingularAttribute<TradeStatistics, Integer> id;
	public static volatile SingularAttribute<TradeStatistics, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<TradeStatistics, Integer> totalTrades;
	public static volatile SingularAttribute<TradeStatistics, Double> winOverLossTrades;
	public static volatile SingularAttribute<TradeStatistics, Double> breakEvenTrades;
	public static volatile SingularAttribute<TradeStatistics, Double> maximumWinOverMaximumLossTrades;
	public static volatile SingularAttribute<TradeStatistics, Integer> maximumNumberOfConsecutiveLosses;
	public static volatile SingularAttribute<TradeStatistics, Integer> maximumNumberOfConsecutiveWins;
	public static volatile SingularAttribute<TradeStatistics, Double> totalCommissionsOrSpreads;
	public static volatile SingularAttribute<TradeStatistics, Double> totalSlippage;
	public static volatile SingularAttribute<TradeStatistics, Double> totalForexCarry;
	public static volatile SingularAttribute<TradeStatistics, Double> maximumAdverseExcursion;
	public static volatile SingularAttribute<TradeStatistics, Double> maximumFavorableExcursion;
}
