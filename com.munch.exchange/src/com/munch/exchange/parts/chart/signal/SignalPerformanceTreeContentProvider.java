package com.munch.exchange.parts.chart.signal;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.ib.statistics.PerformanceMetrics;
import com.munch.exchange.model.core.ib.statistics.RevenueStatistics;
import com.munch.exchange.model.core.ib.statistics.StabilityStatistics;
import com.munch.exchange.model.core.ib.statistics.TimeStatistics;
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
		if(parentElement instanceof TimeStatistics){
			TimeStatistics p=(TimeStatistics) parentElement;
			return p.getChildren();
		}
		if(parentElement instanceof StabilityStatistics){
			StabilityStatistics p=(StabilityStatistics) parentElement;
			return p.getChildren();
		}
		if(parentElement instanceof RevenueStatistics){
			RevenueStatistics p=(RevenueStatistics) parentElement;
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
				|| inputElement instanceof TradeStatistics
				|| inputElement instanceof TimeStatistics
				|| inputElement instanceof StabilityStatistics
				|| inputElement instanceof RevenueStatistics){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	
	
	
	
	
	

}
