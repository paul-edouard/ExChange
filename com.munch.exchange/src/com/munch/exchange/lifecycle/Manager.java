package com.munch.exchange.lifecycle;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.munch.exchange.dialog.WorkspaceDialog;
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IWatchlistProvider;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IContractProvider;

@SuppressWarnings("restriction")
public class Manager {
	
	
	@Inject
	@Preference(nodePath = "com.munch.exchange", value = "workspaces")
	private String workspaces;
	
	@Inject
	@Preference(nodePath = "com.munch.exchange", value = "workspace")
	private String workspace;
	
	/*
	@PostContextCreate
	void postContextCreate(RemoteService remoteService, IEclipseContext parent){
		//BasicConfigurator.configure();
		this.remoteService=remoteService;
		//create and start the login interface
		closeCalled=login(parent);
		
		//save the session in the eclipse context
		parent.set(ClientSession.class,session);
		
		//save the Remote Service in the eclipse context
		//parent.set(RemoteService.class,this.remoteService);
		
	}
	*/
	
	private IExchangeRateProvider exchangeRateProvider;
	
	//@ProcessAdditions
	@PostContextCreate
	public void postContextCreate(@Preference IEclipsePreferences prefs,
			IApplicationContext appContext, Display display,
			IExchangeRateProvider exchangeRateProvider,
			IWatchlistProvider watchlistProvider,
			IContractProvider contractProvider) {
		
		
		BasicConfigurator.configure();
		
		contractProvider.init();
		List<ExContract> list=contractProvider.getAll();
		for(ExContract contract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			System.out.println(contract);
			//System.out.println(contract.getSecType().getClass());
		}
		
		final Shell shell = new Shell(SWT.TOOL | SWT.NO_TRIM);
		
		/*
		boolean res = MessageDialog.openConfirm(shell, "Delete rate?",
				"Do you really want to delete the project: \""+"\"?");
		*/
		WorkspaceDialog dialog = new WorkspaceDialog(shell);
		if (workspaces != null) {
			dialog.setWorkspaces(workspaces);
		}
		if (workspace != null) {
			dialog.setWorkspace(workspace);
		}

		// close the static splash screen
		appContext.applicationRunning();

		// position the shell
		 setLocation(display, shell);
		
		// open the dialog
		if (dialog.open() != Window.OK) {
			// close the application
			System.exit(-1);
		} else {
			
			// Store the user values in the preferences
			prefs.put("workspaces", dialog.getWorkspaces());
			prefs.put("workspace", dialog.getLastWorkspace());
			
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		
		//Initialize the service
		this.exchangeRateProvider=exchangeRateProvider;
		this.exchangeRateProvider.init(dialog.getLastWorkspace());
		watchlistProvider.init(dialog.getLastWorkspace());
		
		
	}

	private void setLocation(Display display, Shell shell) {
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle monitorRect = monitor.getBounds();
		Rectangle shellRect = shell.getBounds();
		int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
		int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
		shell.setLocation(x, y);
	}

}
