package com.munch.exchange.parts.financials;

import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.BalanceSheetPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;

public class StockFinancialsContentProvider implements
IStructuredContentProvider, ITreeContentProvider{
	
	
	private Stock stock;
	private FinancialElement root=new FinancialElement(null,"root","root");
	
	public StockFinancialsContentProvider(Stock stock){
		this.stock=stock;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void buildFinancialElements(){
		FinancialElement bc=new FinancialElement(this.root,"Balance Sheet","Balance Sheet");
		stock.getFinancials().getBalanceSheet();
		//TODO
	}
	
	
	private class FinancialElement{
		
		public FinancialElement parent;
		public FinancialElement child;
		
		public LinkedList<FinancialPoint> values=new LinkedList<FinancialPoint>();
		public String name;
		public String fieldKey;
		
		public FinancialElement(FinancialElement parent,String name,String fieldKey){
			this.name=name;
			this.parent=parent;
			this.parent.child=this;
			if(parent!=null)
				this.values=parent.values;
			
		}
		
		
		
	}
	
	
	

}
