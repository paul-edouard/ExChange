package com.munch.exchange.model.core.ib.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
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
 *  4. Stability Statistics

	These figures aim to evaluate the robustness and stability of a trading system
	or strategy by calculating statistics based on less complex figures.
	Stability is the key to improve performance through money management strategies.
	A proper use of leverage can only be done if the trading edges are well measured.
 * 
 * @author paul-edouard
 *
 */

@Entity
public class StabilityStatistics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 812041034044401842L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne
	@JoinColumn(name="PERFORMANCE_METRICS_ID")
	private PerformanceMetrics performanceMetrics;
	
	
	
	/**
	 *  Win Rate

	The percentage of profitable trades, the Win Rate, is another statistic that many traders use to gauge performance success.
	This figure, sometimes referred to as Success Rate or Profitability, has to do with the reliability of the trading method.
	It’s simply the number of winning trades expressed as a percentage of the total number of trades. It is calculated as following:
 

	Win Rate = Number of Profit Trades X 100 / Total Number of Trades
 

	On a random entry method with no edge - for instance, a “coin toss” approach, - the Win Rate will directly be in line with the Risk-to-Reward Ratio.
	A system entering a trade based on coin toss heads for short and tails for long, each day at 10:30am,
	with a Stop Loss of 50 pips and a Take Profit of 20 pips, will hit the Take Profit more times than the Stop Loss.
	This doesn't mean the system would make money, because even with a 70% Win Rate it would result in a break-even
	or losing system because of the impact of spreads and slippage. But in any case, a 5:2 Risk-to-Reward Ratio provides a positive baseline probability.
	 * 
	 */
	private double winRate;
	
	/**
	 *  Loss Rate

	This is the percentage of unprofitable trades in a trading report.
	It is important to know that this number does not derive automatically from the above by subtracting that one from 100%.
	In trading, there are not only winning and losing trades but also break-even trades.
 

	Loss Rate = Number of Loss Trades X 100 / Total Number of Trades
	 */
	private double lossRate;
	
	
	/**
	 *  Break-even Rate

	This is not a very common statistical measure but nonetheless very useful.
	This is the percentage of break-even trades in a trading report, the proportion of trades that were neither winning nor losing trades.
 

	Break-even Rate = Number of Break-even Trades X 100 / Total Number of Trades
 

	If we were to compare two systems with an equal Win Rate of 55%,
	this figure would be of utmost importance if one of the system would have a Break-even rate of 0% and the other one of 15%.
	We can deduce from a 15% Break-even Rate and a 55% Win rate that the Loss Rate is only 20%,
	while for a system with no break-even trades, the Loss Rate is 45%.
	A 55% Win Rate for a 20% Loss Rate is still a very good ratio, even if the system has such a modest Win Rate.
	 * 
	 */
	private double breakEvenRate;
	
	
	/**
	 *  Risk-to-Reward Ratio

	The Risk-to-Reward Ratio is the maximum risk taken on a particular trade divided by the maximum profit expectation of the same trade.
	It’s a value used to estimate particular trade set-ups and should be not used as the Payoff Ratio.
 

	Risk Reward Ratio = maximum risk taken on a particular trade / maximum profit expectation of the same trade
	 * 
	 */
	private double riskToRewardRatio;
	
	
	/**
	 *  Win/Loss Ratio

	This figure is purely the total number of winning trades compared to the total number of losing trades.
	If in 100 trade,s you have 50 winners and 50 losers, then your win/loss ratio is 50:50. Alternatively,
	if you have 70 winners and 30 losers then the ratio is 7:3, etc.
	Opposed to the Risk-to-Reward Ratio it does not take into account how much was won or lost,
	but simply if they were winners or losers. The formula is:
 

	Win/Loss Ratio = Number of Winning Trades / Number of Losing Trades
 

	It is important to remember that this ratio is not the only a factor in determining if and how profitable a system is.
	It is also important to calculate how much it wins when it is right vs. how much it loses when it is wrong.
	 * 
	 * 
	 */
	private double WinOverLossRatio;
	
	
	/**
	 *
	Standard Deviation

	One of the most meaningful ways to evaluate the profitability is through the standard deviation figure.
	Standard deviation measures how widely the trades results are dispersed from the average results.
	The dispersion is defined as the difference between the actual value and the average value.
	
	The larger the difference between the actual trade result and the average result,
	the higher the standard deviation and volatility of the Equity Curve will be.
	The closer the results are to the average, the lower the standard deviation or volatility of the Equity Curve.

	In order to calculate the Standard Deviation, take the square root of the variance,
	the average of the squared deviations from the mean value.
	One standard deviation away from the mean (the Average Profit per trade),
	either plus or minus, will include 67.5% of the trade results.
	Two standard deviations on either side of the Average Profit figure will encompass approximately 95% of all probable outcomes when trading a certain system.

 

	ST High Range = 2 x Standard Deviation + Average Profit
	ST Low Range = Average Profit - 2 x Standard Deviation

 

	This means that 95% of the time, this system would have returned profits in the range outlined above.
	If the returns are widely dispersed with both large winners and losers,
	the performance shows a high Standard Deviation and is therefore considered risky.
	Conversely, if returns are wrapped tightly around the mean, the performance has a smaller Standard Deviation and is considered less risky.

	The below picture shows a normal distribution of where the frequency of the occurrence of results is distributed like a bell curve around the mean value.
	This means that the density of the results is highest near the center of the range of results.
	 */
	private double standardDeviation;
	
	
	/**
	 *  Average Profitability

	Average Profitability per trade basically refers to the average amount you can expect to win or lose per trade,
	but its calculation differs from the previous Payoff Ratio. Accordingly to Grace Cheng, this is the formula:
 

	Average Profitability Per Trade = (Win Rate x Average Profit) - (Loss Rate x Average Loss)
 

	Let's imagine a hypothetical scenario of a performance record showing a 30% Win Rate and a Payoff Ratio of 2:1.
	In this case, the resulting value would be a negative one, meaning the supposed benefit of having a 2:1 payoff ratio does not compensate a 70% Loss Rate.
	In a scenario of a Payoff of 1:3 you would need a Win Rate 80% to get some decent positive Average Profitability and be profitable over time.

	The number should be expressed in dollar values rather than in pips because of the oscillations of the pip value. But what is most important here is the number to be positive.
	 */
	private double averageProfitability;
	
	
	/**
	 *  Profit-to-Drawdown Ratio

	This ratio measures the amount of profit you experience for a given amount of Drawdown.

	For example, a performance that shows a 20% profit after a 20% Drawdown should be considered better than a performance that reaches a 20% profit after a 60% Drawdown.

	In order to calculate the Profit to Drawdown Ratio, the Net Profit has to be divided by the Maximum Drawdown for a certain period of time. Accordingly to Lars Kestner,

	"The higher the number, the better as it means there is more profit generated for a given amount of Drawdown. In this sense, it creates a measure of Risk-to-Reward.
	Riskier strategies have larger Maximum Dradowns and will lead to lower Profit-to-Drawdown Ratios.”

	Profit to Drawdown Ratio = Total Net Profit / the Maximum Drawdown
	 */
	private double profitToDrawdownRatio;
	
	
	/**
	 *  Sharpe Ratio

	Developed by Nobel Laureate William Sharpe, the Sharpe Ratio is a standard in the money management industry
	and is used to evaluate the Risk-to-Reward efficiency of investments.
	The Sharpe Ratio is calculated by subtracting the risk-free rate - such as the one of the US Treasury bond -
	from the Return Rate and dividing the result by the Standard Deviation of the system's returns. The Sharpe ratio formula is:
 

	Sharpe Ratio = (Return Rate – Risk free rate) / Standard Deviation
	 */
	private double sharpeRatio;
	
	/**
	 * 
	 *  Calmar Ratio

	Another performance number that new traders rarely look at is the Calmar Ratio.
	Although there are numerous variations and twists to it, in its simplest form this number is the ratio of Annual Return Rate to the Maximum Drawdown,
	using the absolute value. It is similar to the Sharpe Ratio, with the difference that it's based on the worst case scenario, rather than on volatility.

	If a performance record shows a 50% annual Return Rate with 25% Maximum Drawdown, the Calmar ratio will be 2.
 

	Calmar Ratio = Compounded Annual Return / Maximum Drawdown
 

	By including the Calmar Ratio in your evaluation arsenal, you will be much better prepared to properly scrutinize your trading methods and compare them more efficiently.
	 * 
	 */
	private double calmarRatio;
	
	
	
	/**
	 *  R-multiple

	R-multiples is the initial risk taken on each trade. It's not a statistical figure in itself but it serves for the calculation of Expectancy.
	For example, if you buy the EUR/USD at 1.5000 and set the Stop Loss at 1.4900.
	This means if the trade results in a loss, it would be a loss of of 100 pips.
	In short, you R is 100 pips. But you are able to sell the pair back at 1.5300 and make 300 pips in profit. This means the final outcome in this trade is +3R.
	The formula is thus:
 

	R-multiple = ( Profit amount / Initial R )
 

	When you have a series of profits and losses expressed as Risk-to-Reward ratios,
	what you really have is what Van Tharp calls an R-multiple distribution.
	This means that any trading system can be characterized as being an R-multiple distribution.
	When you have an R-multiple distribution from your trading system, you need to get the average of that distribution – that is the system’s expectancy.
	 * 
	 */
	private double R_Multiple;
	
	/**
	 *  Average R-Multiple

	This statistic is another way to assess how much one expects to gain for every dollar bet or risked on a given trade.
	Numbers greater than zero point to a winning system, less than zero to a losing system. The formula to calculate it is:
 

	Average R-Multiple = ( Total Win Percent – Total Loss Percent ) / Total Risk Percent
	 * 
	 * 
	 */
	private double averageR_Multiple;
	
	
	/**
	 *  Expectancy

	Van Tharp describes Expectancy only in reference to R-multiples of a hard Stop Loss, so the maximum risk can be calculated for every trade.
	This figure gives you the average R-value that you can expect from the system over many trades. In other words, Expectancy tells you how much you can expect to make on the average, per dollar risked, over a number of trades.

	You can calculate the mathematical expectation of a system by the following formula:
 

	Expectancy = (Average Profit X Win Rate) - (Average Loss X Loss Rate)
 

	This formula requires that you take into account both the Win Rate and the Payoff Ratio when estimating the long-term profit potential. For example, a system with 50% accuracy and the 2 to 1 Payoff Ratio has an expectancy equal to 0,5. This means you can expect to earn 50% of the amount that you risk per trade on average. If you risk 2% of your capital per trade you can expect to earn 1% per trade (50% of 2%) on average with such a system. The formula looks like this:
 

	Expectancy = (1+ Average Profit / Average Loss) X (Win Rate) -1
 

	Short-term traders normally achieve higher Win Rate, while long-term traders generally achieve greater Profit factors (Average Profit / Average Loss). But both strive to have a positive Expectancy.
	Negative mathematical expectation means you will lose your money over the long-term no matter how small or big your positions are. This happens for instance in a Casino where the mathematical expectation is always negative. And a zero expectation means you can expect your account to fluctuate around breakeven for ever. Ralph Vince states about this figure:

	The difference between a negative expectation and a positive one is the difference between life and death. It doesn't matter so much how positive or how negative your expectation is; what matters is whether it is positive or negative.
	 * 
	 * 
	 */
	private double expectancy;


	public StabilityStatistics() {
		super();
	}
	
	
	public void calculate(List<IbBar> bars, HashMap<Long, IbChartPoint> signalMap, 
			IbCommission commission, long volume){
		
		
		int totalTrades=0;
		int winTrades=0;
		int lossTrades=0;
		int breakEvenTrades=0;
		
		double meanProfit=0;
		double meanWins=0;
		double meanLoses=0;
		
		LinkedList<Double> allProfits=new LinkedList<Double>();
		
		IbBar previewBar=bars.get(0);
		double previewSignal=signalMap.get(previewBar.getTimeInMs()).getValue();
		
		int openPosition=0;
		
		int i=0;
		for(IbBar bar:bars){
			if(i==0){
				i++;continue;
			}
			double signal=signalMap.get(bar.getTimeInMs()).getValue();
			double diffSignal=signal-previewSignal;
			double absDiffSignal=Math.abs(diffSignal);
			
			
			//New Position
			if(absDiffSignal>0){
				
				//Close a position
				if(signal==0 || absDiffSignal==2){
					double profit=(bar.getClose()-bars.get(openPosition).getClose())*previewSignal*volume;
					
					meanProfit+=profit;
					allProfits.add(profit);
					//Win
					if(profit>0){
						winTrades++;
						meanWins+=profit;
						
					}
					
					//Loss
					else if(profit<0){
						lossTrades++;
						meanLoses+=profit;
					}
					
					//Break Even
					else{
						breakEvenTrades++;
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
		
		
		winRate=100*((double)winTrades)/totalTrades;
		lossRate=100*((double)lossTrades)/totalTrades;
		breakEvenRate=100*((double)breakEvenTrades)/totalTrades;
		WinOverLossRatio=winRate/lossRate;
		
		//Calculate the variance
		meanProfit/=totalTrades;
		double varianceProfit=0;
		for(Double p:allProfits){
			varianceProfit+=Math.pow((p-meanProfit),2);
		}
		standardDeviation=Math.sqrt(varianceProfit);
		
		//Calculate the Average Profitability Per Trade
		meanWins/=winTrades;
		meanLoses/=lossTrades;
		averageProfitability=(winRate*meanWins)-(lossRate*meanLoses);
		averageProfitability/=100.0;
		
		//Expectancy = (Average Profit X Win Rate) - (Average Loss X Loss Rate)
		expectancy=averageProfitability;
		
	}
	
	
	public Object[] getChildren(){
		
		Object[] children =new Object[9];
		
		children[0]="Win Rate, "+String.format("%1$,.2f",winRate)+"%";
		children[1]="Loss Rate, "+String.format("%1$,.2f",lossRate)+"%";
		children[2]="Break Even Rate, "+String.format("%1$,.2f",breakEvenRate)+"%";
		children[3]="Risk to Reward Ratio, "+String.format("%1$,.2f",riskToRewardRatio);
		children[4]="Win over Loss Ratio, "+String.format("%1$,.2f",WinOverLossRatio);
		children[5]="Standard Deviation, "+String.format("%1$,.2f",standardDeviation);
		children[6]="Average Profitability, "+String.format("%1$,.2f",averageProfitability);
		children[7]="Profit to Drawdown Ratio, "+String.format("%1$,.2f",profitToDrawdownRatio);
		children[8]="Expectancy, "+String.format("%1$,.2f",expectancy);
		
		return children;
		
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


	public double getWinRate() {
		return winRate;
	}


	public void setWinRate(double winRate) {
		this.winRate = winRate;
	}


	public double getLossRate() {
		return lossRate;
	}


	public void setLossRate(double lossRate) {
		this.lossRate = lossRate;
	}


	public double getBreakEvenRate() {
		return breakEvenRate;
	}


	public void setBreakEvenRate(double breakEvenRate) {
		this.breakEvenRate = breakEvenRate;
	}


	public double getRiskToRewardRatio() {
		return riskToRewardRatio;
	}


	public void setRiskToRewardRatio(double riskToRewardRatio) {
		this.riskToRewardRatio = riskToRewardRatio;
	}


	public double getWinOverLossRatio() {
		return WinOverLossRatio;
	}


	public void setWinOverLossRatio(double winOverLossRatio) {
		WinOverLossRatio = winOverLossRatio;
	}


	public double getStandardDeviation() {
		return standardDeviation;
	}


	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}


	public double getAverageProfitability() {
		return averageProfitability;
	}


	public void setAverageProfitability(double averageProfitability) {
		this.averageProfitability = averageProfitability;
	}


	public double getProfitToDrawdownRatio() {
		return profitToDrawdownRatio;
	}


	public void setProfitToDrawdownRatio(double profitToDrawdownRatio) {
		this.profitToDrawdownRatio = profitToDrawdownRatio;
	}


	public double getSharpeRatio() {
		return sharpeRatio;
	}


	public void setSharpeRatio(double sharpeRatio) {
		this.sharpeRatio = sharpeRatio;
	}


	public double getCalmarRatio() {
		return calmarRatio;
	}


	public void setCalmarRatio(double calmarRatio) {
		this.calmarRatio = calmarRatio;
	}


	public double getR_Multiple() {
		return R_Multiple;
	}


	public void setR_Multiple(double r_Multiple) {
		R_Multiple = r_Multiple;
	}


	public double getAverageR_Multiple() {
		return averageR_Multiple;
	}


	public void setAverageR_Multiple(double averageR_Multiple) {
		this.averageR_Multiple = averageR_Multiple;
	}


	public double getExpectancy() {
		return expectancy;
	}


	public void setExpectancy(double expectancy) {
		this.expectancy = expectancy;
	}


	
	
	@Override
	public String toString() {
		return "StabilityStatistics [winRate=" + winRate + ", lossRate="
				+ lossRate + ", breakEvenRate=" + breakEvenRate
				+ ", riskToRewardRatio=" + riskToRewardRatio
				+ ", WinOverLossRatio=" + WinOverLossRatio
				+ ", standardDeviation=" + standardDeviation
				+ ", averageProfitability=" + averageProfitability
				+ ", profitToDrawdownRatio=" + profitToDrawdownRatio
				+ ", sharpeRatio=" + sharpeRatio + ", calmarRatio="
				+ calmarRatio + ", R_Multiple=" + R_Multiple
				+ ", averageR_Multiple=" + averageR_Multiple + ", expectancy="
				+ expectancy + "]";
	}
	
	
	
	
	
}
