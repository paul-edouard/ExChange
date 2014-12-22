package com.munch.exchange.parts.neuralnetwork.input;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class NeuralNetworkInputConfiguratorComposite extends Composite {
	private Button btnSave;
	private Button btnEdit;
	private Composite compositeHeader;
	private Composite compositeMiddle;
	private Composite compositeBottom;
	private Combo comboPeriod;
	private Button btnActivateDayOf;

	public NeuralNetworkInputConfiguratorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group grpInputConfiguration = new Group(this, SWT.NONE);
		grpInputConfiguration.setLayout(new GridLayout(1, false));
		grpInputConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpInputConfiguration.setText("Input Configuration");
		
		compositeHeader = new Composite(grpInputConfiguration, SWT.NONE);
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeHeader.setLayout(new GridLayout(2, false));
		
		btnEdit = new Button(compositeHeader, SWT.NONE);
		btnEdit.setSize(32, 25);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnEdit.setText("Edit");
		
		btnSave = new Button(compositeHeader, SWT.NONE);
		btnSave.setSize(36, 25);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSave.setText("Save");
		
		compositeMiddle = new Composite(grpInputConfiguration, SWT.NONE);
		compositeMiddle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblPeriod = new Label(compositeMiddle, SWT.NONE);
		lblPeriod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPeriod.setText("Period:");
		
		comboPeriod = new Combo(compositeMiddle, SWT.NONE);
		comboPeriod.setEnabled(false);
		comboPeriod.setItems(new String[] {"DAY", "HOUR", "MINUTE", "SECONDE"});
		comboPeriod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboPeriod.setText("DAY");
		
		btnActivateDayOf = new Button(compositeMiddle, SWT.CHECK);
		btnActivateDayOf.setEnabled(false);
		btnActivateDayOf.setText("Day of week");
		
		compositeBottom = new Composite(grpInputConfiguration, SWT.NONE);
		compositeBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// TODO Auto-generated constructor stub
	}

}
