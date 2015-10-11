package com.munch.exchange.parts.chart.tree;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;

public class ChartTreeColorEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	ChartTreeComposite parent=null;
	ChartTreeEditorPart part=null;
	
	public ChartTreeColorEditingSupport(TreeViewer viewer,ChartTreeComposite parent) {
		super(viewer);
		this.viewer=viewer;
		this.parent=parent;
	}
	public ChartTreeColorEditingSupport(TreeViewer viewer,ChartTreeEditorPart part) {
		super(viewer);
		this.viewer=viewer;
		this.part=part;
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
		else if(element instanceof IbChartSerie){
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
		else if(element instanceof IbChartSerie){
			IbChartSerie el=(IbChartSerie) element;
			RGB rgb=new RGB(el.getColor_R(), el.getColor_G(), el.getColor_B());
			return rgb;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if(element instanceof ChartSerie){
			ChartSerie serie=(ChartSerie)element;
			RGB color=(RGB) value;
			serie.getColor()[0]=color.red;
			serie.getColor()[1]=color.green;
			serie.getColor()[2]=color.blue;
		}
		else if(element instanceof IbChartSerie){
			IbChartSerie serie=(IbChartSerie) element;
			RGB color=(RGB) value;
			serie.setColor_R(color.red);
			serie.setColor_G(color.green);
			serie.setColor_B(color.blue);
			part.getEventBroker().post(IEventConstant.IB_CHART_SERIE_COLOR_CHANGED, serie);
		}
		
		viewer.update(element, null);
		
		if(parent!=null){
			parent.refresh();
			parent.setDity();
		}
		/*
		if(part!=null){
			part.refresh();
			part.setDity();
		}
		*/
	}

}
