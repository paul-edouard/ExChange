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
	
	
	private FinancialElement root=new FinancialElement(null,"root","root");
	
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
		new FinancialElement(this.root,"Effetive Date",FinancialPoint.FIELD_EffectiveDate);
		
		
		FinancialElement is=new FinancialElement(this.root,"Income Statement",Financials.FIELD_IncomeStatement);
		FinancialElement totalRevenu=new FinancialElement(is,"Total Revenue",IncomeStatementPoint.FIELD_TotalRevenue);
		new FinancialElement(totalRevenu,"Cost of Revenue",IncomeStatementPoint.FIELD_CostofRevenue);
		new FinancialElement(is,"Gross Profit",IncomeStatementPoint.FIELD_GrossProfit);

		//========================
		//== Operating Expenses ==
		//========================
		FinancialElement OperatingExpenses=new FinancialElement(is,"Operating Expenses",IncomeStatementPoint.FIELD_TotalOperatingExpenses);
		new FinancialElement(OperatingExpenses,"Research Development",IncomeStatementPoint.FIELD_ResearchDevelopment);
		new FinancialElement(OperatingExpenses,"Selling General and Administrative",IncomeStatementPoint.FIELD_SellingGeneralandAdministrative);
		new FinancialElement(OperatingExpenses,"Non Recurring",IncomeStatementPoint.FIELD_NonRecurring);
		new FinancialElement(OperatingExpenses,"Others",IncomeStatementPoint.FIELD_Others);
		
		//==============================
		//== Operating Income or Loss ==
		//==============================
		new FinancialElement(is,"Operating Income or Loss",IncomeStatementPoint.FIELD_OperatingIncomeorLoss);
		
		//================
		//== Net Income ==
		//================
		FinancialElement NetIncome=new FinancialElement(is,"Net Income",IncomeStatementPoint.FIELD_NetIncome);
		new FinancialElement(is,"Preferred Stock And Other Adjustments",IncomeStatementPoint.FIELD_PreferredStockAndOtherAdjustments);
		new FinancialElement(is,"Net Income Applicable To Common Shares",IncomeStatementPoint.FIELD_NetIncomeApplicableToCommonShares);
		
		//=======================================
		//== Income from Continuing Operations ==
		//=======================================
		FinancialElement NetIncomeFromContinuingOps=new FinancialElement(NetIncome,"Net Income From Continuing Ops",IncomeStatementPoint.FIELD_NetIncomeFromContinuingOps);
		new FinancialElement(NetIncomeFromContinuingOps,"Total Other Income Expenses Net",IncomeStatementPoint.FIELD_TotalOtherIncome_ExpensesNet);
		new FinancialElement(NetIncomeFromContinuingOps,"Earnings Before Interest And Taxes",IncomeStatementPoint.FIELD_EarningsBeforeInterestAndTaxes);
		new FinancialElement(NetIncomeFromContinuingOps,"Interest Expense",IncomeStatementPoint.FIELD_InterestExpense);
		new FinancialElement(NetIncomeFromContinuingOps,"Income Before Tax",IncomeStatementPoint.FIELD_IncomeBeforeTax);
		new FinancialElement(NetIncomeFromContinuingOps,"Income Tax Expense",IncomeStatementPoint.FIELD_IncomeTaxExpense);
		new FinancialElement(NetIncomeFromContinuingOps,"Minority Interest",IncomeStatementPoint.FIELD_MinorityInterest);
		
		//==========================
		//== Non-recurring Events ==
		//==========================
		FinancialElement NonrecurringEvents=new FinancialElement(NetIncome,"Non-recurring Events","Non-recurring Events");
		new FinancialElement(NonrecurringEvents,"Discontinued Operations",IncomeStatementPoint.FIELD_DiscontinuedOperations);
		new FinancialElement(NonrecurringEvents,"Extraordinary Items",IncomeStatementPoint.FIELD_ExtraordinaryItems);
		new FinancialElement(NonrecurringEvents,"Effect Of Accounting Changes",IncomeStatementPoint.FIELD_EffectOfAccountingChanges);
		new FinancialElement(NonrecurringEvents,"Other Items",IncomeStatementPoint.FIELD_OtherItems);
		
		
		
		FinancialElement bc=new FinancialElement(this.root,"Balance Sheet",Financials.FIELD_BalanceSheet);
		//========================
		//==       Assets       == (Anlagevermögen)
		//========================
		
		FinancialElement Assets=new FinancialElement(bc,"Total Assets",BalanceSheetPoint.FIELD_TotalAssets);
		FinancialElement CurrentAssets=new FinancialElement(Assets,"Total Current Assets",BalanceSheetPoint.FIELD_TotalCurrentAssets);
		new FinancialElement(CurrentAssets,"Cash And Cash Equivalents",BalanceSheetPoint.FIELD_CashAndCashEquivalents);
		new FinancialElement(CurrentAssets,"Short Term Investments",BalanceSheetPoint.FIELD_ShortTermInvestments);
		new FinancialElement(CurrentAssets,"Net Receivables",BalanceSheetPoint.FIELD_NetReceivables);
		new FinancialElement(CurrentAssets,"Inventory",BalanceSheetPoint.FIELD_Inventory);
		new FinancialElement(CurrentAssets,"Other Current Assets",BalanceSheetPoint.FIELD_OtherCurrentAssets);
		
		new FinancialElement(Assets,"LongTermInvestments",BalanceSheetPoint.FIELD_LongTermInvestments);
		new FinancialElement(Assets,"Property Plant and Equipment",BalanceSheetPoint.FIELD_PropertyPlantandEquipment);
		new FinancialElement(Assets,"Goodwill",BalanceSheetPoint.FIELD_Goodwill);
		new FinancialElement(Assets,"Intangible Assets",BalanceSheetPoint.FIELD_IntangibleAssets);
		new FinancialElement(Assets,"Accumulated Amortization",BalanceSheetPoint.FIELD_AccumulatedAmortization);
		new FinancialElement(Assets,"Other Assets",BalanceSheetPoint.FIELD_OtherAssets);
		new FinancialElement(Assets,"Deferred Long Term Asset Charges",BalanceSheetPoint.FIELD_DeferredLongTermAssetCharges);
		
		//========================
		//==    Liabilities     == (Verbindlichkeit)
		//========================
		FinancialElement Liabilities=new FinancialElement(bc,"Total Liabilities",BalanceSheetPoint.FIELD_TotalLiabilities);
		FinancialElement CurrentLiabilities=new FinancialElement(Liabilities,"Total Current Liabilities",BalanceSheetPoint.FIELD_TotalCurrentLiabilities);
		new FinancialElement(CurrentLiabilities,"Accounts Payable",BalanceSheetPoint.FIELD_AccountsPayable);
		new FinancialElement(CurrentLiabilities,"Short Current Long Term Debt",BalanceSheetPoint.FIELD_Short_CurrentLongTermDebt);
		new FinancialElement(CurrentLiabilities,"Other Current Liabilities",BalanceSheetPoint.FIELD_OtherCurrentLiabilities);
		
		new FinancialElement(Liabilities,"Long Term Debt",BalanceSheetPoint.FIELD_LongTermDebt);
		new FinancialElement(Liabilities,"Other Liabilities",BalanceSheetPoint.FIELD_OtherLiabilities);
		new FinancialElement(Liabilities,"Deferred Long Term Liability Charges",BalanceSheetPoint.FIELD_DeferredLongTermLiabilityCharges);
		new FinancialElement(Liabilities,"Minority Interest",BalanceSheetPoint.FIELD_MinorityInterest);
		new FinancialElement(Liabilities,"Negative Goodwill",BalanceSheetPoint.FIELD_NegativeGoodwill);
		//TODO
		
		FinancialElement cf=new FinancialElement(this.root,"Cash Flow",Financials.FIELD_CashFlow);
		FinancialElement cfNetIncome=new FinancialElement(cf,"Net Income",CashFlowPoint.FIELD_NetIncome);
		
		//==================================================================
		//==      Operating Activities, Cash Flows Provided By or Used In == (Betriebliche Tätigkeit)
		//==================================================================
		FinancialElement  CashFlowFromOperatingActivities=new FinancialElement(cf,"Total Cash Flow From Operating Activities",CashFlowPoint.FIELD_TotalCashFlowFromOperatingActivities);
		new FinancialElement(CashFlowFromOperatingActivities,"Depreciation",CashFlowPoint.FIELD_Depreciation);
		new FinancialElement(CashFlowFromOperatingActivities,"Adjustments To Net Income",CashFlowPoint.FIELD_AdjustmentsToNetIncome);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Accounts Receivables",CashFlowPoint.FIELD_ChangesInAccountsReceivables);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Liabilities",CashFlowPoint.FIELD_ChangesInLiabilities);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Inventories",CashFlowPoint.FIELD_ChangesInInventories);
		new FinancialElement(CashFlowFromOperatingActivities,"Changes In Other Operating Activities",CashFlowPoint.FIELD_ChangesInOtherOperatingActivities);
		
		//==================================================================
		//==      Investing Activities, Cash Flows Provided By or Used In == (Investitionstätigkeit)
		//==================================================================
		FinancialElement  TotalCashFlowsFromInvestingActivities=new FinancialElement(cf,"Total Cash Flows From Investing Activities",CashFlowPoint.FIELD_TotalCashFlowsFromInvestingActivities);
		new FinancialElement(TotalCashFlowsFromInvestingActivities,"Capital Expenditures",CashFlowPoint.FIELD_CapitalExpenditures);
		new FinancialElement(TotalCashFlowsFromInvestingActivities,"Investments",CashFlowPoint.FIELD_Investments);
		new FinancialElement(TotalCashFlowsFromInvestingActivities,"Other Cash flows from Investing Activities",CashFlowPoint.FIELD_OtherCashflowsfromInvestingActivities);
		
		//==================================================================
		//==      Financing Activities, Cash Flows Provided By or Used In == (Finanzierungstätigkeit)
		//==================================================================
		FinancialElement  TotalCashFlowsFromFinancingActivities=new FinancialElement(cf,"Total Cash Flows From Financing Activities",CashFlowPoint.FIELD_TotalCashFlowsFromFinancingActivities);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Dividends Paid",CashFlowPoint.FIELD_DividendsPaid);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Sale Purchase of Stock",CashFlowPoint.FIELD_SalePurchaseofStock);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Net Borrowings",CashFlowPoint.FIELD_NetBorrowings);
		new FinancialElement(TotalCashFlowsFromFinancingActivities,"Other Cash Flows from Financing Activities",CashFlowPoint.FIELD_OtherCashFlowsfromFinancingActivities);
		
		
		new FinancialElement(cf,"Effect Of Exchange Rate Changes",CashFlowPoint.FIELD_EffectOfExchangeRateChanges);
		new FinancialElement(cf,"Change In Cash and Cash Equivalents",CashFlowPoint.FIELD_ChangeInCashandCashEquivalents);
		
		
	}
	
	
	public class FinancialElement{
		
		public FinancialElement parent;
		public LinkedList<FinancialElement> child=new LinkedList<FinancialElement>();
		
		public String name;
		public String fieldKey;
		
		public FinancialElement(FinancialElement parent,String name,String fieldKey){
			this.name=name;
			this.parent=parent;
			this.fieldKey=fieldKey;
			if(this.parent!=null)
			this.parent.child.add(this);
		}

		@Override
		public String toString() {
			return "FinancialElement [name=" + name + "]";
		}
		
		
		
	}
	
	
	

}
