package com.munch.exchange.parts.neuralnetwork.data;



import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.apache.log4j.Logger;


import org.eclipse.wb.swt.ResourceManager;
















import com.munch.exchange.IEventConstant;
import com.munch.exchange.dialog.AddTimeSeriesDialog;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.parts.neuralnetwork.NeuralNetworkConfigEditor;
import com.munch.exchange.parts.neuralnetwork.data.NeuralNetworkInputConfiguratorContentProvider.NeuralNetworkSerieCategory;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.INeuralNetworkProvider;

import org.eclipse.swt.widgets.ProgressBar;


public class NeuralNetworkInputConfiguratorComposite extends Composite {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkInputConfiguratorComposite.class);
	
	private boolean isEditing=false;
	private Stock stock;
	private Configuration configLocal;
	private NeuralNetworkInputConfiguratorContentProvider contentProvider;
	private TimeSeriesUpdater timeSeriesUpdater;
	private NeuralNetworkConfigEditor configEditor;
	
	
	private INeuralNetworkProvider neuralNetworkProvider;
	
	@Inject
	private Shell shell;
	
	@Inject
	private IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	private Tree tree;
	private TreeViewer treeViewer;
	
	private Menu menu;
	private MenuItem mntmAddSerie;
	private MenuItem mntmRemove;
	
	private Composite parent;
	
	private Button btnSave;
	private Button btnEdit;
	private Composite compositeHeader;
	private Composite compositeMiddle;
	private Composite compositeBottom;
	private Combo comboPeriod;
	private Button btnActivateDayOf;
	private Button btnCancel;
	private ProgressBar progressBar;
	private Button btnRefreshMinmax;
	//private Point progressBarPoint;
	
	@Inject
	public NeuralNetworkInputConfiguratorComposite(Composite parent,ExchangeRate rate,
			INeuralNetworkProvider nnProvider, NeuralNetworkConfigEditor configEditor) {
		super(parent, SWT.NONE);
		this.configEditor=configEditor;
		this.parent=parent;
		this.stock=(Stock) rate;
		this.neuralNetworkProvider=nnProvider;
		contentProvider=new NeuralNetworkInputConfiguratorContentProvider();
		configLocal=this.stock.getNeuralNetwork().getConfiguration();
		timeSeriesUpdater=new TimeSeriesUpdater();
		
		setLayout(new GridLayout(1, false));
		Group grpInputConfiguration = new Group(this, SWT.NONE);
		grpInputConfiguration.setLayout(new GridLayout(1, false));
		grpInputConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpInputConfiguration.setText("Input Configuration");
		
		compositeHeader = new Composite(grpInputConfiguration, SWT.NONE);
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeHeader.setLayout(new GridLayout(4, false));
		
		btnEdit = new Button(compositeHeader, SWT.NONE);
		btnEdit.setSize(32, 25);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startEditModus();
			}
		});
		btnEdit.setText("Edit");
		
		btnSave = new Button(compositeHeader, SWT.NONE);
		btnSave.setSize(36, 25);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveAfterEdition();
			}
		});
		btnSave.setText("Save");
		btnSave.setVisible(false);
		
		btnCancel = new Button(compositeHeader, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelEditModus();
			}
		});
		btnCancel.setText("Cancel");
		btnCancel.setVisible(false);
		
		btnRefreshMinmax = new Button(compositeHeader, SWT.NONE);
		btnRefreshMinmax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(TimeSeries s:configLocal.getAllTimeSeries())
					s.resetMinMaxValues(true);
			}
		});
		btnRefreshMinmax.setEnabled(false);
		btnRefreshMinmax.setText("Refresh MinMax");
		
		compositeMiddle = new Composite(grpInputConfiguration, SWT.NONE);
		compositeMiddle.setLayout(new GridLayout(3, false));
		compositeMiddle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblPeriod = new Label(compositeMiddle, SWT.NONE);
		lblPeriod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPeriod.setText("Period:");
		
		comboPeriod = new Combo(compositeMiddle, SWT.NONE);
		comboPeriod.setEnabled(false);
		comboPeriod.setItems(new String[] {"DAY", "HOUR", "MINUTE", "SECONDE"});
		comboPeriod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboPeriod.setText("DAY");
		
		btnActivateDayOf = new Button(compositeMiddle, SWT.CHECK);
		btnActivateDayOf.setEnabled(false);
		btnActivateDayOf.setText("Day of week");
		
		compositeBottom = new Composite(grpInputConfiguration, SWT.NONE);
		compositeBottom.setLayout(new GridLayout(1, false));
		compositeBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		treeViewer = new TreeViewer(compositeBottom, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setInput(contentProvider.getRoot());
		
		tree = treeViewer.getTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(!isEditing)return;
				if(e.button==3 && tree.getSelection().length==1){
					TreeItem item=tree.getSelection()[0];
					if(item.getData() instanceof NeuralNetworkSerieCategory){
						mntmAddSerie.setEnabled(true);
						mntmRemove.setEnabled(false);
					}
					else if(item.getData() instanceof TimeSeries){
						mntmAddSerie.setEnabled(false);
						mntmRemove.setEnabled(true);
					}
					else{
						mntmAddSerie.setEnabled(false);
						mntmRemove.setEnabled(false);
					}
					menu.setVisible(true);
				}
				
			}
		});
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		
		TreeViewerColumn treeViewerColumnInputSeries = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumnInputSeries.setLabelProvider(new InputSeriesLabelProvider());
		TreeColumn trclmnInputSeries = treeViewerColumnInputSeries.getColumn();
		trclmnInputSeries.setWidth(150);
		trclmnInputSeries.setText("Input Series");
		
		TreeViewerColumn treeViewerColumnNbOfValues = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumnNbOfValues.setLabelProvider(new NbOfValuesLabelProvider());
		treeViewerColumnNbOfValues.setEditingSupport(new NumberOfValuesEditingSupport(treeViewer));
		TreeColumn trclmnNbOfValues = treeViewerColumnNbOfValues.getColumn();
		trclmnNbOfValues.setWidth(100);
		trclmnNbOfValues.setText("Nb. of values");
		
		TreeViewerColumn treeViewerColumnTimeLeft = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumnTimeLeft.setLabelProvider(new TimeLeftLabelProvider());
		treeViewerColumnTimeLeft.setEditingSupport(new TimeLeftEditingSupport(treeViewer));
		TreeColumn trclmnTimeLeft = treeViewerColumnTimeLeft.getColumn();
		trclmnTimeLeft.setWidth(100);
		trclmnTimeLeft.setText("Time left");
		
		TreeViewerColumn treeViewerColumnMinMax = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumnMinMax.setLabelProvider(new MinMaxValuesLabelProvider());
		TreeColumn trclmnMinMax = treeViewerColumnMinMax.getColumn();
		trclmnMinMax.setWidth(100);
		trclmnMinMax.setText("Min/Max");
		
		menu = new Menu(tree);
		tree.setMenu(menu);
		
		mntmAddSerie = new MenuItem(menu, SWT.NONE);
		mntmAddSerie.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/add.png"));
		mntmAddSerie.setEnabled(false);
		mntmAddSerie.addSelectionListener(new AddSeriesSelectionAdapter());
		mntmAddSerie.setText("Add Serie");
		
		mntmRemove = new MenuItem(menu, SWT.NONE);
		mntmRemove.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/delete.png"));
		mntmRemove.addSelectionListener(new RemoveSeriesSelectionAdapter());
		mntmRemove.setEnabled(false);
		mntmRemove.setText("Remove");
		
		progressBar = new ProgressBar(compositeBottom, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//progressBar.setVisible(false);
		//progressBarPoint=progressBar.getSize();
		//compositeBottom.layout();
		//compositeBottom.pack();
		//progressBar.setSize(0, 0);
		
		//compositeBottom.update();
		
	}



	public void setEnabled(boolean enabled){
		btnEdit.setEnabled(enabled);
		//menu.setEnabled(enabled);
	}
	
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	private void refreshTimeSeries(){
		contentProvider.refreshCategories(configLocal);
		
		treeViewer.refresh();
		treeViewer.expandAll();
	}
	
	
	
	//################################
	//##       Button Actions       ##
	//################################		
	private void startEditModus(){
		isEditing=true;
		btnCancel.setVisible(true);
		btnSave.setVisible(true);
		btnEdit.setEnabled(false);
		btnRefreshMinmax.setEnabled(true);
		
		configLocal=this.stock.getNeuralNetwork().getConfiguration().createCopy();
		refreshTimeSeries();
		
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_INPUT_EDITING,stock);
		
	}
	
	private void cancelEditModus(){
		isEditing=false;
		btnEdit.setEnabled(true);
		btnCancel.setVisible(false);
		btnSave.setVisible(false);
		btnRefreshMinmax.setEnabled(false);
		
		configLocal=this.stock.getNeuralNetwork().getConfiguration();
		refreshTimeSeries();
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_INPUT_CANCELED,stock);
	}
	
	private void saveAfterEdition(){
		isEditing=false;
		btnEdit.setEnabled(true);
		btnCancel.setVisible(false);
		btnSave.setVisible(false);
		btnRefreshMinmax.setEnabled(false);
		
		
		//Start the Time Series Updater
		timeSeriesUpdater.schedule();
	}

	//################################
	//##     Selection Adapter      ##
	//################################	
	class AddSeriesSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			//logger.info("Add serie selected");
			TreeItem item=tree.getSelection()[0];
			NeuralNetworkSerieCategory category=(NeuralNetworkSerieCategory) item.getData();
			//Configuration config=stock.getNeuralNetwork().getConfiguration();
			
			AddTimeSeriesDialog dialog=new AddTimeSeriesDialog(shell, category.name, configLocal);
			if(dialog.open()==AddTimeSeriesDialog.OK && dialog.getSeries()!=null){
				//TODO
				
				
				configLocal.addTimeSeries(dialog.getSeries(),false);
				refreshTimeSeries();
				//neuralNetworkProvider.createAllInputPoints(stock);
				
			}
		}
	}
	
	class RemoveSeriesSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			TreeItem item=tree.getSelection()[0];
			TimeSeries series=(TimeSeries) item.getData();
			
			configLocal.removeTimeSeries(series);
			tree.removeAll();
			
			refreshTimeSeries();
			//neuralNetworkProvider.createAllInputPoints(stock);
			//stock.getNeuralNetwork().getConfiguration().inputNeuronChanged();
			//fireReadyToTrain();
			
		}
	}
	
	//################################
	//##     Editing Support        ##
	//################################	
	
	class NumberOfValuesEditingSupport extends EditingSupport {
		
		
		public NumberOfValuesEditingSupport(TreeViewer viewer) {
			super(viewer);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			String[] nb = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
			return new ComboBoxCellEditor(treeViewer.getTree(), nb);
		}

		@Override
		protected boolean canEdit(Object element) {
			if(isEditing)
				return element instanceof TimeSeries;
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof TimeSeries){
				TimeSeries el=(TimeSeries) element;
				return el.getNumberOfPastValues()-1;
			}
			return 0;
		}

		@Override
		protected void setValue(Object element, Object value) {
			TimeSeries el = (TimeSeries) element;
			Integer nb=(Integer) value;
			el.setNumberOfPastValues(nb+1);
			
			//neuralNetworkProvider.createAllInputPoints(stock);
			//stock.getNeuralNetwork().getConfiguration().inputNeuronChanged();
			
		    //viewer.update(element, null);
			treeViewer.refresh();
		  }

	}
	
	class TimeLeftEditingSupport extends EditingSupport {
		
		public TimeLeftEditingSupport(TreeViewer viewer) {
			super(viewer);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			 return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
		}

		@Override
		protected boolean canEdit(Object element) {
			if(isEditing)
				return element instanceof TimeSeries;
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof TimeSeries){
				TimeSeries el=(TimeSeries) element;
				return el.isTimeRemainingActivated();
			}
			return false;
		}

		@Override
		protected void setValue(Object element, Object value) {
			TimeSeries el = (TimeSeries) element;
			el.setTimeRemainingActivated((Boolean) value);
			
			//neuralNetworkProvider.createAllInputPoints(stock);
			//stock.getNeuralNetwork().getConfiguration().inputNeuronChanged();
			
			treeViewer.refresh();

		}

	}	
	
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
	private void neuralNetworkConfigSelected(
			@Optional @UIEventTopic(IEventConstant.NEURAL_NETWORK_CONFIG_SELECTED) Configuration config) {
		if(config==null)return;
		
		if(config.getParent()==null)return;
		if(!isCompositeAbleToReact(config.getParent().getUUID()))return;
		if(isEditing)return;
		
		configLocal=config;
		
		refreshTimeSeries();
		
	}

	
	//################################
	//##     ColumnLabelProvider    ##
	//################################
	
	    
	class InputSeriesLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralNetworkSerieCategory){
				NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory) element;
				return el.name.getCategoryLabel();
			}
			else if(element instanceof TimeSeries){
				TimeSeries el=(TimeSeries) element;
				return String.valueOf(el.getName());
			}
			return super.getText(element);
		}
		
	}
	
	class NbOfValuesLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralNetworkSerieCategory){
				//NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory) element;
				return "-";
			}
			else if(element instanceof TimeSeries){
				TimeSeries el=(TimeSeries) element;
				return String.valueOf(el.getNumberOfPastValues());
			}
			return super.getText(element);
		}
		
	}
	
	class TimeLeftLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralNetworkSerieCategory){
				//NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory) element;
				return "-";
			}
			else if(element instanceof TimeSeries){
				TimeSeries el=(TimeSeries) element;
				return String.valueOf(el.isTimeRemainingActivated());
			}
			return super.getText(element);
		}
		
	}
	
	
	class MinMaxValuesLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof NeuralNetworkSerieCategory){
				//NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory) element;
				return "-";
			}
			else if(element instanceof TimeSeries){
				TimeSeries el=(TimeSeries) element;
				
				String min=String.format("%.3f", el.getMinValue());
				String max=String.format("%.3f", el.getMaxValue());
				String date=DateTool.dateToString(el.getMinMaxLastRefreshDate());
				
				return "["+min+":"+max+"] "+date;
			}
			return super.getText(element);
		}
		
	}
	
	//################################
	//##             Job            ##
	//################################
	
	class TimeSeriesUpdater extends Job{
		
		
		private void finilizeProgressBar(){
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisible(false);
					parent.layout();
					configEditor.refreshTrainingDataViewer();
					refreshTimeSeries();
					
					//Save the configuration
					configEditor.save();
					
				}
			});
		}
		
		private void initProgressBar(){
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					progressBar.setMaximum(stock.getNeuralNetwork().getConfiguration().getNetworkArchitectures().size());
					progressBar.setSelection(0);
					progressBar.setVisible(true);
					parent.layout();
				}
			});
			
		}
		
		
		private void increaseProgressBar(){
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					progressBar.setSelection(progressBar.getSelection()+1);
				}
			});
		}
		
		
		public TimeSeriesUpdater() {
			super("Time Series Updater");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			initProgressBar();
			
			//Save the old min max inner neurons
			int[] minMax=stock.getNeuralNetwork().getConfiguration().getMinMaxInnerNeurons();
			
			//Copy the time Series from the local one to the current configuration
			for(TimeSeries s:configLocal.getAllTimeSeries())
				InfoPart.postInfoText(eventBroker, "Series: "+s);
			stock.getNeuralNetwork().getConfiguration().copyTimeSeriesFrom(configLocal);
			configLocal=stock.getNeuralNetwork().getConfiguration();
			
			//Recreate all inputs points
			if (monitor.isCanceled())return Status.CANCEL_STATUS;
			neuralNetworkProvider.createAllValuePoints(configLocal,true);
			
			//Update all architectures
			int pos=1;
			for(NetworkArchitecture archi:configLocal.getNetworkArchitectures()){
				if (monitor.isCanceled())return Status.CANCEL_STATUS;
				
				
				InfoPart.postInfoText(eventBroker, "Adation of Architecture: "+archi.getId()+" "+
						(pos++)+"/"+configLocal.getNetworkArchitectures().size());
				archi.adaptNetwork(configLocal.getInputNeuronNames());
				
				//Update the Progress Bar
				increaseProgressBar();
				//eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_INPUT_SAVING_STEP,stock);
				
			}
			//Reset the Min Max inner neurons
			configLocal.setMinMaxInnerNeurons(minMax);
			configLocal.resetTrainingData();
			neuralNetworkProvider.createAllValuePoints(configLocal,false);
			configLocal.setResultsCalculationNeeded(true);
			for(TimeSeries s:configLocal.getAllTimeSeries()){
				s.resetMinMaxValues(false);
			}
			
			
			finilizeProgressBar();
			
			//eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_INPUT_SAVED,stock);
			return Status.OK_STATUS;
		}
		
	}
	
			
}
