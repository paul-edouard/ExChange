package com.munch.exchange.services.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.Project;
import com.munch.exchange.model.xml.XmlFile;
import com.munch.exchange.services.IProjectProvider;

public class ProjectProviderImpl extends XmlFile implements IProjectProvider {

	private Project project;
	

	@Override
	public boolean save(Project p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Project p) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public Element toDomElement(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Element Root) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return null;
	}

}
