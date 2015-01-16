package com.munch.exchange.parts.neuralnetwork.data;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;
import com.munch.exchange.services.INeuralNetworkProvider;

public class TimeLeftEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	private INeuralNetworkProvider neuralNetworkProvider;
	private Stock stock;

	public TimeLeftEditingSupport(TreeViewer viewer,
			INeuralNetworkProvider neuralNetworkProvider,
			Stock stock) {
		super(viewer);
		this.viewer=viewer;
		this.neuralNetworkProvider=neuralNetworkProvider;
		this.stock=stock;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		 return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
	}

	@Override
	protected boolean canEdit(Object element) {
		return element instanceof TimeSeries;
	}

	@Override
	protected Object getValue(Object element) {
		if(element instanceof TimeSeries){
			TimeSeries el=(TimeSeries) element;
			return el.isTimeRemainingActivated();
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {
		TimeSeries el = (TimeSeries) element;
		el.setTimeRemainingActivated((Boolean) value);
		
	    viewer.update(element, null);

	}

}
