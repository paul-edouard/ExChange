package com.munch.exchange.model.core.neuralnetwork;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class RegularizationParameters extends XmlParameterElement {
	
	
	static final String FIELD_Name="Name";
	
	//FaMe Neurons
	public static final String VARIANZ="Varianz";
	
	private String name;
	
	public RegularizationParameters(String name){
		this.name=name;
	}
	
	
	public RegularizationParameters createCopy(){
		
		RegularizationParameters copy=new RegularizationParameters(this.name);
		copy.setParameter(this.getParameter().createCopy());
		
		return copy;
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);}
	
	
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}
	
	
	//****************************************
	//***           STATIC                ****
	//****************************************
	public static  void setDefaultParameters(RegularizationParameters param){
		
		param.setParam(RegularizationParameters.VARIANZ, 0.1d);
		
	}
	
	
}
