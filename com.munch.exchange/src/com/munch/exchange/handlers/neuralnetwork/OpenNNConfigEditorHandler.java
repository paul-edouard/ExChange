 
package com.munch.exchange.handlers.neuralnetwork;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.parts.RateEditorPart;
import com.munch.exchange.parts.RatesOverviewPart;
import com.munch.exchange.parts.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.parts.neuralnetwork.NeuralNetworkConfigEditor;
import com.munch.exchange.services.IBundleResourceLoader;

public class OpenNNConfigEditorHandler {
	
	
	private boolean canExcecute=false;
	private ExchangeRate selectedRate;
	private Stock selectedStock;
	
	
	private static Logger logger = Logger
			.getLogger(OpenNNConfigEditorHandler.class);
	
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
	
	
	
	@Execute
	public void execute(Shell shell) {
		if(selectedStock==null){
			MessageDialog.openError(shell, "Selection error", "No rate selected");
			return;
		}
		
		//MessageDialog.openInformation(shell, "Stock selected", selectedStock.getFullName());
		openConfigEditor();
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return canExcecute;
	}
	
	@Inject
	public void analyseSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) 
	ISelection selection){
		
		selectedRate=null;
		selectedStock=null;
		
		if(selection instanceof IStructuredSelection){
			IStructuredSelection sel=(IStructuredSelection) selection;
			if(sel.size()==1 && sel.getFirstElement() instanceof ExchangeRate && !(sel.getFirstElement() instanceof RateContainer)){
				selectedRate=(ExchangeRate)sel.getFirstElement();
				if(selectedRate instanceof Stock){
					selectedStock=(Stock) selectedRate;
					canExcecute=true;
					return;
				}
			}
		}
		
		selectedRate=null;
		canExcecute=false;
	}
	
	
	
	
	
	
	/**
	 * Open a new Rate Editor,
	 * If the Editor already exist it will be bring to the top
	 * @param rate
	 */
	private void openConfigEditor(){
		
		MPart part=searchPart(NeuralNetworkConfigEditor.CONFIG_EDITOR_ID,selectedStock.getUUID());
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setConfigEditorPartContext(part);
			}
			
				partService.bringToTop(part);
				return;
		}
		
		//Create the part
		part=createRateEditorPart();
		
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightup", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
	
		
	}
	
	private MPart createRateEditorPart(){
		MPart part = partService.createPart(NeuralNetworkConfigEditor.CONFIG_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel("NN Config: "+selectedStock.getName());
		part.setIconURI(getIconURI());
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(selectedStock.getUUID());
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setConfigEditorPartContext(part);
		
		return part;
	}
	
	private String getIconURI(){
		
		return bundleResourceLoader.getImageURI(getClass(),IImageKeys.RATE_STOCK).toString();
		
	}
	
	
	private void setConfigEditorPartContext(MPart part){
		part.setContext(context.createChild());
		part.getContext().set(Stock.class, selectedStock);
		part.getContext().set(ExchangeRate.class, selectedRate);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
		
}