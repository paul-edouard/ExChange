package com.munch.exchange.model.core;



public class ParameterElement {
	
	/**********************
	 *     PARAMETER      *
	 *********************/
	
	protected Parameter parameter;
	    
	public Parameter getParameter() {
			if(parameter==null)parameter=Parameter.createRoot(this.getClass());
			return parameter;
	}

	public void setParameter(Parameter parameter) {
			this.parameter = parameter;
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

}
