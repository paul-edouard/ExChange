package com.munch.exchange.parts.financials;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class FinancialReportParserComposite extends Composite {
	private Text textCompanyWebsite;

	public FinancialReportParserComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setSashWidth(4);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeLeft = new Composite(sashForm, SWT.NONE);
		compositeLeft.setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(compositeLeft, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(2, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		compositeHeader.setBounds(0, 0, 64, 64);
		
		Label lblCompany = new Label(compositeHeader, SWT.NONE);
		lblCompany.setSize(100, 15);
		lblCompany.setText("Company Website:");
		new Label(compositeHeader, SWT.NONE);
		
		textCompanyWebsite = new Text(compositeHeader, SWT.BORDER);
		textCompanyWebsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textCompanyWebsite.setSize(138, 21);
		
		Button buttonCompWeb = new Button(compositeHeader, SWT.NONE);
		buttonCompWeb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		buttonCompWeb.setText(">");
		
		StyledText styledText = new StyledText(compositeLeft, SWT.BORDER);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new GridLayout(1, false));
		
		TreeViewer treeViewer = new TreeViewer(compositeRight, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setBounds(0, 0, 85, 85);
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnItem = treeViewerColumn.getColumn();
		trclmnItem.setWidth(100);
		trclmnItem.setText("Item");
		sashForm.setWeights(new int[] {266, 271});
		// TODO Auto-generated constructor stub
	}
}
