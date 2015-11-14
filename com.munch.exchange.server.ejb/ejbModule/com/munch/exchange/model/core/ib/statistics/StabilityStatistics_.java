package com.munch.exchange.model.core.ib.statistics;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-11-14T16:47:56.982+0100")
@StaticMetamodel(StabilityStatistics.class)
public class StabilityStatistics_ {
	public static volatile SingularAttribute<StabilityStatistics, Integer> id;
	public static volatile SingularAttribute<StabilityStatistics, PerformanceMetrics> performanceMetrics;
	public static volatile SingularAttribute<StabilityStatistics, Double> winRate;
	public static volatile SingularAttribute<StabilityStatistics, Double> lossRate;
	public static volatile SingularAttribute<StabilityStatistics, Double> breakEvenRate;
	public static volatile SingularAttribute<StabilityStatistics, Double> riskToRewardRatio;
	public static volatile SingularAttribute<StabilityStatistics, Double> WinOverLossRatio;
	public static volatile SingularAttribute<StabilityStatistics, Double> standardDeviation;
	public static volatile SingularAttribute<StabilityStatistics, Double> averageProfitability;
	public static volatile SingularAttribute<StabilityStatistics, Double> profitToDrawdownRatio;
	public static volatile SingularAttribute<StabilityStatistics, Double> sharpeRatio;
	public static volatile SingularAttribute<StabilityStatistics, Double> calmarRatio;
	public static volatile SingularAttribute<StabilityStatistics, Double> R_Multiple;
	public static volatile SingularAttribute<StabilityStatistics, Double> averageR_Multiple;
	public static volatile SingularAttribute<StabilityStatistics, Double> expectancy;
}
