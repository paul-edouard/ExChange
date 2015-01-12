package com.munch.exchange.parts.neuralnetwork.data;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;

public class NeuralNetworkTrainingDataComposite extends Composite{
	private Text textNbOfAvailableData;
	private Text textPercentOfTraining;
	private Text textNbOfBlocks;
	private Slider sliderNbOfBlocks;
	private Slider sliderPercentOfTraining;
	
	@Inject
	public NeuralNetworkTrainingDataComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(this, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(3, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfAvailableData = new Label(compositeHeader, SWT.NONE);
		lblNbOfAvailableData.setText("Nb. of Data:");
		
		textNbOfAvailableData = new Text(compositeHeader, SWT.BORDER);
		textNbOfAvailableData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textNbOfAvailableData.setEditable(false);
		new Label(compositeHeader, SWT.NONE);
		
		Label lblPerOfTraining = new Label(compositeHeader, SWT.NONE);
		lblPerOfTraining.setText("Per. of Training:");
		
		sliderPercentOfTraining = new Slider(compositeHeader, SWT.NONE);
		
		textPercentOfTraining = new Text(compositeHeader, SWT.BORDER);
		textPercentOfTraining.setEditable(false);
		textPercentOfTraining.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNbOfBlocks = new Label(compositeHeader, SWT.NONE);
		lblNbOfBlocks.setText("Nb. of Blocks:");
		
		sliderNbOfBlocks = new Slider(compositeHeader, SWT.NONE);
		
		textNbOfBlocks = new Text(compositeHeader, SWT.BORDER);
		textNbOfBlocks.setEditable(false);
		textNbOfBlocks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		TreeViewer treeViewer = new TreeViewer(this, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// TODO Auto-generated constructor stub
	}
}
