package com.munch.exchange.parts.chart.tree;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.munch.exchange.model.core.chart.ChartSerie;

public class ChartTreeColorEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	ChartTreeComposite parent;
	
	
	public ChartTreeColorEditingSupport(TreeViewer viewer,ChartTreeComposite parent) {
		super(viewer);
		this.viewer=viewer;
		this.parent=parent;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new ColorCellEditor(viewer.getTree());
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
			RGB rgb=new RGB(el.getColor()[0], el.getColor()[1], el.getColor()[2]);
			return rgb;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		ChartSerie serie=(ChartSerie)element;
		RGB color=(RGB) value;
		serie.getColor()[0]=color.red;
		serie.getColor()[1]=color.green;
		serie.getColor()[2]=color.blue;
		
		viewer.update(element, null);
		parent.refresh();
		parent.setDity();
	}

}
