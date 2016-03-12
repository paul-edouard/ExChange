 
package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.tool.DateTool;

public class InfoPart {
	private StyledText styledText;
	@Inject
	public InfoPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		styledText = new StyledText(parent, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		styledText.setAlwaysShowScrollBars(false);
		styledText.setDoubleClickEnabled(false);
		
		//styledText.append("Paul");	
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
	//##  EVENT REACTIONS          ##
	//################################
	
	private boolean isAbleToReact(String text){
		
		if (styledText == null || text == null)
			return false;
		
		return true;
	}
	
	
	@Inject
	private void catchInfoText(@Optional @UIEventTopic(IEventConstant.TEXT_INFO) String text){
		
		
		if(!isAbleToReact(text))return;
		
		String[] tockens=text.split("\n");
		for(int i=0;i<tockens.length;i++){
			if(styledText.getLineCount()==0)
				styledText.append("- "+DateTool.getCurrentDateString()+" >> "+tockens[i]);
			else
				styledText.append("\n- "+DateTool.getCurrentDateString()+" >> "+tockens[i]);
			styledText.setTopIndex(styledText.getTopIndex()+1);
		}
	}
	
	
	
	public static void postInfoText(IEventBroker eventBroker, String text){
		eventBroker.post(IEventConstant.TEXT_INFO,text);
	}
	
	
	
}