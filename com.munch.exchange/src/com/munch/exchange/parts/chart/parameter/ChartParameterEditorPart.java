package com.munch.exchange.parts.chart.parameter;

import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartParameter;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.parts.chart.tree.ChartTreeComposite;

public class ChartParameterEditorPart {
	
	public static final String CHART_PARAMETER_EDITOR_ID="com.munch.exchange.part.chart.parameter.editor";
	
	private static Logger logger = Logger.getLogger(ChartParameterEditorPart.class);
	
	@Inject
	private Shell shell;
	
	@Inject
	private IEventBroker eventBroker;
	
	private Label lblSelection;
	
	private ChartIndicator chartIndicator;
	private IbChartIndicator ibChartIndicator;
	private IbContract contract;
	
	private Composite parent;
	private Button btnReset;
	
	//Volume
	private Spinner volumeSpinner;
	private Label lblVolume;
	private Label dummyLbl;
	
	
	private LinkedList<ChartParameterComposite> parameterComposites=new LinkedList<ChartParameterComposite>();
	private LinkedList<IbChartParameterComposite> ibParameterComposites=new LinkedList<IbChartParameterComposite>();
	
	
	public ChartParameterEditorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		
		parent.setLayout(new GridLayout(1, false));
		
		this.parent=parent;
		
		lblSelection = new Label(parent, SWT.NONE);
		lblSelection.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblSelection.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblSelection.setText("Waitig of a chart indicator selection");
		
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}

	
	private void update(){
		
		if(lblSelection!=null && !lblSelection.isDisposed())
			lblSelection.dispose();
		if(btnReset!=null && !btnReset.isDisposed())
			btnReset.dispose();
		
		clearIbChartSignalElements();
		
		for(ChartParameterComposite p:parameterComposites)
			p.dispose();
		parameterComposites.clear();
		
		for(IbChartParameterComposite p:ibParameterComposites)
			p.dispose();
		ibParameterComposites.clear();
		
		//Clear the IbChart Signal elements
		
		
		
		parent.update();
		parent.setLayout(new GridLayout(3, false));
		if(this.chartIndicator!=null){
			for(ChartParameter param:this.chartIndicator.getChartParameters()){
				ChartParameterComposite pc=new ChartParameterComposite(this,parent , param);
				pc.getSlider().setEnabled(this.chartIndicator.isActivated());
				parameterComposites.add(pc);
			}
		}
		else if(this.ibChartIndicator!=null){
			for(IbChartParameter param:this.ibChartIndicator.getParameters()){
				IbChartParameterComposite pc=new IbChartParameterComposite(this,parent , param);
				pc.getSlider().setEnabled(this.ibChartIndicator.isActivated());
				ibParameterComposites.add(pc);
			}
		}
		
		
		//Create the ib chart signal elements
		createIbChartSignalElements();
		
		
		
		btnReset = new Button(parent, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(chartIndicator!=null){
					chartIndicator.resetDefault();
					for(ChartParameterComposite p:parameterComposites)
						p.refresh();
				}
				else if(ibChartIndicator!=null){
					ibChartIndicator.resetDefault();
					for(IbChartParameterComposite p:ibParameterComposites)
						p.refresh();
					//ibChartIndicator.fireParametersChanged();
					eventBroker.post(IEventConstant.IB_CHART_INDICATOR_PARAMETER_CHANGED, ibChartIndicator);
				}
			}
		});
		btnReset.setText("Reset");
		//new Label(parent, SWT.NONE);
		//new Label(parent, SWT.NONE);
		
		
		parent.layout();
		
	}
	
	private void clearIbChartSignalElements(){
		//if(!(ibChartIndicator instanceof IbChartSignal))return;
		
		if(volumeSpinner!=null && !volumeSpinner.isDisposed())
			volumeSpinner.dispose();
		
		if(dummyLbl!=null && !dummyLbl.isDisposed())
			dummyLbl.dispose();
		
		if(lblVolume!=null && !lblVolume.isDisposed())
			lblVolume.dispose();
		
	}
	
	
	private void createIbChartSignalElements(){
		if(!(ibChartIndicator instanceof IbChartSignal))return;
		
		//logger.info("Contract: "+contract.getLongName());
		IbChartSignal chartSignal=(IbChartSignal) ibChartIndicator;
		
		lblVolume = new Label(parent, SWT.NONE);
		lblVolume.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblVolume.setText("Volume"+":");
		
		volumeSpinner=new Spinner(parent, SWT.NONE);
		volumeSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
		volumeSpinner.setValues((int)chartSignal.getVolume(),
				1, 100000, 0, 10, 1);
		
		volumeSpinner.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				int vol=volumeSpinner.getSelection();
				IbChartSignal chartSignal=(IbChartSignal) ibChartIndicator;
				chartSignal.setVolume(vol);
				chartSignal.setDirty(true);
				eventBroker.post(IEventConstant.IB_CHART_INDICATOR_PARAMETER_CHANGED, ibChartIndicator);
				
			}
		});
		
		dummyLbl=new Label(parent, SWT.NONE);
		
	}
	
	
	public IEventBroker getEventBroker() {
		return eventBroker;
	}
	
	
	//################################
  	//##       Event Reaction       ##
  	//################################
	private boolean isCompositeAbleToReact(){
			if (shell.isDisposed())
				return false;
			
			if(ibChartIndicator==null && chartIndicator==null)return false;
			//if(archi.getParent()==null)return false;
			
			//Stock stock=archi.getParent().getParent();
			//if (stock == null )
			//	return false;
			
			return true;
	}
	
	 @Inject
	public void activationChanged(@Optional  @UIEventTopic(IEventConstant.CHART_INDICATOR_ACTIVATION_CHANGED) ChartIndicator selIndic){
		 if(chartIndicator!=selIndic)return;
		 
		 if(!isCompositeAbleToReact())return;
		 
		 for(ChartParameterComposite p:parameterComposites)
			 p.getSlider().setEnabled(chartIndicator.isActivated());
	 }
	 
	@Inject
	public void analyseSelection( @Optional  @UIEventTopic(IEventConstant.CHART_INDICATOR_SELECTED) ChartIndicator selIndic){
		
		// logger.info("Analyse selection!!");
		 
		 if(chartIndicator!=null && chartIndicator==selIndic)
			return;
		 
		 
		chartIndicator=selIndic;
		ibChartIndicator=null;
	    if(isCompositeAbleToReact()){
	    	//logger.info("Selcted recieved: "+chartIndicator.getName());
	    		update();
	    }
	}
	
	@Inject
	public void analyseIbSelection( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_SELECTED) IbChartIndicator selIndic){
		
		// logger.info("Analyse IB Chart Indiator selection!!");
		 
		 if(ibChartIndicator!=null && ibChartIndicator==selIndic)
			return;
		 
		 
		chartIndicator=null;
		ibChartIndicator=selIndic;
		
	    if(isCompositeAbleToReact()){
			contract=ibChartIndicator.getGroup().getRoot().getContainer().getContract();
	    	update();
	    }
	}
	
	
	@Inject
	public void newCurrentParameters( @Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_NEW_CURRENT_PARAMETER) IbChartIndicator selIndic){
		
		// logger.info("Analyse IB Chart Indiator selection!!");
		 
		 if(ibChartIndicator!=null && ibChartIndicator!=selIndic)
			return;
		 
	    if(isCompositeAbleToReact()){
		//	contract=ibChartIndicator.getGroup().getRoot().getContainer().getContract();
	    	update();
	    }
	}
	
	
	
	 @Inject
	public void activationIbChanged(@Optional  @UIEventTopic(IEventConstant.IB_CHART_INDICATOR_ACTIVATION_CHANGED) IbChartIndicator selIndic){
		 if(ibChartIndicator!=selIndic)return;
		 
		 if(!isCompositeAbleToReact())return;
		 
		 for(IbChartParameterComposite p:ibParameterComposites)
			 p.getSlider().setEnabled(ibChartIndicator.isActivated());
	 }
	
	
}
