package com.munch.exchange.model.core.watchlist;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class Watchlists extends XmlParameterElement {
	
	
	static final String FIELD_Lists="Lists";
	
	private LinkedList<Watchlist> lists=new LinkedList<Watchlist>();
	

	public LinkedList<Watchlist> getLists() {
		return lists;
	}

	public void setLists(LinkedList<Watchlist> lists) {
	changes.firePropertyChange(FIELD_Lists, this.lists, this.lists = lists);}
	
	/*
	 * add a new list
	 * return the fresh added list on success null on error
	 */
	public Watchlist addNewList(String listName){
		for(Watchlist list:lists){
			if(list.getName().equals(listName))return null;
		}
		
		for(Watchlist list:lists){
			list.setSelected(false);
		}
		
		
		Watchlist list=new Watchlist();
		list.setName(listName);
		list.setSelected(true);
		lists.add(list);
		
		return list;
	}
	
	
	@Override
	protected void initAttribute(Element rootElement) {
		lists.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		Watchlist ent=new Watchlist();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			lists.add(ent);
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(Watchlist ent:lists){
			rootElement.appendChild(ent.toDomElement(doc));
		}

	}

}
