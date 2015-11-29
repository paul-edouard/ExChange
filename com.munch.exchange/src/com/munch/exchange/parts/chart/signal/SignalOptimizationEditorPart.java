package com.munch.exchange.parts.chart.signal;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.parts.chart.ChartEditorPart;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Spinner;

public class SignalOptimizationEditorPart {
	
	private static Logger logger = Logger.getLogger(SignalOptimizationEditorPart.class);
	
	
	public static final String SIGNAL_OPTIMIZATION_EDITOR_ID="com.munch.exchange.partdescriptor.chart.signal.optimization.editor";
	private Text text;
	
	
	@Inject
	IbChartSignal signal;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	private List<IbBar> allCollectedBars;
	
	LinkedList<LinkedList<IbBar>> backTestingBlocks;
	LinkedList<LinkedList<IbBar>> optimizationBlocks;
	
	private Combo comboBarSize;
	private Text textContainerName;
	private Composite composite;
	private Text text_1;
	private Spinner spinnerPercentOfData;
	private Label lblPercentOfData;
	private Button btnStartOptimization;
	
	public SignalOptimizationEditorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite compositeDataParameters = new Composite(parent, SWT.NONE);
		compositeDataParameters.setLayout(new GridLayout(2, false));
		compositeDataParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		textContainerName = new Text(compositeDataParameters, SWT.BORDER);
		textContainerName.setEditable(false);
		textContainerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textContainerName.setText(getBarContainer().getType().toString());
		
		comboBarSize = new Combo(compositeDataParameters, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String bSize:IbBar.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		comboBarSize.setText(comboBarSize.getItem(0));
		
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Button btnAskData = new Button(composite, SWT.NONE);
		btnAskData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAskData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BarSize barSize=IbBar.getBarSizeFromString(comboBarSize.getText());
				
				allCollectedBars=hisDataProvider.getAllBars(getBarContainer(), barSize);
				text.setText("Number of bar: "+allCollectedBars.size());
				
				//Start the splitting in Blocks
				//LinkedList<LinkedList<IbBar>> blocks = splitCollectedBarsInBlocks();
				
				//text_1.setText("Number of blocks: "+blocks.size());
				btnStartOptimization.setEnabled(true);
				
			}
		});
		btnAskData.setText("Ask Data");
		
		text = new Text(composite, SWT.BORDER);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblPercentOfData = new Label(composite, SWT.NONE);
		lblPercentOfData.setText("Percent of Data");
		
		spinnerPercentOfData = new Spinner(composite, SWT.BORDER);
		spinnerPercentOfData.setPageIncrement(1);
		spinnerPercentOfData.setIncrement(5);
		spinnerPercentOfData.setMinimum(10);
		spinnerPercentOfData.setSelection(70);
		spinnerPercentOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		text_1 = new Text(parent, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnStartOptimization = new Button(parent, SWT.NONE);
		btnStartOptimization.setEnabled(false);
		btnStartOptimization.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				logger.info("Start Optimization!");
				backTestingBlocks=splitCollectedBarsInBlocks();
				optimizationBlocks=collectOptimizationBlocks(backTestingBlocks);
				
				LinkedList<IbBar> optimizationBars=new LinkedList<>();
				for(LinkedList<IbBar> block:optimizationBlocks)
					optimizationBars.addAll(block);
				
				signal.initProblem(optimizationBars);
				signal.optimize();
				
			}
		});
		btnStartOptimization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnStartOptimization.setText("Start Optimization");
		
		
	}
	
	private LinkedList<LinkedList<IbBar>> collectOptimizationBlocks(LinkedList<LinkedList<IbBar>> allBlocks){
		LinkedList<LinkedList<IbBar>> optBlocks=new LinkedList<>();
		int numberOfBars=0;
		int percentRequired=spinnerPercentOfData.getSelection();
		int numberOfRequired=allCollectedBars.size()*percentRequired/100;
		
		while(numberOfBars<numberOfRequired){
			Random rand = new Random();
			int index=rand.nextInt(allBlocks.size());
			LinkedList<IbBar> removedBlock=allBlocks.remove(index);
			optBlocks.add(removedBlock);
			
			numberOfBars+=removedBlock.size();
		}
		logger.info("Number of blocks: "+optBlocks.size());
		logger.info("Number of bars: "+numberOfBars);
		
		return optBlocks;
	}
	
	
	private LinkedList<LinkedList<IbBar>> splitCollectedBarsInBlocks(){
		LinkedList<LinkedList<IbBar>> blocks=new LinkedList<>();
		
		IbBar lastBar=allCollectedBars.get(0);
		long interval=lastBar.getIntervallInSec();
		LinkedList<IbBar> block=new LinkedList<IbBar>();
		block.add(lastBar);
		
		for(int i=1;i<allCollectedBars.size();i++){
			IbBar currentBar=allCollectedBars.get(i);
			long timeDiff=currentBar.getTime()-lastBar.getTime();
			if(timeDiff > interval ){
				//Add the block to the list
				blocks.add(block);
			
				//Reset the block
				block=new LinkedList<IbBar>();
			}
			block.add(currentBar);
			lastBar=currentBar;
		}
		if(block.size()>0){
			blocks.add(block);
		}
	
		
		
		return blocks;
	}
	
	
	private IbBarContainer getBarContainer(){
		return signal.getGroup().getRoot().getContainer();
	}
	
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
}
