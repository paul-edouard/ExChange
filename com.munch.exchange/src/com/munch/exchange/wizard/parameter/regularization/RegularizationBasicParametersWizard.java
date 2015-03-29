package com.munch.exchange.wizard.parameter.regularization;

import org.eclipse.jface.wizard.Wizard;

import com.munch.exchange.model.core.neuralnetwork.LearnParameters;
import com.munch.exchange.model.core.neuralnetwork.RegularizationParameters;

public class RegularizationBasicParametersWizard extends Wizard {
	
	
	private RegularizationParameters regParameters;
	
	private RegularizationBasicParametersWizardPage regPage;
	
	
	public RegularizationBasicParametersWizard(RegularizationParameters regParameters) {
		setWindowTitle("Optimization Algorithm");
		
		this.regParameters=regParameters;
		
		regPage=new RegularizationBasicParametersWizardPage(this.regParameters);
		
	}
	
	
	
	public RegularizationParameters getRegParameters() {
		return regParameters;
	}

	@Override
	public void addPages() {
		addPage(regPage);
	}


	@Override
	public boolean performFinish() {
		return true;
	}

}
