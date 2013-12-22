package com.munch.exchange.model.xml;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;



public abstract class XmlFile implements XmlElementIF {
	
	
	//private static Logger logger = Logger.getLogger(XmlFile.class);
	
	public synchronized boolean saveAsXml(String fileName){
		File f=new File(fileName);
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try{
			
		DocumentBuilder builder=factory.newDocumentBuilder();
		Document doc=builder.newDocument();
		doc.appendChild(toDomElement(doc));
		
		Transformer t=TransformerFactory.newInstance().newTransformer();
	
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		
		FileOutputStream f_out=new FileOutputStream(f);
		
		t.transform(new DOMSource(doc),  new StreamResult(f_out));
		
		f_out.close();
		
		return true;
		
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	public synchronized boolean readFromXml(String fileName){
		
		
		File f=new File(fileName);
		//logger.info("Debug -00");

		if(!f.exists() ){
			return false;
			/*
			logger.info("Try to create the new file: "+fileName);
			try{
				return saveAsXml(fileName);
				
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
				
			}
			*/
			
		}
		
		if(!f.canRead())return false;
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document doc=builder.parse(f);
			init(doc.getDocumentElement());
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
			
		}
	}

}
