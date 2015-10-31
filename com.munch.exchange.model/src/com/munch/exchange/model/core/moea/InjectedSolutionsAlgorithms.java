package com.munch.exchange.model.core.moea;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.algorithm.RandomSearch;
import org.moeaframework.algorithm.StandardAlgorithms;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.operator.InjectedInitialization;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;


public class InjectedSolutionsAlgorithms extends StandardAlgorithms{
	
	private List<Solution> injectedSolutions;
	
	public InjectedSolutionsAlgorithms(List<Solution> injectedSolutions){
		this.injectedSolutions=injectedSolutions;
	}
	
	public InjectedSolutionsAlgorithms(Solution injectedSolution){
		this.injectedSolutions=new LinkedList<Solution>();
		this.injectedSolutions.add(injectedSolution);
	}

	@Override
	public Algorithm getAlgorithm(String name, Properties properties,
			Problem problem) {
		Algorithm algorithm=super.getAlgorithm(name, properties, problem);
		
		TypedProperties typedProperties = new TypedProperties(properties);
		int populationSize = (int)typedProperties.getDouble("populationSize", 100);
		Initialization initialization=createInitialization(problem, populationSize);
		
		if(algorithm instanceof AbstractEvolutionaryAlgorithm){
			AbstractEvolutionaryAlgorithm evolutionaryAlgorithm =
					(AbstractEvolutionaryAlgorithm) algorithm;
			evolutionaryAlgorithm.setInitialization(initialization);
		}
		else if(algorithm instanceof MOEAD){
			((MOEAD) algorithm).setInitialization(initialization);
		}
		else if(algorithm instanceof RandomSearch){
			((RandomSearch) algorithm).setGenerator(initialization);
		}
		else{
			System.err.println("the algorithm "+name+" won't be initializated with the given values");
		}
		
		return algorithm;
	}
	
	private Initialization createInitialization(Problem problem, int populationSize){
		
		List<Solution> initialSolution=new LinkedList<Solution>();
		for(Solution injSol:this.injectedSolutions){
			Solution solution = problem.newSolution();
			
			for (int j = 0; j < solution.getNumberOfVariables(); j++) {
				Variable variable = solution.getVariable(j);
				Variable injVariable = injSol.getVariable(j);
				copyValue(injVariable,variable);
			}
			
			initialSolution.add(solution);
		}
		
		Initialization initialization = new InjectedInitialization(problem,
				populationSize,initialSolution);
		
		return initialization;
	}
	
	private void copyValue(Variable injVariable,Variable variable){
		if (variable instanceof RealVariable) {
			RealVariable real = (RealVariable)variable;
			RealVariable injReal = (RealVariable)injVariable;
			real.setValue(injReal.getValue());
			
		} else if (variable instanceof BinaryVariable) {
			BinaryVariable binary = (BinaryVariable)variable;
			BinaryVariable injBinary = (BinaryVariable)injVariable;

			for (int i = 0; i < binary.getNumberOfBits(); i++) {
				binary.set(i, injBinary.get(i));
			}
			
		} else if (variable instanceof Permutation) {
			Permutation permutation = (Permutation)variable;
			Permutation injPermutation = (Permutation)injVariable;

			permutation.fromArray(injPermutation.toArray());
			
		} else if (variable instanceof Grammar) {
			Grammar grammar = (Grammar)variable;
			Grammar injGrammar = (Grammar)injVariable;

			grammar.fromArray(injGrammar.toArray());
			
		} else if (variable instanceof Program) {
			// ramped half-and-half initialization
			Program program = (Program)variable;
			Program injProgram = (Program)injVariable;
			
			program.setArgument(0, injProgram.getArgument(0));
		} else {
			System.err.println("can not copy the values");
		}
	}
	
	
	

}
