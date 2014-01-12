package com.munch.exchange.model.core.keystat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class ContentAndTerm extends XmlParameterElement {
	
	
	static final String FIELD_Content="content";
	static final String FIELD_Term="term";
	
	private String Content="";
	private String Term="";
	
	private String tagName="";
	
	public ContentAndTerm(){
		super();
	}
	
	public ContentAndTerm(String tagName){
		super();
		this.tagName=tagName;
	}
	public ContentAndTerm(String tagName,Element childElement){
		super();
		this.tagName=tagName;
		this.init(childElement);
	}
	
	public ContentAndTerm(Element childElement){
		super();
		this.init(childElement);
	}
	
	@Override
	public String getTagName() {
		if(!tagName.isEmpty())return this.tagName;
		return super.getTagName();
	}
	
	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		changes.firePropertyChange(FIELD_Content, this.Content,
				this.Content = content);
	}

	public String getTerm() {
		return Term;
	}

	public void setTerm(String term) {
		changes.firePropertyChange(FIELD_Term, this.Term, this.Term = term);
	}
	

	@Override
	public String toString() {
		return "[Content=" + Content + ", Term=" + Term+ "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {
		this.setContent((rootElement.getAttribute(FIELD_Content)));
		this.setTerm((rootElement.getAttribute(FIELD_Term)));
	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Content, this.getContent());
		rootElement.setAttribute(FIELD_Term, this.getTerm());
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {}

}
