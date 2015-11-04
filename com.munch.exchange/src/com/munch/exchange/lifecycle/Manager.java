package com.munch.exchange.lifecycle;


import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.munch.exchange.dialog.WorkspaceDialog;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IWatchlistProvider;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarProvider;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataProvider;

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
			IWatchlistProvider watchlistProvider/*,
			IIBContractProvider contractProvider,
			IIBTopMktDataProvider topMktDataProvider,
			IIBHistoricalDataProvider ibHistoricalDataProvider,
			IIBRealTimeBarProvider ibRealTimeBarProvider*/) {
		
		
		//BasicConfigurator.configure();
		//BasicConfigurator.
		
		//contractProvider.init();
		//topMktDataProvider.init();
		//ibHistoricalDataProvider.init();
		//ibRealTimeBarProvider.init();
		
		//testIBProviders(contractProvider, topMktDataProvider, ibHistoricalDataProvider);
		
		final Shell shell = new Shell(SWT.TOOL | SWT.NO_TRIM);
		
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
	
	/*
	private void testIBProviders(IIBContractProvider contractProvider,
			IIBTopMktDataProvider topMktDataProvider,
			IIBHistoricalDataProvider ibHistoricalDataProvider){
		
		System.out.println("Test IB started!: ");
		
		//Log.info("Test IB started!: ");
		
		List<ExContract> contracts=contractProvider.getAll();
		for(ExContract exContract:contracts){
			System.out.println("Contract: "+exContract.toString());
			List<ExContractBars> bars=ibHistoricalDataProvider.getAllExContractBars(exContract);
			if(bars==null)continue;
			for(ExContractBars contractBar:bars){
				ExBar bar=ibHistoricalDataProvider.getLastBar(contractBar, ExSecondeBar.class);
				Log.info(bar.toString());
			}
		}
	}
	*/
	
	
	@PreSave
	public void preSave(IIBContractProvider contractProvider,
			IIBTopMktDataProvider topMktDataProvider,
			IIBHistoricalDataProvider ibHistoricalDataProvider,
			IIBRealTimeBarProvider ibRealTimeBarProvider){
		System.out.println("Application is going to close!");
		//ibHistoricalDataProvider.close();
		ibRealTimeBarProvider.close();
		//contractProvider.close();
		topMktDataProvider.close();
		
		
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
