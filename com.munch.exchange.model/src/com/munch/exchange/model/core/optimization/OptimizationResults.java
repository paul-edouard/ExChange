package com.munch.exchange.model.core.optimization;

import java.util.Collection;
import java.util.Collections;
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
	static private final String RELATIVE_STRENGTH_INDEX_STR="Relative Strength Index";
	static private final String NMAW_STR="NMAW";
	static private final String NONE_STR="None";
	static private final String NEURAL_NETWORK_OUTPUT_DAY_STR="Neural Network Ouput day";
	static private final String NEURAL_NETWORK_OUTPUT_HOUR_STR="Neural Network Ouput hour";
	static private final String NEURAL_NETWORK_OUTPUT_MINUTE_STR="Neural Network Ouput minute";
	static private final String NEURAL_NETWORK_OUTPUT_SECONDE_STR="Neural Network Ouput seconde";
	
	
	
	private LinkedList<ResultEntity> results=new LinkedList<ResultEntity>();
	private Type type=Type.NONE;
	private int maxResult=200;
	
	public OptimizationResults(){
		
	}
	
	public boolean addResult(ResultEntity result){
		results.addFirst(result);
		Collections.sort(results);
		//Collections.reverse(list);
		if(results.size()>maxResult)
			results.removeLast();
		
		return result==results.getFirst();
		
	}
	
	public ResultEntity getBestResult(){
		if(results.isEmpty())return null;
		return results.getFirst();
	}
	
	public double compareBestResultWith(ResultEntity reference){
		ResultEntity best=this.getBestResult();
		
		if(reference==null || best ==null)return Double.NaN;
		
		if(reference.getValue()==0)return Double.NaN;
		
		
		double percent=100.0*(reference.getValue()-best.getValue())/reference.getValue();
		
		return percent;
		
	}
	
	
	
	public void setMaxResult(int maxResult) {
	this.maxResult = maxResult;
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



	public enum Type { MOVING_AVERAGE, MACD,
						BILLINGER_BAND,PARABOLIC_SAR ,
						RELATIVE_STRENGTH_INDEX, NMAW,
						NEURAL_NETWORK_OUTPUT_DAY,
						NEURAL_NETWORK_OUTPUT_HOUR,
						NEURAL_NETWORK_OUTPUT_MINUTE,
						NEURAL_NETWORK_OUTPUT_SECONDE,
						NONE};
	
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
		case RELATIVE_STRENGTH_INDEX:
			return RELATIVE_STRENGTH_INDEX_STR;
		case NMAW:
			return NMAW_STR;
		case NEURAL_NETWORK_OUTPUT_DAY:
			return NEURAL_NETWORK_OUTPUT_DAY_STR;
		case NEURAL_NETWORK_OUTPUT_HOUR:
			return NEURAL_NETWORK_OUTPUT_HOUR_STR;
		case NEURAL_NETWORK_OUTPUT_MINUTE:
			return NEURAL_NETWORK_OUTPUT_MINUTE_STR;
		case NEURAL_NETWORK_OUTPUT_SECONDE:
			return NEURAL_NETWORK_OUTPUT_SECONDE_STR;
			

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
		else if(type.equals(RELATIVE_STRENGTH_INDEX_STR)){
			return Type.RELATIVE_STRENGTH_INDEX;
		}
		else if(type.equals(NMAW_STR)){
			return Type.NMAW;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_DAY_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_DAY;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_HOUR_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_HOUR;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_MINUTE_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_MINUTE;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_SECONDE_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_SECONDE;
		}
		
		
		return Type.NONE;
		
	}
	
	
	

	@Override
	public String toString() {
		String ret="OptimizationResults [ type=" + type
				+ ", maxResult=" + maxResult + ", results=\n";
		
		for(ResultEntity ent:this.results){
			ret+=String.valueOf(ent)+"\n";
		}
		
		ret+="]";
		return ret;
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
