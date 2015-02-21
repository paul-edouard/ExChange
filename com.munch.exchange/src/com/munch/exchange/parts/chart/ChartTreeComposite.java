package com.munch.exchange.parts.chart;

import java.awt.BasicStroke;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeriesCollection;

import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.parts.composite.CollectionRemovedListener;
import com.munch.exchange.services.IBundleResourceLoader;


public class ChartTreeComposite extends Composite {
	
	
	private static Logger logger = Logger.getLogger(ChartTreeComposite.class);
	
	private Tree tree;
	private TreeViewer treeViewer;
	
	
	IBundleResourceLoader loader;
	
	protected ExchangeRate rate;
	
	
	//the period
	private int[] period=new int[2];
		
	//The renderers
	protected XYLineAndShapeRenderer mainPlotRenderer;
	protected XYLineAndShapeRenderer secondPlotrenderer;
	protected XYLineAndShapeRenderer percentPlotrenderer;
	protected XYErrorRenderer errorPlotRenderer;
	protected DeviationRenderer deviationPercentPlotRenderer;
	protected DeviationRenderer deviationRenderer ;
					
	//The Series Collections
	protected XYSeriesCollection mainCollection;
	protected XYSeriesCollection secondCollection;
	protected XYSeriesCollection percentCollection;
	protected YIntervalSeriesCollection errorCollection;
	protected YIntervalSeriesCollection deviationPercentCollection;
	protected YIntervalSeriesCollection deviationCollection;
	
	
	
	
	@Inject
	public ChartTreeComposite(Composite parent,ExchangeRate rate,IBundleResourceLoader loader) {
		super(parent, SWT.NONE);
		
		this.rate=rate;
		this.loader=loader;
		
		setLayout(new GridLayout(1, false));
		treeViewer = new TreeViewer(this,  SWT.BORDER| SWT.MULTI
				| SWT.V_SCROLL );
		
		treeViewer.setContentProvider(new ChartTreeContentProvider());
		treeViewer.setInput(this.rate.getIndicatorGroup());
		treeViewer.setAutoExpandLevel(1);
		
		ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE); 
		
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		
		addColumn("Name",300,new NameLabelProvider());
		addColumn("X",30,new ActivatedLabelProvider(), new ChartTreeActivatedEditingSupport(treeViewer,this));
		addColumn("C",30,new ColorLabelProvider(), new ChartTreeColorEditingSupport(treeViewer,this));
		
		treeViewer.refresh();
		
	}
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider,EditingSupport editingSupport){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		treeViewerColumn.setEditingSupport(editingSupport);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		//trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	private TreeColumn addColumn(String columnName, int width, CellLabelProvider cellLabelProvider){
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(cellLabelProvider);
		TreeColumn trclmnId = treeViewerColumn.getColumn();
		trclmnId.setWidth(width);
		trclmnId.setText(columnName);
		//trclmnId.addSelectionListener(getSelectionAdapter(trclmnId, columnId));
		
		return trclmnId;
	}
	
	
	public void setRenderers(XYLineAndShapeRenderer mainPlotRenderer,
			XYLineAndShapeRenderer secondPlotrenderer,
			XYLineAndShapeRenderer percentPlotrenderer,
			XYErrorRenderer errorPlotRenderer,
			DeviationRenderer deviationPercentPlotRenderer,
			DeviationRenderer deviationRenderer){
		this.mainPlotRenderer=mainPlotRenderer;
		this.secondPlotrenderer=secondPlotrenderer;
		this.percentPlotrenderer=percentPlotrenderer;
		this.errorPlotRenderer=errorPlotRenderer;
		this.deviationPercentPlotRenderer=deviationPercentPlotRenderer;
		this.deviationRenderer=deviationRenderer;
	}
	
	public void setSeriesCollections(XYSeriesCollection mainCollection,
			XYSeriesCollection secondCollection,
			XYSeriesCollection percentCollection,
			YIntervalSeriesCollection errorCollection,
			YIntervalSeriesCollection deviationPercentCollection,
			YIntervalSeriesCollection deviationCollection){
		this.mainCollection=mainCollection;
		this.secondCollection=secondCollection;
		this.percentCollection=percentCollection;
		this.errorCollection=errorCollection;
		this.deviationPercentCollection=deviationPercentCollection;
		this.deviationCollection=deviationCollection;
	}

	public void setPeriod(int[] period){
		this.period=period;
		
		refresh();
	}
	
	public void refresh() {
		clearSeries();
		
		createSeries();
	}
	
	
	//################################
	//##     Series operations      ##
	//################################
	
	private void createSeries(){
		for(ChartSerie serie:searchSeriesToAdd(this.rate.getIndicatorGroup()))
			addSerie(serie);
	}
	
	private void addSerie(ChartSerie serie){
		XYSeries xySerie=createXYSerie(serie);
		int pos=0;
		
		switch (serie.getRendererType()) {
		case MAIN:
			mainCollection.addSeries(xySerie);
			pos=mainCollection.indexOf(serie.getName());
			if(pos>=0){
				mainPlotRenderer.setSeriesShapesVisible(pos, false);
				mainPlotRenderer.setSeriesLinesVisible(pos, true);
				mainPlotRenderer.setSeriesStroke(pos,new BasicStroke(2.0f));
				mainPlotRenderer.setSeriesPaint(pos, new java.awt.Color(serie.getColor()[0], serie.getColor()[1], serie.getColor()[2]));
			}
			break;
		case SECOND:
			pos=secondCollection.indexOf(serie.getName());
			if(pos>=0)secondCollection.removeSeries(pos);
			break;
		case PERCENT:
			pos=percentCollection.indexOf(serie.getName());
			if(pos>=0)percentCollection.removeSeries(pos);
			break;
		case ERROR:
			pos=errorCollection.indexOf(serie.getName());
			if(pos>=0)errorCollection.removeSeries(pos);
			break;
		case DEVIATION:
			pos=deviationCollection.indexOf(serie.getName());
			if(pos>=0)deviationCollection.removeSeries(pos);
			break;
		case DEVIATION_PERCENT:
			pos=deviationPercentCollection.indexOf(serie.getName());
			if(pos>=0)deviationPercentCollection.removeSeries(pos);
			break;
		default:
			break;
		}
		
		
	}
	
	private LinkedList<ChartSerie> searchSeriesToAdd(ChartIndicatorGroup group){
		LinkedList<ChartSerie> toAddList=new LinkedList<ChartSerie>();
		if(group==null)return toAddList;
		
		for(ChartIndicatorGroup subGroup:group.getSubGroups()){
			toAddList.addAll(searchSeriesToAdd(subGroup));
		}
		
		for(ChartIndicator indicator:group.getIndicators()){
			if(!indicator.isActivated())continue;
			
			//Compute the series
			indicator.compute(rate.getHistoricalData());
			
			for(ChartSerie serie:indicator.getChartSeries()){
				if(serie.isActivated() && serie.getValues()!=null){
					toAddList.add(serie);
				}
			}
		}
		return toAddList;
		
	}
	
	private XYSeries  createXYSerie(ChartSerie serie){
		logger.info("Serie Name: "+serie.getName());
		
		
		XYSeries r_series =new XYSeries(serie.getName());
		int pos=1;
		for(int i=0;i<serie.getValues().length;i++){
			if(i>=period[0] && i<period[1]){
				r_series.add(pos,serie.getValues()[i]);
				pos++;
			}
		}
		
		return 	r_series;
	}
	
	
	
	
	private void clearSeries(){
		for(ChartSerie serie:searchSeriesToRemove(this.rate.getIndicatorGroup()))
			removeChartSerie(serie);
	}
	
	private LinkedList<ChartSerie> searchSeriesToRemove(ChartIndicatorGroup group){
		LinkedList<ChartSerie> toRemoveList=new LinkedList<ChartSerie>();
		
		
		if(group==null)return toRemoveList;
		
		
		for(ChartIndicatorGroup subGroup:group.getSubGroups()){
			toRemoveList.addAll(searchSeriesToRemove(subGroup));
		}
		
		for(ChartIndicator indicator:group.getIndicators()){
			for(ChartSerie serie:indicator.getChartSeries()){
				//if(!serie.isActivated()){
					toRemoveList.add(serie);
				//}
			}
		}
		return toRemoveList;
		
	}
	
	private void removeChartSerie(ChartSerie serie){
		int pos=0;
		switch (serie.getRendererType()) {
		case MAIN:
			pos=mainCollection.indexOf(serie.getName());
			if(pos>=0)mainCollection.removeSeries(pos);
			break;
		case SECOND:
			pos=secondCollection.indexOf(serie.getName());
			if(pos>=0)secondCollection.removeSeries(pos);
			break;
		case PERCENT:
			pos=percentCollection.indexOf(serie.getName());
			if(pos>=0)percentCollection.removeSeries(pos);
			break;
		case ERROR:
			pos=errorCollection.indexOf(serie.getName());
			if(pos>=0)errorCollection.removeSeries(pos);
			break;
		case DEVIATION:
			pos=deviationCollection.indexOf(serie.getName());
			if(pos>=0)deviationCollection.removeSeries(pos);
			break;
		case DEVIATION_PERCENT:
			pos=deviationPercentCollection.indexOf(serie.getName());
			if(pos>=0)deviationPercentCollection.removeSeries(pos);
			break;

		default:
			break;
		}
		
		
	}
	
	
	//################################
	//##     ColumnLabelProvider    ##
	//################################
	
	class NameLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			
			//logger.info("Ele class: "+element.getClass());
			
			if(element instanceof ChartIndicatorGroup){
				ChartIndicatorGroup el=(ChartIndicatorGroup) element;
				return el.getName();
			}
			else if(element instanceof ChartIndicator){
				ChartIndicator el=(ChartIndicator) element;
				return el.getName();
			}
			else if(element instanceof ChartSerie){
				ChartSerie el=(ChartSerie) element;
				return el.getName();
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object element) {
			if(element instanceof ChartIndicatorGroup){
				return loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_STOCKS );
			}
			else if(element instanceof ChartIndicator){
				return loader.loadImage(getClass(),IImageKeys.RATE_STOCK );
			}
			else if(element instanceof ChartSerie){
				return loader.loadImage(getClass(),IImageKeys.RATE_INDICE );
			}
			return super.getImage(element);
		}
		
		
		
	}
	
	class ActivatedLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			/*
			if(element instanceof ChartIndicatorGroup){
				return "";
			}
			else if(element instanceof ChartIndicator){
				//ChartIndicator el=(ChartIndicator) element;
				return "";
			}
			else if(element instanceof ChartSerie){
				ChartSerie el=(ChartSerie) element;
				if(el.isActivated())
					return "X";
			}
			*/
			return null;
		}
		
		@Override
		public Image getImage(Object element) {
			
			if(element instanceof ChartSerie){
				ChartSerie el=(ChartSerie) element;
				if(el.isActivated())
					return loader.loadImage(getClass(),IImageKeys.CHECKED );
				else
					return loader.loadImage(getClass(),IImageKeys.UNCHECKED );
			}
			return super.getImage(element);
		}
		
		
		
	}
	
	class ColorLabelProvider extends ColumnLabelProvider{

		@Override
		public String getText(Object element) {
			if(element instanceof ChartSerie){
			return "C";
			}
			return "";
		}

		@Override
		public Color getBackground(Object element) {
			if(element instanceof ChartSerie){
				ChartSerie el=(ChartSerie) element;
				Color color=new Color(null,
						el.getColor()[0], 
						el.getColor()[1],
						el.getColor()[2]);
				
				return color;
			}
			
			return super.getBackground(element);
		}

		@Override
		public Color getForeground(Object element) {
			if(element instanceof ChartSerie){
				ChartSerie el=(ChartSerie) element;
				Color color=new Color(null,
						el.getColor()[0], 
						el.getColor()[1],
						el.getColor()[2]);
				
				return color;
			}
			return super.getForeground(element);
		}
		
		
		
	}
	
	
	
	
	// ///////////////////////////
	// // LISTERNER ////
	// ///////////////////////////
	private List<CollectionRemovedListener> listeners = new LinkedList<CollectionRemovedListener>();

	public void addCollectionRemovedListener(CollectionRemovedListener l) {
			listeners.add(l);
	}

	public void removeCollectionRemovedListener(CollectionRemovedListener l) {
			listeners.remove(l);
	}

	public void fireCollectionRemoved() {
			for (CollectionRemovedListener l : listeners)
				l.CollectionRemoved();
	}
	

}
