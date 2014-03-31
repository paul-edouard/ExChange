package com.munch.exchange.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.goataa.impl.algorithms.ea.selection.RandomSelection;
import org.goataa.impl.algorithms.ea.selection.TournamentSelection;
import org.goataa.impl.algorithms.es.EvolutionStrategy;
import org.goataa.impl.searchOperations.strings.real.nullary.DoubleArrayUniformCreation;
import org.goataa.impl.termination.StepLimitPropChange;
import org.goataa.impl.utils.Individual;
import org.goataa.spec.IGPM;
import org.goataa.spec.IObjectiveFunction;
import org.goataa.spec.ISOOptimizationAlgorithm;

public class OptimizationWizard<X> extends Wizard {
	
	boolean finish = true;
	
	private ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> algorithm;
	private IObjectiveFunction<X> f;
	private IGPM<double[], X> gpm;
	StepLimitPropChange<X> term;
	
	private int dimension;
	private double min;
	private double max;
	
	
	//Pages
	private OptAlgorithmWizardPage optAlgorithmWizardPage=new OptAlgorithmWizardPage(); 
	private OptEvoStrategyWizardPage optEvoStrategyWizardPage=new OptEvoStrategyWizardPage();

	public OptimizationWizard(IObjectiveFunction<X> f,IGPM<double[], X> gpm,int dimension,double min,double max) {
		setWindowTitle("Optimization");
		
		this.f=f;
		this.gpm=gpm;
		this.dimension=dimension;
		this.min=min;
		this.max=max;
	}

	@Override
	public void addPages() {
		addPage(optAlgorithmWizardPage);
		addPage(optEvoStrategyWizardPage);
	}

	
	@Override
	public boolean performFinish() {
		
		String algorithmName =  optAlgorithmWizardPage.getComboAlgorithmType().getText();
		int steps=optAlgorithmWizardPage.getSpinnerNumberOfSteps().getSelection();
		
		//System.out.println("algorithmName: "+algorithmName);
		
		//EVOLTUTION STRATEGY
		if(algorithmName.equals(OptAlgorithmWizardPage.ALGORITHM_Evolution_Strategy)){
			//Creation
			EvolutionStrategy<X> ES = new EvolutionStrategy<X>();
			
			ES.setDimension(dimension);
			ES.setMinimum(min);
			ES.setMaximum(max);
			//Number of parents
		    ES.setMu(optEvoStrategyWizardPage.getSpinnerTotalPopulationSize().getSelection());
		    //Number of offspring
		    ES.setLambda(optEvoStrategyWizardPage.getSpinnerNumberOfOffspring().getSelection());
		    //Number of parents per offspring
		    ES.setRho(optEvoStrategyWizardPage.getSpinnerNumberOfParetnPerOffspring().getSelection());
			// (lambda+mu) strategy 
		    ES.setPlus(optEvoStrategyWizardPage.getBtnLambdaPlusMuStrategy().getSelection());
		    
		    //Selection Algorithm
			if (optEvoStrategyWizardPage
					.getComboSelectionAlgorithm()
					.getText()
					.equals(OptEvoStrategyWizardPage.SELECTION_ALGORITHM_Tournament)) {
				ES.setSelectionAlgorithm(new TournamentSelection(
						optEvoStrategyWizardPage.getSpinnerTournamentSize()
								.getSelection()));
			}
			else if(optEvoStrategyWizardPage
					.getComboSelectionAlgorithm()
					.getText()
					.equals(OptEvoStrategyWizardPage.SELECTION_ALGORITHM_Random)){
				ES.setSelectionAlgorithm(RandomSelection.RANDOM_SELECTION);
			}
			
			// Nullary Search Operation
			if (optEvoStrategyWizardPage
					.getComboNullarySearchOperation()
					.getText()
					.equals(OptEvoStrategyWizardPage.NULLARY_SEARCH_OPERATION_Uniform_Creation)) {
				ES.setNullarySearchOperation(new DoubleArrayUniformCreation(
						dimension, min, max));
			}
		    
			if(ES.isPlus()){
				steps=steps*(ES.getLambda()+ES.getMu())+ES.getMu();
			}
			else{
				steps=steps*ES.getLambda()+ES.getMu();
			}
			
			algorithm=ES;
			
		}
		
		//Set the GPM
		algorithm.setGPM(gpm);
		//Set the objectiv function
		algorithm.setObjectiveFunction(f);
		//Termination
		term = new StepLimitPropChange<X>(steps);
		algorithm.setTerminationCriterion(term);
		
		return finish;
	}
	
	@Override
	public boolean canFinish() {
		return finish;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	public ISOOptimizationAlgorithm<double[], X, Individual<double[], X>> getAlgorithm() {
		return algorithm;
	}

	public StepLimitPropChange<X> getTerm() {
		return term;
	}
	
	

}
