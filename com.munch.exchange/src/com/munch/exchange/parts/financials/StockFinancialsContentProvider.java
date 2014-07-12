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
