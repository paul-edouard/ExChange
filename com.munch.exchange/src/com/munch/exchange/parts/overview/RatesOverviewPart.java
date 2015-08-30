 
package com.munch.exchange.parts.overview;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jfree.util.Log;

import com.ib.controller.Types.SecType;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.IImageKeys;
import com.munch.exchange.job.quote.QuoteLoader;
import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.parts.RateEditorPart;
import com.munch.exchange.parts.chart.ChartEditorPart;
import com.munch.exchange.services.IBundleResourceLoader;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataListener;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataProvider;


public class RatesOverviewPart implements IIBTopMktDataListener{
	
	private static Logger logger = Logger
			.getLogger(RatesOverviewPart.class);
	
	@Inject
	IEclipseContext context;
	
	@Inject
	ESelectionService selectionService;
	
	@Inject
	EMenuService menuService;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	EPartService partService;
	
	@Inject
	private MApplication application;
	
	@Inject
	IBundleResourceLoader bundleResourceLoader;
	
	@Inject
	IIBTopMktDataProvider ibTopMktDataProvider;
	
	
	
	private TreeViewer treeViewer;
	private RatesTreeContentProvider contentProvider;
	private RatesTreeLabelProvider labelProvider;
	private QuoteLoader quoteLoader;
	
	
	
	@Inject
	public RatesOverviewPart() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		
		//Selection listener
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection=event.getSelection();
				selectionService.setSelection(selection);
					
			}
		});
		
		//Double Click listener
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				//System.out.println("Double Click!");
				
				ISelection selection =treeViewer.getSelection();
				if (selection != null & selection instanceof IStructuredSelection) {
					IStructuredSelection strucSelection = (IStructuredSelection) selection;
					Object item =strucSelection.getFirstElement();
				
					
					if(item instanceof ExchangeRate){
						ExchangeRate rate=(ExchangeRate)item;
						openRateEditor(rate);	
					}
					else if(item instanceof IbContract){
						IbContract contract=(IbContract) item;
						openGraphEditor(contract);
					}
					
				}
				
			}
		});
		
		contentProvider=ContextInjectionFactory.make( RatesTreeContentProvider.class,context);
		treeViewer.setContentProvider(contentProvider);
		labelProvider=ContextInjectionFactory.make( RatesTreeLabelProvider.class,context);
		treeViewer.setLabelProvider(labelProvider);
		//treeViewer.setLabelProvider(new TestLabelProvider());
		
		treeViewer.setInput(contentProvider.getRoot());
		
		//Add Drag Support
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		treeViewer.addDragSupport(operations, transferTypes , new RatesTreeDragSourceListener(treeViewer));

		
		menuService.registerContextMenu(treeViewer.getTree(), "com.munch.exchange.popupmenu.rates_overview");
		
		//Create and start the quote loader
		quoteLoader=ContextInjectionFactory.make( QuoteLoader.class,context);
		
		
		//Add a listener
		ibTopMktDataProvider.addIbTopMktDataListener(this);
		
	}
	
	
	//
	//OPEN RATE EDITOR FUNCTIONS
	//
	
	/**
	 * Open a new Rate Editor,
	 * If the Editor already exist it will be bring to the top
	 * @param rate
	 */
	private void openRateEditor(ExchangeRate rate){
		
		MPart part=searchPart(RateEditorPart.RATE_EDITOR_ID,rate.getUUID());
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setRateEditorPartContext(part,rate);
			}
			
				partService.bringToTop(part);
				return;
		}
		
		//Create the part
		part=createRateEditorPart(rate);
		
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightup", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
	
		
	}
	
	
	private MPart createRateEditorPart(ExchangeRate rate){
		MPart part = partService.createPart(RateEditorPart.RATE_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(rate.getName());
		part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(rate.getUUID());
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setRateEditorPartContext(part,rate);
		
		return part;
	}
	
	private String getIconURI(ExchangeRate rate){
		if(rate instanceof Stock){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_STOCK).toString();
		}
		else if(rate instanceof Commodity){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_COMMODITY).toString();
		}
		else if(rate instanceof Fund){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_FUND).toString();
		}
		else if(rate instanceof Currency){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_CURRENCY).toString();
		}
		else if(rate instanceof Indice){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_INDICE).toString();
		}
		
		return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_COMMON).toString();
	}
	
	private void setRateEditorPartContext(MPart part,ExchangeRate rate){
		part.setContext(context.createChild());
		part.getContext().set(ExchangeRate.class, rate);
		part.getContext().set(MDirtyable.class, new MyMDirtyable(part));
	}
	
	private MPart searchPart(String partId,String tag){
		
		List<MPart> parts=getPartList(partId, tag);
		if(parts.isEmpty())return null;
		return parts.get(0);
	}
	
	private List<MPart> getPartList(String partId,String tag){
		List<String> tags=new LinkedList<String>();
		tags.add(tag);
			
		List<MPart> parts=modelService.findElements(application,
				partId, MPart.class,tags );
		return parts;
	}
	
	//
	//OPEN GRAPH EDITOR FUNCTIONS
	//
	
	private void openGraphEditor(IbContract contract){
		
		MPart part=searchPart(ChartEditorPart.CHART_EDITOR_ID,String.valueOf(contract.getId()));
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setGraphEditorContext(part,contract);
			}
			
				partService.bringToTop(part);
				return;
		}
		
		//Create the part
		part=createGraphEditorPart(contract);
		
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightup", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);	
	}
	
	private MPart createGraphEditorPart(IbContract contract){
		MPart part = partService.createPart(ChartEditorPart.CHART_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(contract.getLongName());
		part.setIconURI(getIconURI(contract));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(String.valueOf(contract.getId()));
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setGraphEditorContext(part,contract);
		
		return part;
	}
	
	private void setGraphEditorContext(MPart part,IbContract contract){
		part.setContext(context.createChild());
		part.getContext().set(IbContract.class, contract);
		//part.getContext().set(MDirtyable.class, new MyMDirtyable(part));
	}
	
	private String getIconURI(IbContract contract){
		if(contract.getSecType()==SecType.STK){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_STOCK).toString();
		}
		else if(contract.getSecType()==SecType.CMDTY){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_COMMODITY).toString();
		}
		else if(contract.getSecType()==SecType.FUND){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_FUND).toString();
		}
		else if(contract.getSecType()==SecType.CASH){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_CURRENCY).toString();
		}
		else if(contract.getSecType()==SecType.IND){
			return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_INDICE).toString();
		}
		
		return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_COMMON).toString();
	}
	
	
	
	@Inject
	private void openRate(@Optional  @UIEventTopic(IEventConstant.RATE_OPEN) ExchangeRate rate ){
		
		if(treeViewer!=null && rate!=null){
			openRateEditor( rate);
		}
	}
	
	@Inject
	private void addRate(@Optional  @UIEventTopic(IEventConstant.RATE_NEW) ExchangeRate rate ){
		
		if(treeViewer!=null && rate!=null){
			contentProvider.addExChangeRate(rate);
			Object[] elements=treeViewer.getExpandedElements();
			treeViewer.refresh();
			treeViewer.setExpandedElements(elements);
		}
	}
	
	@Inject
	private void deleteRate(@Optional  @UIEventTopic(IEventConstant.RATE_DELETE) ExchangeRate rate ){
		if(treeViewer!=null && rate!=null){
			contentProvider.deleteExChangeRate(rate);
			Object[] elements=treeViewer.getExpandedElements();
			
			
			treeViewer.refresh();
			treeViewer.setExpandedElements(elements);
		}
	}
	
	@Inject
	private void loadingRate(@Optional  @UIEventTopic(IEventConstant.RATE_LOADING) ExchangeRate rate ){
		if(treeViewer!=null && rate!=null){
			//treeViewer.refresh();
		}
	}
	
	@Inject
	private void loadedRate(@Optional  @UIEventTopic(IEventConstant.RATE_LOADED) ExchangeRate rate ){
		if(treeViewer!=null && rate!=null){
			treeViewer.refresh();
		}
	}
	
	/*
	@Inject
	private void quoteUpdate(@Optional  @UIEventTopic(IEventConstant.QUOTE_UPDATE) ExchangeRate rate ){
		logger.info("Message recieved: Quote update!");
	}
	@Inject
	private void quoteLoaded(@Optional  @UIEventTopic(IEventConstant.QUOTE_LOADED) ExchangeRate rate ){
		logger.info("Message recieved: Quote loaded!");
	}
	*/
	//
	//CONTRACT EVENT
	//
	
	@Inject
	private void addContract(@Optional  @UIEventTopic(IEventConstant.CONTRACT_NEW) IbContract contract ){
		
		if(treeViewer!=null && contract!=null){
			contentProvider.addContract(contract);
			
			Object[] elements=treeViewer.getExpandedElements();
			treeViewer.refresh();
			treeViewer.setExpandedElements(elements);
		}
	}
	
	@Inject
	private void removeContract(@Optional  @UIEventTopic(IEventConstant.CONTRACT_DELETE) IbContract contract ){
		
		if(treeViewer!=null && contract!=null){
			//contentProvider.removeContract(contract);
			
			Object[] elements=treeViewer.getExpandedElements();
			contentProvider.relaodContracts();
			treeViewer.setInput(contentProvider.getRoot());
			treeViewer.refresh();
			treeViewer.setExpandedElements(elements);
		}
	}
	
	
	
	
	@PreDestroy
	public void preDestroy() {
		
		//Stop the quote loader
		try {
			quoteLoader.cancel();
			quoteLoader.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//remove a listener
		ibTopMktDataProvider.removeIbTopMktDataListener(this);
		
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}

	@Override
	public void ibTopMktDataChanged(IbTopMktData ibTopMktData) {
		labelProvider.getTopMktDataMap().put(ibTopMktData.getContractId(), ibTopMktData);
		
		Display.getDefault().asyncExec(new mktDataUpdater(ibTopMktData.getContractId())); 
		
	}
	
	private class mktDataUpdater implements Runnable{
		
		int contractId;
		
		

		public mktDataUpdater(int contractId) {
			super();
			this.contractId = contractId;
		}



		@Override
		public void run() {
			IbContract contract=contentProvider.getRoot().searchIbContract(contractId);
			if(contract==null)return;
			
			treeViewer.update(contract, null);
			
			
		}
		
	}
	
	
}