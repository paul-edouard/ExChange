package com.munch.exchange.model.core.neuralnetwork;

import org.apache.log4j.Logger;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.optimization.AlgorithmParameters;
import com.munch.exchange.model.xml.XmlParameterElement;

public class LearnParameters extends XmlParameterElement {
	
	static final String FIELD_Type="Type";
	static final String FIELD_Name="Name";
	
	//MomentumBackpropagation
	public static final String MOMENTUM_BACK_PROPAGATION="Momentum Back Propagation";
	public static final String MBP_Momentum="MBP Momentum";
	
	
	//ResilientPropagation
	
	
	
	//IterativeLearning
	public static final String IL_LearningRate="IL Learning Rate";
	
	//Iterations
	public static final String Max_Iterations="Max Iterations";
	
	//Batch Modus
	public static final String BatchMode="BatchMode";
	
	
	
	private String type="";
	private String name;
	
	
	private static Logger logger = Logger.getLogger(LearnParameters.class);
	
	public LearnParameters(String name){
		this.name=name;
	}
	
	
	public LearningRule createLearningRule(){
		if(type.equals(MOMENTUM_BACK_PROPAGATION)){
			MomentumBackpropagation bp=new MomentumBackpropagation();
			bp.setMomentum(this.getDoubleParam(MBP_Momentum));
			bp.setLearningRate(this.getDoubleParam(IL_LearningRate));
			
			bp.setMaxIterations(this.getIntegerParam(Max_Iterations));
			bp.setBatchMode(this.getBooleanParam(BatchMode));
		}
		
		return null;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		changes.firePropertyChange(FIELD_Type, this.type, this.type = type);
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);}
	
	
	

	@Override
	public Integer getIntegerParam(String key) {
		// TODO Auto-generated method stub
		return super.getIntegerParam(key);
	}


	@Override
	public Float getFloatParam(String key) {
		// TODO Auto-generated method stub
		return super.getFloatParam(key);
	}


	@Override
	public Double getDoubleParam(String key) {
		// TODO Auto-generated method stub
		return super.getDoubleParam(key);
	}


	@Override
	public Boolean getBooleanParam(String key) {
		// TODO Auto-generated method stub
		return super.getBooleanParam(key);
	}
	
	


	@Override
	public void setParam(String key, Object value) {
		// TODO Auto-generated method stub
		super.setParam(key, value);
	}


	@Override
	protected void initAttribute(Element rootElement) {
		this.setType(rootElement.getAttribute(FIELD_Type));
		this.setName(rootElement.getAttribute(FIELD_Name));
	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Type,this.getType());
		rootElement.setAttribute(FIELD_Name,this.getName());
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}
	
	

}
