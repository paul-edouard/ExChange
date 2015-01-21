package com.munch.exchange.parts;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class MyMDirtyable implements MDirtyable {

	private MPart part;
	
	public MyMDirtyable(MPart part){
		this.part=part;
		this.part.setDirty(false);
	}
	
	
	@Override
	public void setDirty(boolean value) {
		part.setDirty(value);
		/*
		if(value && !part.getLabel().startsWith("*")){
			part.setLabel("*"+part.getLabel());
		}
		else if(part.getLabel().startsWith("*")){
			part.setLabel(part.getLabel().substring(1,part.getLabel().length() ));
		}
		*/
	}
	
	@Override
	public boolean isDirty() {
		return part.isDirty();
	}
	

}
