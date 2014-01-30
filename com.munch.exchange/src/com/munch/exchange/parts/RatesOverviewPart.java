 
package com.munch.exchange.parts;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IBundleResourceLoader;


public class RatesOverviewPart {
	
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
	
	
	private TreeViewer treeViewer;
	private RatesTreeContentProvider contentProvider;
	
	
	@Inject
	public RatesOverviewPart() {
		//TODO Your code here
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
						//logger.info("Double click on: "+rate.getFullName());
						openRateEditor(rate);
						
					}
					
				}
				
			}
		});
		
		contentProvider=ContextInjectionFactory.make( RatesTreeContentProvider.class,context);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(ContextInjectionFactory.make( RatesTreeLabelProvider.class,context));
		//treeViewer.setLabelProvider(new TestLabelProvider());
		
		treeViewer.setInput(contentProvider.getRoot());
		
		menuService.registerContextMenu(treeViewer.getTree(), "com.munch.exchange.popupmenu.rates_overview");
		
		
	}
	
	
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
		part.setContext(EclipseContextFactory.create());
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
			treeViewer.refresh();
		}
	}
	@Inject
	private void loadedRate(@Optional  @UIEventTopic(IEventConstant.RATE_LOADED) ExchangeRate rate ){
		if(treeViewer!=null && rate!=null){
			treeViewer.refresh();
		}
	}
	
	
	
	@PreDestroy
	public void preDestroy() {
		//TODO Your code here
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}
	
	
}