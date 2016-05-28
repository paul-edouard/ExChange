package com.munch.exchange.dialog;

import java.util.Calendar;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;

public class EditTradingTimeDialog extends TitleAreaDialog {
	
	private long startTime;
	private long endTime;
	private DateTime dateTimeStart;
	private DateTime dateTimeEnd;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditTradingTimeDialog(Shell parentShell, long startTime, long endTime) {
		super(parentShell);
		setHelpAvailable(false);
		
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/72/stock-market-icon.png"));
		setTitle("IB Trading Time");
		setMessage("Please set the tradig for the current contract");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Label lblStart = new Label(composite, SWT.NONE);
		lblStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblStart.setSize(76, 25);
		lblStart.setText("Start:");
		
		dateTimeStart = new DateTime(composite, SWT.BORDER | SWT.TIME);
		dateTimeStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Calendar time=Calendar.getInstance();time.setTimeInMillis(this.startTime);
		dateTimeStart.setTime(time.get(Calendar.HOUR_OF_DAY)-1, time.get(Calendar.MINUTE), time.get(Calendar.SECOND));
		
		Label lblEnd = new Label(composite, SWT.NONE);
		lblEnd.setText("End:");
		
		dateTimeEnd = new DateTime(composite, SWT.BORDER | SWT.TIME);
		dateTimeEnd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		time.setTimeInMillis(this.endTime);
		dateTimeEnd.setTime(time.get(Calendar.HOUR_OF_DAY)-1, time.get(Calendar.MINUTE), time.get(Calendar.SECOND));
		
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		return container;
	}
	
	


	@Override
	protected void okPressed() {
		
		startTime = dateTimeStart.getHours()*60L*60L+dateTimeStart.getMinutes()*60L + dateTimeStart.getSeconds();
		startTime *=1000L;
		endTime = dateTimeEnd.getHours()*60L*60L+dateTimeEnd.getMinutes()*60L + dateTimeEnd.getSeconds();
		endTime *=1000L;
		
		System.out.println("dateTimeStart:"+dateTimeStart.getHours());
		
		if(endTime <= startTime){
			this.setErrorMessage("The end time should be after the start time");
			return;
		}
		
		
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(377, 309);
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	
	
	

}
