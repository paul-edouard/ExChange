 
package com.munch.exchange.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.parts.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IHistoricalDataProvider;

public class ClearHistoryPointsHandler {
	
	private boolean canExcecute=false;
	private ExchangeRate selectedRate;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	@Execute
	public void execute(Shell shell, IHistoricalDataProvider historicalDataProvider) {
		System.out.println("Selected Rate:"+selectedRate.getFullName());
		if(selectedRate==null){
			MessageDialog.openError(shell, "Selection error", "No rate selected");
			return;
		}
		
		boolean res = MessageDialog.openConfirm(shell, "Clear all history points from rate?",
				"Do you really want to clear all the history points from rate: \""+selectedRate.getFullName()+"\"?");
		
		if(!res)return;
		
		if(selectedRate!=null){
			
			historicalDataProvider.clear(selectedRate);
			eventBroker.post(IEventConstant.HISTORICAL_DATA_CLEARED,selectedRate.getUUID());
		
		}
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return canExcecute;
	}
	
	@Inject
	public void analyseSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) 
	ISelection selection){
		if(selection instanceof IStructuredSelection){
			IStructuredSelection sel=(IStructuredSelection) selection;
			if(sel.size()==1 && sel.getFirstElement() instanceof ExchangeRate && !(sel.getFirstElement() instanceof RateContainer)){
				selectedRate=(ExchangeRate)sel.getFirstElement();
				canExcecute=true;
				return;
			}
		}
		
		selectedRate=null;
		canExcecute=false;
	}
		
}