package com.munch.exchange.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.watchlist.Watchlist;
import com.munch.exchange.parts.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.services.IExchangeRateProvider;

public class WatchlistTreeContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	
	private Watchlist currentList;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	public WatchlistTreeContentProvider() {
		super();
	}
	

	public void setCurrentList(Watchlist currentList) {
		this.currentList = currentList;
	}
	
	

	public Watchlist getCurrentList() {
		return currentList;
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
		if(currentList==null)return null;
		
		if(parentElement instanceof Watchlist){
			Watchlist cont=(Watchlist) parentElement;
			return cont.getList().toArray();
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
		if(currentList==null)return false;
		
		if(element instanceof Watchlist){
			Watchlist cont=(Watchlist) element;
			return !cont.getList().isEmpty();
			
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(currentList==null)return null;
		
		if(inputElement instanceof Watchlist){
			return this.getChildren(inputElement);
		}
		return null;
	}

}
