package com.munch.exchange.model.core.optimization;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class ResultEntity extends XmlParameterElement {
	
	
	private LinkedList<Object> genome=new LinkedList<Object>();
	
	private final String STRING="string";
	private final String INTEGER="integer";
	private final String FLOAT="float";
	private final String DOUBLE="double";
	
	static final String FIELD_Genome="genome";
	
	public ResultEntity(){
		
	}
	
	public ResultEntity(double[] doubles){
		for(int i=0;i<doubles.length;i++){
			genome.add(doubles[i]);
		}
	}
	
	public double[] getDoubleArray(){
		LinkedList<Double> list=new LinkedList<Double>();
		for(Object obj:this.genome){
			if(obj instanceof Double){
				list.add((double)obj);
			}
		}
		
		double[] array=new double[list.size()];
		for(int i=0;i<list.size();i++){
			array[i]=list.get(i);
		}
		
		return array;
		
	}
	
	
	public LinkedList<Object> getGenome() {
		return genome;
	}

	public void setGenome(LinkedList<Object> genome) {
	changes.firePropertyChange(FIELD_Genome, this.genome, this.genome = genome);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		genome.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(STRING)){
			genome.add(String.valueOf(childElement.getTextContent()));
		}
		else if(childElement.getTagName().equals(INTEGER)){
			genome.add(Integer.valueOf(childElement.getTextContent()));
		}
		else if(childElement.getTagName().equals(FLOAT)){
			genome.add(Float.valueOf(childElement.getTextContent()));
		}
		else if(childElement.getTagName().equals(DOUBLE)){
			genome.add(Double.valueOf(childElement.getTextContent()));
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(Object obj : genome){
			Element element=null;
			if(obj instanceof String){
				element=doc.createElement(STRING);
				element.setTextContent((String) obj);
			}
			else if(obj instanceof Integer){
				element=doc.createElement(INTEGER);
				element.setTextContent(String.valueOf((Integer) obj));
			}
			else if(obj instanceof Float){
				element=doc.createElement(FLOAT);
				element.setTextContent(String.valueOf((Float) obj));
			}
			else if(obj instanceof Double){
				element=doc.createElement(DOUBLE);
				element.setTextContent(String.valueOf((Double) obj));
			}
			if(element!=null)
				rootElement.appendChild(element);
		}

	}

}
