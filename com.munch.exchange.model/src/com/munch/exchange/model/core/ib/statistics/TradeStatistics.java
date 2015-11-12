package com.munch.exchange.model.core.ib.statistics;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


/**
 * http://www.fxstreet.com/education/learning-center/unit-3/chapter-2/trade-statistics/
 * 
 * Trade Statistics

	The goal of these figures is to start evaluating the overall performance of a system or strategy by measuring each and all trades objectively.
	The figures disclosed herewith are considered the most basic ones and are by themselves not enough to establish a complete statistical report.
	Nonetheless, they serve as starting points to calculate more complex figures.
 * 
 * 
 * @author paul-edouard
 *
 */

@Entity
public class TradeStatistics implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6494382247409820853L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne
	@JoinColumn(name="PERFORMANCE_METRICS_ID")
	private PerformanceMetrics performanceMetrics;
	
	/**
	 * This is simply the number of all trades - winning, losing and break-even trades - opened in a certain period of time.
	 * Note that at any time of a track record this figure will also include trades that aren't yet closed.
	 */
	private int totalTrades;
	
	/**
	 * Win Trades / Loss Trades
	 * 
	 * Win Trades are also called Profit Trades and it's the number of winning closed trades during the time period of a performance report.
	 *	Conversely, Loss Trades is the number of closed trades resulted in losses during the same time period.
	 */
	private double winOverLossTrades;
	
	/**
	 * Break-even Trades

	This figure represents the number of trades that are neither winners nor losers. A break-even trade is a trade closed at zero profit.
	Because the cost of the trade is recovered in break-even trades, some people consider them winning trades.
	But for more advanced statistical measures, let's keep them as a separated class.
	 */
	private double breakEvenTrades;
	
	
	/**
	 * Maximum Win Trade / Maximum Loss Trade

	This figure refers to the biggest Win Trade and the biggest Loss Trade - usually closed trades - at the moment of the report.
	 * 
	 * 
	 */
	private double maximumWinOverMaximumLossTrades;
	
	
	/**
	 * Maximum Number of Consecutive Losses

	Regardless of how well a system performs, there is always the likelihood of losing strings.
	This figure registers the number of losing trades in a row.
	 * 
	 */
	private int  maximumNumberOfConsecutiveLosses;
	
	
	/**
	 * Maximum Number of Consecutive Wins

	The same happens when several winning trades cluster in a relatively short amount of time.
	This figure reflects the number of consecutive winning trades.
	 * 
	 */
	private int maximumNumberOfConsecutiveWins;
	
	/**
	 * Total Commissions / Spreads

	This is the total costs paid out for all positions in the form of spreads and/or commissions.
	This can be a decisive figure specially if the reported Trade Frequency of a system is high.
	 * 
	 */
	private double commissionsOverSpreads;
	
}
