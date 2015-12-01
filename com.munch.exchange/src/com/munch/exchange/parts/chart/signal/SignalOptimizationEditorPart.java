package com.munch.exchange.parts.chart.signal;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.services.ejb.interfaces.IIBHistoricalDataProvider;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ProgressBar;

public class SignalOptimizationEditorPart {
	
	private static Logger logger = Logger.getLogger(SignalOptimizationEditorPart.class);
	
	
	public static final String SIGNAL_OPTIMIZATION_EDITOR_ID="com.munch.exchange.partdescriptor.chart.signal.optimization.editor";
	
	
	@Inject
	IbChartSignal signal;
	
	@Inject
	IIBHistoricalDataProvider hisDataProvider;
	
	private List<IbBar> allCollectedBars;
	
	LinkedList<LinkedList<IbBar>> backTestingBlocks;
	LinkedList<LinkedList<IbBar>> optimizationBlocks;
	
	private Combo comboBarSize;
	private Spinner spinnerPercentOfData;
	private Label lblPercentOfData;
	private Composite compositeCommand;
	private Composite compositeChart;
	private Group groupControls;
	private Label lblBarSize;
	private Composite compositeMain;
	private Group grpDisplayedResults;
	private Table tableResults;
	private TableViewer tableViewerResults;
	private Composite composite;
	private Button btnSelectAll;
	private Button btnShowStatistic;
	private Group grpDisplayedMetrics;
	private org.eclipse.swt.widgets.List listMetrics;
	private ListViewer listViewerMetrics;
	private Label lblAlgorithm;
	private Combo comboAlgorithm;
	private Label lblSeeds;
	private Spinner spinnerSeeds;
	private Label lblMaxNfe;
	private Spinner spinnerMaxNFE;
	private Composite compositeCommandBtns;
	private Button btnRun;
	private Button btnCancel;
	private Button btnClear;
	private ProgressBar progressBarRun;
	private Label lblMemory;
	
	public SignalOptimizationEditorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		compositeMain = new Composite(parent, SWT.NONE);
		compositeMain.setLayout(new GridLayout(2, false));
		compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		compositeCommand = new Composite(compositeMain, SWT.NONE);
		compositeCommand.setLayout(new GridLayout(1, false));
		compositeCommand.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		
		groupControls = new Group(compositeCommand, SWT.NONE);
		groupControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		groupControls.setLayout(new GridLayout(2, false));
		groupControls.setText("Controls");
		
		lblBarSize = new Label(groupControls, SWT.NONE);
		lblBarSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBarSize.setText("Bar size:");
		
		comboBarSize = new Combo(groupControls, SWT.NONE);
		comboBarSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//comboBarSize.setText(comboBarSize.getItem(0));
		
		lblPercentOfData = new Label(groupControls, SWT.NONE);
		lblPercentOfData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPercentOfData.setText("Percent of Data:");
		
		spinnerPercentOfData = new Spinner(groupControls, SWT.BORDER);
		spinnerPercentOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerPercentOfData.setPageIncrement(1);
		spinnerPercentOfData.setIncrement(5);
		spinnerPercentOfData.setMinimum(10);
		spinnerPercentOfData.setSelection(70);
		
		lblAlgorithm = new Label(groupControls, SWT.NONE);
		lblAlgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAlgorithm.setText("Algorithm:");
		
		comboAlgorithm = new Combo(groupControls, SWT.NONE);
		comboAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblSeeds = new Label(groupControls, SWT.NONE);
		lblSeeds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeeds.setText("Seeds:");
		
		spinnerSeeds = new Spinner(groupControls, SWT.BORDER);
		spinnerSeeds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lblMaxNfe = new Label(groupControls, SWT.NONE);
		lblMaxNfe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaxNfe.setText("Max NFE:");
		
		spinnerMaxNFE = new Spinner(groupControls, SWT.BORDER);
		spinnerMaxNFE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		compositeCommandBtns = new Composite(compositeCommand, SWT.NONE);
		compositeCommandBtns.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeCommandBtns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnRun = new Button(compositeCommandBtns, SWT.NONE);
		btnRun.setText("Run");
		
		btnCancel = new Button(compositeCommandBtns, SWT.NONE);
		btnCancel.setText("Cancel");
		
		btnClear = new Button(compositeCommandBtns, SWT.NONE);
		btnClear.setText("Clear");
		
		grpDisplayedResults = new Group(compositeCommand, SWT.NONE);
		grpDisplayedResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpDisplayedResults.setLayout(new GridLayout(1, false));
		grpDisplayedResults.setText("Displayed Results");
		
		tableViewerResults = new TableViewer(grpDisplayedResults, SWT.BORDER | SWT.FULL_SELECTION);
		tableResults = tableViewerResults.getTable();
		tableResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		composite = new Composite(grpDisplayedResults, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnSelectAll = new Button(composite, SWT.NONE);
		btnSelectAll.setText("Select All");
		
		btnShowStatistic = new Button(composite, SWT.NONE);
		btnShowStatistic.setText("Show Statistic");
		
		grpDisplayedMetrics = new Group(compositeCommand, SWT.NONE);
		grpDisplayedMetrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		grpDisplayedMetrics.setText("Displayed Metrics");
		grpDisplayedMetrics.setLayout(new GridLayout(1, false));
		
		listViewerMetrics = new ListViewer(grpDisplayedMetrics, SWT.BORDER | SWT.V_SCROLL);
		listMetrics = listViewerMetrics.getList();
		listMetrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		compositeChart = new Composite(compositeMain, SWT.NONE);
		compositeChart.setLayout(new GridLayout(1, false));
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeChart.setSize(288, 107);
		
		TabFolder tabFolder = new TabFolder(compositeChart, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmMetrics = new TabItem(tabFolder, SWT.NONE);
		tbtmMetrics.setText("Metrics");
		
		Composite compositeBottom = new Composite(parent, SWT.BORDER);
		compositeBottom.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeBottom.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
		progressBarRun = new ProgressBar(compositeBottom, SWT.NONE);
		
		Label lblSeedsNb = new Label(compositeBottom, SWT.NONE);
		lblSeedsNb.setText("Seed");
		
		lblMemory = new Label(compositeBottom, SWT.NONE);
		lblMemory.setText("Memory");
		for(String bSize:IbBar.getAllBarSizesAsString())
			comboBarSize.add(bSize);
		
		
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
