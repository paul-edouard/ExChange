 
package com.munch.exchange.handlers.contract;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.services.ejb.interfaces.IContractProvider;

public class AddContract {
	
	@Inject
	private IContractProvider contractProvider;
	
	@Execute
	public void execute() {
		//TODO Your code goes here
		List<ExContract> list=contractProvider.getAll();
		for(ExContract contract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			System.out.println(contract);
			System.out.println(contract.getSecType().getClass());
		}
		
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		//TODO Your code goes here
		return true;
	}
		
}