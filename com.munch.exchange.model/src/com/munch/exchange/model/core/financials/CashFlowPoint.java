package com.munch.exchange.model.core.financials;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;

public class CashFlowPoint extends FinancialPoint {
	

	public static final String FIELD_NetIncome = "NetIncome";
	public static final String FIELD_Depreciation = "Depreciation";
	public static final String FIELD_AdjustmentsToNetIncome = "AdjustmentsToNetIncome";
	public static final String FIELD_ChangesInAccountsReceivables = "ChangesInAccountsReceivables";
	public static final String FIELD_ChangesInLiabilities = "ChangesInLiabilities";
	public static final String FIELD_ChangesInInventories = "ChangesInInventories";
	public static final String FIELD_ChangesInOtherOperatingActivities = "ChangesInOtherOperatingActivities";
	public static final String FIELD_TotalCashFlowFromOperatingActivities = "TotalCashFlowFromOperatingActivities";
	public static final String FIELD_CapitalExpenditures = "CapitalExpenditures";
	public static final String FIELD_Investments = "Investments";
	public static final String FIELD_OtherCashflowsfromInvestingActivities = "OtherCashflowsfromInvestingActivities";
	public static final String FIELD_TotalCashFlowsFromInvestingActivities = "TotalCashFlowsFromInvestingActivities";
	public static final String FIELD_DividendsPaid = "DividendsPaid";
	public static final String FIELD_SalePurchaseofStock = "SalePurchaseofStock";
	public static final String FIELD_NetBorrowings = "NetBorrowings";
	public static final String FIELD_OtherCashFlowsfromFinancingActivities = "OtherCashFlowsfromFinancingActivities";
	public static final String FIELD_TotalCashFlowsFromFinancingActivities = "TotalCashFlowsFromFinancingActivities";
	public static final String FIELD_EffectOfExchangeRateChanges = "EffectOfExchangeRateChanges";
	public static final String FIELD_ChangeInCashandCashEquivalents = "ChangeInCashandCashEquivalents";
	
	
	
	//==  Net Income
	// 1
	private long NetIncome;
	
	
	//==================================================================
	//==      Operating Activities, Cash Flows Provided By or Used In == (Betriebliche Tätigkeit)
	//==================================================================

	// 2
	private long Depreciation;
	// 3
	private long AdjustmentsToNetIncome;
	// 4
	private long ChangesInAccountsReceivables;
	// 5
	private long ChangesInLiabilities;
	// 6
	private long ChangesInInventories;
	// 7
	private long ChangesInOtherOperatingActivities;
	//==  Total Cash Flow From Operating Activities 
	// 8
	private long TotalCashFlowFromOperatingActivities;
	
	//==================================================================
	//==      Investing Activities, Cash Flows Provided By or Used In == (Investitionstätigkeit)
	//==================================================================

	// 9
	private long CapitalExpenditures;
	// 10
	private long Investments;
	// 11
	private long OtherCashflowsfromInvestingActivities;
	//==  Total Cash Flows From Investing Activities
	// 12
	private long TotalCashFlowsFromInvestingActivities;
	
	
	//==================================================================
	//==      Financing Activities, Cash Flows Provided By or Used In == (Finanzierungstätigkeit)
	//==================================================================
	// 13
	private long DividendsPaid;
	// 14
	private long SalePurchaseofStock;
	// 15
	private long NetBorrowings;
	// 16
	private long OtherCashFlowsfromFinancingActivities;
	//==  Total Cash Flows From Financing Activities 
	// 17
	private long TotalCashFlowsFromFinancingActivities;
	
	
	// 18
	private long EffectOfExchangeRateChanges;
	
	//==  Change In Cash and Cash Equivalents 
	// 19
	private long ChangeInCashandCashEquivalents;
	
	
	
	//=======================
	//== GETTER AND SETTER ==
	//=======================
	

	public long getNetIncome() {
		return NetIncome;
	}

	public void setNetIncome(long netIncome) {
		changes.firePropertyChange(FIELD_NetIncome, this.NetIncome,
				this.NetIncome = netIncome);
	}

	public long getDepreciation() {
		return Depreciation;
	}

	public void setDepreciation(long depreciation) {
		changes.firePropertyChange(FIELD_Depreciation, this.Depreciation,
				this.Depreciation = depreciation);
	}
	

	public long getAdjustmentsToNetIncome() {
		return AdjustmentsToNetIncome;
	}

	public void setAdjustmentsToNetIncome(long adjustmentsToNetIncome) {
		changes.firePropertyChange(FIELD_AdjustmentsToNetIncome,
				this.AdjustmentsToNetIncome,
				this.AdjustmentsToNetIncome = adjustmentsToNetIncome);
	}

	public long getChangesInAccountsReceivables() {
		return ChangesInAccountsReceivables;
	}

	public void setChangesInAccountsReceivables(long changesInAccountsReceivables) {
		changes.firePropertyChange(FIELD_ChangesInAccountsReceivables,
				this.ChangesInAccountsReceivables,
				this.ChangesInAccountsReceivables = changesInAccountsReceivables);
	}

	public long getChangesInLiabilities() {
		return ChangesInLiabilities;
	}

	public void setChangesInLiabilities(long changesInLiabilities) {
		changes.firePropertyChange(FIELD_ChangesInLiabilities,
				this.ChangesInLiabilities,
				this.ChangesInLiabilities = changesInLiabilities);
	}

	public long getChangesInInventories() {
		return ChangesInInventories;
	}

	public void setChangesInInventories(long changesInInventories) {
		changes.firePropertyChange(FIELD_ChangesInInventories,
				this.ChangesInInventories,
				this.ChangesInInventories = changesInInventories);
	}

	public long getChangesInOtherOperatingActivities() {
		return ChangesInOtherOperatingActivities;
	}

	public void setChangesInOtherOperatingActivities(
			long changesInOtherOperatingActivities) {
		changes.firePropertyChange(
				FIELD_ChangesInOtherOperatingActivities,
				this.ChangesInOtherOperatingActivities,
				this.ChangesInOtherOperatingActivities = changesInOtherOperatingActivities);
	}

	public long getTotalCashFlowFromOperatingActivities() {
		return TotalCashFlowFromOperatingActivities;
	}

	public void setTotalCashFlowFromOperatingActivities(
			long totalCashFlowFromOperatingActivities) {
		changes.firePropertyChange(
				FIELD_TotalCashFlowFromOperatingActivities,
				this.TotalCashFlowFromOperatingActivities,
				this.TotalCashFlowFromOperatingActivities = totalCashFlowFromOperatingActivities);
	}

	public long getCapitalExpenditures() {
		return CapitalExpenditures;
	}

	public void setCapitalExpenditures(long capitalExpenditures) {
		changes.firePropertyChange(FIELD_CapitalExpenditures,
				this.CapitalExpenditures,
				this.CapitalExpenditures = capitalExpenditures);
	}

	public long getInvestments() {
		return Investments;
	}

	public void setInvestments(long investments) {
		changes.firePropertyChange(FIELD_Investments, this.Investments,
				this.Investments = investments);
	}

	public long getOtherCashflowsfromInvestingActivities() {
		return OtherCashflowsfromInvestingActivities;
	}

	public void setOtherCashflowsfromInvestingActivities(
			long otherCashflowsfromInvestingActivities) {
		changes.firePropertyChange(
				FIELD_OtherCashflowsfromInvestingActivities,
				this.OtherCashflowsfromInvestingActivities,
				this.OtherCashflowsfromInvestingActivities = otherCashflowsfromInvestingActivities);
	}

	public long getTotalCashFlowsFromInvestingActivities() {
		return TotalCashFlowsFromInvestingActivities;
	}

	public void setTotalCashFlowsFromInvestingActivities(
			long totalCashFlowsFromInvestingActivities) {
		changes.firePropertyChange(
				FIELD_TotalCashFlowsFromInvestingActivities,
				this.TotalCashFlowsFromInvestingActivities,
				this.TotalCashFlowsFromInvestingActivities = totalCashFlowsFromInvestingActivities);
	}

	public long getDividendsPaid() {
		return DividendsPaid;
	}

	public void setDividendsPaid(long dividendsPaid) {
		changes.firePropertyChange(FIELD_DividendsPaid, this.DividendsPaid,
				this.DividendsPaid = dividendsPaid);
	}

	public long getSalePurchaseofStock() {
		return SalePurchaseofStock;
	}

	public void setSalePurchaseofStock(long salePurchaseofStock) {
		changes.firePropertyChange(FIELD_SalePurchaseofStock,
				this.SalePurchaseofStock,
				this.SalePurchaseofStock = salePurchaseofStock);
	}

	public long getNetBorrowings() {
		return NetBorrowings;
	}

	public void setNetBorrowings(long netBorrowings) {
		changes.firePropertyChange(FIELD_NetBorrowings, this.NetBorrowings,
				this.NetBorrowings = netBorrowings);
	}

	public long getOtherCashFlowsfromFinancingActivities() {
		return OtherCashFlowsfromFinancingActivities;
	}

	public void setOtherCashFlowsfromFinancingActivities(
			long otherCashFlowsfromFinancingActivities) {
		changes.firePropertyChange(
				FIELD_OtherCashFlowsfromFinancingActivities,
				this.OtherCashFlowsfromFinancingActivities,
				this.OtherCashFlowsfromFinancingActivities = otherCashFlowsfromFinancingActivities);
	}

	public long getTotalCashFlowsFromFinancingActivities() {
		return TotalCashFlowsFromFinancingActivities;
	}

	public void setTotalCashFlowsFromFinancingActivities(
			long totalCashFlowsFromFinancingActivities) {
		changes.firePropertyChange(
				FIELD_TotalCashFlowsFromFinancingActivities,
				this.TotalCashFlowsFromFinancingActivities,
				this.TotalCashFlowsFromFinancingActivities = totalCashFlowsFromFinancingActivities);
	}

	public long getEffectOfExchangeRateChanges() {
		return EffectOfExchangeRateChanges;
	}

	public void setEffectOfExchangeRateChanges(long effectOfExchangeRateChanges) {
		changes.firePropertyChange(FIELD_EffectOfExchangeRateChanges,
				this.EffectOfExchangeRateChanges,
				this.EffectOfExchangeRateChanges = effectOfExchangeRateChanges);
	}

	public long getChangeInCashandCashEquivalents() {
		return ChangeInCashandCashEquivalents;
	}

	public void setChangeInCashandCashEquivalents(
			long changeInCashandCashEquivalents) {
		changes.firePropertyChange(
				FIELD_ChangeInCashandCashEquivalents,
				this.ChangeInCashandCashEquivalents,
				this.ChangeInCashandCashEquivalents = changeInCashandCashEquivalents);
	}

	

	@Override
	public String toString() {
		
		return "BalanceSheetPoint [ PeriodEnding="+DateTool.dateToString(PeriodEnding) 
				+ ", type=" + this.getPeriodType()
				+ ", NetIncome="+ this.LongValueToString(NetIncome )
				+ ", Depreciation="+ this.LongValueToString(Depreciation )
				+ ", AdjustmentsToNetIncome=" + this.LongValueToString(AdjustmentsToNetIncome)
				+ ", ChangesInAccountsReceivables=" + this.LongValueToString(ChangesInAccountsReceivables )
				+ ", ChangesInLiabilities="+ this.LongValueToString(ChangesInLiabilities )
				+ ", ChangesInInventories="+ this.LongValueToString(ChangesInInventories )
				+ ", ChangesInOtherOperatingActivities="+ this.LongValueToString(ChangesInOtherOperatingActivities )
				+ ", TotalCashFlowFromOperatingActivities="+ this.LongValueToString(TotalCashFlowFromOperatingActivities )
				+ ", CapitalExpenditures=" + this.LongValueToString(CapitalExpenditures)
				+ ", Investments=" + this.LongValueToString(Investments)
				+ ", OtherCashflowsfromInvestingActivities=" + this.LongValueToString(OtherCashflowsfromInvestingActivities)
				+ ", TotalCashFlowsFromInvestingActivities=" +this.LongValueToString( TotalCashFlowsFromInvestingActivities)
				+ ", DividendsPaid="+this.LongValueToString( DividendsPaid)
				+ ", SalePurchaseofStock=" +this.LongValueToString( SalePurchaseofStock)
				+ ", NetBorrowings=" + this.LongValueToString(NetBorrowings)
				+ ", OtherCashFlowsfromFinancingActivities=" + this.LongValueToString(OtherCashFlowsfromFinancingActivities)
				+ ", TotalCashFlowsFromFinancingActivities=" + this.LongValueToString(TotalCashFlowsFromFinancingActivities)
				+ ", EffectOfExchangeRateChanges=" +this.LongValueToString( EffectOfExchangeRateChanges)
				+ ", ChangeInCashandCashEquivalents=" + this.LongValueToString(ChangeInCashandCashEquivalents )
				+ "]";
	}


	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setNetIncome(Long.parseLong(rootElement.getAttribute(FIELD_NetIncome)));
		this.setDepreciation(Long.parseLong(rootElement.getAttribute(FIELD_Depreciation)));
		this.setAdjustmentsToNetIncome(Long.parseLong(rootElement.getAttribute(FIELD_AdjustmentsToNetIncome)));
		this.setChangesInAccountsReceivables(Long.parseLong(rootElement.getAttribute(FIELD_ChangesInAccountsReceivables)));
		this.setChangesInLiabilities(Long.parseLong(rootElement.getAttribute(FIELD_ChangesInLiabilities)));
		this.setChangesInInventories(Long.parseLong(rootElement.getAttribute(FIELD_ChangesInInventories)));
		this.setChangesInOtherOperatingActivities(Long.parseLong(rootElement.getAttribute(FIELD_ChangesInOtherOperatingActivities)));
		this.setTotalCashFlowFromOperatingActivities(Long.parseLong(rootElement.getAttribute(FIELD_TotalCashFlowFromOperatingActivities)));
		this.setCapitalExpenditures(Long.parseLong(rootElement.getAttribute(FIELD_CapitalExpenditures)));
		this.setInvestments(Long.parseLong(rootElement.getAttribute(FIELD_Investments)));
		this.setOtherCashflowsfromInvestingActivities(Long.parseLong(rootElement.getAttribute(FIELD_OtherCashflowsfromInvestingActivities)));
		this.setTotalCashFlowsFromInvestingActivities(Long.parseLong(rootElement.getAttribute(FIELD_TotalCashFlowsFromInvestingActivities)));
		this.setDividendsPaid(Long.parseLong(rootElement.getAttribute(FIELD_DividendsPaid)));
		this.setSalePurchaseofStock(Long.parseLong(rootElement.getAttribute(FIELD_SalePurchaseofStock)));
		this.setNetBorrowings(Long.parseLong(rootElement.getAttribute(FIELD_NetBorrowings)));
		this.setOtherCashFlowsfromFinancingActivities(Long.parseLong(rootElement.getAttribute(FIELD_OtherCashFlowsfromFinancingActivities)));
		this.setTotalCashFlowsFromFinancingActivities(Long.parseLong(rootElement.getAttribute(FIELD_TotalCashFlowsFromFinancingActivities)));
		this.setEffectOfExchangeRateChanges(Long.parseLong(rootElement.getAttribute(FIELD_EffectOfExchangeRateChanges)));
		this.setChangeInCashandCashEquivalents(Long.parseLong(rootElement.getAttribute(FIELD_ChangeInCashandCashEquivalents)));
		
		
		super.initAttribute(rootElement);
	}

	@Override
	protected void setAttribute(Element rootElement) {
		
		
		rootElement.setAttribute(FIELD_NetIncome,String.valueOf(this.getNetIncome()));
		rootElement.setAttribute(FIELD_Depreciation,String.valueOf(this.getDepreciation()));
		rootElement.setAttribute(FIELD_AdjustmentsToNetIncome,String.valueOf(this.getAdjustmentsToNetIncome()));
		rootElement.setAttribute(FIELD_ChangesInAccountsReceivables,String.valueOf(this.getChangesInAccountsReceivables()));
		rootElement.setAttribute(FIELD_ChangesInLiabilities,String.valueOf(this.getChangesInLiabilities()));
		rootElement.setAttribute(FIELD_ChangesInInventories,String.valueOf(this.getChangesInInventories()));
		rootElement.setAttribute(FIELD_ChangesInOtherOperatingActivities,String.valueOf(this.getChangesInOtherOperatingActivities()));
		rootElement.setAttribute(FIELD_TotalCashFlowFromOperatingActivities,String.valueOf(this.getTotalCashFlowFromOperatingActivities()));
		rootElement.setAttribute(FIELD_CapitalExpenditures,String.valueOf(this.getCapitalExpenditures()));
		rootElement.setAttribute(FIELD_Investments,String.valueOf(this.getInvestments()));
		rootElement.setAttribute(FIELD_OtherCashflowsfromInvestingActivities,String.valueOf(this.getOtherCashflowsfromInvestingActivities()));
		rootElement.setAttribute(FIELD_TotalCashFlowsFromInvestingActivities,String.valueOf(this.getTotalCashFlowsFromInvestingActivities()));
		rootElement.setAttribute(FIELD_DividendsPaid,String.valueOf(this.getDividendsPaid()));
		rootElement.setAttribute(FIELD_SalePurchaseofStock,String.valueOf(this.getSalePurchaseofStock()));
		rootElement.setAttribute(FIELD_NetBorrowings,String.valueOf(this.getNetBorrowings()));
		rootElement.setAttribute(FIELD_OtherCashFlowsfromFinancingActivities,String.valueOf(this.getOtherCashFlowsfromFinancingActivities()));
		rootElement.setAttribute(FIELD_TotalCashFlowsFromFinancingActivities,String.valueOf(this.getTotalCashFlowsFromFinancingActivities()));
		rootElement.setAttribute(FIELD_EffectOfExchangeRateChanges,String.valueOf(this.getEffectOfExchangeRateChanges()));
		rootElement.setAttribute(FIELD_ChangeInCashandCashEquivalents,String.valueOf(this.getChangeInCashandCashEquivalents()));
		
		super.setAttribute(rootElement);
	}

	@Override
	public long getValue(String fieldKey) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setValue(String fieldKey, long value) {
		// TODO Auto-generated method stub
		
	}

}
