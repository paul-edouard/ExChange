package com.munch.exchange.parts.chart;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartSerie;

public class ChartTreeActivatedEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	ChartTreeComposite parent;

	public ChartTreeActivatedEditingSupport(TreeViewer viewer,ChartTreeComposite parent) {
		super(viewer);
		this.viewer=viewer;
		this.parent=parent;
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
		else if(element instanceof ChartIndicator){
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
		else if(element instanceof ChartIndicator){
			ChartIndicator el=(ChartIndicator) element;
			return el.isActivated();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if(element instanceof ChartSerie){
			ChartSerie el=(ChartSerie) element;
			el.setActivated((Boolean) value);
		}
		else if(element instanceof ChartIndicator){
			ChartIndicator el=(ChartIndicator) element;
			el.setActivated((Boolean) value);
		}
		
		 viewer.update(element, null);
		 parent.refresh();
	}

}