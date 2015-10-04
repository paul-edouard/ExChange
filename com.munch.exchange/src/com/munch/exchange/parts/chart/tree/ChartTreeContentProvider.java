package com.munch.exchange.parts.chart.tree;

import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;

public class ChartTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

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
		if(parentElement instanceof ChartIndicatorGroup){
			ChartIndicatorGroup group=(ChartIndicatorGroup) parentElement;
			LinkedList<Object> children=new LinkedList<Object>();
			children.addAll(group.getSubGroups());
			children.addAll(group.getIndicators());
			return children.toArray();
		}
		else if(parentElement instanceof ChartIndicator){
			ChartIndicator indicator=(ChartIndicator) parentElement;
			return indicator.getChartSeries().toArray();
		}
		else if(parentElement instanceof IbChartIndicatorGroup){
			IbChartIndicatorGroup group=(IbChartIndicatorGroup) parentElement;
			LinkedList<Object> children=new LinkedList<Object>();
			children.addAll(group.getChildren());
			children.addAll(group.getIndicators());
			return children.toArray();
		}
		else if(parentElement instanceof IbChartIndicator){
			IbChartIndicator indicator=(IbChartIndicator) parentElement;
			return indicator.getSeries().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof ChartIndicatorGroup){
			ChartIndicatorGroup group=(ChartIndicatorGroup) element;
			return group.getSubGroups().size()>0 || group.getIndicators().size()>0;
		}
		else if(element instanceof ChartIndicator){
			ChartIndicator indicator=(ChartIndicator) element;
			return indicator.getChartSeries().size()>0;
		}
		else if(element instanceof IbChartIndicatorGroup){
			IbChartIndicatorGroup group=(IbChartIndicatorGroup) element;
			return group.getChildren().size()>0 || group.getIndicators().size()>0;
		}
		else if(element instanceof IbChartIndicator){
			IbChartIndicator indicator=(IbChartIndicator) element;
			return indicator.getSeries().size()>0;
		}
		
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof ChartIndicatorGroup 
				|| inputElement instanceof ChartIndicator
				|| inputElement instanceof IbChartIndicator
				|| inputElement instanceof IbChartIndicatorGroup){
			return this.getChildren(inputElement);
		}
		return null;
	}

}
