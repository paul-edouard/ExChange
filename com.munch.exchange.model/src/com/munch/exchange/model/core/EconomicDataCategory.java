package com.munch.exchange.model.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EconomicDataCategory extends XmlParameterElement{
	
	static final String FIELD_Id="id";
	static final String FIELD_ParentId="parentId";
	static final String FIELD_Name="name";
	static final String FIELD_Parent="parent";
	
	private String id;
	private String parentId;
	private String name;
	private EconomicDataCategory parent;
	
	
	public EconomicDataCategory(){
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
	changes.firePropertyChange(FIELD_Id, this.id, this.id = id);}
	

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
	changes.firePropertyChange(FIELD_ParentId, this.parentId, this.parentId = parentId);}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);}
	

	public EconomicDataCategory getParent() {
		return parent;
	}

	public void setParent(EconomicDataCategory parent) {
	changes.firePropertyChange(FIELD_Parent, this.parent, this.parent = parent);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setId(rootElement.getAttribute(FIELD_Id));
		this.setParentId(rootElement.getAttribute(FIELD_ParentId));
		this.setName(rootElement.getAttribute(FIELD_Name));
		
	}

	@Override
	protected void initChild(Element childElement) {
		if(childElement.getTagName().equals(this.getTagName())){
			EconomicDataCategory cat=new EconomicDataCategory();
			cat.init(childElement);
			this.setParent(cat);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Id, this.getId());
		rootElement.setAttribute(FIELD_ParentId, this.getParentId());
		rootElement.setAttribute(FIELD_Name, this.getName());
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		//Parameter
		if(this.getParent()==null)return;
		rootElement.appendChild(this.getParent().toDomElement(doc));
		
	}

	@Override
	public String toString() {
		return "EconomicDataCategory [id=" + id + ", parentId=" + parentId
				+ ", name=" + name + ", parent=" + parent + "]";
	}
	
	
	
	
}
