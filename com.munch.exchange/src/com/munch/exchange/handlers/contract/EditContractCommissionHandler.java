 
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
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.parts.overview.RatesTreeContentProvider.ExContractContainer;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;


public class EditContractCommissionHandler {
	
	private static Logger logger = Logger.getLogger(EditContractCommissionHandler.class);
	
	@Inject
	private IIBContractProvider contractProvider;
	
	@Inject
	private Shell shell;
	
	private boolean canExcecute=false;
	private IbContract selectedContract;
	
	
	@Execute
	public void execute() {
		logger.info("Hi, I'm EditContractHandler");
		IbCommission commission=contractProvider.getCommission(selectedContract);
		logger.info("Commission found: "+commission.getFixed());
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