package com.munch.exchange.parts.chart;

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

import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.services.IBundleResourceLoader;


public class ChartTreeComposite extends Composite {
	
	
	private static Logger logger = Logger.getLogger(ChartTreeComposite.class);
	
	private Tree tree;
	private TreeViewer treeViewer;
	
	
	IBundleResourceLoader loader;
	
	protected ExchangeRate rate;
	
	
	
	
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
		addColumn("X",30,new ActivatedLabelProvider(), new ChartTreeActivatedEditingSupport(treeViewer));
		addColumn("C",30,new ColorLabelProvider(), new ChartTreeColorEditingSupport(treeViewer));
		
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
			return "";
		}
		
		@Override
		public Image getImage(Object element) {
			
			if(element instanceof ChartSerie){
				ChartSerie el=(ChartSerie) element;
				if(el.isActivated())
					return loader.loadImage(getClass(),IImageKeys.TICK );
				else
					return loader.loadImage(getClass(),IImageKeys.CANCEL );
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
	
	
	
	
	
	

}
