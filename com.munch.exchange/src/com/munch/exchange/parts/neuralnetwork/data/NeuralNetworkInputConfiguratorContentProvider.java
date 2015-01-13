package com.munch.exchange.parts.neuralnetwork.data;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.TimeSeries;
import com.munch.exchange.model.core.neuralnetwork.TimeSeriesCategory;

public class NeuralNetworkInputConfiguratorContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	
	private static Logger logger = Logger.getLogger(NeuralNetworkInputConfiguratorContentProvider.class);
	
	private NeuralNetworkSerieCategory root=
			new NeuralNetworkSerieCategory(null, TimeSeriesCategory.ROOT);
	
	//private Stock stock;
	
	private NeuralNetworkSerieCategory rateCategory;
	private NeuralNetworkSerieCategory financialCategory;
	private NeuralNetworkSerieCategory OutputCategory;
	
	public NeuralNetworkInputConfiguratorContentProvider(){
		//this.stock=stock;
		
		buildNeuralNetworkSerieCategories();
		refreshCategories(null);
	}
	
	public NeuralNetworkSerieCategory getRoot() {
		return root;
	}

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
		if(parentElement instanceof NeuralNetworkSerieCategory){
			NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory)parentElement;
			return el.childs.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof NeuralNetworkSerieCategory){
			NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory)element;
			return el.parent;
		}
		else if(element instanceof TimeSeries){
			TimeSeries el=(TimeSeries)element;
			return el.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof NeuralNetworkSerieCategory){
			NeuralNetworkSerieCategory el=(NeuralNetworkSerieCategory)element;
			return el.childs.size()>0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof NeuralNetworkSerieCategory){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	private void buildNeuralNetworkSerieCategories(){
		rateCategory=new NeuralNetworkSerieCategory(this.root,TimeSeriesCategory.RATE);
		financialCategory=new NeuralNetworkSerieCategory(this.root,TimeSeriesCategory.FINANCIAL);
		OutputCategory=new NeuralNetworkSerieCategory(this.root,TimeSeriesCategory.TARGET_OUTPUT);
	}
	
	public void refreshCategories(Configuration config){
		if(config==null)return;
		
		rateCategory.childs.clear();
		financialCategory.childs.clear();
		OutputCategory.childs.clear();
		
		for(TimeSeries series:config.getAllTimeSeries()){
			switch (series.getCategory()) {
			case RATE:
				rateCategory.addChild(series);
				break;
			case FINANCIAL:
				financialCategory.addChild(series);
				break;
			case TARGET_OUTPUT:
				OutputCategory.addChild(series);
				break;

			default:
				break;
			}
		}
		
		//logger.info("******* Number of childs: "+rateCategory.childs.size());
		
	}
	
	public class NeuralNetworkSerieCategory{
		
		public NeuralNetworkSerieCategory parent;
		public LinkedList<Object> childs=new LinkedList<Object>();
		
		public TimeSeriesCategory name;
		
		public NeuralNetworkSerieCategory(NeuralNetworkSerieCategory parent,TimeSeriesCategory name){
			this.name=name;
			this.parent=parent;
			if(this.parent!=null)
				this.parent.childs.add(this);
		}
		
		
		
		public void addChild(Object obj){
			if(obj instanceof TimeSeries){
				TimeSeries ts=(TimeSeries) obj;
				ts.setParent(this);
			}
			childs.add(obj);
		}
		
		public void remove(Object obj){
			if(obj instanceof TimeSeries){
				TimeSeries ts=(TimeSeries) obj;
				ts.setParent(null);
			}
			childs.remove(obj);
		}
		
		@Override
		public String toString() {
			return "FinancialElement [name=" + name + "]";
		}
		
	}

}
