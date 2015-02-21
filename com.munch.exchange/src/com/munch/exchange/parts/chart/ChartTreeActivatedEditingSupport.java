package com.munch.exchange.parts.chart;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.munch.exchange.model.core.chart.ChartSerie;

public class ChartTreeActivatedEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	

	public ChartTreeActivatedEditingSupport(TreeViewer viewer) {
		super(viewer);
		this.viewer=viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element instanceof ChartSerie){
			return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		if(element instanceof ChartSerie){
			ChartSerie el=(ChartSerie) element;
			return el.isActivated();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		ChartSerie el=(ChartSerie) element;
		el.setActivated((Boolean) value);
		
		 viewer.update(element, null);
	}

}