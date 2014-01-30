package com.munch.exchange.parts.composite;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.model.core.ExchangeRate;

public class RateTitle extends Composite {
	
	@Inject
	ExchangeRate rate;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RateTitle(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label lblFulleName = new Label(this, SWT.NONE);
		lblFulleName.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFulleName.setBackground(SWTResourceManager.getColor(0, 0, 255));
		lblFulleName.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblFulleName.setText("New Label");
		
		Label lblQuote = new Label(this, SWT.NONE);
		lblQuote.setText("New Label");
		
		

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
