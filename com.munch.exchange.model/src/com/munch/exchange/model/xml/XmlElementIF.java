package com.munch.exchange.model.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlElementIF {
	
	public Element toDomElement(Document doc);
	public void init(Element Root);
	
	public String getTagName();
}
