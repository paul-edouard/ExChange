 
package com.munch.exchange.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.dialog.AddRateDialog;
import com.munch.exchange.dialog.WorkspaceDialog;
import com.munch.exchange.services.IExchangeRateProvider;

public class AddRateHandler {
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Execute
	public void execute(Shell shell) {
		
		AddRateDialog dialog=new AddRateDialog(shell,exchangeRateProvider);
		if (dialog.open() == Window.OK) {
			System.out.println("OK pressed");
		}
		//WorkspaceDialog dialog = new WorkspaceDialog(shell);
		//if (dialog.open() != Window.OK) {
	}
	
	
	@CanExecute
	public boolean canExecute() {
		//TODO Your code goes here
		return true;
	}
		
}