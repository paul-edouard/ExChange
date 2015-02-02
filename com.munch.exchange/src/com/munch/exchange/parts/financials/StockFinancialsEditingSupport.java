package com.munch.exchange.parts.financials;

import java.util.Calendar;

import javax.print.attribute.standard.Finishings;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.CashFlowPoint;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.tool.DateTool;
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
		FinancialElement entity = (FinancialElement) element;
		if (stockFinancials.getModus().equals(
				FinancialPoint.PeriodeTypeQuaterly)) {
			if (date.get(Calendar.MONTH) > 9 && date.get(Calendar.MONTH) <= 12) {
				return false;
			}
		}
		return true;
	}

	  String savedValue="";
	  
	  @Override
	  protected Object getValue(Object element) {
		FinancialElement entity = (FinancialElement) element;
		if(entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate)){
			savedValue=DateTool.dateToDayString(stock.getFinancials().getEffectiveDate(stockFinancials.getModus(),date));
			return savedValue;
		}
		else{
			long val=stock.getFinancials().getValue(stockFinancials.getModus(),date, entity.fieldKey,entity.sectorKey);
			if(val==0)savedValue="-";
			else
				savedValue=stockFinancials.getStringOfValue(val,entity.fieldKey);
			return savedValue;
		}
	  }

	  @Override
	  protected void setValue(Object element, Object value) {
		FinancialElement entity = (FinancialElement) element;
		String value_str=(String) value;
		//if(value_str.equals(savedValue))return;
		
		if(entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate)){
			Calendar effDate=DateTool.StringToDay(value_str);
			if(effDate==null)return;
			stock.getFinancials().setEffectiveDate(stockFinancials.getModus(),date, effDate);
			setQ4EffectiveDate(effDate);
		}
		else{
			long val=stockFinancials.getValueOfString(value_str,entity.fieldKey);
			stock.getFinancials().setValue(stockFinancials.getModus(),date, entity.fieldKey,entity.sectorKey,val);
			
			
			specialSettingRules(val,stockFinancials.getModus(),date, entity.fieldKey,entity.sectorKey);
			
			setQ4Employees(val,entity.fieldKey);
			setQ4OutstandingShares(val,entity.fieldKey);
			setQ4Values(val,entity.fieldKey,entity.sectorKey);
		}
		
		this.stockFinancials.getTreeViewer().refresh();
		this.stockFinancials.getDirty().setDirty(true);
		
		//this.stockFinancials.getBtnCancel().setEnabled(true);
		//this.stockFinancials.getBtnSave().setEnabled(true);
		
		
	  }
	  
	  private void setQ4Values(long value,String key,String sectorKey){
		  if(key.equals(IncomeStatementPoint.FIELD_Employees))return;
		  if(key.equals(IncomeStatementPoint.FIELD_OutstandingShares))return;
		  if(key.equals(IncomeStatementPoint.FIELD_EarningsPerShare))return;
		  
		  if(!stockFinancials.getModus().equals(FinancialPoint.PeriodeTypeAnnual))return;
		  
		  
		  Calendar q1Date=stock.getFinancials().getQ1Date(date.get(Calendar.YEAR));
		  Calendar q2Date=stock.getFinancials().getQ2Date(date.get(Calendar.YEAR));
		  Calendar q3Date=stock.getFinancials().getQ3Date(date.get(Calendar.YEAR));
		  Calendar q4Date=stock.getFinancials().getQ4Date(date.get(Calendar.YEAR));
		  
		  if(q1Date==null || q2Date==null || q3Date==null || q4Date==null )return;
		  
		  
		  long val1=stock.getFinancials().getValue(FinancialPoint.PeriodeTypeQuaterly,q1Date, key,sectorKey);
		  long val2=stock.getFinancials().getValue(FinancialPoint.PeriodeTypeQuaterly,q2Date, key,sectorKey);
		  long val3=stock.getFinancials().getValue(FinancialPoint.PeriodeTypeQuaterly,q3Date, key,sectorKey);
		  
		  if(val1==Long.MIN_VALUE || val2==Long.MIN_VALUE || val3==Long.MIN_VALUE)return;
		  
		  long val4=value-(val1+val2+val3);
			
		  stock.getFinancials().setValue(FinancialPoint.PeriodeTypeQuaterly,date, key,sectorKey,val4);
		  
		  if(key.equals(IncomeStatementPoint.FIELD_NetIncome) && sectorKey.equals(Financials.FIELD_IncomeStatement)){
			  long OutstandingShares=stock.getFinancials().getValue(FinancialPoint.PeriodeTypeQuaterly, q4Date,
						 IncomeStatementPoint.FIELD_OutstandingShares, Financials.FIELD_IncomeStatement);
			  long netIncome=stock.getFinancials().getValue(FinancialPoint.PeriodeTypeQuaterly, q4Date,
						 IncomeStatementPoint.FIELD_NetIncome, Financials.FIELD_IncomeStatement);
			  
			  if(OutstandingShares==Long.MIN_VALUE || netIncome==Long.MIN_VALUE)return;
				 
			long erPerShare=(netIncome*100)/OutstandingShares;
			  
			stock.getFinancials().setValue(FinancialPoint.PeriodeTypeQuaterly,q4Date,
					 IncomeStatementPoint.FIELD_EarningsPerShare,Financials.FIELD_IncomeStatement,erPerShare);
			  
			  
		  }
		  
		  // IncomeStatementPoint.FIELD_NetIncome,Financials.FIELD_IncomeStatement
	  }
	  
	  
	  private void setQ4OutstandingShares(long value,String key){
		  
		  if(!key.equals(IncomeStatementPoint.FIELD_OutstandingShares))return;
		  
		  if(!stockFinancials.getModus().equals(FinancialPoint.PeriodeTypeAnnual))return;
		  
		  Calendar q4Date=stock.getFinancials().getQ4Date(date.get(Calendar.YEAR));
		  if(q4Date==null)return;
		  
		  stock.getFinancials().setValue(FinancialPoint.PeriodeTypeQuaterly,q4Date,
					 IncomeStatementPoint.FIELD_OutstandingShares,Financials.FIELD_IncomeStatement,value);
		  
	  }
	  
	  
	  private void setQ4Employees(long value,String key){
		  
		  if(!key.equals(IncomeStatementPoint.FIELD_Employees))return;
		  
		  if(!stockFinancials.getModus().equals(FinancialPoint.PeriodeTypeAnnual))return;
		  
		  Calendar q4Date=stock.getFinancials().getQ4Date(date.get(Calendar.YEAR));
		  if(q4Date==null)return;
		  
		  stock.getFinancials().setValue(FinancialPoint.PeriodeTypeQuaterly,q4Date,
					 IncomeStatementPoint.FIELD_Employees,Financials.FIELD_IncomeStatement,value);
	  }
	  
	  
	  private void setQ4EffectiveDate(Calendar effDate){
		  if(!stockFinancials.getModus().equals(FinancialPoint.PeriodeTypeAnnual))return;
		  
		  Calendar q4Date=stock.getFinancials().getQ4Date(date.get(Calendar.YEAR));
		  if(q4Date==null)return;
		  
		  stock.getFinancials().setEffectiveDate(FinancialPoint.PeriodeTypeQuaterly,q4Date, effDate);
		  
		  
	  }
	  
	  
	  
	  private void specialSettingRules(long value, String modus,Calendar date ,String fieldKey, String sectorKey){
		  
		  if(fieldKey.equals(IncomeStatementPoint.FIELD_OutstandingShares) && 
				  sectorKey.equals(Financials.FIELD_IncomeStatement) ){
			  
			 long erPerShare=stock.getFinancials().getValue(modus, date,
					 IncomeStatementPoint.FIELD_EarningsPerShare, Financials.FIELD_IncomeStatement);
			 
			 if(erPerShare==Long.MIN_VALUE)return;
			 
			 if(!MessageDialog.openQuestion(stockFinancials.getShell(),
					 "Update Net Income", "Do you want to update the net income?"))return;
			 
			 
			 long netIncome=erPerShare*value/100;
			 
			 
			 stock.getFinancials().setValue(stockFinancials.getModus(),date,
					 IncomeStatementPoint.FIELD_NetIncome,Financials.FIELD_IncomeStatement,netIncome);
			 stock.getFinancials().setValue(stockFinancials.getModus(),date,
					 CashFlowPoint.FIELD_NetIncome,Financials.FIELD_CashFlow,netIncome);
		  }
		  
		  
		  
		  
		  if(fieldKey.equals(IncomeStatementPoint.FIELD_NetIncome) && 
				  sectorKey.equals(Financials.FIELD_IncomeStatement) ){
			  stock.getFinancials().setValue(stockFinancials.getModus(),date,
						 CashFlowPoint.FIELD_NetIncome,Financials.FIELD_CashFlow,value);
			  
			  long erPerShare=stock.getFinancials().getValue(modus, date,
						 IncomeStatementPoint.FIELD_EarningsPerShare, Financials.FIELD_IncomeStatement);
				 
			  if(erPerShare==Long.MIN_VALUE)return;
				 
			  if(!MessageDialog.openQuestion(stockFinancials.getShell(),
						 "Update Net Income", "Do you want to update the Outstanding Shares?"))return;
			  
			  long OutstandingShares=100*value/erPerShare;
			  stock.getFinancials().setValue(stockFinancials.getModus(),date,
						 IncomeStatementPoint.FIELD_OutstandingShares,Financials.FIELD_IncomeStatement,OutstandingShares);
			  
		  }
		  
	  }
	  
	  
	} 
