package com.munch.exchange.model.core.financials;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;


public class IncomeStatementPoint extends FinancialPoint {
	
	
	public static final String FIELD_TotalRevenue = "TotalRevenue";
	public static final String FIELD_CostofRevenue = "CostofRevenue";
	public static final String FIELD_GrossProfit = "GrossProfit";
	
	public static final String FIELD_ResearchDevelopment = "ResearchDevelopment";
	public static final String FIELD_SellingGeneralandAdministrative = "SellingGeneralandAdministrative";
	public static final String FIELD_NonRecurring = "NonRecurring";
	public static final String FIELD_Others = "Others";
	public static final String FIELD_TotalOperatingExpenses = "TotalOperatingExpenses";
	
	public static final String FIELD_OperatingIncomeorLoss = "OperatingIncomeorLoss";
	
	public static final String FIELD_TotalOtherIncome_ExpensesNet = "TotalOtherIncome_ExpensesNet";
	public static final String FIELD_EarningsBeforeInterestAndTaxes = "EarningsBeforeInterestAndTaxes";
	public static final String FIELD_InterestExpense = "InterestExpense";
	public static final String FIELD_IncomeBeforeTax = "IncomeBeforeTax";
	public static final String FIELD_IncomeTaxExpense = "IncomeTaxExpense";
	public static final String FIELD_MinorityInterest = "MinorityInterest";
	public static final String FIELD_NetIncomeFromContinuingOps = "NetIncomeFromContinuingOps";
	
	public static final String FIELD_DiscontinuedOperations = "DiscontinuedOperations";
	public static final String FIELD_ExtraordinaryItems = "ExtraordinaryItems";
	public static final String FIELD_EffectOfAccountingChanges = "EffectOfAccountingChanges";
	public static final String FIELD_OtherItems = "OtherItems";
	
	public static final String FIELD_NetIncome = "NetIncome";
	public static final String FIELD_PreferredStockAndOtherAdjustments = "PreferredStockAndOtherAdjustments";
	public static final String FIELD_NetIncomeApplicableToCommonShares = "NetIncomeApplicableToCommonShares";
	
	
	public static final String FIELD_Employees  = "Employees";
	public static final String FIELD_EarningsPerShare = "EarningsPerShare";
	public static final String FIELD_OutstandingShares  = "OutstandingShares";
	
	
	//========================
	//==    Added values    ==
	//========================
	private long Employees;
	private long EarningsPerShare;
	private long OutstandingShares;
	
	
	
	// 1
	private long TotalRevenue;
	// 2
	private long CostofRevenue;
	//grossProfit=TotalRevenue - CostofRevenue
	// 3
	private long GrossProfit;
	
	//========================
	//== Operating Expenses ==
	//========================
	// 4
	private long ResearchDevelopment;
	// 5
	private long SellingGeneralandAdministrative;
	// 6
	private long NonRecurring;
	// 7
	private long Others;
	//TotalOperatingExpenses=Others+NonRecurring+SellingGeneralandAdministrative+ResearchDevelopment
	// 8
	private long TotalOperatingExpenses;
	
	
	
	//==============================
	//== Operating Income or Loss ==
	//==============================
	//OperatingIncomeorLoss=TotalRevenue-TotalOperatingExpenses
	// 9
	private long OperatingIncomeorLoss;
	
	
	//=======================================
	//== Income from Continuing Operations ==
	//=======================================
	// 10
	private long TotalOtherIncome_ExpensesNet;
	// 11
	private long EarningsBeforeInterestAndTaxes;
	// 12
	private long InterestExpense;
	// 13
	private long IncomeBeforeTax;
	// 14
	private long IncomeTaxExpense;	
	// 15
	private long MinorityInterest;
	// 16
	private long NetIncomeFromContinuingOps;
	
	//==========================
	//== Non-recurring Events ==
	//==========================
	// 17
	private long DiscontinuedOperations;
	// 18
	private long ExtraordinaryItems;
	// 19
	private long EffectOfAccountingChanges;
	// 20
	private long OtherItems;
	
	//================
	//== Net Income ==
	//================
	//NetIncome=NetIncomeFromContinuingOps-MinorityInterest
	// 21
	private long NetIncome;
	// 22
	private long PreferredStockAndOtherAdjustments;
	// 23
	private long NetIncomeApplicableToCommonShares;
	
	
	
	//=======================
	//== GETTER AND SETTER ==
	//=======================
	
	
	
	public long getTotalRevenue() {
		return TotalRevenue;
	}
	public long getEmployees() {
		return Employees;
	}
	public void setEmployees(long employees) {
		changes.firePropertyChange(FIELD_Employees, this.Employees,
				this.Employees = employees);
	}
	public long getEarningsPerShare() {
		return EarningsPerShare;
	}
	public void setEarningsPerShare(long earningsPerShare) {
		changes.firePropertyChange(FIELD_EarningsPerShare, this.EarningsPerShare,
				this.EarningsPerShare = earningsPerShare);
	}
	public long getOutstandingShares() {
		return OutstandingShares;
	}
	public void setOutstandingShares(long outstandingShares) {
		changes.firePropertyChange(FIELD_OutstandingShares, this.OutstandingShares,
				this.OutstandingShares = outstandingShares);
	}
	public void setTotalRevenue(long totalRevenue) {
		changes.firePropertyChange(FIELD_TotalRevenue, TotalRevenue, TotalRevenue = totalRevenue);
		//TotalRevenue = totalRevenue;
	}
	public long getCostofRevenue() {
		return CostofRevenue;
	}
	public void setCostofRevenue(long costofRevenue) {
		changes.firePropertyChange(FIELD_CostofRevenue, CostofRevenue, CostofRevenue = costofRevenue);
		//CostofRevenue = costofRevenue;
	}
	public long getGrossProfit() {
		return GrossProfit;
	}
	public void setGrossProfit(long grossProfit) {
		changes.firePropertyChange(FIELD_GrossProfit, GrossProfit, GrossProfit = grossProfit);
		//GrossProfit = grossProfit;
	}
	public long getResearchDevelopment() {
		return ResearchDevelopment;
	}
	public void setResearchDevelopment(long researchDevelopment) {
		changes.firePropertyChange(FIELD_ResearchDevelopment, ResearchDevelopment, ResearchDevelopment = researchDevelopment);
		//ResearchDevelopment = researchDevelopment;
	}
	public long getSellingGeneralandAdministrative() {
		return SellingGeneralandAdministrative;
	}
	public void setSellingGeneralandAdministrative(
			long sellingGeneralandAdministrative) {
		changes.firePropertyChange(FIELD_SellingGeneralandAdministrative, SellingGeneralandAdministrative, SellingGeneralandAdministrative = sellingGeneralandAdministrative);
		//SellingGeneralandAdministrative = sellingGeneralandAdministrative;
	}
	public long getNonRecurring() {
		return NonRecurring;
	}
	public void setNonRecurring(long nonRecurring) {
		changes.firePropertyChange(FIELD_NonRecurring, NonRecurring, NonRecurring = nonRecurring);
		//NonRecurring = nonRecurring;
	}
	public long getOthers() {
		return Others;
	}
	public void setOthers(long others) {
		changes.firePropertyChange(FIELD_Others, Others, Others = others);
		//Others = others;
	}
	public long getTotalOperatingExpenses() {
		return TotalOperatingExpenses;
	}
	public void setTotalOperatingExpenses(long totalOperatingExpenses) {
		changes.firePropertyChange(FIELD_TotalOperatingExpenses, TotalOperatingExpenses, TotalOperatingExpenses = totalOperatingExpenses);
		//TotalOperatingExpenses = totalOperatingExpenses;
	}
	public long getOperatingIncomeorLoss() {
		return OperatingIncomeorLoss;
	}
	public void setOperatingIncomeorLoss(long operatingIncomeorLoss) {
		changes.firePropertyChange(FIELD_OperatingIncomeorLoss, OperatingIncomeorLoss, OperatingIncomeorLoss = operatingIncomeorLoss);
		//OperatingIncomeorLoss = operatingIncomeorLoss;
	}
	public long getTotalOtherIncome_ExpensesNet() {
		return TotalOtherIncome_ExpensesNet;
	}
	public void setTotalOtherIncome_ExpensesNet(long totalOtherIncome_ExpensesNet) {
		changes.firePropertyChange(FIELD_TotalOtherIncome_ExpensesNet, TotalOtherIncome_ExpensesNet, TotalOtherIncome_ExpensesNet = totalOtherIncome_ExpensesNet);
		//TotalOtherIncome_ExpensesNet = totalOtherIncome_ExpensesNet;
	}
	public long getEarningsBeforeInterestAndTaxes() {
		return EarningsBeforeInterestAndTaxes;
	}
	public void setEarningsBeforeInterestAndTaxes(
			long earningsBeforeInterestAndTaxes) {
		changes.firePropertyChange(FIELD_EarningsBeforeInterestAndTaxes, EarningsBeforeInterestAndTaxes, EarningsBeforeInterestAndTaxes = earningsBeforeInterestAndTaxes);
		//EarningsBeforeInterestAndTaxes = earningsBeforeInterestAndTaxes;
	}
	public long getInterestExpense() {
		return InterestExpense;
	}
	public void setInterestExpense(long interestExpense) {
		changes.firePropertyChange(FIELD_InterestExpense, InterestExpense, InterestExpense = interestExpense);
		//InterestExpense = interestExpense;
	}
	public long getIncomeBeforeTax() {
		return IncomeBeforeTax;
	}
	public void setIncomeBeforeTax(long incomeBeforeTax) {
		changes.firePropertyChange(FIELD_IncomeBeforeTax, IncomeBeforeTax, IncomeBeforeTax = incomeBeforeTax);
		//IncomeBeforeTax = incomeBeforeTax;
	}
	public long getIncomeTaxExpense() {
		return IncomeTaxExpense;
	}
	public void setIncomeTaxExpense(long incomeTaxExpense) {
		changes.firePropertyChange(FIELD_IncomeTaxExpense, IncomeTaxExpense, IncomeTaxExpense = incomeTaxExpense);
		//IncomeTaxExpense = incomeTaxExpense;
	}
	public long getMinorityInterest() {
		return MinorityInterest;
	}
	public void setMinorityInterest(long minorityInterest) {
		changes.firePropertyChange(FIELD_MinorityInterest, MinorityInterest, MinorityInterest = minorityInterest);
		//MinorityInterest = minorityInterest;
	}
	public long getNetIncomeFromContinuingOps() {
		return NetIncomeFromContinuingOps;
	}
	public void setNetIncomeFromContinuingOps(long netIncomeFromContinuingOps) {
		changes.firePropertyChange(FIELD_NetIncomeFromContinuingOps, NetIncomeFromContinuingOps, NetIncomeFromContinuingOps = netIncomeFromContinuingOps);
		//NetIncomeFromContinuingOps = netIncomeFromContinuingOps;
	}
	public long getDiscontinuedOperations() {
		return DiscontinuedOperations;
	}
	public void setDiscontinuedOperations(long discontinuedOperations) {
		changes.firePropertyChange(FIELD_DiscontinuedOperations, DiscontinuedOperations, DiscontinuedOperations = discontinuedOperations);
		//DiscontinuedOperations = discontinuedOperations;
	}
	public long getExtraordinaryItems() {
		return ExtraordinaryItems;
	}
	public void setExtraordinaryItems(long extraordinaryItems) {
		changes.firePropertyChange(FIELD_ExtraordinaryItems, ExtraordinaryItems, ExtraordinaryItems = extraordinaryItems);
		//ExtraordinaryItems = extraordinaryItems;
	}
	public long getEffectOfAccountingChanges() {
		return EffectOfAccountingChanges;
	}
	public void setEffectOfAccountingChanges(long effectOfAccountingChanges) {
		changes.firePropertyChange(FIELD_EffectOfAccountingChanges, EffectOfAccountingChanges, EffectOfAccountingChanges = effectOfAccountingChanges);
		//EffectOfAccountingChanges = effectOfAccountingChanges;
	}
	public long getOtherItems() {
		return OtherItems;
	}
	public void setOtherItems(long otherItems) {
		changes.firePropertyChange(FIELD_OtherItems, OtherItems, OtherItems = otherItems);
		//OtherItems = otherItems;
	}
	public long getNetIncome() {
		return NetIncome;
	}
	public void setNetIncome(long netIncome) {
		changes.firePropertyChange(FIELD_NetIncome, NetIncome, NetIncome = netIncome);
		//NetIncome = netIncome;
	}
	public long getPreferredStockAndOtherAdjustments() {
		return PreferredStockAndOtherAdjustments;
	}
	public void setPreferredStockAndOtherAdjustments(
			long preferredStockAndOtherAdjustments) {
		changes.firePropertyChange(FIELD_PreferredStockAndOtherAdjustments, PreferredStockAndOtherAdjustments, PreferredStockAndOtherAdjustments = preferredStockAndOtherAdjustments);
		//PreferredStockAndOtherAdjustments = preferredStockAndOtherAdjustments;
	}
	public long getNetIncomeApplicableToCommonShares() {
		return NetIncomeApplicableToCommonShares;
	}
	public void setNetIncomeApplicableToCommonShares(
			long netIncomeApplicableToCommonShares) {
		changes.firePropertyChange(FIELD_NetIncomeApplicableToCommonShares, NetIncomeApplicableToCommonShares, NetIncomeApplicableToCommonShares = netIncomeApplicableToCommonShares);
		//NetIncomeApplicableToCommonShares = netIncomeApplicableToCommonShares;
	}
	
	
	
	
	@Override
	public String toString() {
		return "IncomeStatementPoint [ PeriodEnding="+DateTool.dateToString(PeriodEnding) 
				+ ", type=" + this.getPeriodType()
				+ ", TotalRevenue=" + this.LongValueToString(TotalRevenue)
				+ ", CostofRevenue=" +this.LongValueToString(CostofRevenue )
				+ ", GrossProfit="+ this.LongValueToString(GrossProfit  )
				+ ", ResearchDevelopment=" + this.LongValueToString(ResearchDevelopment)
				+ ", SellingGeneralandAdministrative="+ this.LongValueToString(SellingGeneralandAdministrative )
				+ ", NonRecurring="+ this.LongValueToString(NonRecurring )
				+ ", Others=" + this.LongValueToString(Others)
				+ ", TotalOperatingExpenses=" + this.LongValueToString(TotalOperatingExpenses)
				+ ", OperatingIncomeorLoss=" + this.LongValueToString(OperatingIncomeorLoss)
				+ ", TotalOtherIncome_ExpensesNet="+ this.LongValueToString(TotalOtherIncome_ExpensesNet)
				+ ", EarningsBeforeInterestAndTaxes="+ this.LongValueToString(EarningsBeforeInterestAndTaxes )
				+ ", InterestExpense=" + this.LongValueToString(InterestExpense )
				+ ", IncomeBeforeTax=" + this.LongValueToString(IncomeBeforeTax)
				+ ", IncomeTaxExpense=" + this.LongValueToString(IncomeTaxExpense)
				+ ", MinorityInterest=" + this.LongValueToString(MinorityInterest)
				+ ", NetIncomeFromContinuingOps=" + this.LongValueToString(NetIncomeFromContinuingOps)
				+ ", DiscontinuedOperations=" + this.LongValueToString(DiscontinuedOperations)
				+ ", ExtraordinaryItems=" + this.LongValueToString(ExtraordinaryItems)
				+ ", EffectOfAccountingChanges=" + this.LongValueToString(EffectOfAccountingChanges)
				+ ", OtherItems=" + this.LongValueToString(OtherItems )
				+ ", NetIncome=" + this.LongValueToString(NetIncome)
				+ ", PreferredStockAndOtherAdjustments="+ this.LongValueToString(PreferredStockAndOtherAdjustments)
				+ ", NetIncomeApplicableToCommonShares="+ this.LongValueToString(NetIncomeApplicableToCommonShares)
				+ "]";
	}
	@Override
	protected void initAttribute(Element rootElement) {
		this.setTotalRevenue(Long.parseLong(rootElement.getAttribute(FIELD_TotalRevenue)));
		this.setCostofRevenue(Long.parseLong(rootElement.getAttribute(FIELD_CostofRevenue)));
		this.setGrossProfit(Long.parseLong(rootElement.getAttribute(FIELD_GrossProfit)));
		this.setResearchDevelopment(Long.parseLong(rootElement.getAttribute(FIELD_ResearchDevelopment)));
		this.setSellingGeneralandAdministrative(Long.parseLong(rootElement.getAttribute(FIELD_SellingGeneralandAdministrative)));
		this.setNonRecurring(Long.parseLong(rootElement.getAttribute(FIELD_NonRecurring)));
		this.setOthers(Long.parseLong(rootElement.getAttribute(FIELD_Others)));
		this.setTotalOperatingExpenses(Long.parseLong(rootElement.getAttribute(FIELD_TotalOperatingExpenses)));
		this.setOperatingIncomeorLoss(Long.parseLong(rootElement.getAttribute(FIELD_OperatingIncomeorLoss)));
		this.setTotalOtherIncome_ExpensesNet(Long.parseLong(rootElement.getAttribute(FIELD_TotalOtherIncome_ExpensesNet)));
		this.setEarningsBeforeInterestAndTaxes(Long.parseLong(rootElement.getAttribute(FIELD_EarningsBeforeInterestAndTaxes)));
		this.setInterestExpense(Long.parseLong(rootElement.getAttribute(FIELD_InterestExpense)));
		this.setIncomeBeforeTax(Long.parseLong(rootElement.getAttribute(FIELD_IncomeBeforeTax)));
		this.setIncomeTaxExpense(Long.parseLong(rootElement.getAttribute(FIELD_IncomeTaxExpense)));
		this.setMinorityInterest(Long.parseLong(rootElement.getAttribute(FIELD_MinorityInterest)));
		this.setNetIncomeFromContinuingOps(Long.parseLong(rootElement.getAttribute(FIELD_NetIncomeFromContinuingOps)));
		this.setDiscontinuedOperations(Long.parseLong(rootElement.getAttribute(FIELD_DiscontinuedOperations)));
		this.setExtraordinaryItems(Long.parseLong(rootElement.getAttribute(FIELD_ExtraordinaryItems)));
		this.setEffectOfAccountingChanges(Long.parseLong(rootElement.getAttribute(FIELD_EffectOfAccountingChanges)));
		this.setOtherItems(Long.parseLong(rootElement.getAttribute(FIELD_OtherItems)));
		this.setNetIncome(Long.parseLong(rootElement.getAttribute(FIELD_NetIncome)));
		this.setPreferredStockAndOtherAdjustments(Long.parseLong(rootElement.getAttribute(FIELD_PreferredStockAndOtherAdjustments)));
		this.setNetIncomeApplicableToCommonShares(Long.parseLong(rootElement.getAttribute(FIELD_NetIncomeApplicableToCommonShares)));
		
		if(rootElement.hasAttribute(FIELD_Employees))
			this.setEmployees(Long.parseLong(rootElement.getAttribute(FIELD_Employees)));
		if(rootElement.hasAttribute(FIELD_EarningsPerShare))
			this.setEarningsPerShare(Long.parseLong(rootElement.getAttribute(FIELD_EarningsPerShare)));
		if(rootElement.hasAttribute(FIELD_OutstandingShares))
			this.setOutstandingShares(Long.parseLong(rootElement.getAttribute(FIELD_OutstandingShares)));
		
		

		super.initAttribute(rootElement);
	}
	@Override
	protected void setAttribute(Element rootElement) {
		
		
		rootElement.setAttribute(FIELD_TotalRevenue,String.valueOf(this.getTotalRevenue()));
		rootElement.setAttribute(FIELD_CostofRevenue,String.valueOf(this.getCostofRevenue()));
		rootElement.setAttribute(FIELD_GrossProfit,String.valueOf(this.getGrossProfit()));
		rootElement.setAttribute(FIELD_ResearchDevelopment,String.valueOf(this.getResearchDevelopment()));
		rootElement.setAttribute(FIELD_SellingGeneralandAdministrative,String.valueOf(this.getSellingGeneralandAdministrative()));
		rootElement.setAttribute(FIELD_NonRecurring,String.valueOf(this.getNonRecurring()));
		rootElement.setAttribute(FIELD_Others,String.valueOf(this.getOthers()));
		rootElement.setAttribute(FIELD_TotalOperatingExpenses,String.valueOf(this.getTotalOperatingExpenses()));
		rootElement.setAttribute(FIELD_OperatingIncomeorLoss,String.valueOf(this.getOperatingIncomeorLoss()));
		rootElement.setAttribute(FIELD_TotalOtherIncome_ExpensesNet,String.valueOf(this.getTotalOtherIncome_ExpensesNet()));
		rootElement.setAttribute(FIELD_EarningsBeforeInterestAndTaxes,String.valueOf(this.getEarningsBeforeInterestAndTaxes()));
		rootElement.setAttribute(FIELD_InterestExpense,String.valueOf(this.getInterestExpense()));
		rootElement.setAttribute(FIELD_IncomeBeforeTax,String.valueOf(this.getIncomeBeforeTax()));
		rootElement.setAttribute(FIELD_IncomeTaxExpense,String.valueOf(this.getIncomeTaxExpense()));
		rootElement.setAttribute(FIELD_MinorityInterest,String.valueOf(this.getMinorityInterest()));
		rootElement.setAttribute(FIELD_NetIncomeFromContinuingOps,String.valueOf(this.getNetIncomeFromContinuingOps()));
		rootElement.setAttribute(FIELD_DiscontinuedOperations,String.valueOf(this.getDiscontinuedOperations()));
		rootElement.setAttribute(FIELD_ExtraordinaryItems,String.valueOf(this.getExtraordinaryItems()));
		rootElement.setAttribute(FIELD_EffectOfAccountingChanges,String.valueOf(this.getEffectOfAccountingChanges()));
		rootElement.setAttribute(FIELD_OtherItems,String.valueOf(this.getOtherItems()));
		rootElement.setAttribute(FIELD_NetIncome,String.valueOf(this.getNetIncome()));
		rootElement.setAttribute(FIELD_PreferredStockAndOtherAdjustments,String.valueOf(this.getPreferredStockAndOtherAdjustments()));
		rootElement.setAttribute(FIELD_NetIncomeApplicableToCommonShares,String.valueOf(this.getNetIncomeApplicableToCommonShares()));
		
		rootElement.setAttribute(FIELD_Employees,String.valueOf(this.getEmployees()));
		rootElement.setAttribute(FIELD_EarningsPerShare,String.valueOf(this.getEarningsPerShare()));
		rootElement.setAttribute(FIELD_OutstandingShares,String.valueOf(this.getOutstandingShares()));
		
		
		
		super.setAttribute(rootElement);
	}
	@Override
	public long getValue(String fieldKey) {
		if(fieldKey.equals(FIELD_CostofRevenue))return CostofRevenue;
		if(fieldKey.equals(FIELD_DiscontinuedOperations))return DiscontinuedOperations;
		if(fieldKey.equals(FIELD_EarningsBeforeInterestAndTaxes))return EarningsBeforeInterestAndTaxes;
		if(fieldKey.equals(FIELD_EffectOfAccountingChanges))return EffectOfAccountingChanges;
		if(fieldKey.equals(FIELD_ExtraordinaryItems))return ExtraordinaryItems;
		if(fieldKey.equals(FIELD_GrossProfit))return GrossProfit;
		if(fieldKey.equals(FIELD_IncomeBeforeTax))return IncomeBeforeTax;
		if(fieldKey.equals(FIELD_InterestExpense))return InterestExpense;
		if(fieldKey.equals(FIELD_IncomeTaxExpense))return IncomeTaxExpense;
		if(fieldKey.equals(FIELD_MinorityInterest))return MinorityInterest;
		if(fieldKey.equals(FIELD_NetIncome))return NetIncome;
		if(fieldKey.equals(FIELD_NetIncomeApplicableToCommonShares))return NetIncomeApplicableToCommonShares;
		if(fieldKey.equals(FIELD_NetIncomeFromContinuingOps))return NetIncomeFromContinuingOps;
		if(fieldKey.equals(FIELD_NonRecurring))return NonRecurring;
		if(fieldKey.equals(FIELD_OperatingIncomeorLoss))return OperatingIncomeorLoss;
		if(fieldKey.equals(FIELD_OtherItems))return OtherItems;
		if(fieldKey.equals(FIELD_Others))return Others;
		if(fieldKey.equals(FIELD_PreferredStockAndOtherAdjustments))return PreferredStockAndOtherAdjustments;
		if(fieldKey.equals(FIELD_ResearchDevelopment))return ResearchDevelopment;
		if(fieldKey.equals(FIELD_SellingGeneralandAdministrative))return SellingGeneralandAdministrative;
		if(fieldKey.equals(FIELD_TotalOperatingExpenses))return TotalOperatingExpenses;
		if(fieldKey.equals(FIELD_TotalOtherIncome_ExpensesNet))return TotalOtherIncome_ExpensesNet;
		if(fieldKey.equals(FIELD_TotalRevenue))return TotalRevenue;
		
		if(fieldKey.equals(FIELD_Employees))return Employees;
		if(fieldKey.equals(FIELD_EarningsPerShare))return EarningsPerShare;
		if(fieldKey.equals(FIELD_OutstandingShares))return OutstandingShares;
	
		return 0;
	}
	@Override
	public void setValue(String fieldKey , long value) {
		if(fieldKey.equals(FIELD_CostofRevenue))CostofRevenue=value;
		if(fieldKey.equals(FIELD_DiscontinuedOperations))DiscontinuedOperations=value;
		if(fieldKey.equals(FIELD_EarningsBeforeInterestAndTaxes))EarningsBeforeInterestAndTaxes=value;
		if(fieldKey.equals(FIELD_EffectOfAccountingChanges))EffectOfAccountingChanges=value;
		if(fieldKey.equals(FIELD_ExtraordinaryItems))ExtraordinaryItems=value;
		if(fieldKey.equals(FIELD_GrossProfit))GrossProfit=value;
		if(fieldKey.equals(FIELD_IncomeBeforeTax))IncomeBeforeTax=value;
		if(fieldKey.equals(FIELD_InterestExpense))InterestExpense=value;
		if(fieldKey.equals(FIELD_IncomeTaxExpense))IncomeTaxExpense=value;
		if(fieldKey.equals(FIELD_MinorityInterest)) MinorityInterest=value;
		if(fieldKey.equals(FIELD_NetIncome))NetIncome=value;
		if(fieldKey.equals(FIELD_NetIncomeApplicableToCommonShares)) NetIncomeApplicableToCommonShares=value;
		if(fieldKey.equals(FIELD_NetIncomeFromContinuingOps))NetIncomeFromContinuingOps=value;
		if(fieldKey.equals(FIELD_NonRecurring))NonRecurring=value;
		if(fieldKey.equals(FIELD_OperatingIncomeorLoss))OperatingIncomeorLoss=value;
		if(fieldKey.equals(FIELD_OtherItems)) OtherItems=value;
		if(fieldKey.equals(FIELD_Others))Others=value;
		if(fieldKey.equals(FIELD_PreferredStockAndOtherAdjustments))PreferredStockAndOtherAdjustments=value;
		if(fieldKey.equals(FIELD_ResearchDevelopment))ResearchDevelopment=value;
		if(fieldKey.equals(FIELD_SellingGeneralandAdministrative))SellingGeneralandAdministrative=value;
		if(fieldKey.equals(FIELD_TotalOperatingExpenses))TotalOperatingExpenses=value;
		if(fieldKey.equals(FIELD_TotalOtherIncome_ExpensesNet))TotalOtherIncome_ExpensesNet=value;
		if(fieldKey.equals(FIELD_TotalRevenue))TotalRevenue=value;
		
		if(fieldKey.equals(FIELD_Employees)) Employees=value;
		if(fieldKey.equals(FIELD_EarningsPerShare)) EarningsPerShare=value;
		if(fieldKey.equals(FIELD_OutstandingShares)) OutstandingShares=value;
		
	}
	
	
	

}
