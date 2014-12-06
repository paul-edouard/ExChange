package com.munch.exchange.model.core.neuralnetwork;

import org.apache.log4j.Logger;
import org.neuroph.core.learning.LearningRule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.neuralnetwork.learning.FinancialLearning;
import com.munch.exchange.model.core.neuralnetwork.learning.FinancialMomentumBackpropagation;
import com.munch.exchange.model.xml.XmlParameterElement;

public class LearnParameters extends XmlParameterElement implements FinancialLearning {
	
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
	
	private double[] diffFactorArray=null;
	
	private static Logger logger = Logger.getLogger(LearnParameters.class);
	
	public LearnParameters(String name){
		this.name=name;
	}
	
	public LearnParameters createCopy(){
		
		LearnParameters copy=new LearnParameters(this.name);
		copy.setType(this.type);
		copy.setParameter(this.getParameter().createCopy());
		
		return copy;
		
	}
	
	
	public LearningRule createLearningRule(){
		//logger.info("Learning rule type: "+type);
		
		if(type.equals(MOMENTUM_BACK_PROPAGATION)){
			FinancialMomentumBackpropagation bp=new FinancialMomentumBackpropagation();
			bp.setDiffFactorArray(diffFactorArray);
			bp.setMomentum(this.getDoubleParam(MBP_Momentum));
			bp.setLearningRate(this.getDoubleParam(IL_LearningRate));
			
			bp.setMaxIterations(this.getIntegerParam(Max_Iterations));
			bp.setBatchMode(this.getBooleanParam(BatchMode));
			return bp;
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
	
	
	//****************************************
	//***             XML                 ****
	//****************************************	

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

	@Override
	public void setDiffFactorArray(double[] diffFactorArray) {
		this.diffFactorArray=diffFactorArray;
	}
	
	
	//****************************************
	//***           STATIC                ****
	//****************************************
	
	public static  void setDefaultLearnParameters(LearnParameters param){
		
		param.setType(LearnParameters.MOMENTUM_BACK_PROPAGATION);
		param.setParam(LearnParameters.Max_Iterations, 3);
		param.setParam(LearnParameters.BatchMode, true);
		
		param.setParam(LearnParameters.IL_LearningRate, 0.1d);
		param.setParam(LearnParameters.MBP_Momentum, 0.25d);
		
		
	}
	
}
