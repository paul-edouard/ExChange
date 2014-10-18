package com.munch.exchange.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class NeuralNetworkOptimizer extends Job {

	public NeuralNetworkOptimizer() {
		super("Neural Network Optimizer");
		setSystem(true);
		setPriority(SHORT);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
