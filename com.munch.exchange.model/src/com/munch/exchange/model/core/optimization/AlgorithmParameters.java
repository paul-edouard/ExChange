package com.munch.exchange.model.core.optimization;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.goataa.impl.algorithms.ea.SimpleGenerationalEA;
import org.goataa.impl.algorithms.ea.selection.RandomSelection;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.searchOperations.strings.bits.booleans.binary.BooleanArrayUniformCrossover;
import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;
import org.goataa.impl.searchOperations.strings.bits.booleans.unary.BooleanArraySingleBitFlipMutation;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.INullarySearchOperation;
import org.goataa.spec.ISOOptimizationAlgorithm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.neuralnetwork.FullyStraigthFowardNetworkCreation;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.xml.XmlParameterElement;

public class AlgorithmParameters<X> extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(AlgorithmParameters.class);
	
	//Evolution Strategy
	public static final String ALGORITHM_Evolution_Strategy="Evolution Strategy";
	public static final String ES_Dimension="ES Dimension";
	public static final String ES_Minimum="ES Minimum";
	public static final String ES_Maximum="ES Maximum";
	public static final String ES_Mu="ES Mu";
	public static final String ES_Lambda="ES Lambda";
	public static final String ES_Rho="ES Rho";
	public static final String ES_Plus="ES Plus";
	
	//Dimension
	public static final String MaxDimension="Max Dimension";
	public static final String MinDimension="Min Dimension";
	
	//Evolutionary Algorithm
	public static final String ALGORITHM_Simple_Generational_EA="Simple Generational EA";
	public static final String EA_MutationRate="EA Mutation Rate";
	public static final String EA_CrossoverRate="EA Crossover Rate";
	public static final String EA_PopulationSize="EA Population Size";
	public static final String EA_MatingPoolSize="EA Mating Pool Size";
	public static final String EA_Dimension="EA Dimension";
	
	
	//Selection Algorithm
	public static final String SELECTION_ALGORITHM="Selection Algorithm";
	public static final String SELECTION_ALGORITHM_Tournament="Tournament";
	public static final String Tournament_Size="Tournament Size";
	
	public static final String SELECTION_ALGORITHM_Random="Random";
	
	//Nullary Search Operation
	public static final String NULLARY_SEARCH_OPERATION ="Nullary search operation";
	public static final String NSO_Uniform_Creation="NSO Uniform Creation";
	public static final String NSO_BooleanArrayUniformCreation="NSO Boolean Array Uniform Creation";
	public static final String NSO_FullyStraigthFowardNetworkCreation="NSO Boolean Fully Straigth Foward Network Creation";
	
	
	//Binary Search Operation
	public static final String BINARY_SEARCH_OPERATION="Binary Search Operation";
	public static final String BSO_BooleanArrayUniformCrossover="BSO Boolean Array Uniform Crossover";
	
	//Unary Search Operation
	public static final String UNARY_SEARCH_OPERATION="Unary Search Operation";
	public static final String USO_BooleanArraySingleBitFlipMutation="USO Boolean Array Single Bit Flip Mutation";
	
	//Termination
	public static final String TERMINATION_Steps="Steps";
	
	//Optimization Loops
	public static final String OPTIMIZATION_Loops="Optimization loops";
	
	
	static final String FIELD_Type="Type";
	static final String FIELD_Name="Name";
	
	
	private String type="";
	private String name="";
	
	public AlgorithmParameters(String name){
		this.name=name;
	}
	
	public AlgorithmParameters<X> createCopy(){
		AlgorithmParameters<X> copy=new AlgorithmParameters<X>(this.name);
		copy.setType(this.type);
		copy.setParameter(this.getParameter().createCopy());
		return copy;
		
	}
	
	public ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> createDoubleAlgorithm(){
		if(type.equals(ALGORITHM_Evolution_Strategy)){
			//Creation
			EvolutionStrategy<X> ES = new EvolutionStrategy<X>();
			ES.setDimension(this.getIntegerParam(ES_Dimension));
			
			if(this.hasParamKey(ES_Minimum))
				ES.setMinimum(this.getDoubleParam(ES_Minimum));
			else
				ES.setMinimum(-1.0);
			
			if(this.hasParamKey(ES_Maximum))
				ES.setMaximum(this.getDoubleParam(ES_Maximum));
			else
				ES.setMaximum(1.0);
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
			if(this.getStringParam(NULLARY_SEARCH_OPERATION).equals(NSO_Uniform_Creation)){
				
				DoubleArrayUniformCreation creation=new DoubleArrayUniformCreation(ES.getDimension(), ES.getMinimum(), ES.getMaximum());
				ES.setNullarySearchOperation(creation);
			}
			
			int steps=this.getIntegerParam(TERMINATION_Steps);
			if(ES.isPlus()){
				steps=steps*(ES.getLambda()+ES.getMu())+ES.getMu();
			}
			else{
				steps=steps*ES.getLambda()+ES.getMu();
			}
			
			
			ES.setTerminationCriterion(new StepLimitPropChange<double[],X>(steps));
			
			return ES;
		}
		
		return null;
	}
	
	
	public ISOOptimizationAlgorithm<boolean[], X, Individual<boolean[], X>> createBooleanAlgorithm(int numberOfInputNeurons){
		if(type.equals(ALGORITHM_Simple_Generational_EA)){
			//Creation
			SimpleGenerationalEA<boolean[],X> EA = new SimpleGenerationalEA<boolean[],X>();
			EA.setMutationRate(this.getDoubleParam(EA_MutationRate));
			EA.setCrossoverRate(this.getDoubleParam(EA_CrossoverRate));
			EA.setPopulationSize(this.getIntegerParam(EA_PopulationSize));
			EA.setMatingPoolSize(this.getIntegerParam(EA_MatingPoolSize));
			
			//Selection Algorithm
			if(this.getStringParam(SELECTION_ALGORITHM).equals(SELECTION_ALGORITHM_Tournament)){
				EA.setSelectionAlgorithm(new TournamentSelection(this.getIntegerParam(Tournament_Size)));
			}
			else if(this.getStringParam(SELECTION_ALGORITHM).equals(SELECTION_ALGORITHM_Random)){
				EA.setSelectionAlgorithm(RandomSelection.RANDOM_SELECTION);
			}
			
			// Nullary Search Operation
			if(this.getStringParam(NULLARY_SEARCH_OPERATION).equals(NSO_BooleanArrayUniformCreation)){
				INullarySearchOperation<boolean[]> create=new BooleanArrayUniformCreation(this.getIntegerParam(EA_Dimension));
				EA.setNullarySearchOperation(create);
			}
			else if(this.getStringParam(NULLARY_SEARCH_OPERATION).equals(NSO_FullyStraigthFowardNetworkCreation)){
				INullarySearchOperation<boolean[]> create=new FullyStraigthFowardNetworkCreation(this.getIntegerParam(EA_Dimension),numberOfInputNeurons);
				EA.setNullarySearchOperation(create);
			}
			
			//Binary Search Operation
			if(this.getStringParam(BINARY_SEARCH_OPERATION).equals(BSO_BooleanArrayUniformCrossover)){
				EA.setBinarySearchOperation(BooleanArrayUniformCrossover.BOOLEAN_ARRAY_UNIFORM_CROSSOVER);
			}
			
			//Unary Search Operation
			if(this.getStringParam(UNARY_SEARCH_OPERATION).equals(USO_BooleanArraySingleBitFlipMutation)){
				EA.setUnarySearchOperation(BooleanArraySingleBitFlipMutation.BOOLEAN_ARRAY_SINGLE_BIT_FLIP_MUTATION);
			}
			
			int steps=this.getIntegerParam(TERMINATION_Steps);
			steps=steps*EA.getMatingPoolSize()+EA.getPopulationSize();
			
			logger.info("Numer of steps:"+steps);
			
			EA.setTerminationCriterion(new StepLimitPropChange<boolean[],X>(steps));
			
			return EA;
		}
		
		return null;
		
	}
	
	
	public void addBooleanLastBestResults(ISOOptimizationAlgorithm<boolean[], X, Individual<boolean[], X>> algorithm,OptimizationResults oldBestResults){
		
		//SimpleGenerationalEA<boolean[],X> EA
		if(!(algorithm instanceof SimpleGenerationalEA))return;
		SimpleGenerationalEA<boolean[],X> EA=(SimpleGenerationalEA<boolean[],X>) algorithm;
		
		
		if(!(EA.getNullarySearchOperation() instanceof BooleanArrayUniformCreation))return;
		
		BooleanArrayUniformCreation creation=(BooleanArrayUniformCreation) EA.getNullarySearchOperation();
		
		
		LinkedList<boolean[]> oldResults =new LinkedList<boolean[]>();
		if(oldBestResults!=null && oldBestResults.getResults()!=null){	
			for(ResultEntity ent : oldBestResults.getResults()){
				oldResults.add(ent.getBooleanArray());
			}
		}
		
		creation.setOldResults(oldResults);
		
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
