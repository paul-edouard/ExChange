package com.munch.exchange.model.core.moea;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceConfigurationError;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;

public class InjectedSolutionsAlgorithmFactory extends AlgorithmFactory {
	
	private InjectedSolutionsAlgorithms injectedSolutionsAlgorithms;
	
	
	public InjectedSolutionsAlgorithmFactory(Population startPopulation){
		List<Solution> injectedSolutions=new LinkedList<Solution>();
		for(Solution sol:startPopulation){
			injectedSolutions.add(sol);
		}
		
		injectedSolutionsAlgorithms=new InjectedSolutionsAlgorithms(injectedSolutions);
	}
	
	
	
	public InjectedSolutionsAlgorithmFactory(List<Solution> injectedSolutions){
		injectedSolutionsAlgorithms=new InjectedSolutionsAlgorithms(injectedSolutions);
	}
	
	
	
	
	public InjectedSolutionsAlgorithmFactory(Solution injectedSolution){
		injectedSolutionsAlgorithms=new InjectedSolutionsAlgorithms(injectedSolution);
	}

	@Override
	public synchronized Algorithm getAlgorithm(String name,
			Properties properties, Problem problem) {
		// TODO Auto-generated method stub
		Algorithm algorithm = instantiateInjectedSolutionAlgorithm( name,
				properties, problem);
		
		if (algorithm != null) {
			return algorithm;
		}
		
		
		throw new ProviderNotFoundException(name);
	}
	
	private Algorithm instantiateInjectedSolutionAlgorithm(
			String name, Properties properties, Problem problem) {
		try {
			return injectedSolutionsAlgorithms.getAlgorithm(name, properties, problem);
		} catch (ServiceConfigurationError e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}
}
