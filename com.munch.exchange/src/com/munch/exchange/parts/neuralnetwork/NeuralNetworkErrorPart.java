package com.munch.exchange.parts.neuralnetwork;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.parts.OptimizationErrorPart;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;

public class NeuralNetworkErrorPart {
	
private static Logger logger = Logger.getLogger(NeuralNetworkErrorPart.class);
	
	public static final String NEURALNETWORK_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.neuralnetworkerroreditor";
	
	@Inject
	private Stock stock;

	public NeuralNetworkErrorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		
		Button btnTest = new Button(parent, SWT.NONE);
		btnTest.setText("Test");
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
	
	
	//################################
	//##          STATIC            ##
	//################################
	
	public static MPart openNeuralNetworkErrorPart(
			Stock stock,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			IEclipseContext context){
		
		MPart part=searchPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID,stock.getUUID(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setPartContext(part,stock,context);
			}
			
				partService.bringToTop(part);
				return  part;
		}
		
		
		//Create the part
		part=createPart(stock,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	private static MPart createPart(Stock stock,EPartService partService,IEclipseContext context){
		MPart part = partService.createPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(stock.getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(stock.getUUID());
		//part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setPartContext(part,stock,context);
		
		//OptimizationErrorPart p=(OptimizationErrorPart) part;
		//p.setType(Optimizer.stringToOptimizationType(optimizationType));
		
		return part;
	}
	
	private static void setPartContext(MPart part,Stock stock,IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(Stock.class, stock);
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
