package com.munch.exchange.parts.watchlist;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.HistoricalDataLoader;
import com.munch.exchange.job.Optimizer.OptimizationInfo;
import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.watchlist.Watchlist;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;
import com.munch.exchange.parts.composite.RateChart;
import com.munch.exchange.parts.composite.RateChartBollingerBandsComposite;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IQuoteProvider;
import com.munch.exchange.services.IWatchlistProvider;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.goataa.impl.utils.Individual;

public class WatchlistPart {
	
	@Inject
	IEclipseContext context;
	
	@Inject
	Shell shell;
	
	@Inject
	IWatchlistProvider watchlistProvider;
	
	@Inject
	IQuoteProvider quoteProvider;
	
	@Inject
	IExchangeRateProvider rateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	//Loader
	HistoricalDataLoader historicalDataLoader;
	
	private WatchlistTreeContentProvider contentProvider;
	private WatchlistService watchlistService;
	private WatchlistViewerComparator comparator;
	
	private Calendar startWatchDate=Calendar.getInstance();
	
	//private Watchlist currentList=null;
	private Combo comboWachtlist;
	private Button btnDelete;
	private TreeViewer treeViewer;
	private TreeViewerColumn treeViewerColumnName;
	private TreeViewerColumn treeViewerColumnPrice;
	private TreeViewerColumn treeViewerColumnChange;
	private DateTime dateTimeWatchPeriod;
	private TreeViewerColumn treeViewerColumnBuyAndOld;
	private TreeViewerColumn treeViewerColumnMaxProfit;
	private TreeViewerColumn treeViewerColumnBollingerBand;
	
	public WatchlistPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		
		contentProvider=ContextInjectionFactory.make( WatchlistTreeContentProvider.class,context);
		watchlistService=ContextInjectionFactory.make( WatchlistService.class,context);
		historicalDataLoader=ContextInjectionFactory.make( HistoricalDataLoader.class,context);
		
		comparator=new WatchlistViewerComparator(watchlistService);
		
		parent.setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(parent, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(4, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboWachtlist = new Combo(compositeHeader, SWT.NONE);
		comboWachtlist.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboWachtlist.setText("Empty...");
		fillComboWachtlist();
		comboWachtlist.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(comboWachtlist.getText().isEmpty())return;
				
				for(Watchlist list:watchlistProvider.load().getLists()){
					list.setSelected(comboWachtlist.getText().equals(list.getName()));
					if(list.isSelected()){
						contentProvider.setCurrentList(list);
						loadNextHistoricalData();
						refreshViewer();
					}
				}
				watchlistProvider.save();
			}
		});
		
		dateTimeWatchPeriod = new DateTime(compositeHeader, SWT.BORDER | SWT.DROP_DOWN | SWT.LONG);
		startWatchDate.add(Calendar.DAY_OF_YEAR, -120);
		comparator.setStartWatchDate(startWatchDate);
		dateTimeWatchPeriod.setDate(startWatchDate.get(Calendar.YEAR), startWatchDate.get(Calendar.MONTH), startWatchDate.get(Calendar.DAY_OF_MONTH));
		dateTimeWatchPeriod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Details: "+e.detail);
				startWatchDate.set(	dateTimeWatchPeriod.getYear(),
									dateTimeWatchPeriod.getMonth(),
									dateTimeWatchPeriod.getDay(),
									dateTimeWatchPeriod.getHours(),
									dateTimeWatchPeriod.getMinutes(),
									dateTimeWatchPeriod.getSeconds());
				comparator.setStartWatchDate(startWatchDate);
				for(WatchlistEntity ent:contentProvider.getCurrentList().getList()){
					watchlistService.refreshHistoricalData(ent, startWatchDate);
				}
				refreshViewer();
			}
		});
		dateTimeWatchPeriod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		//startWatchDate.get(Calendar.DAY_OF_MONTH)
		
		
		
		
		Button btnNewList = new Button(compositeHeader, SWT.NONE);
		btnNewList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnNewList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				InputDialog dlg=new InputDialog(shell, "Create new watchlist", "Enter the new wachtlichname", "new list", null);
				 if (dlg.open() == Window.OK) {
					
					 Watchlist list=watchlistProvider.load().addNewList(dlg.getValue());
					 if(list==null){
						 MessageDialog.openError(shell, "Watchlist error", "Cannot create the watchlist: "+dlg.getValue());
						 return;
					 }
					 
					 //Save the list
					 contentProvider.setCurrentList(list);
					 refreshViewer();
					 //watchlistProvider.save();
					 fillComboWachtlist();
					 
				 }
				
			}
		});
		btnNewList.setText("New List");
		
		btnDelete = new Button(compositeHeader, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean res=MessageDialog.openQuestion(shell, "Delete current watchlist",
						"Do you want to delete the watchlist \""+contentProvider.getCurrentList().getName()+"\"");
				if (!res) return;
				
				int index=0;
				for(Watchlist list:watchlistProvider.load().getLists()){
					if(list.getName().equals(contentProvider.getCurrentList().getName()))break;
					index++;
				}
				if(watchlistProvider.load().getLists().size()==1){
					watchlistProvider.load().getLists().clear();
					fillComboWachtlist();
					watchlistProvider.save();
					return;
				}
				
				if(watchlistProvider.load().getLists().remove(index)!=null){
					if(!watchlistProvider.load().getLists().isEmpty()){
						contentProvider.setCurrentList(watchlistProvider.load().getLists().getFirst());
						contentProvider.getCurrentList().setSelected(true);
					}
					refreshViewer();
					fillComboWachtlist();
					watchlistProvider.save();
				}
				else{
					 MessageDialog.openError(shell, "Watchlist error", "Cannot delete the watchlist: "+contentProvider.getCurrentList().getName());
				}
				
			}
		});
		btnDelete.setText("Delete");
		btnDelete.setEnabled(!watchlistProvider.load().getLists().isEmpty());
		
		
		//##############################
		//##   Tree viewer definition ##
		//##############################
		
		treeViewer = new TreeViewer(parent, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(contentProvider);
		Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		//Add Drop Support
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
	    treeViewer.addDropSupport(operations, transferTypes, 
	    new WatchlistTreeViewerDropAdapter(treeViewer,contentProvider,watchlistProvider,rateProvider,historicalDataLoader,watchlistService));
	   
		
	    treeViewer.setInput(contentProvider.getCurrentList());
	    treeViewer.setComparator(comparator);
	    //Double Click listener
	  	treeViewer.addDoubleClickListener(new IDoubleClickListener() {
	  			public void doubleClick(DoubleClickEvent event) {
	  				ISelection selection =treeViewer.getSelection();
					if (selection != null & selection instanceof IStructuredSelection) {
						IStructuredSelection strucSelection = (IStructuredSelection) selection;
						Object item =strucSelection.getFirstElement();
					
						if(item instanceof WatchlistEntity){
							WatchlistEntity entity=(WatchlistEntity)item;
							if(entity.getRate()!=null)
								eventBroker.send(IEventConstant.RATE_OPEN,entity.getRate());
							
						}
					}
	  			}
	  		});
	    
		//##############################
		//##          Columns         ##
		//##############################	    
	    
		treeViewerColumnName = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumnName.setLabelProvider(new NameColumnLabelProvider());
		TreeColumn trclmnName = treeViewerColumnName.getColumn();
		trclmnName.setWidth(300);
		trclmnName.setText("Name");
		trclmnName.addSelectionListener(getSelectionAdapter(trclmnName, 0));
		
	    
	    treeViewerColumnPrice = new TreeViewerColumn(treeViewer, SWT.NONE);
	    treeViewerColumnPrice.setLabelProvider(new PriceColumnLabelProvider());
	    TreeColumn trclmnPrice = treeViewerColumnPrice.getColumn();
	    trclmnPrice.setAlignment(SWT.CENTER);
	    trclmnPrice.setWidth(100);
	    trclmnPrice.setText("Price");
	    trclmnPrice.addSelectionListener(getSelectionAdapter(trclmnPrice, 1));
	    
	    treeViewerColumnChange = new TreeViewerColumn(treeViewer, SWT.NONE);
	    treeViewerColumnChange.setLabelProvider(new ChangeColumnLabelProvider());
	    TreeColumn trclmnChange = treeViewerColumnChange.getColumn();
	    trclmnChange.setAlignment(SWT.RIGHT);
	    trclmnChange.setWidth(100);
	    trclmnChange.setText("Change");
	    trclmnChange.addSelectionListener(getSelectionAdapter(trclmnChange, 2));
	    
	    treeViewerColumnBuyAndOld = new TreeViewerColumn(treeViewer, SWT.NONE);
	    treeViewerColumnBuyAndOld.setLabelProvider(new BuyAndOldColumnLabelProvider());
	    TreeColumn trclmnBuyandold = treeViewerColumnBuyAndOld.getColumn();
	    trclmnBuyandold.setAlignment(SWT.RIGHT);
	    trclmnBuyandold.setWidth(110);
	    trclmnBuyandold.setText("Buy and old");
	    trclmnBuyandold.addSelectionListener(getSelectionAdapter(trclmnBuyandold, 3));
	    
	    treeViewerColumnMaxProfit = new TreeViewerColumn(treeViewer, SWT.NONE);
	    treeViewerColumnMaxProfit.setLabelProvider(new MaxProfitColumnLabelProvider());
	    TreeColumn trclmnNewColumn = treeViewerColumnMaxProfit.getColumn();
	    trclmnNewColumn.setAlignment(SWT.RIGHT);
	    trclmnNewColumn.setWidth(100);
	    trclmnNewColumn.setText("Max profit");
	    trclmnNewColumn.addSelectionListener(getSelectionAdapter(trclmnNewColumn, 4));
	    
	    treeViewerColumnBollingerBand = new TreeViewerColumn(treeViewer, SWT.NONE);
	    treeViewerColumnBollingerBand.setLabelProvider(new BollingerBandColumnLabelProvider());
	    TreeColumn trclmnBollingerBand = treeViewerColumnBollingerBand.getColumn();
	    trclmnNewColumn.setAlignment(SWT.RIGHT);
	    trclmnBollingerBand.setWidth(130);
	    trclmnBollingerBand.setText("Bollinger Band");
	    trclmnBollingerBand.addSelectionListener(getSelectionAdapter(trclmnBollingerBand, 5));
	    
	    
	    tree.setSortColumn(trclmnChange);
	    tree.setSortDirection(1);
	    
	    refreshViewer();
	}
	
	private void refreshViewer(){
		if(treeViewer!=null){
			treeViewer.setInput(contentProvider.getCurrentList());
			treeViewer.refresh();
		}
	}
	
	private void fillComboWachtlist(){
		comboWachtlist.removeAll();
		if(btnDelete!=null){
			btnDelete.setEnabled(!watchlistProvider.load().getLists().isEmpty());
		}
		
		if(watchlistProvider.load().getLists().isEmpty()){
			comboWachtlist.setText("Empty..");
			return;
		}
		
		
		//comboWachtlist.clearSelection();
		
		for(Watchlist list: watchlistProvider.load().getLists()){
			comboWachtlist.add(list.getName());
			if(list.isSelected()){
				contentProvider.setCurrentList(list);
				comboWachtlist.setText(list.getName());
			}
		}
		
		
	}
	
	
	private void loadNextHistoricalData(){
		WatchlistEntity toLoad=null;
		for(WatchlistEntity ent:this.contentProvider.getCurrentList().getList()){
			if(ent.getRate()==null)continue;
			if(ent.getRate().getHistoricalData().isEmpty())
				toLoad=ent;
		}
		
		if(toLoad!=null){
			historicalDataLoader.setRate(toLoad.getRate());
			historicalDataLoader.schedule();
		}
		else{
			for(WatchlistEntity ent:this.contentProvider.getCurrentList().getList()){
				watchlistService.refreshHistoricalData(ent, startWatchDate);
			}
			treeViewer.refresh();
		}
	}
	
	 
	//################################
	//##       Event Reaction       ##
	//################################
	
	
	@Inject
	private void loadedRate(@Optional  @UIEventTopic(IEventConstant.RATE_LOADED) ExchangeRate rate ){
		if(treeViewer!=null && rate!=null){
			
			List<WatchlistEntity> list=watchlistService.findAllWatchlistEntities(rate.getUUID());
			for(WatchlistEntity ent:list){
				ent.setRate(rate);
				watchlistService.refreshQuote(ent);
			}
			if(list.size()>0)
				treeViewer.refresh();
			
			boolean areAllLoaded=true;
			for(WatchlistEntity ent:contentProvider.getCurrentList().getList()){
				if(ent.getRate()==null)areAllLoaded=false;
			}
			if(areAllLoaded)
				loadNextHistoricalData();
		}
	}
	
	
	@Inject
	private void quoteLoaded(@Optional  @UIEventTopic(IEventConstant.QUOTE_LOADED) String rate_uuid ){
		
		if(!isReadyToReact(rate_uuid)){return;}
		
		List<WatchlistEntity> list=watchlistService.findAllWatchlistEntities(rate_uuid);
		if(list.size()>0){
			for(WatchlistEntity entity:list){
				watchlistService.refreshQuote(entity);
				watchlistService.refreshHistoricalData(entity, startWatchDate);
			}
			treeViewer.refresh();
		}
	}
	
	@Inject
	private void quoteUpdated(@Optional  @UIEventTopic(IEventConstant.QUOTE_UPDATE) String rate_uuid ){
		
		if(!isReadyToReact(rate_uuid)){return;}
		
		List<WatchlistEntity> list=watchlistService.findAllWatchlistEntities(rate_uuid);
		if(list.size()>0){
			for(WatchlistEntity entity:list){
				watchlistService.refreshQuote(entity);
				watchlistService.refreshHistoricalData(entity, startWatchDate);
			}
			treeViewer.refresh();
		}
	}
	
	@Inject
	private void historicalDataLoaded(@Optional  @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid ){
		
		if(!isReadyToReact(rate_uuid)){return;}
		
		WatchlistEntity entity=watchlistService.findEntityFromList(contentProvider.getCurrentList(),  rate_uuid);
		if(entity!=null){
			watchlistService.refreshHistoricalData(entity, startWatchDate);
			loadNextHistoricalData();
			treeViewer.refresh();
		}
	}
	
	@Inject
	private void OptimizerDataLoaded(@Optional  @UIEventTopic(IEventConstant.OPTIMIZATION_RESULTS_LOADED) String rate_uuid ){
		
		if(!isReadyToReact(rate_uuid)){return;}
		
		WatchlistEntity entity=watchlistService.findEntityFromList(contentProvider.getCurrentList(),  rate_uuid);
		if(entity!=null){
			watchlistService.refreshHistoricalData(entity, startWatchDate);
			treeViewer.refresh();
		}
	}
	
	
	
	@Inject
	private void OptimizerNewBestFound(
			@Optional @UIEventTopic(IEventConstant.OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptimizationInfo info) {
		if(info==null)return;
		if(!isReadyToReact(info.getRate().getUUID())){return;}
		
		WatchlistEntity entity=watchlistService.findEntityFromList(contentProvider.getCurrentList(),  info.getRate().getUUID());
		if(entity!=null){
			watchlistService.refreshHistoricalData(entity, startWatchDate);
			treeViewer.refresh();
		}
		
	}
	
	
	private boolean isReadyToReact(String rate_uuid){
		if(rate_uuid==null || rate_uuid.isEmpty()){
			return false;
		}
		if(treeViewer==null)return false;
		
		return true;
	}
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
	
	//################################
	//##     ColumnLabelProvider    ##
	//################################
	
	class NameColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
			return null;
		}
		public String getText(Object element) {
			if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getRate()!=null)
					return entity.getRate().getFullName();
				return entity.getRateUuid();
			}
			return element == null ? "" : element.toString();
		}
	}
	
	class PriceColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
    		return null;
    	}
    	public String getText(Object element) {
    		if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getLastQuote()!=null){
					return String.valueOf(entity.getLastQuote().getLastTradePrice());
				}
			}
    		return "loading..";
    	}
	}
	
	class ChangeColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
    		// TODO Auto-generated method stub
    		return null;
    	}
    	public String getText(Object element) {
    		if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getLastQuote()!=null){
					float per = entity.getLastQuote().getChange() * 100
							/ entity.getLastQuote().getLastTradePrice();
					return String.format("%.2f", per) + "%";
					
				}
			}
    		return "";
    	}
	}
	
	class BuyAndOldColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
    		// TODO Auto-generated method stub
    		return null;
    	}
    	public String getText(Object element) {
    		if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getRate()!=null && !entity.getRate().getHistoricalData().isEmpty()){
					return String.format("%.2f",100*entity.getBuyAndOld())+ "%";
				}
			}
    		return "loading...";
    	}
	}
	
	class MaxProfitColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
    		return null;
    	}
    	public String getText(Object element) {
    		if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getRate()!=null && !entity.getRate().getHistoricalData().isEmpty()){
					double profit=100*entity.getMaxProfit();
					return String.format("%.2f",profit)+ "%";
				}
			}
    		return "loading...";
    	}
	}
	
	class BollingerBandColumnLabelProvider extends ColumnLabelProvider{
		public Image getImage(Object element) {
    		return null;
    	}
    	public String getText(Object element) {
    		if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getBollingerBandTrigger()!=null ){
					return String.valueOf(entity.getBollingerBandTrigger());
				}
			}
    		return "no opt. data";
    	}
		@Override
		public Color getBackground(Object element) {
			if(element instanceof WatchlistEntity){
				WatchlistEntity entity=(WatchlistEntity) element;
				if(entity.getBollingerBandTrigger()!=null ){
					switch (entity.getBollingerBandTrigger().calculateTriggerType(2)) {
					case CLOSE_TO_BUY:
						return new Color(null,	255,165,0);
					case CLOSE_TO_SELL:
						return new Color(null, 165, 255, 0);
					case NONE:
						return new Color(null, 255, 255, 255);
					case TO_BUY:
						return new Color(null, 255, 0, 0);
					case TO_SELL:
						return new Color(null, 0, 255, 0);
					
					}
				}
			}
			return super.getBackground(element);
		}
    	
    	
	}
	
	
	 private SelectionAdapter getSelectionAdapter(final  TreeColumn  column,
		      final int index) {
		    SelectionAdapter selectionAdapter = new SelectionAdapter() {
		      @Override
		      public void widgetSelected(SelectionEvent e) {
		        comparator.setColumn(index);
		        int dir = comparator.getDirection();
		        treeViewer.getTree().setSortDirection(dir);
		        treeViewer.getTree().setSortColumn(column);
		        treeViewer.refresh();
		      }
		    };
		    return selectionAdapter;
		  }
	
	
}
