package com.munch.exchange.parts.financials;

import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

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
		//FinancialElement NetIncomeFromContinuingOps=new FinancialElement(NetIncome,"Net Income From Continuing Ops",IncomeStatementPoint.FIELD_NetIncomeFromContinuingOps);
		
		
		
		
		FinancialElement bc=new FinancialElement(this.root,"Balance Sheet",Financials.FIELD_BalanceSheet);
		
		
		FinancialElement cf=new FinancialElement(this.root,"Cash Flow",Financials.FIELD_CashFlow);
		
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
