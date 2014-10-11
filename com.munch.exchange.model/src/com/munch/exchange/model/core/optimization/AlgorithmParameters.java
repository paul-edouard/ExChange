package com.munch.exchange.model.core.optimization;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.goataa.impl.algorithms.ea.selection.RandomSelection;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.ISOOptimizationAlgorithm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.xml.XmlParameterElement;

public class AlgorithmParameters<X> extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(AlgorithmParameters.class);
	
	//Evolution Strategy
	public static String ALGORITHM_Evolution_Strategy="Evolution Strategy";
	
	public static String ES_Dimension="ES Dimension";
	public static String ES_Minimum="ES Minimum";
	public static String ES_Maximum="ES Maximum";
	public static String ES_Mu="ES Mu";
	public static String ES_Lambda="ES Lambda";
	public static String ES_Rho="ES Rho";
	public static String ES_Plus="ES Plus";
	
	
	//Evolutionary Algorithm
	public static String ALGORITHM_Simple_Generational_EA="Simple Generational EA";
	
	public static String EA_BinarySearchOperation_="ES Dimension";
	
	
	//Selection Algorithm
	public static String SELECTION_ALGORITHM="Selection Algorithm";
	public static String SELECTION_ALGORITHM_Tournament="Tournament";
	public static String Tournament_Size="Tournament Size";
	
	public static String SELECTION_ALGORITHM_Random="Random";
	
	//Nullary Search Operation
	public static String NULLARY_SEARCH_OPERATION ="Nullary search operation";
	public static String NULLARY_SEARCH_OPERATION_Uniform_Creation="Uniform_Creation";
	
	//Termination
	public static String TERMINATION_Steps="Steps";
	
	
	static final String FIELD_Type="Type";
	static final String FIELD_Name="Name";
	
	
	private String type;
	private String name;
	
	public AlgorithmParameters(String name){
		this.name=name;
	}
	
	public ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> createAlgorithm(){
		if(type.equals(ALGORITHM_Evolution_Strategy)){
			//Creation
			EvolutionStrategy<X> ES = new EvolutionStrategy<X>();
			ES.setDimension(this.getIntegerParam(ES_Dimension));
			ES.setMinimum(this.getDoubleParam(ES_Minimum));
			ES.setMaximum(this.getDoubleParam(ES_Maximum));
			//Number of parents
			ES.setMu(this.getIntegerParam(ES_Mu));
			//Number of offspring
			ES.setLambda(this.getIntegerParam(ES_Lambda));
			//Number of parents per offspring
			ES.setRho(this.getIntegerParam(ES_Rho));
			// (lambda+mu) strategy 
			ES.setPlus(this.getBooleanParam(ES_Plus));
			
			//Selection Algorithm
			if(this.getStringParam(SELECTION_ALGORITHM).equals(SELECTION_ALGORITHM_Tournament)){
				ES.setSelectionAlgorithm(new TournamentSelection(this.getIntegerParam(Tournament_Size)));
			}
			else if(this.getStringParam(SELECTION_ALGORITHM).equals(SELECTION_ALGORITHM_Random)){
				ES.setSelectionAlgorithm(RandomSelection.RANDOM_SELECTION);
			}
			
			// Nullary Search Operation
			if(this.getStringParam(NULLARY_SEARCH_OPERATION).equals(NULLARY_SEARCH_OPERATION_Uniform_Creation)){
				
				DoubleArrayUniformCreation creation=new DoubleArrayUniformCreation(ES.getDimension(), ES.getMinimum(), ES.getMaximum());
				ES.setNullarySearchOperation(creation);
			}
			
			int steps=0;
			if(ES.isPlus()){
				steps=steps*(ES.getLambda()+ES.getMu())+ES.getMu();
			}
			else{
				steps=steps*ES.getLambda()+ES.getMu();
			}
			
			
			ES.setTerminationCriterion(new StepLimitPropChange<X>(steps));
			
			
			return ES;
		}
		
		return null;
		
	}
	
	
	public void addLastBestResults(ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm,OptimizationResults oldBestResults){
		
		if(!(algorithm instanceof EvolutionStrategy))return;
		EvolutionStrategy<X> ES=(EvolutionStrategy<X>) algorithm;
		
		if(!(ES.getNullarySearchOperation() instanceof DoubleArrayUniformCreation))return;
		
		DoubleArrayUniformCreation creation=(DoubleArrayUniformCreation) ES.getNullarySearchOperation();
		
		LinkedList<double[]> oldResults =new LinkedList<double[]>();
		if(oldBestResults!=null && oldBestResults.getResults()!=null){	
			for(ResultEntity ent : oldBestResults.getResults()){
				oldResults.add(ent.getDoubleArray());
			}
		}
		
		creation.setOldResults(oldResults);
		
		
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
	public String getTagName() {
		return this.name;
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
