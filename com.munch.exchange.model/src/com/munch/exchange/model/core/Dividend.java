package com.munch.exchange.model.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.Parameter;
import com.munch.exchange.model.xml.ParameterElement;
import com.munch.exchange.model.xml.XmlElementIF;

public class Dividend extends ParameterElement implements XmlElementIF {
	
	static final String ValueStr="value";
	static final String DateStr="date";
	
	private float value=0;
	private Calendar date=Calendar.getInstance();
	
	public Dividend(String dateValue){
		String[] v=dateValue.split(",");
		if(v.length<2){
			date=null;
			return;
		}
		
		String dateStr=v[0];
    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
    	Date d=null;
		try {
			d = format.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(d!=null){
			date.setTime(d);
			date.set(Calendar.HOUR_OF_DAY, 23);
			date.set(Calendar.MINUTE, 59);
			date.set(Calendar.SECOND, 59);
		}
		
		value=Float.valueOf(v[1]);
		
		
	}
	
	public Dividend(){
		super();
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getDateString(){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return format.format(date.getTime());
	}
	
	public void setDateString(String dateStr){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			Date d=format.parse(dateStr);
			if(d!=null){
				date.setTime(d);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	@Override
	public String toString() {
		return "Dividend [date=" + getDateString() + ", value=" + value + "]";
	}

	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	@Override
	public Element toDomElement(Document doc) {
		Element e=doc.createElement(this.getTagName());
		
		e.setAttribute(ValueStr,String.valueOf(this.getValue()));
		e.setAttribute(DateStr, this.getDateString());
		
		//Parameter
		e.appendChild(this.getParameter().toDomElement(doc));
	
		return e;
	}

	@Override
	public void init(Element Root) {
		if(Root.getTagName().equals(this.getTagName())){
			
			this.setValue(Float.valueOf(Root.getAttribute(ValueStr)));
			setDateString(Root.getAttribute(DateStr));
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Parameter
					if(childElement.getTagName().equals(new Parameter().getTagName())){
						this.setParameter(new Parameter(childElement));
					}
					
				}
			}
			
			
		}

	}

	@Override
	public String getTagName() {
		return "dividend";
	}

}
