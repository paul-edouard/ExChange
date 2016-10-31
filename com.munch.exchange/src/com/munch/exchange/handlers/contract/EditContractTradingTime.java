

package com.munch.exchange.handlers.contract;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.dialog.EditCommissionDialog;
import com.munch.exchange.dialog.EditTradingTimeDialog;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.parts.overview.RatesTreeContentProvider.ExContractContainer;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;


public class EditContractTradingTime {
	
	private static Logger logger = Logger.getLogger(EditContractTradingTime.class);
	
	@Inject
	private IIBContractProvider contractProvider;
	
	
	private boolean canExcecute=false;
	private IbContract selectedContract;
	
	
	@Execute
	public void execute(Shell shell) {
		
		
		EditTradingTimeDialog dialog=new EditTradingTimeDialog(shell,
				selectedContract.getStartTradeTimeInMs(), selectedContract.getEndTradeTimeInMs(),
				selectedContract.getEndTradeEntryTimeInMs(), selectedContract.getSelectedTradingPeriod());
		if (dialog.open() == Window.OK) {
			selectedContract.setStartTradeTimeInMs(dialog.getStartTime());
			selectedContract.setEndTradeTimeInMs(dialog.getEndTime());
			
			selectedContract.setEndTradeEntryTimeInMs(dialog.getEndEntryTime());
			selectedContract.setSelectedTradingPeriod(dialog.getTradingPeriod());
			
			contractProvider.update(selectedContract);	
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
			
			selectedContract=null;
			canExcecute=false;
			
			
			IStructuredSelection sel=(IStructuredSelection) selection;
			if(sel.size()==1 && sel.getFirstElement() instanceof IbContract && !(sel.getFirstElement() instanceof ExContractContainer)){
				selectedContract=(IbContract) sel.getFirstElement();
				canExcecute=true;
				return;
			}
		}
		
	}
		
}