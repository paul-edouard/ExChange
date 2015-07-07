 
package com.munch.exchange.handlers.contract;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.AddContractDialog;
import com.munch.exchange.dialog.AddRateDialog;
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.services.ejb.interfaces.IContractProvider;

public class AddContract {
	
	@Inject
	private IContractProvider contractProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private Shell shell;
	
	@Execute
	public void execute() {
		//TODO Your code goes here
		
		AddContractDialog dialog=new AddContractDialog(shell,contractProvider);
		if (dialog.open() == Window.OK) {
			eventBroker.post(IEventConstant.CONTRACT_NEW, dialog.getContract());
		}
		
		/*
		List<ExContract> list=contractProvider.getAll();
		for(ExContract contract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			System.out.println(contract);
			System.out.println(contract.getSecType().getClass());
		}
		*/
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		//TODO Your code goes here
		return true;
	}
		
}