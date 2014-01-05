package com.munch.exchange.model.core.financials;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;

public class BalanceSheetPoint extends FinancialPoint {
	
	
	public static final String FIELD_CashAndCashEquivalents = "CashAndCashEquivalents";
	public static final String FIELD_ShortTermInvestments = "ShortTermInvestments";
	public static final String FIELD_NetReceivables = "NetReceivables";
	public static final String FIELD_Inventory = "Inventory";
	public static final String FIELD_OtherCurrentAssets = "OtherCurrentAssets";
	public static final String FIELD_TotalCurrentAssets = "TotalCurrentAssets";
	public static final String FIELD_LongTermInvestments = "LongTermInvestments";
	public static final String FIELD_PropertyPlantandEquipment = "PropertyPlantandEquipment";
	public static final String FIELD_Goodwill = "Goodwill";
	public static final String FIELD_IntangibleAssets = "IntangibleAssets";
	public static final String FIELD_AccumulatedAmortization = "AccumulatedAmortization";
	public static final String FIELD_OtherAssets = "OtherAssets";
	public static final String FIELD_DeferredLongTermAssetCharges = "DeferredLongTermAssetCharges";
	public static final String FIELD_TotalAssets = "TotalAssets";
	public static final String FIELD_AccountsPayable = "AccountsPayable";
	public static final String FIELD_Short_CurrentLongTermDebt = "Short_CurrentLongTermDebt";
	public static final String FIELD_OtherCurrentLiabilities = "OtherCurrentLiabilities";
	public static final String FIELD_TotalCurrentLiabilities = "TotalCurrentLiabilities";
	public static final String FIELD_LongTermDebt = "LongTermDebt";
	public static final String FIELD_OtherLiabilities = "OtherLiabilities";
	public static final String FIELD_DeferredLongTermLiabilityCharges = "DeferredLongTermLiabilityCharges";
	public static final String FIELD_MinorityInterest = "MinorityInterest";
	public static final String FIELD_NegativeGoodwill = "NegativeGoodwill";
	public static final String FIELD_TotalLiabilities = "TotalLiabilities";
	public static final String FIELD_MiscStocksOptionsWarrants = "MiscStocksOptionsWarrants";
	public static final String FIELD_RedeemablePreferredStock = "RedeemablePreferredStock";
	public static final String FIELD_PreferredStock = "PreferredStock";
	public static final String FIELD_CommonStock = "CommonStock";
	public static final String FIELD_RetainedEarnings = "RetainedEarnings";
	public static final String FIELD_TreasuryStock = "TreasuryStock";
	public static final String FIELD_CapitalSurplus = "CapitalSurplus";
	public static final String FIELD_OtherStockholderEquity = "OtherStockholderEquity";
	public static final String FIELD_TotalStockholderEquity = "TotalStockholderEquity";
	public static final String FIELD_NetTangibleAssets = "NetTangibleAssets";
	
	//========================
	//==       Assets       == (Anlagevermögen)
	//========================
	
	//==  Current Assets
	// 1
	private long CashAndCashEquivalents;
	// 2
	private long ShortTermInvestments;
	// 3
	private long NetReceivables;

	// 4
	private long Inventory;
	// 5
	private long OtherCurrentAssets;
	//==  Total Current Assets
	// 6
	private long TotalCurrentAssets;
	
	// 7
	private long LongTermInvestments;
	// 8
	private long PropertyPlantandEquipment;
	// 9
	private long Goodwill;
	// 10
	private long IntangibleAssets;
	// 11
	private long AccumulatedAmortization;
	// 12
	private long OtherAssets;
	// 13
	private long DeferredLongTermAssetCharges;
	//==  Total Assets
	// 14
	private long TotalAssets;
	
	
	//========================
	//==    Liabilities     == (Verbindlichkeit)
	//========================

	//==  Current Liabilities
	// 15
	private long AccountsPayable;
	// 16
	private long Short_CurrentLongTermDebt;
	// 17
	private long OtherCurrentLiabilities;
	//==  Total Current Liabilities
	// 18
	private long TotalCurrentLiabilities;
	
	// 19
	private long LongTermDebt;
	// 20
	private long OtherLiabilities;
	// 21
	private long DeferredLongTermLiabilityCharges;
	// 22
	private long MinorityInterest;
	// 23
	private long NegativeGoodwill;
	//==  Total Liabilities
	// 24
	private long TotalLiabilities;
	
	
	//=========================
	//== Stockholders Equity == (Aktionärskapital)
	//=========================
	
	// 25
	private long MiscStocksOptionsWarrants;
	// 26
	private long RedeemablePreferredStock;
	// 27
	private long PreferredStock;
	// 28
	private long CommonStock;
	// 29
	private long RetainedEarnings;
	// 30
	private long TreasuryStock;
	// 31
	private long CapitalSurplus;
	// 32
	private long OtherStockholderEquity;
	//==  Total Stockholder Equity
	// 33
	private long TotalStockholderEquity;
	
	// 34
	private long NetTangibleAssets;

	
	
	//=======================
	//== GETTER AND SETTER ==
	//=======================
	

	public long getCashAndCashEquivalents() {
		return CashAndCashEquivalents;
	}

	public void setCashAndCashEquivalents(long cashAndCashEquivalents) {
		changes.firePropertyChange(FIELD_CashAndCashEquivalents,
				CashAndCashEquivalents,
				CashAndCashEquivalents = cashAndCashEquivalents);
	}

	public long getShortTermInvestments() {
		return ShortTermInvestments;
	}

	public void setShortTermInvestments(long shortTermInvestments) {
		changes.firePropertyChange(FIELD_ShortTermInvestments,
				ShortTermInvestments, ShortTermInvestments = shortTermInvestments);
	}

	public long getNetReceivables() {
		return NetReceivables;
	}

	public void setNetReceivables(long netReceivables) {
		changes.firePropertyChange(FIELD_NetReceivables, NetReceivables,
				NetReceivables = netReceivables);
	}

	public long getInventory() {
		return Inventory;
	}

	public void setInventory(long inventory) {
		changes.firePropertyChange(FIELD_Inventory, Inventory,
				Inventory = inventory);
	}

	public long getOtherCurrentAssets() {
		return OtherCurrentAssets;
	}

	public void setOtherCurrentAssets(long otherCurrentAssets) {
		changes.firePropertyChange(FIELD_OtherCurrentAssets, OtherCurrentAssets,
				OtherCurrentAssets = otherCurrentAssets);
	}

	public long getTotalCurrentAssets() {
		return TotalCurrentAssets;
	}

	public void setTotalCurrentAssets(long totalCurrentAssets) {
		changes.firePropertyChange(FIELD_TotalCurrentAssets, TotalCurrentAssets,
				TotalCurrentAssets = totalCurrentAssets);
	}

	public long getLongTermInvestments() {
		return LongTermInvestments;
	}

	public void setLongTermInvestments(long longTermInvestments) {
		changes.firePropertyChange(FIELD_LongTermInvestments, LongTermInvestments,
				LongTermInvestments = longTermInvestments);
	}

	public long getPropertyPlantandEquipment() {
		return PropertyPlantandEquipment;
	}

	public void setPropertyPlantandEquipment(long propertyPlantandEquipment) {
		changes.firePropertyChange(FIELD_PropertyPlantandEquipment,
				PropertyPlantandEquipment,
				PropertyPlantandEquipment = propertyPlantandEquipment);
	}

	public long getGoodwill() {
		return Goodwill;
	}

	public void setGoodwill(long goodwill) {
		changes.firePropertyChange(FIELD_Goodwill, Goodwill, Goodwill = goodwill);
	}

	public long getIntangibleAssets() {
		return IntangibleAssets;
	}

	public void setIntangibleAssets(long intangibleAssets) {
		changes.firePropertyChange(FIELD_IntangibleAssets, IntangibleAssets,
				IntangibleAssets = intangibleAssets);
	}

	public long getAccumulatedAmortization() {
		return AccumulatedAmortization;
	}

	public void setAccumulatedAmortization(long accumulatedAmortization) {
		changes.firePropertyChange(FIELD_AccumulatedAmortization,
				AccumulatedAmortization,
				AccumulatedAmortization = accumulatedAmortization);
	}

	public long getOtherAssets() {
		return OtherAssets;
	}

	public void setOtherAssets(long otherAssets) {
		changes.firePropertyChange(FIELD_OtherAssets, OtherAssets,
				OtherAssets = otherAssets);
	}

	public long getDeferredLongTermAssetCharges() {
		return DeferredLongTermAssetCharges;
	}

	public void setDeferredLongTermAssetCharges(long deferredLongTermAssetCharges) {
		changes.firePropertyChange(FIELD_DeferredLongTermAssetCharges,
				DeferredLongTermAssetCharges,
				DeferredLongTermAssetCharges = deferredLongTermAssetCharges);
	}

	public long getTotalAssets() {
		return TotalAssets;
	}

	public void setTotalAssets(long totalAssets) {
		changes.firePropertyChange(FIELD_TotalAssets, TotalAssets,
				TotalAssets = totalAssets);
	}

	public long getAccountsPayable() {
		return AccountsPayable;
	}

	public void setAccountsPayable(long accountsPayable) {
		changes.firePropertyChange(FIELD_AccountsPayable, AccountsPayable,
				AccountsPayable = accountsPayable);
	}

	public long getShort_CurrentLongTermDebt() {
		return Short_CurrentLongTermDebt;
	}

	public void setShort_CurrentLongTermDebt(long short_CurrentLongTermDebt) {
		changes.firePropertyChange(FIELD_Short_CurrentLongTermDebt,
				Short_CurrentLongTermDebt,
				Short_CurrentLongTermDebt = short_CurrentLongTermDebt);
	}

	public long getOtherCurrentLiabilities() {
		return OtherCurrentLiabilities;
	}

	public void setOtherCurrentLiabilities(long otherCurrentLiabilities) {
		changes.firePropertyChange(FIELD_OtherCurrentLiabilities,
				OtherCurrentLiabilities,
				OtherCurrentLiabilities = otherCurrentLiabilities);
	}

	public long getTotalCurrentLiabilities() {
		return TotalCurrentLiabilities;
	}

	public void setTotalCurrentLiabilities(long totalCurrentLiabilities) {
		changes.firePropertyChange(FIELD_TotalCurrentLiabilities,
				TotalCurrentLiabilities,
				TotalCurrentLiabilities = totalCurrentLiabilities);
	}

	public long getLongTermDebt() {
		return LongTermDebt;
	}

	public void setLongTermDebt(long longTermDebt) {
		changes.firePropertyChange(FIELD_LongTermDebt, LongTermDebt,
				LongTermDebt = longTermDebt);
	}

	public long getOtherLiabilities() {
		return OtherLiabilities;
	}

	public void setOtherLiabilities(long otherLiabilities) {
		changes.firePropertyChange(FIELD_OtherLiabilities, OtherLiabilities,
				OtherLiabilities = otherLiabilities);
	}

	public long getDeferredLongTermLiabilityCharges() {
		return DeferredLongTermLiabilityCharges;
	}

	public void setDeferredLongTermLiabilityCharges(
			long deferredLongTermLiabilityCharges) {
		changes.firePropertyChange(FIELD_DeferredLongTermLiabilityCharges,
				DeferredLongTermLiabilityCharges,
				DeferredLongTermLiabilityCharges = deferredLongTermLiabilityCharges);
	}

	public long getMinorityInterest() {
		return MinorityInterest;
	}

	public void setMinorityInterest(long minorityInterest) {
		changes.firePropertyChange(FIELD_MinorityInterest, MinorityInterest,
				MinorityInterest = minorityInterest);
	}

	public long getNegativeGoodwill() {
		return NegativeGoodwill;
	}

	public void setNegativeGoodwill(long negativeGoodwill) {
		changes.firePropertyChange(FIELD_NegativeGoodwill, NegativeGoodwill,
				NegativeGoodwill = negativeGoodwill);
	}

	public long getTotalLiabilities() {
		return TotalLiabilities;
	}

	public void setTotalLiabilities(long totalLiabilities) {
		changes.firePropertyChange(FIELD_TotalLiabilities, TotalLiabilities,
				TotalLiabilities = totalLiabilities);
	}

	public long getMiscStocksOptionsWarrants() {
		return MiscStocksOptionsWarrants;
	}

	public void setMiscStocksOptionsWarrants(long miscStocksOptionsWarrants) {
		changes.firePropertyChange(FIELD_MiscStocksOptionsWarrants,
				MiscStocksOptionsWarrants,
				MiscStocksOptionsWarrants = miscStocksOptionsWarrants);
	}

	public long getRedeemablePreferredStock() {
		return RedeemablePreferredStock;
	}

	public void setRedeemablePreferredStock(long redeemablePreferredStock) {
		changes.firePropertyChange(FIELD_RedeemablePreferredStock,
				RedeemablePreferredStock,
				RedeemablePreferredStock = redeemablePreferredStock);
	}

	public long getPreferredStock() {
		return PreferredStock;
	}

	public void setPreferredStock(long preferredStock) {
		changes.firePropertyChange(FIELD_PreferredStock, PreferredStock,
				PreferredStock = preferredStock);
	}

	public long getCommonStock() {
		return CommonStock;
	}

	public void setCommonStock(long commonStock) {
		changes.firePropertyChange(FIELD_CommonStock, CommonStock,
				CommonStock = commonStock);
	}

	public long getRetainedEarnings() {
		return RetainedEarnings;
	}

	public void setRetainedEarnings(long retainedEarnings) {
		changes.firePropertyChange(FIELD_RetainedEarnings, RetainedEarnings,
				RetainedEarnings = retainedEarnings);
	}

	public long getTreasuryStock() {
		return TreasuryStock;
	}

	public void setTreasuryStock(long treasuryStock) {
		changes.firePropertyChange(FIELD_TreasuryStock, TreasuryStock,
				TreasuryStock = treasuryStock);
	}

	public long getCapitalSurplus() {
		return CapitalSurplus;
	}

	public void setCapitalSurplus(long capitalSurplus) {
		changes.firePropertyChange(FIELD_CapitalSurplus, CapitalSurplus,
				CapitalSurplus = capitalSurplus);
	}

	public long getOtherStockholderEquity() {
		return OtherStockholderEquity;
	}

	public void setOtherStockholderEquity(long otherStockholderEquity) {
		changes.firePropertyChange(FIELD_OtherStockholderEquity,
				OtherStockholderEquity,
				OtherStockholderEquity = otherStockholderEquity);
	}

	public long getTotalStockholderEquity() {
		return TotalStockholderEquity;
	}

	public void setTotalStockholderEquity(long totalStockholderEquity) {
		changes.firePropertyChange(FIELD_TotalStockholderEquity,
				TotalStockholderEquity,
				TotalStockholderEquity = totalStockholderEquity);
	}

	public long getNetTangibleAssets() {
		return NetTangibleAssets;
	}

	public void setNetTangibleAssets(long netTangibleAssets) {
		changes.firePropertyChange(FIELD_NetTangibleAssets, NetTangibleAssets,
				NetTangibleAssets = netTangibleAssets);
	}

	@Override
	public String toString() {
		
		return "BalanceSheetPoint [ PeriodEnding="+DateTool.dateToString(PeriodEnding) 
				+ ", type=" + this.getPeriodType()
				+ ", CashAndCashEquivalents="+ this.LongValueToString(CashAndCashEquivalents )
				+ ", ShortTermInvestments="+ this.LongValueToString(ShortTermInvestments )
				+ ", NetReceivables=" + this.LongValueToString(NetReceivables)
				+ ", Inventory=" + this.LongValueToString(Inventory )
				+ ", OtherCurrentAssets="+ this.LongValueToString(OtherCurrentAssets )
				+ ", TotalCurrentAssets="+ this.LongValueToString(TotalCurrentAssets )
				+ ", LongTermInvestments="+ this.LongValueToString(LongTermInvestments )
				+ ", PropertyPlantandEquipment="+ this.LongValueToString(PropertyPlantandEquipment )
				+ ", Goodwill=" + this.LongValueToString(Goodwill)
				+ ", IntangibleAssets=" + this.LongValueToString(IntangibleAssets)
				+ ", AccumulatedAmortization=" + this.LongValueToString(AccumulatedAmortization)
				+ ", OtherAssets=" +this.LongValueToString( OtherAssets)
				+ ", DeferredLongTermAssetCharges="+this.LongValueToString( DeferredLongTermAssetCharges)
				+ ", TotalAssets=" +this.LongValueToString( TotalAssets)
				+ ", AccountsPayable=" + this.LongValueToString(AccountsPayable)
				+ ", Short_CurrentLongTermDebt=" + this.LongValueToString(Short_CurrentLongTermDebt)
				+ ", OtherCurrentLiabilities=" + this.LongValueToString(OtherCurrentLiabilities)
				+ ", TotalCurrentLiabilities=" +this.LongValueToString( TotalCurrentLiabilities)
				+ ", LongTermDebt=" + this.LongValueToString(LongTermDebt )
				+ ", OtherLiabilities="+ this.LongValueToString(OtherLiabilities )
				+ ", DeferredLongTermLiabilityCharges="+this.LongValueToString( DeferredLongTermLiabilityCharges )
				+ ", MinorityInterest="+this.LongValueToString( MinorityInterest )
				+ ", NegativeGoodwill=" +this.LongValueToString( NegativeGoodwill)
				+ ", TotalLiabilities=" + this.LongValueToString(TotalLiabilities)
				+ ", MiscStocksOptionsWarrants=" + this.LongValueToString(MiscStocksOptionsWarrants)
				+ ", RedeemablePreferredStock=" +this.LongValueToString( RedeemablePreferredStock)
				+ ", PreferredStock=" + this.LongValueToString(PreferredStock )
				+ ", CommonStock="+ this.LongValueToString(CommonStock )
				+ ", RetainedEarnings=" +this.LongValueToString( RetainedEarnings)
				+ ", TreasuryStock=" +this.LongValueToString( TreasuryStock )
				+ ", CapitalSurplus="+this.LongValueToString( CapitalSurplus )
				+ ", OtherStockholderEquity="+this.LongValueToString( OtherStockholderEquity )
				+ ", TotalStockholderEquity="+ this.LongValueToString(TotalStockholderEquity )
				+ ", NetTangibleAssets="+this.LongValueToString( NetTangibleAssets )
				+ "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setCashAndCashEquivalents(Long.parseLong(rootElement.getAttribute(FIELD_CashAndCashEquivalents)));
		this.setShortTermInvestments(Long.parseLong(rootElement.getAttribute(FIELD_ShortTermInvestments)));
		this.setNetReceivables(Long.parseLong(rootElement.getAttribute(FIELD_NetReceivables)));
		this.setInventory(Long.parseLong(rootElement.getAttribute(FIELD_Inventory)));
		this.setOtherCurrentAssets(Long.parseLong(rootElement.getAttribute(FIELD_OtherCurrentAssets)));
		this.setTotalCurrentAssets(Long.parseLong(rootElement.getAttribute(FIELD_TotalCurrentAssets)));
		this.setLongTermInvestments(Long.parseLong(rootElement.getAttribute(FIELD_LongTermInvestments)));
		this.setPropertyPlantandEquipment(Long.parseLong(rootElement.getAttribute(FIELD_PropertyPlantandEquipment)));
		this.setGoodwill(Long.parseLong(rootElement.getAttribute(FIELD_Goodwill)));
		this.setIntangibleAssets(Long.parseLong(rootElement.getAttribute(FIELD_IntangibleAssets)));
		this.setAccumulatedAmortization(Long.parseLong(rootElement.getAttribute(FIELD_AccumulatedAmortization)));
		this.setOtherAssets(Long.parseLong(rootElement.getAttribute(FIELD_OtherAssets)));
		this.setDeferredLongTermAssetCharges(Long.parseLong(rootElement.getAttribute(FIELD_DeferredLongTermAssetCharges)));
		this.setTotalAssets(Long.parseLong(rootElement.getAttribute(FIELD_TotalAssets)));
		this.setAccountsPayable(Long.parseLong(rootElement.getAttribute(FIELD_AccountsPayable)));
		this.setShort_CurrentLongTermDebt(Long.parseLong(rootElement.getAttribute(FIELD_Short_CurrentLongTermDebt)));
		this.setOtherCurrentLiabilities(Long.parseLong(rootElement.getAttribute(FIELD_OtherCurrentLiabilities)));
		this.setTotalCurrentLiabilities(Long.parseLong(rootElement.getAttribute(FIELD_TotalCurrentLiabilities)));
		this.setLongTermDebt(Long.parseLong(rootElement.getAttribute(FIELD_LongTermDebt)));
		this.setOtherLiabilities(Long.parseLong(rootElement.getAttribute(FIELD_OtherLiabilities)));
		this.setDeferredLongTermLiabilityCharges(Long.parseLong(rootElement.getAttribute(FIELD_DeferredLongTermLiabilityCharges)));
		this.setMinorityInterest(Long.parseLong(rootElement.getAttribute(FIELD_MinorityInterest)));
		this.setNegativeGoodwill(Long.parseLong(rootElement.getAttribute(FIELD_NegativeGoodwill)));
		this.setTotalLiabilities(Long.parseLong(rootElement.getAttribute(FIELD_TotalLiabilities)));
		this.setMiscStocksOptionsWarrants(Long.parseLong(rootElement.getAttribute(FIELD_MiscStocksOptionsWarrants)));
		this.setRedeemablePreferredStock(Long.parseLong(rootElement.getAttribute(FIELD_RedeemablePreferredStock)));
		this.setPreferredStock(Long.parseLong(rootElement.getAttribute(FIELD_PreferredStock)));
		this.setCommonStock(Long.parseLong(rootElement.getAttribute(FIELD_CommonStock)));
		this.setRetainedEarnings(Long.parseLong(rootElement.getAttribute(FIELD_RetainedEarnings)));
		this.setTreasuryStock(Long.parseLong(rootElement.getAttribute(FIELD_TreasuryStock)));
		this.setCapitalSurplus(Long.parseLong(rootElement.getAttribute(FIELD_CapitalSurplus)));
		this.setOtherStockholderEquity(Long.parseLong(rootElement.getAttribute(FIELD_OtherStockholderEquity)));
		this.setTotalStockholderEquity(Long.parseLong(rootElement.getAttribute(FIELD_TotalStockholderEquity)));
		this.setNetTangibleAssets(Long.parseLong(rootElement.getAttribute(FIELD_NetTangibleAssets)));
		
		super.initAttribute(rootElement);
	}

	@Override
	protected void setAttribute(Element rootElement) {
		
		
		rootElement.setAttribute(FIELD_CashAndCashEquivalents,String.valueOf(this.getCashAndCashEquivalents()));
		rootElement.setAttribute(FIELD_ShortTermInvestments,String.valueOf(this.getShortTermInvestments()));
		rootElement.setAttribute(FIELD_NetReceivables,String.valueOf(this.getNetReceivables()));
		rootElement.setAttribute(FIELD_Inventory,String.valueOf(this.getInventory()));
		rootElement.setAttribute(FIELD_OtherCurrentAssets,String.valueOf(this.getOtherCurrentAssets()));
		rootElement.setAttribute(FIELD_TotalCurrentAssets,String.valueOf(this.getTotalCurrentAssets()));
		rootElement.setAttribute(FIELD_LongTermInvestments,String.valueOf(this.getLongTermInvestments()));
		rootElement.setAttribute(FIELD_PropertyPlantandEquipment,String.valueOf(this.getPropertyPlantandEquipment()));
		rootElement.setAttribute(FIELD_Goodwill,String.valueOf(this.getGoodwill()));
		rootElement.setAttribute(FIELD_IntangibleAssets,String.valueOf(this.getIntangibleAssets()));
		rootElement.setAttribute(FIELD_AccumulatedAmortization,String.valueOf(this.getAccumulatedAmortization()));
		rootElement.setAttribute(FIELD_OtherAssets,String.valueOf(this.getOtherAssets()));
		rootElement.setAttribute(FIELD_DeferredLongTermAssetCharges,String.valueOf(this.getDeferredLongTermAssetCharges()));
		rootElement.setAttribute(FIELD_TotalAssets,String.valueOf(this.getTotalAssets()));
		rootElement.setAttribute(FIELD_AccountsPayable,String.valueOf(this.getAccountsPayable()));
		rootElement.setAttribute(FIELD_Short_CurrentLongTermDebt,String.valueOf(this.getShort_CurrentLongTermDebt()));
		rootElement.setAttribute(FIELD_OtherCurrentLiabilities,String.valueOf(this.getOtherCurrentLiabilities()));
		rootElement.setAttribute(FIELD_TotalCurrentLiabilities,String.valueOf(this.getTotalCurrentLiabilities()));
		rootElement.setAttribute(FIELD_LongTermDebt,String.valueOf(this.getLongTermDebt()));
		rootElement.setAttribute(FIELD_OtherLiabilities,String.valueOf(this.getOtherLiabilities()));
		rootElement.setAttribute(FIELD_DeferredLongTermLiabilityCharges,String.valueOf(this.getDeferredLongTermLiabilityCharges()));
		rootElement.setAttribute(FIELD_MinorityInterest,String.valueOf(this.getMinorityInterest()));
		rootElement.setAttribute(FIELD_NegativeGoodwill,String.valueOf(this.getNegativeGoodwill()));
		rootElement.setAttribute(FIELD_TotalLiabilities,String.valueOf(this.getTotalLiabilities()));
		rootElement.setAttribute(FIELD_MiscStocksOptionsWarrants,String.valueOf(this.getMiscStocksOptionsWarrants()));
		rootElement.setAttribute(FIELD_RedeemablePreferredStock,String.valueOf(this.getRedeemablePreferredStock()));
		rootElement.setAttribute(FIELD_PreferredStock,String.valueOf(this.getPreferredStock()));
		rootElement.setAttribute(FIELD_CommonStock,String.valueOf(this.getCommonStock()));
		rootElement.setAttribute(FIELD_RetainedEarnings,String.valueOf(this.getRetainedEarnings()));
		rootElement.setAttribute(FIELD_TreasuryStock,String.valueOf(this.getTreasuryStock()));
		rootElement.setAttribute(FIELD_CapitalSurplus,String.valueOf(this.getCapitalSurplus()));
		rootElement.setAttribute(FIELD_OtherStockholderEquity,String.valueOf(this.getOtherStockholderEquity()));
		rootElement.setAttribute(FIELD_TotalStockholderEquity,String.valueOf(this.getTotalStockholderEquity()));
		rootElement.setAttribute(FIELD_NetTangibleAssets,String.valueOf(this.getNetTangibleAssets()));
		
		super.setAttribute(rootElement);
	}
	

	
}
