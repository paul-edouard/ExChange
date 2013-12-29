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

public class Xml {
	
	/**
	 * Save a xml element in a local file
	 * 
	 * @param fileName
	 * @param element
	 * @return
	 */
	public static boolean save( XmlElementIF element,String fileName) {
		File f = new File(fileName);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			doc.appendChild(element.toDomElement(doc));

			Transformer t = TransformerFactory.newInstance().newTransformer();

			t.setOutputProperty(OutputKeys.INDENT, "yes");

			FileOutputStream f_out = new FileOutputStream(f);

			t.transform(new DOMSource(doc), new StreamResult(f_out));

			f_out.close();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static boolean load( XmlElementIF element,String fileName){
		File f=new File(fileName);

		if(!f.exists() ){
			return false;	
		}
		
		if(!f.canRead())return false;
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document doc=builder.parse(f);
			element.init(doc.getDocumentElement());
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
			
		}
	}
	
	

}
