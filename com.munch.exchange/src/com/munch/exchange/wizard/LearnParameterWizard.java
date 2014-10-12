package com.munch.exchange.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;

public class LearnParameterWizard extends Wizard {
	
	
	private LearnParameters param;
	
	private LearningStrategyWizardPage strategyPage;
	
	public LearnParameterWizard(LearnParameters param) {
		setWindowTitle("Learning Parameter Setting");
		this.param=param;
		
		strategyPage=new LearningStrategyWizardPage(this.param);
		
	}

	@Override
	public void addPages() {
		addPage(strategyPage);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
