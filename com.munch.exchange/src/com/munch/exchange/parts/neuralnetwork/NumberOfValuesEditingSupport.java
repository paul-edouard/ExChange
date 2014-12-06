package com.munch.exchange.parts.neuralnetwork;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.TimeSeries;
import com.munch.exchange.services.INeuralNetworkProvider;

public class NumberOfValuesEditingSupport extends EditingSupport {
	
	TreeViewer viewer;
	private INeuralNetworkProvider neuralNetworkProvider;
	private Stock stock;

	public NumberOfValuesEditingSupport(TreeViewer viewer,
			INeuralNetworkProvider neuralNetworkProvider,
			Stock stock) {
		super(viewer);
		this.viewer=viewer;
		this.neuralNetworkProvider=neuralNetworkProvider;
		this.stock=stock;
		
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		String[] nb = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		return new ComboBoxCellEditor(viewer.getTree(), nb);
	}

	@Override
	protected boolean canEdit(Object element) {
		return element instanceof TimeSeries;
	}

	@Override
	protected Object getValue(Object element) {
		if(element instanceof TimeSeries){
			TimeSeries el=(TimeSeries) element;
			return el.getNumberOfPastValues()-1;
		}
		return 0;
	}

	@Override
	protected void setValue(Object element, Object value) {
		TimeSeries el = (TimeSeries) element;
		Integer nb=(Integer) value;
		el.setNumberOfPastValues(nb+1);
		
		//neuralNetworkProvider.createAllInputPoints(stock);
		//stock.getNeuralNetwork().getConfiguration().inputNeuronChanged();
		
	    //viewer.update(element, null);
	    viewer.refresh();
	  }

	

}
