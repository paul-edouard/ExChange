package com.munch.exchange.model.xml;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHashMap<K, V> extends HashMap<K, V> implements XmlElementIF {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4198250748843040587L;
	
	
	static final String FIELD_HashMapElement="ELement";
	static final String FIELD_HashMapElementKey="Key";
	static final String FIELD_HashMapElementValue="Value";
	
	
	private final Class<?> keyClass;
	private final Class<?> valueClass;
	
	private String tagName="";
	
	public XmlHashMap( Class<?> keyClass,Class<?> valueClass ) {
		
		this.keyClass=keyClass;
		this.valueClass=valueClass;
	}
	
	
	public XmlHashMap(String tagName, Class<?> keyClass,Class<?> valueClass ) {
		this.tagName = tagName;

		this.keyClass=keyClass;
		this.valueClass=valueClass;
	}
	
	public void tst() throws Exception {
		 System.out.println("Key Type: " + this.keyClass.getName());
		 System.out.println("Value Type: " + this.valueClass.getName());
	}
	
	

	public boolean update(XmlHashMap<K, V> other){
		boolean isUpdated=false;
		if(!this.getTagName().equals(other.getTagName()))
			return isUpdated;
		
		//System.out.println(this);
		//System.out.println(other);
		
		for(K key : other.keySet()){
			if(!this.containsKey(key) || !other.get(key).equals(this.get(key))){
				this.put(key, other.get(key));
				isUpdated=true;
			}
		}
		
		
		//System.out.println("IsUpdated: "+isUpdated);
		
		return isUpdated;
	}
	


	@Override
	public Element toDomElement(Document doc) {
		Element e=doc.createElement(this.getTagName());
		
		for(K k  : this.keySet()){
			V v = this.get(k);
			
			Element k_v_element=doc.createElement(FIELD_HashMapElement);
			k_v_element.setAttribute(FIELD_HashMapElementKey, String.valueOf(k));
			k_v_element.setAttribute(FIELD_HashMapElementValue, String.valueOf(v));
			
			e.appendChild(k_v_element);
		}
		
		return e;
		
	}

	
	@Override
	public void init(Element Root) {
		if(Root.getTagName().equals(this.getTagName())){
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Elements
					//DatePoint point=createPoint();
					if(childElement.getTagName().equals(FIELD_HashMapElement)){
						String key=childElement.getAttribute(FIELD_HashMapElementKey);
						String value=childElement.getAttribute(FIELD_HashMapElementValue);
						
						K k=getKeyIntanceFromString(key);
						V v=getValueIntanceFromString(value);
						
						this.put(k, v);
						
					}
					
				}
			}
			
			
		}

	}
	
	
	@SuppressWarnings("unchecked")
	private K getKeyIntanceFromString(String key){
		
		//Class<K> k;
	
		if(keyClass== String.class){
			 return (K) key;      
		 }else if( keyClass == Integer.class){
			 return (K) (Integer) Integer.parseInt(key);
		 }
		 else if( keyClass == Long.class){
			 return (K) (Long) Long.parseLong(key);
		 }
		 else if( keyClass == Float.class){
			 return (K) (Float) Float.parseFloat(key);
		 }
		 else if( keyClass == Double.class){
			 return (K) (Double) Double.parseDouble(key);
		 }
		 else if( keyClass == Boolean.class){
			 return (K) (Boolean) Boolean.parseBoolean(key);
		 }
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private V getValueIntanceFromString(String value){
		
		if(valueClass == String.class){
			 return (V) value;      
		 }else if(valueClass == Integer.class){
			 return (V) (Integer) Integer.parseInt(value);
		 }
		 else if(valueClass == Long.class){
			 return (V) (Long) Long.parseLong(value);
		 }
		 else if(valueClass == Float.class){
			 return (V) (Float) Float.parseFloat(value);
		 }
		 else if(valueClass == Double.class){
			 return (V) (Double) Double.parseDouble(value);
		 }
		 else if(valueClass == Boolean.class){
			 return (V) (Boolean) Boolean.parseBoolean(value);
		 }
		
		return null;
	}
	
	
	

	@Override
	public String getTagName() {
		if(!tagName.isEmpty())
			return tagName;
		return this.getClass().getSimpleName();
	}
	
	public static void main(String[] args) throws Exception {
		 (new XmlHashMap<String, Long>("Test",String.class,Long.class)).tst();
		 //(new StringTest()).tst();
	}

}
