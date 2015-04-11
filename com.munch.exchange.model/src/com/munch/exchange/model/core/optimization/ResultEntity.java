package com.munch.exchange.model.core.optimization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

import org.goataa.impl.utils.Constants;
import org.goataa.impl.utils.Individual;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class ResultEntity extends XmlParameterElement implements Comparable<ResultEntity>, Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -762487853243889950L;

	private LinkedList<Object> genome=new LinkedList<Object>();
	
	
	private final String STRING="string";
	private final String INTEGER="integer";
	private final String FLOAT="float";
	private final String DOUBLE="double";
	private final String BOOLEAN="boolean";
	
	static final String FIELD_Genome="genome";
	static final String FIELD_Value="Value";
	
	//Properties
	public static final String GENERATED_FROM="GENERATED_FROM";
	
	private double value=Constants.WORST_FITNESS;
	
	private String parentId;
	private String id=UUID.randomUUID().toString();
	
	public ResultEntity(){
		
	}
	
	public ResultEntity(double[] doubles){
		for(int i=0;i<doubles.length;i++){
			genome.add(doubles[i]);
		}
	}
	
	public ResultEntity(double[] doubles,double value){
		for(int i=0;i<doubles.length;i++){
			genome.add(doubles[i]);
		}
		this.value=value;
	}
	
	public ResultEntity(boolean[] booleans,double value){
		for(int i=0;i<booleans.length;i++){
			genome.add(booleans[i]);
		}
		this.value=value;
	}
	
	public ResultEntity(Double[] doubles,double value){
		for(int i=0;i<doubles.length;i++){
			genome.add(doubles[i]);
		}
		this.value=value;
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
	
	public boolean[] getBooleanArray(){
		LinkedList<Boolean> list=new LinkedList<Boolean>();
		for(Object obj:this.genome){
			if(obj instanceof Boolean){
				list.add((boolean)obj);
			}
		}
		
		boolean[] array=new boolean[list.size()];
		for(int i=0;i<list.size();i++){
			array[i]=list.get(i);
		}
		
		return array;
		
	}
	
	
	
	@Override
	public String toString() {
		return "ResultEntity [value x 100=" + value*100 + ", genome=" + Arrays.toString(genome.toArray()) + "]";
	}

	public Individual<double[], double[]> toDoubleIndividual(){
		Individual<double[], double[]> ind=new Individual<double[], double[]>();
		
		LinkedList<Double> allDoubles=new LinkedList<Double>();
		
		for(Object obj : this.genome){
			if(obj instanceof Double){
				allDoubles.add((double) obj);
			}
		}
		
		ind.x=new double[allDoubles.size()];
		ind.g=new double[allDoubles.size()];
		
		for(int i=0;i<allDoubles.size();i++){
			ind.x[i]=ind.g[i]=allDoubles.get(i);
		}
		
		ind.v=this.value;
		
		return ind;
		
	}
	
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
	this.parentId = parentId;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
	changes.firePropertyChange(FIELD_Value, this.value, this.value = value);}
	
	
	public boolean isGenomeEqualsTo(ResultEntity compare_ent){
		if(this.genome.size()!=compare_ent.getGenome().size())
			return false;
		
		double[] master=this.getDoubleArray();
		double[] compare=compare_ent.getDoubleArray();
		
		for(int i=0;i<master.length;i++){
			if(master[i]!=compare[i])return false;
		}
		
		return true;
		
	}
	

	public String getId() {
		return id;
	}
	
	

	public void setId(String id) {
	this.id = id;
	}
	

	public LinkedList<Object> getGenome() {
		return genome;
	}

	public void setGenome(LinkedList<Object> genome) {
	changes.firePropertyChange(FIELD_Genome, this.genome, this.genome = genome);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		
		if(rootElement.hasAttribute(FIELD_Value))
			this.setValue(Double.parseDouble(rootElement.getAttribute(FIELD_Value)));
		
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
		else if(childElement.getTagName().equals(BOOLEAN)){
			genome.add(Boolean.valueOf(childElement.getTextContent()));
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Value,String.valueOf(this.getValue()));
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
			else if(obj instanceof Boolean){
				element=doc.createElement(BOOLEAN);
				element.setTextContent(String.valueOf((Boolean) obj));
			}
			if(element!=null)
				rootElement.appendChild(element);
		}

	}

	@Override
	public int compareTo(ResultEntity b) {
		 if (b.getValue() == Constants.WORST_FITNESS && this.getValue() == Constants.WORST_FITNESS) {
		      return 0;
		    }
		    if (this.getValue() == Constants.WORST_FITNESS) {
		      return -1;
		    }
		    if (b.getValue() ==  Constants.WORST_FITNESS) {
		      return 1;
		    }
		    
		    if(this.getValue() < b.getValue()){
		    	return -1;
		    }
		    else if(this.getValue() == b.getValue()){
		    	return 0;
		    }
		    else{
		    	return 1;
		    }
		    
	}

}
