package com.munch.exchange.model.core.neuralnetwork;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.xml.XmlParameterElement;

public class NNetwork extends XmlParameterElement{
	
	
	
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
	
	
	public boolean removeCurrentConfiguration(){
		
		if(Configurations.size()<2)return false;
		
		for(int i=0;i<Configurations.size();i++){
			Configuration config=Configurations.get(i);
		//for(Configuration config:Configurations){
			if(config.getName().equals(currentConfiguration)){
				Configurations.remove(i);
				currentConfiguration=Configurations.getFirst().getName();
				fireConfigurationChanged();
				return true;
			}
		}
		
		
		return false;
		
	}
	
	
	public boolean setCurrentConfiguration(String currentConfiguration) {
		if(this.currentConfiguration==null || !this.currentConfiguration.equals(currentConfiguration)){
			this.currentConfiguration = currentConfiguration;
			for(Configuration config:Configurations){
				if(config.getName().equals(currentConfiguration)){
					fireConfigurationChanged();
					return true;
				}
			}
		}
		return false;
		//changes.firePropertyChange(FIELD_CurrentConfiguration, this.currentConfiguration, this.currentConfiguration = currentConfiguration);
	}
	
	public boolean addNewConfiguration(String configName, Stock stock){
		for(Configuration config:Configurations){
			if(config.getName().equals(configName))
				return false;
		}
		
		Configuration conf=new Configuration();
		conf.setName(configName);
		conf.setDirty(true);
		conf.setParent(stock);
		
		Configurations.add(conf);
		currentConfiguration=configName;
		
		fireConfigurationChanged();
		
		return true;
	}
	
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
		
		fireConfigurationChanged();
		
	}
	
	//****************************************
	//***            LISTENER             ****
	//****************************************
	
	public class NeuralNetworkEvent extends java.util.EventObject {
		
		private static final long serialVersionUID = 5192635579775921912L;

		// here's the constructor
		public NeuralNetworkEvent(Object source) {
			super(source);
		}
	}

	private List<NNetworkListener> _listeners = new ArrayList<NNetworkListener>();

	public synchronized void addEventListener(NNetworkListener listener) {
		_listeners.add(listener);
	}

	public synchronized void removeEventListener(NNetworkListener listener) {
		_listeners.remove(listener);
	}

	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void fireConfigurationChanged() {
		NeuralNetworkEvent event = new NeuralNetworkEvent(this);
		Iterator<NNetworkListener> i = _listeners.iterator();
		while (i.hasNext()) {
			((NNetworkListener) i.next())
					.currentConfigurationChanged(event);
		}
	}
	
	
}
