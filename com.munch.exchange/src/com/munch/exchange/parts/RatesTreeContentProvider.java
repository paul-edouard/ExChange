package com.munch.exchange.parts;

import java.util.LinkedList;

import javax.inject.Inject;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IExchangeRateProvider;

public class RatesTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	
	
	RateContainer root;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;

	public RatesTreeContentProvider() {
		super();
		// TODO Auto-generated constructor stub
		//System.out.println("RatesTreeContentProvider contructor called");
		
		
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
		if(parentElement instanceof RateContainer){
			RateContainer cont=(RateContainer) parentElement;
			return cont.getChilds().toArray();
			
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof RateContainer){
			RateContainer cont=(RateContainer) element;
			return !cont.getChilds().isEmpty();
			
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof RateContainer){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	
	
	
	public RateContainer getRoot() {
		if(root!=null)return root;
		
		
		root=new RateContainer("ROOT", null);
		
		RateContainer Stocks=new RateContainer("Stocks",root);
		Stocks.setChilds(exchangeRateProvider.loadAll(Stock.class));
		
		RateContainer Indices=new RateContainer("Indices",root);
		Indices.setChilds(exchangeRateProvider.loadAll(Indice.class));
		
		RateContainer Funds=new RateContainer("Funds",root);
		Funds.setChilds(exchangeRateProvider.loadAll(Fund.class));
		
		RateContainer Commodities=new RateContainer("Commodities",root);
		Commodities.setChilds(exchangeRateProvider.loadAll(Commodity.class));
		
		RateContainer Currencies=new RateContainer("Currencies",root);
		Currencies.setChilds(exchangeRateProvider.loadAll(Currency.class));
		
		
		return root;
	}

	public class RateContainer extends ExchangeRate{
		
		protected ExchangeRate parent;
		protected LinkedList<ExchangeRate> childs=new LinkedList<ExchangeRate>();
		
		public RateContainer(String name, ExchangeRate parent) {
			super();
			this.name = name;
			this.parent = parent;
			if(this.parent!=null && parent instanceof RateContainer){
				RateContainer p=(RateContainer) this.parent;
				p.getChilds().add(this);
			}
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ExchangeRate getParent() {
			return parent;
		}
		public void setParent(ExchangeRate parent) {
			this.parent = parent;
		}
		public LinkedList<ExchangeRate> getChilds() {
			return childs;
		}
		
		public void setChilds(LinkedList<ExchangeRate> childs) {
			this.childs = childs;
		}
		
		
	}
	

}
