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
	private double totalCommissionsOverSpreads;
	
	
	/**
	 * Total Slippage

	It's usually expressed in dollar amounts, although a "Slippage Percent" figure may be used.
	A larger explanation of the phenomena can be found in Chapter A03.
	 */
	private double totalSlippage;
	
	/**
	 * Total Forex Carry

	Another figure which belongs to the trading costs is money earned or paid as a result of Forex Carry
	(see in Chapter A03 how the Interest Rate Rollover is calculated).
	 */
	private double totalForexCarry;
	
	/**
	 * Equity Curve

	This is not a statistical figure in itself, but a very useful tool: a quick review of a system�s equity curve can provide some additional insight into its performance.
	Equity curve charts tally a system�s individual trades to present a time line of trade-by-trade results in a graphic format.

	By displaying the net profit curve it reveals equity Drawdowns (series of loses) and the gain periods (also called �run-ups�).
	Flat or non-trading periods are also shown to present the equity performance in a detailed graph.

	What is important to notice in a equity curve is the magnitude and time duration of the Drawdowns.
	This graphic tool may be used at a portfolio level to match trading systems and offsetting periods of loss from one system with gain from another to create well balanced trading portfolios.

	The below picture is the equity curve of the system introduced in Chapter C01, tested on the CHF/USD from July 2006 until January 2009.
	 */
	
	/**
	 * Number of Closed Trades and Number of Trades in a Drawdown

	Although this figure also belongs to the Drawdown statistics (covered in the next section),
	it's a trade statistic because it shows the number of closed trades comprised in a Drawdown,
	or number of trades including open ones.
	This figure is specially useful as unreported losing trades can hide a bad performing system.
	 * 
	 * 
	 */
	
	/**
	 * Maximum Adverse Excursion

	This is the maximum potential loss that the trade had before the trade closed in profit.
	For example, a trade closed with 25 points in profit but during the time it was open,
	at one point, it was losing 100 points - that was the Maximum Adverse Excursion for that trade.
	 */
	private double maximumAdverseExcursion;
	
	/**
	 * Maximum Favorable Excursion

	Maximum Favorable Excursion is the peak profit before closing the trade.
	For example, you may have a closed trade which lost 25 pips but during the time the trade was open,
	it was making a 100 pips profit at some point - that was the Maximum Favorable Excursion for that particular trade.

	This statistical concept originally created by John Sweeney to measure the distinctive characteristics of profitable trades,
	can be used as part of an analytical process to enable traders to distinguish between average trades and those that offer substantially greater profit potential.
	 */
	private double maximumFavorableExcursion;
	
	
	
	
	
	public TradeStatistics() {
		super();
	}
	
	
	
	
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PerformanceMetrics getPerformanceMetrics() {
		return performanceMetrics;
	}

	public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) {
		this.performanceMetrics = performanceMetrics;
	}

	public int getTotalTrades() {
		return totalTrades;
	}

	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}

	public double getWinOverLossTrades() {
		return winOverLossTrades;
	}

	public void setWinOverLossTrades(double winOverLossTrades) {
		this.winOverLossTrades = winOverLossTrades;
	}

	public double getBreakEvenTrades() {
		return breakEvenTrades;
	}

	public void setBreakEvenTrades(double breakEvenTrades) {
		this.breakEvenTrades = breakEvenTrades;
	}

	public double getMaximumWinOverMaximumLossTrades() {
		return maximumWinOverMaximumLossTrades;
	}

	public void setMaximumWinOverMaximumLossTrades(
			double maximumWinOverMaximumLossTrades) {
		this.maximumWinOverMaximumLossTrades = maximumWinOverMaximumLossTrades;
	}

	public int getMaximumNumberOfConsecutiveLosses() {
		return maximumNumberOfConsecutiveLosses;
	}

	public void setMaximumNumberOfConsecutiveLosses(
			int maximumNumberOfConsecutiveLosses) {
		this.maximumNumberOfConsecutiveLosses = maximumNumberOfConsecutiveLosses;
	}

	public int getMaximumNumberOfConsecutiveWins() {
		return maximumNumberOfConsecutiveWins;
	}

	public void setMaximumNumberOfConsecutiveWins(int maximumNumberOfConsecutiveWins) {
		this.maximumNumberOfConsecutiveWins = maximumNumberOfConsecutiveWins;
	}

	public double getTotalCommissionsOverSpreads() {
		return totalCommissionsOverSpreads;
	}

	public void setTotalCommissionsOverSpreads(double totalCommissionsOverSpreads) {
		this.totalCommissionsOverSpreads = totalCommissionsOverSpreads;
	}

	public double getTotalSlippage() {
		return totalSlippage;
	}

	public void setTotalSlippage(double totalSlippage) {
		this.totalSlippage = totalSlippage;
	}

	public double getTotalForexCarry() {
		return totalForexCarry;
	}

	public void setTotalForexCarry(double totalForexCarry) {
		this.totalForexCarry = totalForexCarry;
	}

	public double getMaximumAdverseExcursion() {
		return maximumAdverseExcursion;
	}

	public void setMaximumAdverseExcursion(double maximumAdverseExcursion) {
		this.maximumAdverseExcursion = maximumAdverseExcursion;
	}

	public double getMaximumFavorableExcursion() {
		return maximumFavorableExcursion;
	}

	public void setMaximumFavorableExcursion(double maximumFavorableExcursion) {
		this.maximumFavorableExcursion = maximumFavorableExcursion;
	}
	
	
	
	
	
	
	
	
	
}