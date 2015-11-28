package com.munch.exchange.parts.chart.performance;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import com.munch.exchange.model.core.ib.statistics.TradeStatistics;

public class SignalPerformanceTreeContentProvider implements IStructuredContentProvider,
ITreeContentProvider{

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof PerformanceMetrics){
			PerformanceMetrics p=(PerformanceMetrics) parentElement;
			return p.getChildren();
		}
		if(parentElement instanceof TradeStatistics){
			TradeStatistics p=(TradeStatistics) parentElement;
			return p.getChildren();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children=this.getChildren(element);
		
		if(children!=null)
			return children.length>0;
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof PerformanceMetrics 
				|| inputElement instanceof TradeStatistics){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	
	
	
	
	
	

}
