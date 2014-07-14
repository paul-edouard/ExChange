package com.munch.exchange.parts.financials;

import java.util.Calendar;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;

public class StockFinancialsEditingSupport extends EditingSupport {

	  private final CellEditor editor;
	  private Calendar date;
	  private Stock stock;
	  private StockFinancials stockFinancials;

	  public StockFinancialsEditingSupport(StockFinancials stockFinancials, Stock stock, Calendar date) {
		  super(stockFinancials.getTreeViewer());
		  this.stockFinancials = stockFinancials;
		  this.date=date;
		  this.stock=stock;
		  this.editor = new TextCellEditor(this.stockFinancials.getTreeViewer().getTree());
	  }

	  @Override
	  protected CellEditor getCellEditor(Object element) {
	    return this.editor;
	  }

	  @Override
	  protected boolean canEdit(Object element) {
	    return true;
	  }

	  @Override
	  protected Object getValue(Object element) {
		FinancialElement entity = (FinancialElement) element;
		long val=stock.getFinancials().getValue(stockFinancials.getModus(),date, entity.fieldKey,entity.sectorKey);
		if(val==0)return "-";
		return String.valueOf(val);

	  }

	  @Override
	  protected void setValue(Object element, Object value) {
		FinancialElement entity = (FinancialElement) element;
	   //TODO
	  }
	} 
