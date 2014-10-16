package com.munch.exchange.wizard.parameter.learning;

import org.eclipse.jface.wizard.Wizard;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;

public class LearnParameterWizard extends Wizard {
	
	
	private LearnParameters param;
	
	private LearningStrategyWizardPage strategyPage;
	private MomentumBackPropagationWizardPage mbpPage;
	
	public LearnParameterWizard(LearnParameters param) {
		setWindowTitle("Learning Parameter Setting");
		this.param=param;
		
		strategyPage=new LearningStrategyWizardPage(this.param);
		mbpPage=new MomentumBackPropagationWizardPage(this.param);
		
	}
	
	

	public LearnParameters getParam() {
		return param;
	}



	@Override
	public void addPages() {
		addPage(strategyPage);
		addPage(mbpPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
