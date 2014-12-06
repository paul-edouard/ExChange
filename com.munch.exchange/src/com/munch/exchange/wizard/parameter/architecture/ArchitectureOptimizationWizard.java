package com.munch.exchange.wizard.parameter.architecture;

import org.eclipse.jface.wizard.Wizard;

import com.munch.exchange.model.core.optimization.AlgorithmParameters;

public class ArchitectureOptimizationWizard extends Wizard {
	
	
	private AlgorithmParameters<boolean[]> optArchitectureParam;
	
	private SimpleGenerationalEAWizardPage sgEaPage;
	private ArchitectureOptimizationAlgorithmWizardPage archOptAlPage;

	public ArchitectureOptimizationWizard(AlgorithmParameters<boolean[]> optArchitectureParam,int numberOfInputNeurons) {
		setWindowTitle("Architecture Optimization Wizard");
		
		this.optArchitectureParam=optArchitectureParam;
		
		sgEaPage=new SimpleGenerationalEAWizardPage(this.optArchitectureParam);
		archOptAlPage=new ArchitectureOptimizationAlgorithmWizardPage(this.optArchitectureParam,numberOfInputNeurons);
		
	}
	
	
	public AlgorithmParameters<boolean[]> getOptArchitectureParam() {
		return optArchitectureParam;
	}

	@Override
	public void addPages() {
		addPage(archOptAlPage);
		addPage(sgEaPage);
		
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	
}
