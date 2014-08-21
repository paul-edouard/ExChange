package com.munch.exchange.model.core.neuralnetwork;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class NeuralNetwork extends XmlParameterElement{
	
	
	
	static final String FIELD_Configurations="Configurations";
	static final String FIELD_CurrentConfiguration="CurrentConfiguration";
	
	
	private LinkedList<Configuration> Configurations=new LinkedList<Configuration>();
	private String currentConfiguration;
	
	
	public Configuration getConfiguration(){
		for(Configuration config:Configurations){
			if(config.getName().equals(currentConfiguration))
				return config;
		}
		return null;
	}

	public String getCurrentConfiguration() {
		return currentConfiguration;
	}

	public void setCurrentConfiguration(String currentConfiguration) {
	changes.firePropertyChange(FIELD_CurrentConfiguration, this.currentConfiguration, this.currentConfiguration = currentConfiguration);}
	

	public LinkedList<Configuration> getConfigurations() {
		return Configurations;
	}

	public void setConfigurations(LinkedList<Configuration> configuations) {
		changes.firePropertyChange(FIELD_Configurations, this.Configurations,
				this.Configurations = configuations);
	}

	@Override
	protected void initAttribute(Element rootElement) {
		this.setCurrentConfiguration(rootElement.getAttribute(FIELD_CurrentConfiguration));
		
		Configurations.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		Configuration ent=new Configuration();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			Configurations.add(ent);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_CurrentConfiguration,this.getCurrentConfiguration());
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(Configuration ent:Configurations){
			rootElement.appendChild(ent.toDomElement(doc));
		}
		
	}

}
