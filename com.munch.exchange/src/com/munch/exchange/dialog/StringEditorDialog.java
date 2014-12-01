package com.munch.exchange.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class StringEditorDialog extends Dialog {
	private Text text;
	private Label lblNewString;
	private String oldString=null;
	private String titel=null;
	private String newString=null;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public StringEditorDialog(Shell parentShell, String titel, String oldString) {
		super(parentShell);
		//this.getShell().setText(titel);
		this.titel=titel;
		this.oldString=oldString;
	}
	
	/*
	public StringEditorDialog(Shell parentShell, String titel) {
		super(parentShell);
		this.titel=titel;
		this.getShell().setText(titel);
	}
	*/

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		lblNewString = new Label(container, SWT.NONE);
		lblNewString.setText("New String:");
		if(titel!=null)
			lblNewString.setText("Enter the new "+ titel+ " name:");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if(oldString!=null)
			text.setText(oldString);

		return container;
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
	
	
	
	

	@Override
	protected void okPressed() {
		
		if(oldString==null || !oldString.equals(text.getText())){
			newString=text.getText();
		}
		
		
		super.okPressed();
	}

	public String getNewString() {
		return newString;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(496, 228);
	}

}
