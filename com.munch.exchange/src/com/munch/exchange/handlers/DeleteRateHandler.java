 
package com.munch.exchange.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.parts.overview.RatesTreeContentProvider.ExContractContainer;
import com.munch.exchange.parts.overview.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

public class DeleteRateHandler {
	
	private boolean canExcecute=false;
	private ExchangeRate selectedRate;
	private IbContract selectedContract;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Execute
	public void execute(Shell shell, IExchangeRateProvider rateProvider,
			IIBContractProvider contractProvider, IIBHistoricalDataProvider historicalDataProvider) {
	//	System.out.println("Selected Rate:"+selectedRate.getFullName());
		if(selectedRate==null && selectedContract==null){
			MessageDialog.openError(shell, "Selection error", "No correct selection?");
			return;
		}
		
		//Delete a rate
		if(selectedRate!=null){
		
		boolean res = MessageDialog.openConfirm(shell, "Delete rate?",
				"Do you really want to delete the project: \""+selectedRate.getFullName()+"\"?");
		
		if(!res)return;
		
			if(!rateProvider.delete(selectedRate)){
				MessageDialog.openWarning(shell, "Delete rate error", "Cannot delete the rate: "+selectedRate.getFullName());
			}
			else{
				eventBroker.post(IEventConstant.RATE_DELETE,selectedRate);
			}
		}
		
		//Delete a Contract
		if(selectedContract!=null){
			
			boolean res = MessageDialog.openConfirm(shell, "Delete contract?",
					"Do you really want to delete the project: \""+selectedContract.getLongName()+"\"?");
			
			if(!res)return;
				
//			Tries to delete the bar week after week
			for(BarContainer container:historicalDataProvider.getAllBarContainers(selectedContract)){
				
				removeAllBars(historicalDataProvider, container, BarSize._1_secs);
				removeAllBars(historicalDataProvider, container, BarSize._1_min);
				
				
			}
			
			contractProvider.remove(selectedContract.getId());
//				eventBroker.post(IEventConstant.CONTRACT_DELETE,selectedContract);
				
				/*
				if(!rateProvider.delete(selectedRate)){
					MessageDialog.openWarning(shell, "Delete rate error", "Cannot delete the rate: "+selectedRate.getFullName());
				}
				else{
					eventBroker.post(IEventConstant.RATE_DELETE,selectedRate);
				}
				*/
		}
		
		
		
	}
	
	
	private void removeAllBars(IIBHistoricalDataProvider historicalDataProvider, BarContainer container, BarSize barSize){
		
		long weekInSeconde=7L*24L*60L*60L;
		
		ExBar lastBar=historicalDataProvider.getFirstTimeBar(container, barSize);
		
		while(lastBar!=null){
			
			System.out.println("Last Bar: "+BarUtils.format(lastBar.getTimeInMs()));
			
//			eventBroker.post(IEventConstant.TEXT_INFO,text);
			
			historicalDataProvider.removeBarsFromTo(container, barSize, lastBar.getTime(), lastBar.getTime()+weekInSeconde);

			lastBar=historicalDataProvider.getFirstTimeBar(container, barSize);
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
			
			selectedRate=null;
			selectedContract=null;
			canExcecute=false;
			
			
			IStructuredSelection sel=(IStructuredSelection) selection;
			if(sel.size()==1 && sel.getFirstElement() instanceof ExchangeRate && !(sel.getFirstElement() instanceof RateContainer)){
				selectedRate=(ExchangeRate)sel.getFirstElement();
				canExcecute=true;
				return;
			}
			else if(sel.size()==1 && sel.getFirstElement() instanceof IbContract && !(sel.getFirstElement() instanceof ExContractContainer)){
				selectedContract=(IbContract) sel.getFirstElement();
				canExcecute=true;
				return;
			}
		}
		
	}
		
}