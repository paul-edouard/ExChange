package com.munch.exchange.model.xml;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Parameter implements XmlElementIF, Serializable {
	
	
	static final long serialVersionUID=1L;
	
	static final String KeyStr="key";
	static final String ValueStr="value";
	static final String TypeStr="type";
	
	public static final String ROOT_KEY="ROOT_PARAM";
	
	private String key;
	private Object value;
	private Type type;
	private Collection<Parameter> childs;
	
	public enum Type implements Serializable {
		INTEGER(1), STRING(2), FLOAT(3), NONE(0), PARAMETER(4);
		private int val;

		private Type(int value) {
			this.val = value;
		}

		private void fromString(String value) {
			this.val = Integer.parseInt(value);
		}

		public int getValue() {
			return val;
		}

		public String toString() {
			return String.valueOf(val);
		}

	};
	
	
	
	public Parameter(Element el){
		key="";
		value=null;
		type=Type.NONE;
		childs=new LinkedList<Parameter>();
		this.init(el);
	}
	
	public Parameter(){
		key="";
		value=null;
		type=Type.NONE;
		childs=new LinkedList<Parameter>();
	}
	
	public Parameter(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
		this.type = Type.NONE;this.getType();
		this.childs=new LinkedList<Parameter>();
	}
	
	public Parameter(String key, String value) {
		super();
		this.key = key;
		this.value = value;
		this.type = Type.STRING;
		this.childs=new LinkedList<Parameter>();
	}
	
	public Parameter(String key, int value) {
		super();
		this.key = key;
		this.value = value;
		this.type = Type.INTEGER;
		this.childs=new LinkedList<Parameter>();
	}
	
	public Parameter(String key, float value) {
		super();
		this.key = key;
		this.value = value;
		this.type = Type.FLOAT;
		this.childs=new LinkedList<Parameter>();
	}
	
	
	private Parameter(String key, Object value, Type type) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
		this.childs=new LinkedList<Parameter>();
	}

	public static Parameter createRoot(Class<?> clazz){
		return new Parameter(ROOT_KEY,clazz.getName());
	}
	
	public Parameter createCopy(){
		Parameter param=new Parameter(this.getKey(),this.getValue(),this.getType());
		
		for(Parameter child:this.getChilds()){
			param.addChild(child.createCopy());
		}
		
		return param;
	}
	
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Type getType() {
		//Get Type from value
		if(type==Type.NONE){
			if(value instanceof String)
				type=Type.STRING;
			else if(value instanceof Integer)
				type=Type.INTEGER;
			else if(value instanceof Float)
				type=Type.FLOAT;
		}
		
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Collection<Parameter> getChilds() {
		return childs;
	}

	public void setChilds(Collection<Parameter> childs) {
		this.childs = childs;
	}
	
	public void addChilds(Collection<Parameter> childs) {
		this.childs.addAll(childs);
	}
	
	public void addChild(Parameter child) {
		this.childs.add(child);
	}
	
	public void removeChild(Parameter child) {
		this.childs.remove(child);
	}
	
	public Parameter getChild(String key){
		for(Parameter p:this.childs){
			if(p.getKey().equals(key))
				return p;
		}
		
		return null;
	}
	
	
	private void StringToValue(String val){
		switch (type) {
		case INTEGER:
			value=Integer.parseInt(val);
			break;
		case STRING:
			value=val;
			break;
		case FLOAT:
			value=Float.parseFloat(val);
			break;	

		default:
			value=val;
			break;
		}
	}
	
	private String ValueToString(){
		switch (type) {
		case INTEGER:
			return String.valueOf((Integer) value);
			
		case STRING:
			return String.valueOf((String) value);
			
		case FLOAT:
			return String.valueOf((Float) value);

		default:
			return String.valueOf( value);
			
		}
	}
	
	
	@Override
	public String toString() {
		return "Parameter [key=" + key + ", value=" + value + ", type=" + type
				+ ", childs=" + childs + "]";
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Parameter)) {
			return false;
		}
		Parameter other = (Parameter) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public String getTagName(){
		return this.getClass().getSimpleName();
	}
	
	
	/***********************************
	 *                                 *
	 *	  INITIALIZE AS ELEMENT        *
	 *                                 *
	 ***********************************/
	public void init(Element el){
		
		if(el.getTagName().equals(this.getTagName())){
			
			key=el.getAttribute(KeyStr);
			type.fromString(el.getAttribute(TypeStr));
			StringToValue(el.getAttribute(ValueStr));
			
			//if(!moduleId.equals("None"))System.out.println(moduleId+" Node:"+this);
			
			NodeList Children=el.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element chilElement=(Element)child;
					
					if(chilElement.getTagName().equals(new Parameter().getTagName())){
						Parameter e=new Parameter(chilElement);
						childs.add(e);
					}
					
				}
			}
			
			
		}else{
			System.out.println("this is not a valid pmc node element");
			
		}
	}
	
	
	/***********************************
	 *                                 *
	 *	    RETURN AS ELEMENT          *
	 *                                 *
	 ***********************************/	
	
	
	public Element toDomElement(Document doc){
		Document Doc=doc;
		Element e=Doc.createElement(this.getTagName());
		
		e.setAttribute(KeyStr, this.key);
		e.setAttribute(TypeStr, this.type.toString());
		e.setAttribute(ValueStr, ValueToString());
		
		for(Parameter p:this.childs){
			
			Element child=p.toDomElement(Doc);
			e.appendChild(child);
			
		}

		return e;
	}
	
	
	
	
}
