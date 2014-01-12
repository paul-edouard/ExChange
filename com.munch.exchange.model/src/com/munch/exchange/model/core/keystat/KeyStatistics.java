package com.munch.exchange.model.core.keystat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class KeyStatistics extends XmlParameterElement {
	
	
	//========================
	//== Valuation Measures ==
	//========================
	
	public static final String FIELD_MarketCap="MarketCap";
	public static final String FIELD_EnterpriseValue ="EnterpriseValue";
	public static final String FIELD_TrailingP_E ="TrailingPE";
	public static final String FIELD_ForwardP_E ="ForwardPE";
	public static final String FIELD_PEGRatio ="PEGRatio";
	public static final String FIELD_Price_Sales  ="PriceSales";
	public static final String FIELD_Price_Book  ="PriceBook";
	public static final String FIELD_EnterpriseValue_Revenue  ="EnterpriseValueRevenue";
	public static final String FIELD_EnterpriseValue_EBITDA  ="EnterpriseValueEBITDA";
	
	//==========================
	//== Financial Highlights ==
	//==========================
	
	//Fiscal Year
	public static final String FIELD_FiscalYearEnds="FiscalYearEnds";
	public static final String FIELD_MostRecentQuarter="MostRecentQuarter";
	//Profitability
	public static final String FIELD_ProfitMargin ="ProfitMargin";
	public static final String FIELD_OperatingMargin ="OperatingMargin";
	//Management Effectiveness
	public static final String FIELD_ReturnOnAssets ="ReturnonAssets";
	public static final String FIELD_ReturnOnEquity ="ReturnonEquity";
	//Income Statement
	public static final String FIELD_Revenue="Revenue";
	public static final String FIELD_RevenuePerShare ="RevenuePerShare";
	public static final String FIELD_QtrlyRevenueGrowth ="QtrlyRevenueGrowth";
	public static final String FIELD_GrossProfit ="GrossProfit";
	public static final String FIELD_EBITDA="EBITDA";
	public static final String FIELD_NetIncomeAvlToCommon ="NetIncomeAvltoCommon";
	public static final String FIELD_DilutedEPS ="DilutedEPS";
	public static final String FIELD_QtrlyEarningsGrowth ="QtrlyEarningsGrowth";
	//Balance Sheet
	public static final String FIELD_TotalCash ="TotalCash";
	public static final String FIELD_TotalCashPerShare ="TotalCashPerShare";
	public static final String FIELD_TotalDebt ="TotalDebt";
	public static final String FIELD_TotalDebt_Equity ="TotalDebtEquity";
	public static final String FIELD_CurrentRatio="CurrentRatio";
	public static final String FIELD_BookValuePerShare ="BookValuePerShare";
	//Cash Flow Statement
	public static final String FIELD_OperatingCashFlow="OperatingCashFlow";
	public static final String FIELD_LeveredFreeCashFlow ="LeveredFreeCashFlow";
	
	//=========================
	//== Trading Information ==
	//=========================
	
	//Stock Price History
	public static final String FIELD_Beta="Beta";
	public static final String FIELD_52WeekChange="p_52_WeekChange";
	public static final String FIELD_SP50052_WeekChange="SP50052_WeekChange";
	public static final String FIELD_52WeekHigh ="p_52_WeekHigh";
	public static final String FIELD_52WeekLow ="p_52_WeekLow";
	public static final String FIELD_50DayMovingAverage="p_50_DayMovingAverage";
	public static final String FIELD_200DayMovingAverage="p_200_DayMovingAverage";
	//Share Statistics
	public static final String FIELD_AvgVol1 ="AvgVol_1";
	public static final String FIELD_AvgVol2 ="AvgVol_2";
	public static final String FIELD_SharesOutstanding="SharesOutstanding";
	public static final String FIELD_Float="Float";
	public static final String FIELD_PercHeldbyInsiders="PercentageHeldbyInsiders";
	public static final String FIELD_PerHeldbyInstitutions="PercentageHeldbyInstitutions";
	public static final String FIELD_SharesShort1="SharesShort_1";
	public static final String FIELD_SharesShort2 ="SharesShort_2";
	public static final String FIELD_ShortRatio="ShortRatio";
	public static final String FIELD_ShortPercOfFloat ="ShortPercentageofFloat";
	//Dividends & Splits
	public static final String FIELD_ForwardAnnualDividendRate="ForwardAnnualDividendRate";
	public static final String FIELD_ForwardAnnualDividendYield="ForwardAnnualDividendYield";
	public static final String FIELD_TrailingAnnualDividendYield1="TrailingAnnualDividendYield_1";
	public static final String FIELD_TrailingAnnualDividendYield2="TrailingAnnualDividendYield_2";
	public static final String FIELD_5YearAverageDividendYield="p_5YearAverageDividendYield";
	public static final String FIELD_PayoutRatio="PayoutRatio";
	public static final String FIELD_DividendDate="DividendDate";
	public static final String FIELD_ExDividendDate="Ex_DividendDate";
	public static final String FIELD_LastSplitFactor="LastSplitFactor";
	public static final String FIELD_LastSplitDate="LastSplitDate";

	
	
	static final String FIELD_ValuationMeasures="ValuationMeasures";
	static final String FIELD_FinancialHighlights="FinancialHighlights";
	static final String FIELD_TradingInformation="TradingInformation";
	
	
	protected KeyStatMap ValuationMeasures=new KeyStatMap(FIELD_ValuationMeasures);
	protected KeyStatMap FinancialHighlights=new KeyStatMap(FIELD_FinancialHighlights);
	protected KeyStatMap TradingInformation=new KeyStatMap(FIELD_TradingInformation);
	
	
	public void putContent(String key, ContentAndTerm ct){
		
		
		if(		key.equals(FIELD_MarketCap) ||
				key.equals(FIELD_EnterpriseValue) ||
				key.equals(FIELD_TrailingP_E) ||
				key.equals(FIELD_ForwardP_E) ||
				key.equals(FIELD_PEGRatio) ||
				key.equals(FIELD_Price_Sales) ||
				key.equals(FIELD_Price_Book) ||
				key.equals(FIELD_EnterpriseValue_Revenue) ||
				key.equals(FIELD_EnterpriseValue_EBITDA)){
			ValuationMeasures.put(key, ct);
		}	
		else if(key.equals(FIELD_FiscalYearEnds) ||
				key.equals(FIELD_MostRecentQuarter) ||
				
				key.equals(FIELD_ProfitMargin) ||
				key.equals(FIELD_OperatingMargin) ||
				
				key.equals(FIELD_ReturnOnAssets) ||
				key.equals(FIELD_ReturnOnEquity) ||
				
				key.equals(FIELD_Revenue) ||
				key.equals(FIELD_RevenuePerShare) ||
				key.equals(FIELD_QtrlyRevenueGrowth) ||
				key.equals(FIELD_GrossProfit) ||
				key.equals(FIELD_EBITDA) ||
				key.equals(FIELD_NetIncomeAvlToCommon) ||
				key.equals(FIELD_DilutedEPS) ||
				key.equals(FIELD_QtrlyEarningsGrowth) ||
				
				key.equals(FIELD_TotalCash) ||
				key.equals(FIELD_TotalCashPerShare) ||
				key.equals(FIELD_TotalDebt) ||
				key.equals(FIELD_TotalDebt_Equity) ||
				key.equals(FIELD_CurrentRatio) ||
				key.equals(FIELD_BookValuePerShare) ||
				
				key.equals(FIELD_OperatingCashFlow) ||
				key.equals(FIELD_LeveredFreeCashFlow)
				){
			FinancialHighlights.put(key, ct);
		}
		
		
		else if(key.equals(FIELD_Beta) ||
				key.equals(FIELD_52WeekChange) ||
				key.equals(FIELD_SP50052_WeekChange) ||
				key.equals(FIELD_52WeekHigh) ||
				key.equals(FIELD_52WeekLow) ||
				key.equals(FIELD_50DayMovingAverage) ||
				key.equals(FIELD_200DayMovingAverage) ||
				
				key.equals(FIELD_SharesOutstanding) ||
				key.equals(FIELD_Float) ||
				key.equals(FIELD_PercHeldbyInsiders) ||
				key.equals(FIELD_PerHeldbyInstitutions) ||
				key.equals(FIELD_ShortRatio) ||
				key.equals(FIELD_ShortPercOfFloat) ||
				
				key.equals(FIELD_ForwardAnnualDividendRate) ||
				key.equals(FIELD_ForwardAnnualDividendYield) ||
				key.equals(FIELD_5YearAverageDividendYield) ||
				key.equals(FIELD_PayoutRatio) ||
				key.equals(FIELD_DividendDate) ||
				key.equals(FIELD_ExDividendDate) ||
				key.equals(FIELD_LastSplitFactor) ||
				key.equals(FIELD_LastSplitDate) 
				
				){
			TradingInformation.put(key, ct);
		}
		else if(key.equals("AvgVol")){
			if(TradingInformation.containsKey(FIELD_AvgVol1)){
				TradingInformation.put(FIELD_AvgVol2, ct);
			}
			else{
				TradingInformation.put(FIELD_AvgVol1, ct);
			}
		}
		else if(key.equals("SharesShort")){
			if(TradingInformation.containsKey(FIELD_SharesShort1)){
				TradingInformation.put(FIELD_SharesShort2, ct);
			}
			else{
				TradingInformation.put(FIELD_SharesShort1, ct);
			}
		}
		else if(key.equals("TrailingAnnualDividendYield")){
			if(TradingInformation.containsKey(FIELD_TrailingAnnualDividendYield1)){
				TradingInformation.put(FIELD_TrailingAnnualDividendYield2, ct);
			}
			else{
				TradingInformation.put(FIELD_TrailingAnnualDividendYield1, ct);
			}
		}
		
		
	}
	

	public KeyStatMap getValuationMeasures() {
		return ValuationMeasures;
	}

	public void setValuationMeasures(KeyStatMap valuationMeasures) {
		changes.firePropertyChange(FIELD_ValuationMeasures, this.ValuationMeasures,
				this.ValuationMeasures = valuationMeasures);
	}

	public KeyStatMap getFinancialHighlights() {
		return FinancialHighlights;
	}

	public void setFinancialHighlights(KeyStatMap financialHighlights) {
		changes.firePropertyChange(FIELD_FinancialHighlights,
				this.FinancialHighlights,
				this.FinancialHighlights = financialHighlights);
	}

	public KeyStatMap getTradingInformation() {
		return TradingInformation;
	}

	public void setTradingInformation(KeyStatMap tradingInformation) {
		changes.firePropertyChange(FIELD_TradingInformation,
				this.TradingInformation,
				this.TradingInformation = tradingInformation);
	}
	
	
	
	@Override
	public String toString() {
		return "KeyStatistics [\n\tValuationMeasures=" + ValuationMeasures
				+ "\n\tFinancialHighlights=" + FinancialHighlights
				+ "\n\tTradingInformation=" + TradingInformation + "\n]";
	}


	@Override
	protected void initAttribute(Element rootElement) {
		// TODO Auto-generated method stub
		

	}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_ValuationMeasures)){
			this.setValuationMeasures(new KeyStatMap(FIELD_ValuationMeasures,childElement));
		}
		if(childElement.getTagName().equals(FIELD_FinancialHighlights)){
			this.setValuationMeasures(new KeyStatMap(FIELD_FinancialHighlights,childElement));
		}
		if(childElement.getTagName().equals(FIELD_TradingInformation)){
			this.setValuationMeasures(new KeyStatMap(FIELD_TradingInformation,childElement));
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		rootElement.appendChild(this.getFinancialHighlights().toDomElement(doc));
		rootElement.appendChild(this.getTradingInformation().toDomElement(doc));
		rootElement.appendChild(this.getValuationMeasures().toDomElement(doc));

	}

}
