package com.munch.exchange.parts.chart.tree;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;

public class ChartTreeActivatedEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	ChartTreeComposite parent=null;
	ChartTreeEditorPart part=null;

	public ChartTreeActivatedEditingSupport(TreeViewer viewer,ChartTreeComposite parent) {
		super(viewer);
		this.viewer=viewer;
		this.parent=parent;
	}
	
	public ChartTreeActivatedEditingSupport(TreeViewer viewer,ChartTreeEditorPart part) {
		super(viewer);
		this.viewer=viewer;
		this.part=part;
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
		else if(element instanceof IbChartSerie){
			IbChartSerie serie=(IbChartSerie) element;
			return serie.getIndicator().isActivated();
		}
		else if(element instanceof IbChartIndicator){
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
		else if(element instanceof IbChartSerie){
			IbChartSerie el=(IbChartSerie) element;
			return el.isActivated();
		}
		else if(element instanceof IbChartIndicator){
			IbChartIndicator el=(IbChartIndicator) element;
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
			parent.getEventBroker().post(IEventConstant.CHART_INDICATOR_ACTIVATION_CHANGED, el);
		}
		else if(element instanceof IbChartSerie){
			IbChartSerie el=(IbChartSerie) element;
			el.setActivated((Boolean) value);
			//el.fireActivationChanged();
			part.getEventBroker().post(IEventConstant.IB_CHART_SERIE_ACTIVATION_CHANGED, el);
		}
		else if(element instanceof IbChartIndicator){
			IbChartIndicator el=(IbChartIndicator) element;
			el.setActivated((Boolean) value);
			//el.fireActivationChanged();
			part.getEventBroker().post(IEventConstant.IB_CHART_INDICATOR_ACTIVATION_CHANGED, el);
		}
		
		viewer.refresh();
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