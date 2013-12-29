package com.munch.exchange.model.xml;




public abstract class XmlFile implements XmlElementIF {
	
	
	//private static Logger logger = Logger.getLogger(XmlFile.class);
	
	public synchronized boolean saveAsXml(String fileName){
		return Xml.save(this,fileName);
	}
	public synchronized boolean readFromXml(String fileName){
		return Xml.load(this, fileName);
	}

}
