package com.munch.exchange.model.core.optimization;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;


public class OptimizationResults extends XmlParameterElement{
	
	static final String FIELD_Type="Type";
	static final String FIELD_Results="Results";
	
	static private final String MOVING_AVERAGE_STR="Moving Average";
	static private final String MACD_STR="MACD";
	static private final String BILLINGER_BAND_STR="Bollinger Band";
	static private final String PARABOLIC_SAR_STR="Parabolic SAR";
	static private final String NONE_STR="None";
	
	private LinkedList<ResultEntity> results=new LinkedList<ResultEntity>();
	private Type type=Type.NONE;
	private int maxResult=200;
	
	public OptimizationResults(){
		
	}
	
	public void addResult(ResultEntity result){
		results.addFirst(result);
		if(results.size()>maxResult)
			results.removeLast();
	}
	
	
	
	public int getMaxResult() {
		return maxResult;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
	changes.firePropertyChange(FIELD_Type, this.type, this.type = type);}
	

	public LinkedList<ResultEntity> getResults() {
		return results;
	}



	public enum Type { MOVING_AVERAGE, MACD, BILLINGER_BAND,PARABOLIC_SAR ,NONE};
	
	public static String OptimizationTypeToString(Type type){
		switch (type) {
		case MOVING_AVERAGE:
			return MOVING_AVERAGE_STR;
		case MACD:
			return MACD_STR;
		case BILLINGER_BAND:
			return BILLINGER_BAND_STR;
		case PARABOLIC_SAR:
			return PARABOLIC_SAR_STR;

		default:
			return NONE_STR;
		}
	}
	
	public static Type stringToOptimizationType(String type){
		if(type.equals(MOVING_AVERAGE_STR))
			return Type.MOVING_AVERAGE;
		else if(type.equals(MACD_STR)){
			return Type.MACD;
		}
		else if(type.equals(BILLINGER_BAND_STR)){
			return Type.BILLINGER_BAND;
		}
		else if(type.equals(PARABOLIC_SAR_STR)){
			return Type.PARABOLIC_SAR;
		}
		
		return Type.NONE;
		
	}

	@Override
	protected void initAttribute(Element rootElement) {
		results.clear();
		this.setType(stringToOptimizationType(rootElement.getAttribute(FIELD_Type)));
		
	}

	@Override
	protected void initChild(Element childElement) {
		
		ResultEntity ent=new ResultEntity();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			results.add(ent);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Type,OptimizationTypeToString(this.getType()));
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(ResultEntity ent:results){
			rootElement.appendChild(ent.toDomElement(doc));
		}
	}

}
