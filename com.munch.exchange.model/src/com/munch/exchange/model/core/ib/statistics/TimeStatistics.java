package com.munch.exchange.model.core.ib.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;


/**
 * 
 * http://www.fxstreet.com/education/learning-center/unit-3/chapter-2/time-statistics/
 * 
 *  3. Time Statistics

	This section is about evaluation strictly from the standpoint of time.
	The use of time is essential to properly evaluate a trading system.
	This form of analysis can be used on the entire system or on its individual trades.
	In either case, time-in-the-market is considered a measure of risk.
	Time statistics are important because the longer a position is exposed to the market, the more risk it assumes.
 * 
 * 
 * @author paul-edouard
 *
 */



@Entity
public class TimeStatistics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5407508892465027706L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne
	@JoinColumn(name="PERFORMANCE_METRICS_ID")
	private PerformanceMetrics performanceMetrics;
	
	
	
	/**
	 *  Average Holding Time

	This is an important statistic because in Forex there are roll-over gains and losses associated with the time duration of trades.
	 * 
	 */
	private long averageHoldingTime;
	
	
	/**
	 *  Average Time Holding Winning Trades versus Losing Trades

	A variation of the above figure is the Average Time Holding Winning Trades versus Losing Trades.
	At first glance, this figure may not seem useful, but including it in your arsenal may lead you to collect some valuable information.
	There are traders that after the years perform better in long than in short trades, or vice versa.
	A myriad of other aspects can cause your system to perform better in one side than the other.
	If you don't track this number, it's much more difficult to detect any irregularity.
	 */
	private double averageTimeHoldingWinningTradesVersusLosingTrades;
	
	/**
	 * 
	 *  Longest Total Equity Drawdown

	It's a series of losses measured from a previous equity curve peak to new equity peak, expressed in time.
	Its usefulness relies on the fact that the trader knows what to expect in terms of losing strings
	and adapts risk control measures in order to endure a large Drawdown.
	 * 
	 */
	private long longestTotalEquityDrawdown;
	
	/**
	 *  Maximum Monthly Total Equity Drawndown

	Like Maximum Drawdown, this is also a one-time event that reflects the largest retracement in the equity curve relative to a previous equity high,
	but based on a month-end to month-end and Mark-to-Market. Usually expressed as a percentage,
	this figure is important especially for institutional traders who are subject to rigorous monthly revisions and risk control measures.
	 * 
	 */
	private long maximumMonthlyTotalEquityDrawndown;
	
	

	public TimeStatistics() {
		super();
		// TODO Auto-generated constructor stub
	}




	public void calculate(List<IbBar> bars, HashMap<Long, IbChartPoint> signalMap,
			IbCommission commission, long volume){
		
		averageHoldingTime=0;
		averageTimeHoldingWinningTradesVersusLosingTrades=0;
		
		double averageTimeHoldingWinningTrades=0;
		double averageTimeHoldingLosingTrades=0;
		
		
		int totalTrades=0;
		int winTrades=0;
		int lossTrades=0;
		
		//double previewProfit=0;
		
		IbBar previewBar=bars.get(0);
		double previewSignal=signalMap.get(previewBar.getTimeInMs()).getValue();
		
		int openPosition=0;
		
		for(int i=1;i<bars.size();i++){
			IbBar bar=bars.get(i);
			double signal=signalMap.get(bar.getTimeInMs()).getValue();
			double diffSignal=signal-previewSignal;
			double absDiffSignal=Math.abs(diffSignal);
			
			
			//New Position
			if(absDiffSignal>0){
				
				//Close a position
				if(signal==0 || absDiffSignal==2){
					double profit=(bar.getClose()-bars.get(openPosition).getClose())*previewSignal*volume;
					long holdingTimeInSec=bar.getTime()-bars.get(openPosition).getTime();
					
					averageHoldingTime+=holdingTimeInSec;
					
					
					//Win
					if(profit>0){
						winTrades++;
						averageTimeHoldingWinningTrades+=holdingTimeInSec;
						
					}
					
					//Loss
					else if(profit<0){
						lossTrades++;
						averageTimeHoldingLosingTrades+=holdingTimeInSec;
					}
					
					
				}
				
				//Open a new position
				if(Math.abs(signal)>0){
					totalTrades++;
					openPosition=i;
					
				}
				
				
				
			}
			
			previewBar=bar;
			previewSignal=signal;
		}
		
		
		averageHoldingTime/=totalTrades;
		
		
		averageTimeHoldingWinningTrades/=winTrades;
		averageTimeHoldingLosingTrades/=lossTrades;
		
		averageTimeHoldingWinningTradesVersusLosingTrades=averageTimeHoldingWinningTrades/averageTimeHoldingLosingTrades;
		
	}

	
	public Object[] getChildren(){
		
		Object[] children =new Object[2];
		
		children[0]="Average Holding Time, "+averageHoldingTime;
		children[1]="Average Time Holding Winning Trades Versus Losing Trades, "+String.format("%1$,.2f",averageTimeHoldingWinningTradesVersusLosingTrades);
		
		
		return children;
		
	}
	
	
	
	/**
	 *  Trades per Day/Month/Year

	This figure is related to the Trade Frequency and is important to evaluate more sophisticated figures. For instance, in a system with ten trades per year, the trading costs are less important, but the Win Rate becomes crucial. The higher your trading frequency, the smaller your chances of having a losing month. If you have a trading strategy that has a winning percentage of 70%, but only produces one trade per month, then one loser is enough to have a losing month.
	But if your trading strategy produces five trades per week, then you have on average 20 trades per month. With a Win rate of 70%, your chances of a winning month are extremely high. And that's the goal of all day-traders: having as many winning months as possible!

	A variation of this figure may point to the amount of winning or losing trades per day/month etc.

	So, the question “How long should a performance report be?” mainly depends
	on the trade frequency. If your strategy generates three trades per day, then you might get enough data after several weeks of trading. But if your trading
	strategy generates only three trades per month, then you should perform your strategy for several years to receive reliable data results.

	When displayed in a chart, the distribution of trades becomes an important data. As a trader you want to know if your system is producing winning trades in the same average rage it has been doing in the past of if there are any changes. It can also give you clues as if there are days in the week when more winning or losing trades happen, etc.
	The below chart displays the distribution of trades corresponding to the equity curve displayed in the first section.
	Profit/Loss Trades

	A lot of statistical figures have been covered so far. While back or forwardtesting your trading strategy, you should keep detailed records of
	all these data - or at least the majority of them - in order to produce a significant performance report. Many software packages can help you with that, but a simple excel sheet will do the trick just as well.

	Because the potential gains in Forex are so big, the excitement of a unparalleled lifestyle and the freedom of choice associated to trading might easily cloud our objectivity regarding our abilities and limitations. Statistics help you see the trading without this emotional attachment and build your career on more realistic expectations.

	An aspiring trader can make money trading over a short period of time with a little luck, but specially when starting out you need to measure your expectations with a proper goal setting. Your goals must be in synchrony with where you are in the learning curve. If you have a goal of doubling your account in a few trades, you probably need to concentrate on not blowing up the account overnight.

	The next section will help you to come off the individual results from each trade and look at the overall statistical landscape.
	 * 
	 */
	
	
	
	
	
	public long getAverageHoldingTime() {
		return averageHoldingTime;
	}


	public void setAverageHoldingTime(long averageHoldingTime) {
		this.averageHoldingTime = averageHoldingTime;
	}


	public double getAverageTimeHoldingWinningTradesVersusLosingTrades() {
		return averageTimeHoldingWinningTradesVersusLosingTrades;
	}


	public void setAverageTimeHoldingWinningTradesVersusLosingTrades(
			long averageTimeHoldingWinningTradesVersusLosingTrades) {
		this.averageTimeHoldingWinningTradesVersusLosingTrades = averageTimeHoldingWinningTradesVersusLosingTrades;
	}


	public long getLongestTotalEquityDrawdown() {
		return longestTotalEquityDrawdown;
	}


	public void setLongestTotalEquityDrawdown(long longestTotalEquityDrawdown) {
		this.longestTotalEquityDrawdown = longestTotalEquityDrawdown;
	}


	public long getMaximumMonthlyTotalEquityDrawndown() {
		return maximumMonthlyTotalEquityDrawndown;
	}


	public void setMaximumMonthlyTotalEquityDrawndown(
			long maximumMonthlyTotalEquityDrawndown) {
		this.maximumMonthlyTotalEquityDrawndown = maximumMonthlyTotalEquityDrawndown;
	}




	
	
	
	@Override
	public String toString() {
		return "TimeStatistics [averageHoldingTime=" + averageHoldingTime
				+ ", averageTimeHoldingWinningTradesVersusLosingTrades="
				+ averageTimeHoldingWinningTradesVersusLosingTrades
				+ ", longestTotalEquityDrawdown=" + longestTotalEquityDrawdown
				+ ", maximumMonthlyTotalEquityDrawndown="
				+ maximumMonthlyTotalEquityDrawndown + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
