 
package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;


public class RatesPart {
	
	@Inject
	IEclipseContext context;
	
	@Inject
	ESelectionService selectionService;
	
	private TreeViewer treeViewer;
	private RatesTreeContentProvider contentProvider;
	
	
	@Inject
	public RatesPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection=event.getSelection();
				selectionService.setSelection(selection);
				
				
			}
		});
		
		
		contentProvider=ContextInjectionFactory.make( RatesTreeContentProvider.class,context);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(ContextInjectionFactory.make( RatesTreeLabelProvider.class,context));
		//treeViewer.setLabelProvider(new TestLabelProvider());
		
		treeViewer.setInput(contentProvider.getRoot());
		
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