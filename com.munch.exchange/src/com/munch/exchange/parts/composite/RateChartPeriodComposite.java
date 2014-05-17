package com.munch.exchange.parts.composite;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;

public class RateChartPeriodComposite extends Composite {
	
	private static Logger logger = Logger.getLogger(RateChartPeriodComposite.class);
	
	@Inject
	private ExchangeRate rate;
	
	@Inject
	private IEventBroker eventBroker;
	
	private Label labelMaxProfitPercent;
	private Label labelKeepAndOldPercent;
	
	private Button periodBtnUpTo;
	private Slider periodSliderFrom;
	private Label periodLblFrom;
	private Slider periodSliderUpTo;
	private Label periodlblUpTo;
	
	
	
	
	@Inject
	public RateChartPeriodComposite(Composite parent,ExchangeRate rate) {
		super(parent, SWT.NONE);
		
		this.rate=rate;
		setLayout(new GridLayout(1, false));
		
		Composite compositePeriodDefinition = new Composite(this, SWT.NONE);
		compositePeriodDefinition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositePeriodDefinition.setLayout(new GridLayout(3, false));
		
		Label lblFrom = new Label(compositePeriodDefinition, SWT.NONE);
		lblFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFrom.setText("From :");
		
		periodLblFrom = new Label(compositePeriodDefinition, SWT.NONE);
		periodLblFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		periodLblFrom.setText("100  ");
		
		periodSliderFrom = new Slider(compositePeriodDefinition, SWT.NONE);
		if(!rate.getHistoricalData().isEmpty())
			periodSliderFrom.setMaximum(rate.getHistoricalData().size());
		else{
			periodSliderFrom.setMaximum(200);
		}
		periodSliderFrom.setPageIncrement(1);
		periodSliderFrom.setThumb(1);
		periodSliderFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		periodSliderFrom.setMinimum(2);
		periodSliderFrom.setSelection(100);
		periodSliderFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int upTo=periodSliderUpTo.getSelection();
				if(periodSliderUpTo.getSelection()>periodSliderFrom.getSelection()){
					upTo=0;
				}
				periodSliderUpTo.setMaximum(periodSliderFrom.getSelection()-1);
				periodSliderUpTo.setEnabled(false);
				periodSliderUpTo.setSelection(upTo);
				periodSliderUpTo.setEnabled(periodBtnUpTo.getSelection());
				
				firePeriodChanged();
				
			}
		});
		
		periodBtnUpTo = new Button(compositePeriodDefinition, SWT.CHECK);
		periodBtnUpTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				periodSliderUpTo.setEnabled(periodBtnUpTo.getSelection());
				periodSliderUpTo.setSelection(0);
				
				
				firePeriodChanged();
				
			}
		});
		periodBtnUpTo.setSize(49, 16);
		periodBtnUpTo.setText("Up to:");
		
		periodlblUpTo = new Label(compositePeriodDefinition, SWT.NONE);
		periodlblUpTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		periodlblUpTo.setText("0");
		
		periodSliderUpTo = new Slider(compositePeriodDefinition, SWT.NONE);
		periodSliderUpTo.setThumb(1);
		periodSliderUpTo.setPageIncrement(1);
		periodSliderUpTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		periodSliderUpTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(periodSliderUpTo.isEnabled()){
					firePeriodChanged();
				}
				
			}
		});
		periodSliderUpTo.setEnabled(false);
		
		Composite compositeAnalysis = new Composite(this, SWT.NONE);
		compositeAnalysis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeAnalysis.setLayout(new GridLayout(3, false));
		
		Label lblMaxProfit = new Label(compositeAnalysis, SWT.NONE);
		lblMaxProfit.setSize(60, 15);
		lblMaxProfit.setText("Max. Profit:");
		
		labelMaxProfitPercent = new Label(compositeAnalysis, SWT.NONE);
		labelMaxProfitPercent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelMaxProfitPercent.setText("0,00%");
		
		Label lblNewLabelSep = new Label(compositeAnalysis, SWT.NONE);
		lblNewLabelSep.setText(" ");
		
		Label lblKeepOld = new Label(compositeAnalysis, SWT.NONE);
		lblKeepOld.setSize(74, 15);
		lblKeepOld.setText("Keep and Old:");
		
		labelKeepAndOldPercent = new Label(compositeAnalysis, SWT.NONE);
		labelKeepAndOldPercent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelKeepAndOldPercent.setText("0,00%");
		new Label(compositeAnalysis, SWT.NONE);
		
		
		
	}

	/*
	private void refreshPeriod(){
		eventBroker.send(IEventConstant.PERIOD_CHANGED,true);
	}
	*/

	public Label getLabelMaxProfitPercent() {
		return labelMaxProfitPercent;
	}




	public Label getLabelKeepAndOldPercent() {
		return labelKeepAndOldPercent;
	}




	public Button getPeriodBtnUpTo() {
		return periodBtnUpTo;
	}




	public Slider getPeriodSliderFrom() {
		return periodSliderFrom;
	}




	public Label getPeriodLblFrom() {
		return periodLblFrom;
	}




	public Slider getPeriodSliderUpTo() {
		return periodSliderUpTo;
	}




	public Label getPeriodlblUpTo() {
		return periodlblUpTo;
	}
	
	
	public interface PeriodChangedListener {
		public void PeriodChanged();
	}
	
	// ///////////////////////////
	// // LISTERNER ////
	// ///////////////////////////
	private List<PeriodChangedListener> listeners = new LinkedList<PeriodChangedListener>();

	public void addPeriodChangedListener(PeriodChangedListener l) {
		listeners.add(l);
	}

	public void removePeriodChangedListener(PeriodChangedListener l) {
		listeners.remove(l);
	}

	private void firePeriodChanged() {
		for (PeriodChangedListener l : listeners)
			l.PeriodChanged();
	}
	
	
}
