package com.munch.exchange.wizard.parameter.optimization;

import org.eclipse.jface.wizard.Wizard;

import com.munch.exchange.model.core.optimization.AlgorithmParameters;

public class OptimizationDoubleParamWizard extends Wizard {
	
	
	private AlgorithmParameters<double[]> optLearnParam;
	
	private OptimizationAlgorithmWizardPage optAlgPage;
	private EvolutionStrategyWizardPage esPage;
	
	
	public OptimizationDoubleParamWizard(AlgorithmParameters<double[]> optLearnParam) {
		setWindowTitle("Optimization Algorithm");
		
		this.optLearnParam=optLearnParam;
		
		optAlgPage=new OptimizationAlgorithmWizardPage(this.optLearnParam);
		esPage=new EvolutionStrategyWizardPage(this.optLearnParam);
		
	}
	
	

	public AlgorithmParameters<double[]> getOptLearnParam() {
		return optLearnParam;
	}



	@Override
	public void addPages() {
		addPage(optAlgPage);
		addPage(esPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
