package com.munch.exchange.parts.financials;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

import com.munch.exchange.model.core.financials.ReportReaderConfiguration.SearchKeyValEl;
import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;

public class FinancialReportEditingSupport extends EditingSupport {
	
	
	public static final String FIELD_Activation="Activation";
	public static final String FIELD_StartLineWith="StartLineWith";
	public static final String FIELD_Position="Position";
	public static final String FIELD_Factor="Factor";
	
	
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
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		FinancialElement entity = (FinancialElement) element;
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
