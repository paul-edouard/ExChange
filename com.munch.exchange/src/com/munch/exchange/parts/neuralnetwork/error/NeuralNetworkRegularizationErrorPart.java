 
package com.munch.exchange.parts.neuralnetwork.error;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import com.munch.exchange.job.neuralnetwork.NeuralNetworkRegulizer;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.MyMDirtyable;

public class NeuralNetworkRegularizationErrorPart {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkRegularizationErrorPart.class);
	
	public static final String NEURALNETWORK_REGULARIZATION_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.neuralnetwork.regularizarion.error.editor";
	
	
	
	@Inject
	public NeuralNetworkRegularizationErrorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		//TODO Your code here
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
	
	
	//################################
	//##          STATIC            ##
	//################################
	
	public static MPart openPart(
			Stock stock,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			NeuralNetworkRegulizer regulizer,
			IEclipseContext context){
		
		MPart part=searchPart(NeuralNetworkRegularizationErrorPart.NEURALNETWORK_REGULARIZATION_ERROR_EDITOR_ID,stock.getUUID(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setPartContext(part,stock,regulizer,context);
			}
			
			partService.bringToTop(part);
			return  part;
		}
		
		
		//Create the part
		part=createPart(stock,regulizer,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	private static MPart createPart(
			Stock stock,
			NeuralNetworkRegulizer regulizer,
			EPartService partService,
			IEclipseContext context){
		MPart part = partService.createPart(NeuralNetworkRegularizationErrorPart.NEURALNETWORK_REGULARIZATION_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel("Regularization: " +stock.getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(stock.getUUID());
		//part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setPartContext(part,stock,regulizer,context);
		
		return part;
	}
	
	private static void setPartContext(
			MPart part,
			Stock stock,
			NeuralNetworkRegulizer regulizer,
			IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(Stock.class, stock);
		part.getContext().set(NeuralNetworkRegulizer.class, regulizer);
		part.getContext().set(MDirtyable.class, new MyMDirtyable(part));
	}
	
	private static MPart searchPart(String partId,String tag,EModelService modelService,MApplication application){
		
		List<MPart> parts=getPartList(partId,tag,modelService, application);
		if(parts.isEmpty())return null;
		return parts.get(0);
	}
	
	private static List<MPart> getPartList(String partId,String tag,EModelService modelService,MApplication application){
		List<String> tags=new LinkedList<String>();
		tags.add(tag);
		//tags.add(optimizationType);
			
		List<MPart> parts=modelService.findElements(application,
				partId, MPart.class,tags );
		return parts;
	}
	
}