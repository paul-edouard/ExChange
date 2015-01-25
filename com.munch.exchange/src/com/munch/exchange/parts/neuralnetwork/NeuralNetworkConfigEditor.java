 
package com.munch.exchange.parts.neuralnetwork;

import java.util.LinkedList;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.dialogs.MessageDialog;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.StringEditorDialog;
import com.munch.exchange.job.FinancialDataLoader;
import com.munch.exchange.job.HistoricalDataLoader;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkDataLoader;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager.NNOptManagerInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NNetwork.ConfigLinkInfo;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.parts.neuralnetwork.data.NeuralNetworkInputConfiguratorComposite;
import com.munch.exchange.parts.neuralnetwork.data.NeuralNetworkTrainingDataComposite;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.INeuralNetworkProvider;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;

public class NeuralNetworkConfigEditor {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkConfigEditor.class);
	
	public static final String CONFIG_EDITOR_ID="com.munch.exchange.partdescriptor.configeditor";
	
	//Data Loader
	private HistoricalDataLoader historicalDataLoader;
	private FinancialDataLoader financialDataLoader;
	private NeuralNetworkDataLoader neuralNetworkDataloader;
	private boolean[] dataLoadingStates={false,false,false};
	
	//Gui loading reaction trigger
	private LoadingStateChanger loadingChanger;
	
	private Composite parent;
	
	@Inject
	Stock stock;
	
	@Inject
	INeuralNetworkProvider neuralNetworkProvider;
	
	@Inject
	IEclipseContext context;
	
	@Inject
	IEventBroker eventBroker;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	Shell shell;
	
	@Inject
	MDirtyable dirty;
	
	private Label lblLoading;
	
	
	private Button btnAddConfig;
	private Combo comboConfig;
	//private Button btnSaveConfig;
	private Button btnDeleteConfig;
	private Button btnEditConfig;
	
	private Composite compositeLeftMiddle;
	
	private NeuralNetworkInputConfiguratorComposite inputConfigurator;
	private NeuralNetworkTrainingDataComposite trainingDataViewer;
	
	@Inject
	public NeuralNetworkConfigEditor() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		this.parent=parent;
		
		lblLoading = new Label(parent, SWT.NONE);
		lblLoading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				loadingChanger.cancel();
			}
		});
		lblLoading.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblLoading.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblLoading.setText("Neural Network Configuration Loading...");
		
		startLoading();
		
	}
	
	private void startLoading(){
		
		stock.getHistoricalData().addUsedClass(this.getClass());
		
		historicalDataLoader=ContextInjectionFactory.make( HistoricalDataLoader.class,context);
		financialDataLoader=ContextInjectionFactory.make( FinancialDataLoader.class,context);
		neuralNetworkDataloader=ContextInjectionFactory.make( NeuralNetworkDataLoader.class,context);
		
		loadingChanger=new LoadingStateChanger();
		
		//++++++++++++++++++++++
		//Start Loading Data
		//Historical
		if(stock.getHistoricalData().isEmpty()){
			historicalDataLoader.schedule();
		}
		else{
			eventBroker.send(IEventConstant.HISTORICAL_DATA_LOADED,stock.getUUID());
			eventBroker.send(IEventConstant.OPTIMIZATION_RESULTS_LOADED,stock.getUUID());
		}
		//Financial
		financialDataLoader.schedule();
		neuralNetworkDataloader.schedule();
		
		
	}
	
	@PreDestroy
 	public void preDestroy() {
		loadingChanger.cancel();
	}
	
	@Focus
	public void onFocus() {
		if(stock!=null && stock.getNeuralNetwork().getConfiguration()!=null){
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
		}
	}
	
	@Persist
	public void save() {
		
		Cursor cursor_wait=new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		Cursor cursor_old=shell.getCursor();
		shell.setCursor(cursor_wait);
		
		
		if(neuralNetworkProvider.save(stock))
			setDirty(false);
		
		
		shell.setCursor(cursor_old);
	}
	
	private void setDirty(boolean dirtyState){
		comboConfig.setEnabled(!dirtyState);
		btnAddConfig.setEnabled(!dirtyState);
		dirty.setDirty(dirtyState);
		stock.getNeuralNetwork().getConfiguration().setDirty(dirtyState);
	}
	
	private void setEnabled(boolean enabled){
		trainingDataViewer.setEnabled(enabled);
		inputConfigurator.setEnabled(enabled);
		
		comboConfig.setEnabled(enabled);
		btnAddConfig.setVisible(enabled);
		btnEditConfig.setVisible(enabled);
		btnDeleteConfig.setVisible(enabled);
		
	}
	
	private void loadCurrentConfig(){
		
		//Set the wait cursor
		Cursor cursor_wait=new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		Cursor cursor_old=shell.getCursor();
		shell.setCursor(cursor_wait);
		
		//Load the configuration
		neuralNetworkProvider.loadConfiguration(stock);
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
		refreshTrainingDataViewer();
		
		setDirty(true);
		
		
		//Reset the cursor
		shell.setCursor(cursor_old);
	}
	
	private void createConfigEditorGui(){
		if(!dataLoadingStates[0] || !dataLoadingStates[1] || !dataLoadingStates[2])return;
		
		loadingChanger.cancel();
		
		
		parent.update();
		parent.setLayout(new GridLayout(1, false));
		
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeLeft = new Composite(sashForm, SWT.NONE);
		compositeLeft.setLayout(new GridLayout(1, false));
		
		Composite compositeLeftHeader = new Composite(compositeLeft, SWT.NONE);
		compositeLeftHeader.setLayout(new GridLayout(3, false));
		compositeLeftHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblConfig = new Label(compositeLeftHeader, SWT.NONE);
		lblConfig.setText("Config:");
		
		comboConfig = new Combo(compositeLeftHeader, SWT.NONE);
		comboConfig.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				if( comboConfig.getText().isEmpty() )return;
				
				if(!stock.getNeuralNetwork().getCurrentConfigName().equals(comboConfig.getText())){
					
					stock.getNeuralNetwork().setCurrentConfiguration(comboConfig.getText());
					
					loadCurrentConfig();
					
				}
				
			}
		});
		//comboConfig.setEnabled(false);
		comboConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_2 = new Composite(compositeLeftHeader, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(4, false);
		gl_composite_2.marginHeight = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		
		btnDeleteConfig = new Button(composite_2, SWT.NONE);
		btnDeleteConfig.setEnabled(false);
		btnDeleteConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Configuration config=stock.getNeuralNetwork().getConfiguration();
				
				if(!MessageDialog.openQuestion(shell, "Delete configuration", "Do you realy want to delete the configuration "+config.getName()+"?"))
					return;
				
				//Set the wait cursor
				Cursor cursor_wait=new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
				Cursor cursor_old=shell.getCursor();
				shell.setCursor(cursor_wait);
				
				
				if(neuralNetworkProvider.deleteConfiguration(stock) 
						&& stock.getNeuralNetwork().removeCurrentConfiguration()){
					//enableComboConfigTextChangeReaction=false;
					
					comboConfig.remove(config.getName());
					comboConfig.setText(stock.getNeuralNetwork().getCurrentConfigName());
					
					//Load the configuration
					loadCurrentConfig();
				}
				
				//Reset the cursor
				shell.setCursor(cursor_old);
				btnDeleteConfig.setVisible(stock.getNeuralNetwork().getConfigInfoList().size()>1);
			}
		});
		btnDeleteConfig.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnDeleteConfig.setText("Delete");
		
		btnEditConfig = new Button(composite_2, SWT.NONE);
		btnEditConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if( comboConfig.getText().isEmpty())return;
				
				//InfoPart.postInfoText(eventBroker, "Edit Text: "+comboConfig.getText());
				
				StringEditorDialog dialog=new StringEditorDialog(shell, "Configuration", comboConfig.getText());
				if(dialog.open()==StringEditorDialog.OK && dialog.getNewString()!=null){
					
					//enableComboConfigTextChangeReaction=false;
					
					Configuration config=stock.getNeuralNetwork().getConfiguration();
					if(config==null)return;
					
					int index=comboConfig.indexOf(config.getName());
					comboConfig.remove(index);
					comboConfig.add(dialog.getNewString(), index);
					comboConfig.setText(dialog.getNewString());
					
					config.setName(dialog.getNewString());
					stock.getNeuralNetwork().setCurrentConfiguration(dialog.getNewString());
					eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
					
					
					setDirty(true);
					//InfoPart.postInfoText(eventBroker, "New Config Name: "+stock.getNeuralNetwork().getCurrentConfiguration());
					
					//enableComboConfigTextChangeReaction=true;
				}
				
			}
		});
		btnEditConfig.setText("Edit");
		
		btnAddConfig = new Button(composite_2, SWT.NONE);
		btnAddConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				//stock.getNeuralNetwork().getConfiguration().removePropertyChangeListener(configChangedListener);
				
				int i=0;String configName="New Config";
				Configuration config=new Configuration();config.setName(configName);
				while(!stock.getNeuralNetwork().addNewConfiguration(config,stock)){
					configName="New Config_"+String.valueOf(i);
					config=new Configuration();config.setName(configName);
					i++;
				}
				
				comboConfig.add(stock.getNeuralNetwork().getCurrentConfigName());
				comboConfig.setText(stock.getNeuralNetwork().getCurrentConfigName());
				
				eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
				//stock.getNeuralNetwork().getConfiguration().addPropertyChangeListener(configChangedListener);
				
				btnDeleteConfig.setVisible(stock.getNeuralNetwork().getConfigInfoList().size()>1);
				setDirty(true);
				
				
				
			}
		});
		btnAddConfig.setText("Add");
		
		compositeLeftMiddle = new Composite(compositeLeft, SWT.NONE);
		compositeLeftMiddle.setLayout(new GridLayout(1, false));
		compositeLeftMiddle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		////////////////////////////////
		//Create the Input configuror
		createInputConfigurator(compositeLeftMiddle);
		
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(1, false));
		
		
		Composite compositeData = new Composite(compositeRight, SWT.NONE);
		compositeData.setLayout(new GridLayout(1, false));
		compositeData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {254, 282});
		
		////////////////////////////////
		//Create the Training data Composite
		createTrainingDataViewer( compositeData);
		
		
		initComboConfig();
		
		refreshTrainingDataViewer();
		
		parent.layout();
		
	}
		
	private void createInputConfigurator(Composite parentComposite ){
			//Create a context instance
			IEclipseContext localContact=EclipseContextFactory.create();
			localContact.set(Composite.class, parentComposite);
			localContact.set(NeuralNetworkConfigEditor.class, this);
			localContact.setParent(context);
			
			//////////////////////////////////
			//Create the Input Configurator //
			//////////////////////////////////
			inputConfigurator=ContextInjectionFactory.make( NeuralNetworkInputConfiguratorComposite.class,localContact);
			inputConfigurator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}
		
	private void createTrainingDataViewer( Composite parentComposite ){
			//Create a context instance
			IEclipseContext localContact=EclipseContextFactory.create();
			localContact.set(Composite.class, parentComposite);
			localContact.set(Stock.class, stock);
			localContact.setParent(context);
			
			//////////////////////////////////
			//Create the Input Configurator //
			//////////////////////////////////
			trainingDataViewer=ContextInjectionFactory.make( NeuralNetworkTrainingDataComposite.class,localContact);
			trainingDataViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}	
		

	private void initComboConfig(){
		
		initConfigurations();
		
		LinkedList<ConfigLinkInfo> configList=stock.getNeuralNetwork().getConfigInfoList();
		comboConfig.removeAll();
		
		for(ConfigLinkInfo conf:configList){
			comboConfig.add(conf.name);
		}
		
		comboConfig.setText(stock.getNeuralNetwork().getCurrentConfigName());
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
		
		btnDeleteConfig.setVisible(configList.size()>1);
		btnDeleteConfig.setEnabled(configList.size()>1);
	}
	
	private void initConfigurations(){
		LinkedList<ConfigLinkInfo> configList=stock.getNeuralNetwork().getConfigInfoList();
		
		if(configList.size()==0){
			Configuration config=new Configuration();
			config.setName("New Config");
			stock.getNeuralNetwork().addNewConfiguration(config,stock);
			setDirty(true);
		}
	}
	
	public void refreshTrainingDataViewer(){
		trainingDataViewer.refreshGui();
	}
	
	
	//############################
	//##    WORKERS             ##
	//############################
	
	private class LoadingStateChanger extends Job{
		
		//Label lblLoading;
		String loadingText;
		
		//boolean stop=false;
		
		public LoadingStateChanger() {
			super("Loading State Changer");
			//this.lblLoading=lblLoading;
			
			setSystem(true);
			setPriority(SHORT);
			schedule();
		}
		
		private void setLabelText(){
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					lblLoading.setText(loadingText);
				}
			});
		}
		
		private void disposeLabel(){
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					lblLoading.dispose();
					parent.layout();
				}
			});
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try{
				int i=0;
			while(true){
				
				if (monitor.isCanceled() ){
					disposeLabel();
					return Status.CANCEL_STATUS;
				}
				
				loadingText="Neural Network Configuration Loading";
				for(int j=0;j<i;j++){loadingText+=".";}
				
				setLabelText();
				
				i++;
				if(i>3)i=0;
				
				Thread.sleep(500);
			}
			}catch(InterruptedException e){
				
				return Status.CANCEL_STATUS;
			}
			
			//return Status.CANCEL_STATUS;
		}
		
	}
	
	
	private class OptResultsUpdater extends Job{
		
		
		public OptResultsUpdater() {
			super("Update results");
			//this.lblLoading=lblLoading;
			
			setSystem(true);
			setPriority(SHORT);
			schedule();
		}
		
		
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
		}
		
	}
	
	
	
	//################################
	//##       Event Reaction       ##
	//################################
	
	private boolean isCompositeAbleToReact(String rate_uuid){
		if (parent.isDisposed())
			return false;
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || stock == null )
			return false;
		if (!incoming.getUUID().equals(stock.getUUID()))
			return false;
		
		return true;
	}	
	
	@Optional
	@Inject
	private void neuralNetworkDataLoaded(
			 @UIEventTopic(IEventConstant.NEURAL_NETWORK_DATA_LOADED) String rate_uuid) {
		
		if (!isCompositeAbleToReact(rate_uuid) || dataLoadingStates[2])
			return;
		
		dataLoadingStates[2]=true;
		createConfigEditorGui();
	}
	
	@Optional
	@Inject
	private void historicalDataLoaded(
			 @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid) {

		if (!isCompositeAbleToReact(rate_uuid) || dataLoadingStates[0])
			return;
		dataLoadingStates[0]=true;
		createConfigEditorGui();
	}
	
	@Optional
	@Inject
	private void financialDataLoaded(
			 @UIEventTopic(IEventConstant.FINANCIAL_DATA_LOADED) String rate_uuid) {

		if (!isCompositeAbleToReact(rate_uuid) || dataLoadingStates[1])
			return;
		dataLoadingStates[1]=true;
		createConfigEditorGui();
	}
	
	
	@Inject
	private void neuralNetworkConfigDirty(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_DIRTY) Configuration config) {
		if(config==null)return;
		if(stock.getNeuralNetwork().getConfiguration()!=config)return;
		
		setDirty(config.isDirty());
			
	}
	
	
	@Inject
    private void optimizationStarted(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_STARTED) NNOptManagerInfo info){

    	if(info==null)return;
    	 	
    	if (!isCompositeAbleToReact(info.getRate().getUUID()))return;
    	
    	if(stock.getNeuralNetwork().getConfiguration()!=info.getConfiguration())return;
    	
    	setEnabled(false);
    	
	}
    
	
	
	@Inject
    private void optimizationFinished(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED) NNOptManagerInfo info){
    	
    	
    	if(info==null)return;
    	 	
    	if (!isCompositeAbleToReact(info.getRate().getUUID()))return;
    	
    	if(stock.getNeuralNetwork().getConfiguration()!=info.getConfiguration())return;
    	
    	setEnabled(true);
    	
    	//stock.getNeuralNetwork().getConfiguration().setDirty(true);
    	
    }
	
	
	
	
}