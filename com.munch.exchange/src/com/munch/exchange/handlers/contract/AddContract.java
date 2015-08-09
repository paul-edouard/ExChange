 
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
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

public class AddContract {
	
	@Inject
	private IIBContractProvider contractProvider;
	
	@Inject
	private IIBHistoricalDataProvider historicalDataProvider;
	
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private Shell shell;
	
	@Execute
	public void execute() {
		
		AddContractDialog dialog=new AddContractDialog(shell,contractProvider);
		if (dialog.open() == Window.OK) {
			eventBroker.post(IEventConstant.CONTRACT_NEW, dialog.getContract());
		}
		
		
		/*
		List<ExContract> list=contractProvider.getAll();
		for(ExContract exContract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			//System.out.println(contract);
			//System.out.println(contract.getSecType().getClass());
			
			System.out.println("Contract: "+exContract.toString());
			List<ExContractBars> bars=historicalDataProvider.getAllExContractBars(exContract);
			if(bars==null)continue;
			for(ExContractBars contractBar:bars){
				System.out.println(contractBar.toString());
				ExBar bar=historicalDataProvider.getFirstBar(contractBar, ExSecondeBar.class);
				//ExSecondeBar bar=historicalDataProvider.getFirstSecondeBar(contractBar);
				System.out.println("firast Bar: "+bar);
			}
			
		}
		*/
		
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		//TODO Your code goes here
		return true;
	}
		
}