package com.munch.exchange.parts.financials;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.munch.exchange.parts.financials.StockFinancialsContentProvider.FinancialElement;

public class mainColumnLabelProvider extends ColumnLabelProvider{
	public Image getImage(Object element) {
		return null;
	}
	public String getText(Object element) {
		if(element instanceof FinancialElement){
			//System.out.println("Financial element:"+ element.toString());
			FinancialElement entity=(FinancialElement) element;
			return entity.name;
		}
		//System.out.println("No Financial element?"+ element.toString());
		return element == null ? "" : element.toString();
	}
}
