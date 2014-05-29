package com.munch.exchange.parts;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.watchlist.Watchlist;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;
import com.munch.exchange.services.IQuoteProvider;
import com.munch.exchange.services.IWatchlistProvider;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

public class WatchListPart {
	
	@Inject
	IEclipseContext context;
	
	@Inject
	Shell shell;
	
	@Inject
	IWatchlistProvider watchlistProvider;
	
	@Inject
	IQuoteProvider quoteProvider;
	
	
	private WatchlistTreeContentProvider contentProvider;
	
	//private Watchlist currentList=null;
	private Combo comboWachtlist;
	private Button btnDelete;
	private TreeViewer treeViewer;
	
	public WatchListPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		
		contentProvider=ContextInjectionFactory.make( WatchlistTreeContentProvider.class,context);
		
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
						refreshViewer();
					}
				}
				watchlistProvider.save();
			}
		});
		
		DateTime dateTimeWatchPeriod = new DateTime(compositeHeader, SWT.BORDER | SWT.DROP_DOWN | SWT.LONG);
		dateTimeWatchPeriod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
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
		
		
		treeViewer = new TreeViewer(parent, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(contentProvider);
		Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TreeViewerColumn treeViewerColumnName = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
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
		});
		TreeColumn trclmnName = treeViewerColumnName.getColumn();
		trclmnName.setWidth(100);
		trclmnName.setText("Name");
		
		//Add Drop Support
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
	    treeViewer.addDropSupport(operations, transferTypes, new WatchlistTreeViewerDropAdapter(treeViewer,contentProvider,watchlistProvider));
	    
	    TreeViewerColumn treeViewerColumnPrice = new TreeViewerColumn(treeViewer, SWT.NONE);
	    TreeColumn trclmnPrice = treeViewerColumnPrice.getColumn();
	    trclmnPrice.setWidth(100);
	    trclmnPrice.setText("Price");
	    treeViewer.setInput(contentProvider.getCurrentList());

	    refreshViewer();
	}
	
	
	/**
	 * if not loaded the quote will be loaded
	 */
	private void searchLastQuote(ExchangeRate rate){
		if(rate.getRecordedQuote().isEmpty() && !(rate instanceof EconomicData)){
			quoteProvider.load(rate);
		}
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
	 
	private List<WatchlistEntity> findAllWatchlistEntities(String uuid){
		List<WatchlistEntity> list=new LinkedList<WatchlistEntity>();
		
		return list;
	}
	
	
	@Inject
	private void loadedRate(@Optional  @UIEventTopic(IEventConstant.RATE_LOADED) ExchangeRate rate ){
		if(treeViewer!=null && rate!=null){
			
			for(Watchlist watchlist:this.watchlistProvider.load().getLists()){
				for(WatchlistEntity ent:watchlist.getList()){
					if(ent.getRateUuid().equals(rate.getUUID())){
						ent.setRate(rate);
						treeViewer.refresh();
					}
				}
			}
		}
	}
	
	
	@Inject
	private void quoteLoaded(@Optional  @UIEventTopic(IEventConstant.QUOTE_LOADED) String rate_uuid ){
		
		if(rate_uuid==null || rate_uuid.isEmpty()){
			return;
		}
		
		/*
		ExchangeRate incoming=exchangeRateProvider.load(rate_uuid);
		if(incoming==null || rate==null || lblFulleName==null || lblQuote==null){
			return;
		}
		
		if(!incoming.getUUID().equals(rate.getUUID())){
			return;
		}
		
		setLabelValues();
		*/
	}
	

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}

}
