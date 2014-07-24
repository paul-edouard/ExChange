package com.munch.exchange.parts.financials;

import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.financials.BalanceSheetPoint;
import com.munch.exchange.model.core.financials.CashFlowPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;

public class StockFinancialsContentProvider implements
IStructuredContentProvider, ITreeContentProvider{
	
	
	private FinancialElement root=new FinancialElement(null,"root","root","root");
	
	public StockFinancialsContentProvider(){
		buildFinancialElements();
	}

	public FinancialElement getRoot() {
		return root;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof FinancialElement){
			FinancialElement el=(FinancialElement) parentElement;
			return el.child.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof FinancialElement){
			FinancialElement el=(FinancialElement) element;
			return el.parent;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof FinancialElement){
			FinancialElement el=(FinancialElement) element;
			return el.child.size()>0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof FinancialElement){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	
	private void buildFinancialElements(){
		//new FinancialElement(this.root,"Period Ending",FinancialPoint.FIELD_PeriodEnding);
		new FinancialElement(this.root,"Effective Date",FinancialPoint.FIELD_EffectiveDate,"None");
		
		
		FinancialElement is=new FinancialElement(this.root,"Income Statement",Financials.FIELD_IncomeStatement,Financials.FIELD_IncomeStatement);
		
		FinancialElement earningPerShare=new FinancialElement(is,"Earnings Per Share",IncomeStatementPoint.FIELD_EarningsPerShare,Financials.FIELD_IncomeStatement);
		new FinancialElement(earningPerShare,"Outstanding Shares",IncomeStatementPoint.FIELD_OutstandingShares,Financials.FIELD_IncomeStatement);
		
		FinancialElement totalRevenu=new FinancialElement(is,"Total Revenue",IncomeStatementPoint.FIELD_TotalRevenue,Financials.FIELD_IncomeStatement);
		new FinancialElement(totalRevenu,"Cost of Revenue",IncomeStatementPoint.FIELD_CostofRevenue,Financials.FIELD_IncomeStatement);
		new FinancialElement(is,"Gross Profit",IncomeStatementPoint.FIELD_GrossProfit,Financials.FIELD_IncomeStatement);

		//========================
		//== Operating Expenses ==
		//========================
		FinancialElement OperatingExpenses=new FinancialElement(is,"Operating Expenses",IncomeStatementPoint.FIELD_TotalOperatingExpenses,Financials.FIELD_IncomeStatement);
		new FinancialElement(OperatingExpenses,"Research Development",IncomeStatementPoint.FIELD_ResearchDevelopment,Financials.FIELD_IncomeStatement);
		new FinancialElement(OperatingExpenses,"Selling General and Administrative",IncomeStatementPoint.FIELD_SellingGeneralandAdministrative,Financials.FIELD_IncomeStatement);
		new FinancialElement(OperatingExpenses,"Non Recurring",IncomeStatementPoint.FIELD_NonRecurring,Financials.FIELD_IncomeStatement);
		new FinancialElement(OperatingExpenses,"Others",IncomeStatementPoint.FIELD_Others,Financials.FIELD_IncomeStatement);
		
		//==============================
		//== Operating Income or Loss ==
		//==============================
		new FinancialElement(is,"Operating Income or Loss",IncomeStatementPoint.FIELD_OperatingIncomeorLoss,Financials.FIELD_IncomeStatement);
		
		//================
		//== Net Income ==
		//================
		FinancialElement NetIncome=new FinancialElement(is,"Net Income",IncomeStatementPoint.FIELD_NetIncome,Financials.FIELD_IncomeStatement);
		new FinancialElement(is,"Preferred Stock And Other Adjustments",IncomeStatementPoint.FIELD_PreferredStockAndOtherAdjustments,Financials.FIELD_IncomeStatement);
		new FinancialElement(is,"Net Income Applicable To Common Shares",IncomeStatementPoint.FIELD_NetIncomeApplicableToCommonShares,Financials.FIELD_IncomeStatement);
		
		//=======================================
		//== Income from Continuing Operations ==
		//=======================================
		FinancialElement NetIncomeFromContinuingOps=new FinancialElement(NetIncome,"Net Income From Continuing Ops",IncomeStatementPoint.FIELD_NetIncomeFromContinuingOps,Financials.FIELD_IncomeStatement);
		new FinancialElement(NetIncomeFromContinuingOps,"Total Other Income Expenses Net",IncomeStatementPoint.FIELD_TotalOtherIncome_ExpensesNet,Financials.FIELD_IncomeStatement);
		new FinancialElement(NetIncomeFromContinuingOps,"Earnings Before Interest And Taxes",IncomeStatementPoint.FIELD_EarningsBeforeInterestAndTaxes,Financials.FIELD_IncomeStatement);
		new FinancialElement(NetIncomeFromContinuingOps,"Interest Expense",IncomeStatementPoint.FIELD_InterestExpense,Financials.FIELD_IncomeStatement);
		new FinancialElement(NetIncomeFromContinuingOps,"Income Before Tax",IncomeStatementPoint.FIELD_IncomeBeforeTax,Financials.FIELD_IncomeStatement);
		new FinancialElement(NetIncomeFromContinuingOps,"Income Tax Expense",IncomeStatementPoint.FIELD_IncomeTaxExpense,Financials.FIELD_IncomeStatement);
		new FinancialElement(NetIncomeFromContinuingOps,"Minority Interest",IncomeStatementPoint.FIELD_MinorityInterest,Financials.FIELD_IncomeStatement);
		
		//==========================
		//== Non-recurring Events ==
		//==========================
		FinancialElement NonrecurringEvents=new FinancialElement(NetIncome,"Non-recurring Events","Non-recurring Events",Financials.FIELD_IncomeStatement);
		new FinancialElement(NonrecurringEvents,"Discontinued Operations",IncomeStatementPoint.FIELD_DiscontinuedOperations,Financials.FIELD_IncomeStatement);
		new FinancialElement(NonrecurringEvents,"Extraordinary Items",IncomeStatementPoint.FIELD_ExtraordinaryItems,Financials.FIELD_IncomeStatement);
		new FinancialElement(NonrecurringEvents,"Effect Of Accounting Changes",IncomeStatementPoint.FIELD_EffectOfAccountingChanges,Financials.FIELD_IncomeStatement);
		new FinancialElement(NonrecurringEvents,"Other Items",IncomeStatementPoint.FIELD_OtherItems,Financials.FIELD_IncomeStatement);
		
		
		
		FinancialElement bc=new FinancialElement(this.root,"Balance Sheet",Financials.FIELD_BalanceSheet,Financials.FIELD_BalanceSheet);
		//========================
		//==       Assets       == (Anlagevermögen)
		//========================
		
		FinancialElement Assets=new FinancialElement(bc,"Total Assets",BalanceSheetPoint.FIELD_TotalAssets,Financials.FIELD_BalanceSheet);
		FinancialElement CurrentAssets=new FinancialElement(Assets,"Total Current Assets",BalanceSheetPoint.FIELD_TotalCurrentAssets,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentAssets,"Cash And Cash Equivalents",BalanceSheetPoint.FIELD_CashAndCashEquivalents,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentAssets,"Short Term Investments",BalanceSheetPoint.FIELD_ShortTermInvestments,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentAssets,"Net Receivables",BalanceSheetPoint.FIELD_NetReceivables,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentAssets,"Inventory",BalanceSheetPoint.FIELD_Inventory,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentAssets,"Other Current Assets",BalanceSheetPoint.FIELD_OtherCurrentAssets,Financials.FIELD_BalanceSheet);
		
		new FinancialElement(Assets,"LongTermInvestments",BalanceSheetPoint.FIELD_LongTermInvestments,Financials.FIELD_BalanceSheet);
		new FinancialElement(Assets,"Property Plant and Equipment",BalanceSheetPoint.FIELD_PropertyPlantandEquipment,Financials.FIELD_BalanceSheet);
		new FinancialElement(Assets,"Goodwill",BalanceSheetPoint.FIELD_Goodwill,Financials.FIELD_BalanceSheet);
		new FinancialElement(Assets,"Intangible Assets",BalanceSheetPoint.FIELD_IntangibleAssets,Financials.FIELD_BalanceSheet);
		new FinancialElement(Assets,"Accumulated Amortization",BalanceSheetPoint.FIELD_AccumulatedAmortization,Financials.FIELD_BalanceSheet);
		new FinancialElement(Assets,"Other Assets",BalanceSheetPoint.FIELD_OtherAssets,Financials.FIELD_BalanceSheet);
		new FinancialElement(Assets,"Deferred Long Term Asset Charges",BalanceSheetPoint.FIELD_DeferredLongTermAssetCharges,Financials.FIELD_BalanceSheet);
		
		//========================
		//==    Liabilities     == (Verbindlichkeit)
		//========================
		FinancialElement Liabilities=new FinancialElement(bc,"Total Liabilities",BalanceSheetPoint.FIELD_TotalLiabilities,Financials.FIELD_BalanceSheet);
		FinancialElement CurrentLiabilities=new FinancialElement(Liabilities,"Total Current Liabilities",BalanceSheetPoint.FIELD_TotalCurrentLiabilities,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentLiabilities,"Accounts Payable",BalanceSheetPoint.FIELD_AccountsPayable,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentLiabilities,"Short Current Long Term Debt",BalanceSheetPoint.FIELD_Short_CurrentLongTermDebt,Financials.FIELD_BalanceSheet);
		new FinancialElement(CurrentLiabilities,"Other Current Liabilities",BalanceSheetPoint.FIELD_OtherCurrentLiabilities,Financials.FIELD_BalanceSheet);
		
		new FinancialElement(Liabilities,"Long Term Debt",BalanceSheetPoint.FIELD_LongTermDebt,Financials.FIELD_BalanceSheet);
		new FinancialElement(Liabilities,"Other Liabilities",BalanceSheetPoint.FIELD_OtherLiabilities,Financials.FIELD_BalanceSheet);
		new FinancialElement(Liabilities,"Deferred Long Term Liability Charges",BalanceSheetPoint.FIELD_DeferredLongTermLiabilityCharges,Financials.FIELD_BalanceSheet);
		new FinancialElement(Liabilities,"Minority Interest",BalanceSheetPoint.FIELD_MinorityInterest,Financials.FIELD_BalanceSheet);
		new FinancialElement(Liabilities,"Negative Goodwill",BalanceSheetPoint.FIELD_NegativeGoodwill,Financials.FIELD_BalanceSheet);
		//TODO
		
		FinancialElement cf=new FinancialElement(this.root,"Cash Flow",Financials.FIELD_CashFlow,Financials.FIELD_CashFlow);
		FinancialElement cfNetIncome=new FinancialElement(cf,"Net Income",CashFlowPoint.FIELD_NetIncome,Financials.FIELD_CashFlow);
		
		//==================================================================
		//==      Operating Activities, Cash Flows Provided By or Used In == (Betriebliche Tätigkeit)
		//==================================================================
		FinancialElement  CashFlowFromOperatingActivities=new FinancialElement(cf,"Total Cash Flow From Operating Activities",CashFlowPoint.FIELD_TotalCashFlowFromOperatingActivities,Financials.FIELD_CashFlow);
		new FinancialElement(CashFlowFromOperatingActivities,"Depreciation",CashFlowPoint.FIELD_Depreciation,Financials.FIELD_CashFlow);
		new FinancialElement(CashFlowFromOperatingActivities,"Adjustments To Net Income",CashFlowPoint.FIELD_AdjustmentsToNetIncome,Financials.FIELD_CashFlow);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Accounts Receivables",CashFlowPoint.FIELD_ChangesInAccountsReceivables,Financials.FIELD_CashFlow);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Liabilities",CashFlowPoint.FIELD_ChangesInLiabilities,Financials.FIELD_CashFlow);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Inventories",CashFlowPoint.FIELD_ChangesInInventories,Financials.FIELD_CashFlow);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Other Operating Activities",CashFlowPoint.FIELD_ChangesInOtherOperatingActivities,Financials.FIELD_CashFlow);
		
		//==================================================================
		//==      Investing Activities, Cash Flows Provided By or Used In == (Investitionstätigkeit)
		//==================================================================
		FinancialElement  TotalCashFlowsFromInvestingActivities=new FinancialElement(cf,"Total Cash Flows From Investing Activities",CashFlowPoint.FIELD_TotalCashFlowsFromInvestingActivities,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromInvestingActivities,"Capital Expenditures",CashFlowPoint.FIELD_CapitalExpenditures,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromInvestingActivities,"Investments",CashFlowPoint.FIELD_Investments,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromInvestingActivities,"Other Cash flows from Investing Activities",CashFlowPoint.FIELD_OtherCashflowsfromInvestingActivities,Financials.FIELD_CashFlow);
		
		//==================================================================
		//==      Financing Activities, Cash Flows Provided By or Used In == (Finanzierungstätigkeit)
		//==================================================================
		FinancialElement  TotalCashFlowsFromFinancingActivities=new FinancialElement(cf,"Total Cash Flows From Financing Activities",CashFlowPoint.FIELD_TotalCashFlowsFromFinancingActivities,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Dividends Paid",CashFlowPoint.FIELD_DividendsPaid,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Sale Purchase of Stock",CashFlowPoint.FIELD_SalePurchaseofStock,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Net Borrowings",CashFlowPoint.FIELD_NetBorrowings,Financials.FIELD_CashFlow);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Other Cash Flows from Financing Activities",CashFlowPoint.FIELD_OtherCashFlowsfromFinancingActivities,Financials.FIELD_CashFlow);
		
		
		new FinancialElement(cf,"Effect Of Exchange Rate Changes",CashFlowPoint.FIELD_EffectOfExchangeRateChanges,Financials.FIELD_CashFlow);
		new FinancialElement(cf,"Change In Cash and Cash Equivalents",CashFlowPoint.FIELD_ChangeInCashandCashEquivalents,Financials.FIELD_CashFlow);
		
		
		new FinancialElement(this.root,"Employees",IncomeStatementPoint.FIELD_Employees,Financials.FIELD_IncomeStatement);
		
		
	}
	
	
	public class FinancialElement{
		
		public FinancialElement parent;
		public LinkedList<FinancialElement> child=new LinkedList<FinancialElement>();
		
		public String name;
		public String fieldKey;
		public String sectorKey;
		
		public FinancialElement(FinancialElement parent,String name,String fieldKey,String sectorKey){
			this.name=name;
			this.parent=parent;
			this.fieldKey=fieldKey;
			this.sectorKey=sectorKey;
			if(this.parent!=null)
			this.parent.child.add(this);
		}

		@Override
		public String toString() {
			return "FinancialElement [name=" + name + "]";
		}
		
		
		
	}
	
	
	

}
