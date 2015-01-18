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
	
	private LinkedList<ConfigLinkInfo> configInfoList=new LinkedList<ConfigLinkInfo>();
	
	//private LinkedList<Configuration> Configurations=new LinkedList<Configuration>();
	private String currentConfigName;
	private Configuration configuration;
	
	
	
	
	
	public void setConfiguration(Configuration configuration) {
	this.configuration = configuration;
	}
	

	public Configuration getConfiguration(){
		return configuration;
	}

	public String getCurrentConfigName() {
		return currentConfigName;
	}
	
	public String getCurrentConfigId() {
		
		for(ConfigLinkInfo configInfo:configInfoList){
			if(configInfo.name.equals(currentConfigName))
				return configInfo.id;
		}
		
		return "";
	}
	
	
	public boolean removeCurrentConfiguration(){
		
		if(configInfoList.size()<2)return false;
		
		for(int i=0;i<configInfoList.size();i++){
			ConfigLinkInfo configInfo=configInfoList.get(i);
		//for(Configuration config:Configurations){
			if(configInfo.name.equals(currentConfigName)){
				configInfoList.remove(i);
				currentConfigName=configInfoList.getFirst().name;
				configuration=null;
				fireConfigurationChanged();
				return true;
			}
		}
		
		
		return false;
		
	}
	
	
	public boolean setCurrentConfiguration(String currentConfiguration) {
		if(this.currentConfigName==null || !this.currentConfigName.equals(currentConfiguration)){
			for(ConfigLinkInfo configInfo:configInfoList){
				if(configInfo.name.equals(currentConfiguration)){
					this.currentConfigName = currentConfiguration;
					configuration=null;
					fireConfigurationChanged();
					return true;
				}
			}
		}
		return false;
		//changes.firePropertyChange(FIELD_CurrentConfiguration, this.currentConfiguration, this.currentConfiguration = currentConfiguration);
	}
	
	public boolean addNewConfiguration(Configuration new_config, Stock stock){
		for(ConfigLinkInfo configInfo:configInfoList){
			if(configInfo.name.equals(new_config.getName()))
				return false;
		}
		
		if(new_config==null)return false;
		
		new_config.setParent(stock);
		
		ConfigLinkInfo configInfo=new ConfigLinkInfo();
		configInfo.id=new_config.getId();
		configInfo.name=new_config.getName();
		
		configInfoList.add(configInfo);
		
		
		currentConfigName=configInfo.name;
		configuration=new_config;
		
		fireConfigurationChanged();
		
		return true;
	}
	
	public LinkedList<ConfigLinkInfo> getConfigInfoList() {
		return configInfoList;
	}

	
	
	//****************************************
	//***      CONFIGURATION LINK INFO    ****
	//****************************************
	


	public class ConfigLinkInfo extends XmlParameterElement{
		
		
		static final String FIELD_Id="Id";
		static final String FIELD_Name="Name";
		
		public String id;
		public String name;
		@Override
		protected void initAttribute(Element rootElement) {
			this.id=(rootElement.getAttribute(FIELD_Id));
			this.name=(rootElement.getAttribute(FIELD_Name));
			
		}
		@Override
		protected void initChild(Element childElement) {}
		@Override
		protected void setAttribute(Element rootElement) {
			rootElement.setAttribute(FIELD_Id,this.id);
			rootElement.setAttribute(FIELD_Name,this.name);
		}
		@Override
		protected void appendChild(Element rootElement, Document doc) {}
		
	}
	
	
	
	//****************************************
	//***               XML               ****
	//****************************************
	
	@Override
	protected void initAttribute(Element rootElement) {
		this.currentConfigName=rootElement.getAttribute(FIELD_CurrentConfiguration);
		
		configInfoList.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		ConfigLinkInfo ent=new ConfigLinkInfo();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			configInfoList.add(ent);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_CurrentConfiguration,this.getCurrentConfigName());
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(ConfigLinkInfo ent:configInfoList){
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
