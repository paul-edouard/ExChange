package com.munch.exchange.model.core.watchlist;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class Watchlist extends XmlParameterElement {
	
	static final String FIELD_Name="Name";
	static final String FIELD_List="List";
	static final String FIELD_IsSelected="is selected";
	
	private LinkedList<WatchlistEntity> list=new LinkedList<WatchlistEntity>();
	private String Name="";
	private boolean isSelected=false;
	

	public LinkedList<WatchlistEntity> getList() {
		return list;
	}

	public void setList(LinkedList<WatchlistEntity> list) {
	changes.firePropertyChange(FIELD_List, this.list, this.list = list);}
	

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		changes.firePropertyChange(FIELD_Name, this.Name, this.Name = name);
	}
	
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
	changes.firePropertyChange(FIELD_IsSelected, this.isSelected, this.isSelected = isSelected);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		list.clear();
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setSelected(Boolean.parseBoolean(rootElement.getAttribute(FIELD_IsSelected)));
	}

	@Override
	protected void initChild(Element childElement) {
		WatchlistEntity ent=new WatchlistEntity();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			list.add(ent);
		}
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name, this.getName());
		rootElement.setAttribute(FIELD_IsSelected, String.valueOf(this.isSelected()));
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(WatchlistEntity ent:list){
			rootElement.appendChild(ent.toDomElement(doc));
		}

	}

}
