package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class WatchListPart {

	public WatchListPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite compositeHeader = new Composite(parent, SWT.NONE);
		compositeHeader.setLayout(new GridLayout(2, false));
		compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Combo comboWachtlist = new Combo(compositeHeader, SWT.NONE);
		comboWachtlist.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		DateTime dateTimeWatchPeriod = new DateTime(compositeHeader, SWT.BORDER | SWT.DROP_DOWN | SWT.LONG);
		
		TreeViewer treeViewer = new TreeViewer(parent, SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL);
		Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn trclmnName = treeViewerColumn.getColumn();
		trclmnName.setWidth(100);
		trclmnName.setText("Name");
		
		//Add Drop Support
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
	    treeViewer.addDropSupport(operations, transferTypes, new WatchlistTreeViewerDropAdapter(treeViewer));
	    

		
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}

}
