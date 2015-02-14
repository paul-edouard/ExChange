package com.munch.exchange.parts.financials;

import java.util.Calendar;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration;
import com.munch.exchange.model.core.financials.ReportReaderConfiguration.SearchKeyValEl;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;

public class FinancialReportEditingSupport extends EditingSupport {
	
	
	public static final String FIELD_Activation="Activation";
	public static final String FIELD_StartLineWith="StartLineWith";
	public static final String FIELD_Position="Position";
	public static final String FIELD_Factor="Factor";
	public static final String FIELD_EffectiveDate="Effective Date";
	
	
	private final CellEditor editor;
	FinancialReportParserComposite financialReportParserComposite;
	String modus;
	
	public FinancialReportEditingSupport(FinancialReportParserComposite f,String modus) {
		super(f.getTreeViewer());
		this.financialReportParserComposite=f;
		this.editor = new TextCellEditor(this.financialReportParserComposite.getTreeViewer().getTree());
		this.modus=modus;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return this.editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		FinancialElement entity = (FinancialElement) element;
		if(modus.equals(FIELD_EffectiveDate)){
			return entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate);
		}
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		FinancialElement entity = (FinancialElement) element;
		if(modus.equals(FIELD_EffectiveDate)){
			if(entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate)){
				Stock stock=financialReportParserComposite.getStock();
				ReportReaderConfiguration config=financialReportParserComposite.getConfig();
				String savedValue=DateTool.dateToString(stock.getFinancials().getEffectiveDate(config.getSelectedPeriod()));
				return savedValue;
			}
		}
		
		
		SearchKeyValEl el=null;
		if(financialReportParserComposite.getBtnQuaterly().getSelection()){
			el=financialReportParserComposite.getConfig().getQuaterlySearchKeyValEl(entity.fieldKey, entity.sectorKey);
		}
		else{
			el=financialReportParserComposite.getConfig().getAnnualySearchKeyValEl(entity.fieldKey, entity.sectorKey);
		}
		
		if(el==null)return "";
		
		if(modus.equals(FIELD_Activation)){
			return el.activation;
		}
		else if(modus.equals(FIELD_StartLineWith)){
			return el.startLineWith;
		}
		else if(modus.equals(FIELD_Position)){
			return String.valueOf(el.position);
		}
		else if(modus.equals(FIELD_Factor)){
			return String.valueOf(el.factor);
		}
		
		return "";
	}

	@Override
	protected void setValue(Object element, Object value) {
		
		FinancialElement entity = (FinancialElement) element;
		
		if(modus.equals(FIELD_EffectiveDate)){
			if(entity.fieldKey.equals(FinancialPoint.FIELD_EffectiveDate)){
				Stock stock=financialReportParserComposite.getStock();
				ReportReaderConfiguration config=financialReportParserComposite.getConfig();
				Calendar date= DateTool.StringToDate((String) value);
				if(date==null)return;
				stock.getFinancials().setEffectiveDate(config.getSelectedPeriod(),date);
				
				this.financialReportParserComposite.getTreeViewer().refresh();
				return;
			}
		}
		
		
		
		SearchKeyValEl el=null;
		if(financialReportParserComposite.getBtnQuaterly().getSelection()){
			el=financialReportParserComposite.getConfig().getQuaterlySearchKeyValEl(entity.fieldKey, entity.sectorKey);
		}
		else{
			el=financialReportParserComposite.getConfig().getAnnualySearchKeyValEl(entity.fieldKey, entity.sectorKey);
		}
		
		if(el==null)return;
		
		if(modus.equals(FIELD_Activation)){
			el.activation=(String) value;
		}
		else if(modus.equals(FIELD_StartLineWith)){
			el.startLineWith=(String) value;
		}
		else if(modus.equals(FIELD_Position)){
			el.position=Integer.valueOf((String) value);
		}
		else if(modus.equals(FIELD_Factor)){
			el.factor=Integer.valueOf((String) value);
		}
		financialReportParserComposite.getConfig().updateSearchKeyValEl(el);
		
		this.financialReportParserComposite.getTreeViewer().refresh();

	}

}
