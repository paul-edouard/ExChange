package com.munch.exchange.parts.neuralnetwork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.learning.BackPropagation;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.StringEditorDialog;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkDataLoader;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager.NNOptManagerInfo;
import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.parts.composite.RateChart;
import com.munch.exchange.parts.neuralnetwork.error.NeuralNetworkErrorPart;
import com.munch.exchange.parts.neuralnetwork.input.NeuralNetworkInputConfiguratorComposite;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.wizard.parameter.architecture.ArchitectureOptimizationWizard;
import com.munch.exchange.wizard.parameter.learning.LearnParameterWizard;
import com.munch.exchange.wizard.parameter.optimization.OptimizationDoubleParamWizard;

public class NeuralNetworkComposite extends Composite implements LearningEventListener{
	
	private static Logger logger = Logger.getLogger(NeuralNetworkComposite.class);
	
	private Stock stock;
	
	private INeuralNetworkProvider neuralNetworkProvider;
	
	
	private NeuralNetworkInputConfiguratorComposite inputConfigurator;
	
	private double maxProfit=0;
	private double maxPenaltyProfit=0;
	private boolean isInitiated=false;
	private boolean enableComboConfigTextChangeReaction=true;
	private boolean neuralNetworkLoaded=false;
	
	@Inject
	IEclipseContext context;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	EPartService partService;
	
	@Inject
	private MApplication application;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private Shell shell;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private INeuralNetworkProvider nnprovider;
	
	@Inject
	ESelectionService selectionService;
	
	
	//private NeuralNetworkOptimizer optimizer;
	private NeuralNetworkOptimizerManager optimizerManager;
	
	
	
	private PropertyChangeListener configChangedListener=new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			InfoPart.postInfoText(eventBroker, "Dirty listener: "+evt.getPropertyName());
			if(evt.getPropertyName().equals(Configuration.FIELD_IsDirty)){
				InfoPart.postInfoText(eventBroker, "Dirty listener value "+String.valueOf(evt.getNewValue()));
				if(((boolean)evt.getNewValue()))
					btnSaveConfig.setVisible(true);
			}
		}
	};
	private Button btnAddConfig;
	private Button btnArchOptConf;
	private Button btnLearnOptConf;
	private Button btnLearnAlg;
	
	private Text textMaxProfit;
	private Text textPenaltyProfit;
	private Text textError;
	private Text textGenError;
	
	private Combo comboConfig;
	private NeuralNetworkDataLoader nnd_loader;
	private Button btnSaveConfig;
	private Button btnDeleteConfig;

	private Button btnStartTrain;
	private Group grpLearning;
	private Button btnEditConfig;
	
	
	@Inject
	public NeuralNetworkComposite(Composite parent,
			ExchangeRate rate,IEclipseContext ctxt,
			INeuralNetworkProvider nnProvider) {
		super(parent, SWT.NONE);
		this.stock=(Stock) rate;
		this.neuralNetworkProvider=nnProvider;
		//contentProvider=new NeuralNetworkContentProvider(this.stock);
		
		
		setLayout(new GridLayout(1, false));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
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
				
				if(!isInitiated || comboConfig.getText().isEmpty() || !enableComboConfigTextChangeReaction)return;
				
				if(!stock.getNeuralNetwork().getCurrentConfiguration().equals(comboConfig.getText())){
					
					stock.getNeuralNetwork().setCurrentConfiguration(comboConfig.getText());
					
					eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
					btnSaveConfig.setVisible(true);
				}
				
				//refreshGui();
				fireReadyToTrain();
				
			}
		});
		comboConfig.setEnabled(false);
		comboConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_2 = new Composite(compositeLeftHeader, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(4, false);
		gl_composite_2.marginHeight = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		btnSaveConfig = new Button(composite_2, SWT.NONE);
		btnSaveConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if(isLoaded){
					
					Cursor cursor_wait=new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
					Cursor cursor_old=shell.getCursor();
					shell.setCursor(cursor_wait);
					
					neuralNetworkProvider.save(stock);
					btnSaveConfig.setVisible(false);
					
					shell.setCursor(cursor_old);
				/*}
				else{
					isLoaded=neuralNetworkProvider.load(stock);
					refreshGui();
				}*/
				
			}
		});
		btnSaveConfig.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnSaveConfig.setText("Save");
		btnSaveConfig.setVisible(false);
		
		btnDeleteConfig = new Button(composite_2, SWT.NONE);
		btnDeleteConfig.setEnabled(false);
		btnDeleteConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Configuration config=stock.getNeuralNetwork().getConfiguration();
				
				if(!MessageDialog.openQuestion(shell, "Delete configuration", "Do you realy want to delete the configuration "+config.getName()+"?"))
					return;
				
				if(stock.getNeuralNetwork().removeCurrentConfiguration()){
					enableComboConfigTextChangeReaction=false;
					
					config.removePropertyChangeListener(configChangedListener);
					comboConfig.remove(config.getName());
					
					//InfoPart.postInfoText(eventBroker, "New current: "+stock.getNeuralNetwork().getCurrentConfiguration());
					
					comboConfig.setText(stock.getNeuralNetwork().getCurrentConfiguration());
					eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
					
					//refreshGui();
					fireReadyToTrain();
					
					btnSaveConfig.setVisible(true);
					
					enableComboConfigTextChangeReaction=true;
				}
				
				btnDeleteConfig.setVisible(stock.getNeuralNetwork().getConfigurations().size()>1);
			}
		});
		btnDeleteConfig.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnDeleteConfig.setText("Delete");
		
		btnEditConfig = new Button(composite_2, SWT.NONE);
		btnEditConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(!isInitiated || comboConfig.getText().isEmpty())return;
				
				//InfoPart.postInfoText(eventBroker, "Edit Text: "+comboConfig.getText());
				
				StringEditorDialog dialog=new StringEditorDialog(shell, "Configuration", comboConfig.getText());
				if(dialog.open()==StringEditorDialog.OK && dialog.getNewString()!=null){
					
					enableComboConfigTextChangeReaction=false;
					
					Configuration config=stock.getNeuralNetwork().getConfiguration();
					if(config==null)return;
					
					int index=comboConfig.indexOf(config.getName());
					comboConfig.remove(index);
					comboConfig.add(dialog.getNewString(), index);
					comboConfig.setText(dialog.getNewString());
					
					config.setName(dialog.getNewString());
					stock.getNeuralNetwork().setCurrentConfiguration(dialog.getNewString());
					eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
					
					
					config.setDirty(true);
					btnSaveConfig.setVisible(true);
					//InfoPart.postInfoText(eventBroker, "New Config Name: "+stock.getNeuralNetwork().getCurrentConfiguration());
					
					enableComboConfigTextChangeReaction=true;
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
				while(!stock.getNeuralNetwork().addNewConfiguration(configName,stock)){
					configName="New Config_"+String.valueOf(i);
					i++;
				}
				
				comboConfig.add(stock.getNeuralNetwork().getCurrentConfiguration());
				comboConfig.setText(stock.getNeuralNetwork().getCurrentConfiguration());
				
				eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
				
				
				
				stock.getNeuralNetwork().getConfiguration().addPropertyChangeListener(configChangedListener);
				
				btnDeleteConfig.setVisible(stock.getNeuralNetwork().getConfigurations().size()>1);
				btnSaveConfig.setVisible(true);
				
				fireReadyToTrain();
				
			}
		});
		btnAddConfig.setText("Add");
		
		
		
		//Composite composite = new Composite(compositeLeft, SWT.NONE);
		//composite.setLayout(new GridLayout(1, false));
		//composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		////////////////////////////////
		//Create the input configurator
		createInputConfigurator(ctxt, compositeLeft);
		
		Composite compositeLeftBottom = new Composite(compositeLeft, SWT.NONE);
		compositeLeftBottom.setLayout(new GridLayout(2, false));
		compositeLeftBottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		//btnOpt.setEnabled(false);
		
		Label lblMaxProfit = new Label(compositeLeftBottom, SWT.NONE);
		lblMaxProfit.setText("Max Profit:");
		
		textMaxProfit = new Text(compositeLeftBottom, SWT.BORDER);
		textMaxProfit.setEnabled(false);
		textMaxProfit.setEditable(false);
		textMaxProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblPenalityProfit = new Label(compositeLeftBottom, SWT.NONE);
		lblPenalityProfit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPenalityProfit.setText("Penality Profit:");
		
		textPenaltyProfit = new Text(compositeLeftBottom, SWT.BORDER);
		textPenaltyProfit.setEditable(false);
		textPenaltyProfit.setEnabled(false);
		textPenaltyProfit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(1, false));
		
		grpLearning = new Group(compositeRight, SWT.NONE);
		grpLearning.setLayout(new GridLayout(6, false));
		grpLearning.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpLearning.setText("Learning");
		
		Label lblConfiguration = new Label(grpLearning, SWT.NONE);
		lblConfiguration.setText("Configuration:");
		
		btnArchOptConf = new Button(grpLearning, SWT.NONE);
		btnArchOptConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Configuration conf=stock.getNeuralNetwork().getConfiguration();
				
				ArchitectureOptimizationWizard wizard=new ArchitectureOptimizationWizard(
						conf.getOptArchitectureParam().createCopy(),conf.getNumberOfInputNeurons());
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() == Window.OK){
					stock.getNeuralNetwork().getConfiguration().setOptArchitectureParam(
							wizard.getOptArchitectureParam());
					stock.getNeuralNetwork().getConfiguration().setDirty(true);
				}
			}
		});
		btnArchOptConf.setText("Arch. Opt.");
		
		btnLearnOptConf = new Button(grpLearning, SWT.NONE);
		btnLearnOptConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				OptimizationDoubleParamWizard wizard=new OptimizationDoubleParamWizard(
						stock.getNeuralNetwork().getConfiguration().getOptLearnParam().createCopy());
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() == Window.OK){
					stock.getNeuralNetwork().getConfiguration().setOptLearnParam(wizard.getOptLearnParam());
					stock.getNeuralNetwork().getConfiguration().setDirty(true);
				}
				
			}
		});
		btnLearnOptConf.setText("Learn Opt.");
		
		btnLearnAlg = new Button(grpLearning, SWT.NONE);
		btnLearnAlg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				LearnParameterWizard wizard=new LearnParameterWizard(
						stock.getNeuralNetwork().getConfiguration().getLearnParam().createCopy());
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() == Window.OK){
					stock.getNeuralNetwork().getConfiguration().setLearnParam(wizard.getParam());
					stock.getNeuralNetwork().getConfiguration().setDirty(true);
				}
				
			}
		});
		btnLearnAlg.setText("Learn Alg.");
		
		Label label = new Label(grpLearning, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnStartTrain = new Button(grpLearning, SWT.NONE);
		btnStartTrain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				//logger.info("Start Train click!");
				
				if(!stock.getNeuralNetwork().getConfiguration().areAllTimeSeriesAvailable()){
					neuralNetworkProvider.createAllValuePoints(stock.getNeuralNetwork().getConfiguration());
				}
				
				Configuration config=stock.getNeuralNetwork().getConfiguration();
				config.resetTrainingData();
				DataSet trainingSet=config.getTrainingSet();
				
				
				int minDim=stock.getNeuralNetwork().getConfiguration().getNumberOfInputNeurons();
				int maxDim=minDim;
				
				logger.info("Dataset row size: "+trainingSet.getRowAt(0).getInput().length);
				logger.info("Number of input neurons: "+minDim);
				
				if(minDim!=trainingSet.getRowAt(0).getInput().length){
					logger.info("Input neuron size error: "+minDim);
					return;
				}
				
				//trainingSet.ge
				
				
				AlgorithmParameters<boolean[]> optArchitectureParam=stock.getNeuralNetwork().getConfiguration().getOptArchitectureParam();
				
				if(optArchitectureParam.hasParamKey(AlgorithmParameters.MinDimension)){
					minDim=optArchitectureParam.getIntegerParam(AlgorithmParameters.MinDimension);
				}
				if(optArchitectureParam.hasParamKey(AlgorithmParameters.MaxDimension)){
					maxDim=optArchitectureParam.getIntegerParam(AlgorithmParameters.MaxDimension);
				}
				
				
				
				if(optimizerManager==null){
					optimizerManager=new NeuralNetworkOptimizerManager(
							eventBroker,nnprovider,
							stock,
							stock.getNeuralNetwork().getConfiguration(),
							trainingSet,
							minDim,
							maxDim);
				}
				else{
					optimizerManager.setConfiguration(stock.getNeuralNetwork().getConfiguration());
					optimizerManager.setTrainingSet(trainingSet);
					optimizerManager.setMinMax(minDim, maxDim);
				}
				
				setTrainingStatus(true);
				
				
				//Open the Neural network error part
				NeuralNetworkErrorPart.openNeuralNetworkErrorPart(
						stock,
						partService,
						modelService,
						application,
						optimizerManager,
						context);
				
				
				
				optimizerManager.schedule();
				
				
				stock.getNeuralNetwork().getConfiguration().setDirty(true);
				
			}
		});
		btnStartTrain.setText("Start");
		
		Composite compositeRightHeader = new Composite(compositeRight, SWT.NONE);
		compositeRightHeader.setLayout(new GridLayout(4, false));
		compositeRightHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblError = new Label(compositeRightHeader, SWT.NONE);
		lblError.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblError.setText("Error:");
		
		textError = new Text(compositeRightHeader, SWT.BORDER);
		textError.setEnabled(false);
		textError.setEditable(false);
		textError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblGenError = new Label(compositeRightHeader, SWT.NONE);
		lblGenError.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGenError.setText("Gen. Error:");
		
		textGenError = new Text(compositeRightHeader, SWT.BORDER);
		textGenError.setEnabled(false);
		textGenError.setEditable(false);
		textGenError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite compositeGraph = new Composite(compositeRight, SWT.NONE);
		compositeGraph.setLayout(new GridLayout(1, false));
		compositeGraph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {254, 282});
		
		
		
		//treeViewer.refresh();
		isInitiated=true;
		
		loadNeuralData(ctxt);
		fireReadyToTrain();
	}
	
	
	
	private void createInputConfigurator(IEclipseContext context, Composite parentComposite ){
		//Create a context instance
		IEclipseContext localContact=EclipseContextFactory.create();
		localContact.set(Composite.class, parentComposite);
		//localContact.set(Stock.class, stock);
		//localContact.set(INeuralNetworkProvider.class, nnProvider);
		localContact.setParent(context);
		
		//////////////////////////////////
		//Create the Input Configurator //
		//////////////////////////////////
		//TODO
		inputConfigurator=ContextInjectionFactory.make( NeuralNetworkInputConfiguratorComposite.class,localContact);
		inputConfigurator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//inputConfigurator.setEnabled(false);
	}
	
	private void fireReadyToTrain(){
		Configuration config=stock.getNeuralNetwork().getConfiguration();
		/*
		if(config!=null &&( config.getOutputPointList()==null || config.getOutputPointList().isEmpty())){
			prepareOutputPointList();
		}
		*/
		
		boolean readyToTrain=config!=null &&  config.getOutputPointList()!=null &&
				config.getNumberOfTimeSeries()>0 && config.getOutputPointList().size()>0;
				
		
		
		
		btnSaveConfig.setEnabled(true);
		btnDeleteConfig.setEnabled(true);
		btnEditConfig.setEnabled(true);
		btnAddConfig.setEnabled(true);
		comboConfig.setEnabled(true);
		
		
		btnStartTrain.setEnabled(readyToTrain);
		btnArchOptConf.setEnabled(readyToTrain);
		btnLearnAlg.setEnabled(readyToTrain);
		btnLearnOptConf.setEnabled(readyToTrain);
		
		
	}
	
	private void setTrainingStatus(boolean status){
		
		btnSaveConfig.setEnabled(!status);
		btnDeleteConfig.setEnabled(!status);
		btnEditConfig.setEnabled(!status);
		btnAddConfig.setEnabled(!status);
		comboConfig.setEnabled(!status);
		btnStartTrain.setEnabled(!status);
		btnArchOptConf.setEnabled(!status);
		btnLearnAlg.setEnabled(!status);
		btnLearnOptConf.setEnabled(!status);
		//grpLearning.setEnabled(!status);
	}
	
	private void loadNeuralData(IEclipseContext context){
		
		if(stock==null)return;
		if(stock.getUUID()==null)return;
		
		
		if(nnd_loader==null ){
			IEclipseContext localContact=EclipseContextFactory.create();
			localContact.setParent(context);
			nnd_loader=ContextInjectionFactory.make( NeuralNetworkDataLoader.class,localContact);
		}
		
		
		nnd_loader.schedule();
	}
	
	/*
	private void refreshGui(){
		//refreshComboConfig(true);
		//changeLoadedState();
		contentProvider.refreshCategories();
		treeViewer.setInput(contentProvider.getRoot());
		treeViewer.refresh();
	}
	*/
	
	private void initConfigurations(){
		LinkedList<Configuration> configList=stock.getNeuralNetwork().getConfigurations();
		
		if(configList.size()==0){
			stock.getNeuralNetwork().addNewConfiguration("New Config",stock);
			stock.getNeuralNetwork().getConfiguration().addPropertyChangeListener(configChangedListener);
			
			btnSaveConfig.setVisible(true);
		}
	}
	
	private void initComboConfig(){
		
	//	InfoPart.postInfoText(eventBroker, "Init Combo Configuration  01: ");
		
		LinkedList<Configuration> configList=stock.getNeuralNetwork().getConfigurations();
		comboConfig.removeAll();
		
		for(Configuration conf:configList){
	//		InfoPart.postInfoText(eventBroker, "Init Combo Configuration  02: "+conf.getName());
			comboConfig.add(conf.getName());
			conf.addPropertyChangeListener(configChangedListener);
		}
		
		comboConfig.setText(stock.getNeuralNetwork().getCurrentConfiguration());
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock.getNeuralNetwork().getConfiguration());
		
		//eventBroker.post(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED,stock);
		btnDeleteConfig.setVisible(stock.getNeuralNetwork().getConfigurations().size()>1);
	}
	
	
	/*
	private void prepareOutputPointList(){
		if(stock.getNeuralNetwork().getConfiguration()==null){
			return;
		}
		
		stock.getNeuralNetwork().getConfiguration().setOutputPointList(neuralNetworkProvider.calculateMaxProfitOutputList(stock,RateChart.PENALTY));
		neuralNetworkProvider.createAllInputPoints(stock);
		
		maxPenaltyProfit=maxProfit-getObjFunc().compute(stock.getNeuralNetwork().getConfiguration().getOutputPointList().toDoubleArray(), null);	
	
		String maxPanaltyProfitStr = String.format("%,.2f%%",maxPenaltyProfit * 100);
		String maxProfitStr = String.format("%,.2f%%",maxProfit * 100);
		
		textMaxProfit.setText(maxProfitStr);
		textPenaltyProfit.setText(maxPanaltyProfitStr);
		
		fireReadyToTrain();
	}
	*/
	
	//################################
	//##       Event Reaction       ##
	//################################
	
	private boolean isCompositeAbleToReact(String rate_uuid){
		if (this.isDisposed())
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
	
	@Inject
	private void neuralNetworkDataLoaded(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_DATA_LOADED) String rate_uuid) {
		
		
		if (!isCompositeAbleToReact(rate_uuid) || neuralNetworkLoaded)
			return;
		
		//InfoPart.postInfoText(eventBroker, "NEURAL_NETWORK_DATA_LOADED 02: "+rate_uuid);
		
		initConfigurations();
		
		//prepareOutputPointList();
		
		initComboConfig();
		
		//refreshGui();
		//refreshTimeSeries();
		fireReadyToTrain();
		
		neuralNetworkLoaded=true;
		
	}
	
	@Inject
	private void historicalDataLoaded(
			@Optional @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid) {

		if (!isCompositeAbleToReact(rate_uuid))
			return;

		maxProfit=this.stock.getHistoricalData().calculateMaxProfit(DatePoint.FIELD_Close);
		//InfoPart.postInfoText(eventBroker, "HISTORICAL_DATA_LOADED: "+rate_uuid);
		
		//Load the Neural Data
		loadNeuralData(context);
	}
	
	@Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation)event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
        
        eventBroker.send(IEventConstant.NEURAL_NETWORK_NEW_CURRENT,stock.getUUID());
        
        
    } 
	
	@Inject
    private void optimizationFinished(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED) NNOptManagerInfo info){
    	
    	
    	if(info==null)return;
    	 	
    	if (!isCompositeAbleToReact(info.getRate().getUUID()))
			return;
    	
    	setTrainingStatus(false);
    	stock.getNeuralNetwork().getConfiguration().setDirty(true);
    	btnSaveConfig.setVisible(true);
    }
	
	/**
     * Prints network output for the each element from the specified training set.
     * @param neuralNet neural network
     * @param trainingSet training set
     */
    public static void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {
    	
    	String[] nn_output=new String[testSet.getRows().size()];
    	String[] t_output=new String[testSet.getRows().size()];
    	
    	int pos=0;
        for(DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            //String.format("%,.2f%%",maxPenaltyProfit * 100);
            nn_output[pos]=String.format("%,.2f%%",networkOutput[0]);
            t_output[pos]=String.format("%,.2f%%",testSetRow.getDesiredOutput()[0]);
            
            pos++;
            
            
            //System.out.print("Input: " + Arrays.toString( testSetRow.getInput() ) );
            //System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
        
        
        System.out.println("Output NN: " + Arrays.toString( nn_output ) );
        System.out.println("Output   : " + Arrays.toString( t_output) );
        
    }
	
	
}
