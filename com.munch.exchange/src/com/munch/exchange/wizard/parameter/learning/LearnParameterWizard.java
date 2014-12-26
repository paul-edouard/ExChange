package com.munch.exchange.wizard.parameter.learning;

import org.eclipse.jface.wizard.Wizard;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;

public class LearnParameterWizard extends Wizard {
	
	
	private LearnParameters param;
	
	private LearningStrategyWizardPage strategyPage;
	private MomentumBackPropagationWizardPage mbpPage;
	private ResilientPropagationWizardPage rpPage;
	
	
	public LearnParameterWizard(LearnParameters param) {
		setWindowTitle("Learning Parameter Setting");
		this.param=param;
		
		//strategyPage=new LearningStrategyWizardPage(this.param);
		mbpPage=new MomentumBackPropagationWizardPage(this.param);
		rpPage=new ResilientPropagationWizardPage(this.param);
		
		
		strategyPage=new LearningStrategyWizardPage(this.param,rpPage,  mbpPage);
		
	}
	
	

	public LearnParameters getParam() {
		return param;
	}



	@Override
	public void addPages() {
		addPage(strategyPage);
		addPage(mbpPage);
		addPage(rpPage);
		
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
