package com.munch.exchange.model.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class ParameterElement {
	
	public static final String FIELD_Parameter = "parameter";
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	/**********************
	 *     PARAMETER      *
	 *********************/
	
	protected Parameter parameter;
	    
	public Parameter getParameter() {
			if(parameter==null)parameter=Parameter.createRoot(this.getClass());
			return parameter;
	}
	
	
	public void setParameter(Parameter parameter) {
		changes.firePropertyChange(FIELD_Parameter, this.parameter,
				this.parameter = parameter);
	}
	
	
	protected String getStringParam(String key){
		Object o=getParam(key,Parameter.Type.STRING);
		if(o!=null && o instanceof String)return (String)o;
		return "";
	}
	
	protected Integer getIntegerParam(String key){
		Object o=getParam(key,Parameter.Type.INTEGER);
		if(o!=null && o instanceof Integer)return (Integer)o;
		return Integer.MIN_VALUE;
	}
	
	protected Float getFloatParam(String key){
		Object o=getParam(key,Parameter.Type.INTEGER);
		if(o!=null && o instanceof Float)return (Float)o;
		return Float.MIN_VALUE;
	}
	
	protected Object getParam(String key, Parameter.Type type){
		Parameter par_type=this.getParameter().getChild(key);
		if(par_type!=null && par_type.getType()==type){
			return par_type.getValue();
		}
		return null;
	}
	
	protected void setParam(String key, Object value){
		
		Parameter par_type=this.getParameter().getChild(key);
		if(par_type==null){
			par_type=new Parameter(key,value);
			this.getParameter().addChild(par_type);
		}
		else
			par_type.setValue(value);
		
	}
	
	
	

	@Override
	public String toString() {
		return "ParameterElement [parameter=" + parameter + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterElement other = (ParameterElement) obj;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		return true;
	}


	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
