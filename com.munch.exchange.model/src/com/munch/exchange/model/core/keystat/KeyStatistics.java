package com.munch.exchange.model.core.keystat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class KeyStatistics extends XmlParameterElement {
	
	static final String FIELD_ValuationMeasures="ValuationMeasures";
	static final String FIELD_FinancialHighlights="FinancialHighlights";
	static final String FIELD_TradingInformation="TradingInformation";
	
	
	protected KeyStatMap ValuationMeasures=new KeyStatMap(FIELD_ValuationMeasures);
	protected KeyStatMap FinancialHighlights=new KeyStatMap(FIELD_FinancialHighlights);
	protected KeyStatMap TradingInformation=new KeyStatMap(FIELD_TradingInformation);
	
	

	public KeyStatMap getValuationMeasures() {
		return ValuationMeasures;
	}

	public void setValuationMeasures(KeyStatMap valuationMeasures) {
		changes.firePropertyChange(FIELD_ValuationMeasures, this.ValuationMeasures,
				this.ValuationMeasures = valuationMeasures);
	}

	public KeyStatMap getFinancialHighlights() {
		return FinancialHighlights;
	}

	public void setFinancialHighlights(KeyStatMap financialHighlights) {
		changes.firePropertyChange(FIELD_FinancialHighlights,
				this.FinancialHighlights,
				this.FinancialHighlights = financialHighlights);
	}

	public KeyStatMap getTradingInformation() {
		return TradingInformation;
	}

	public void setTradingInformation(KeyStatMap tradingInformation) {
		changes.firePropertyChange(FIELD_TradingInformation,
				this.TradingInformation,
				this.TradingInformation = tradingInformation);
	}

	@Override
	protected void initAttribute(Element rootElement) {
		// TODO Auto-generated method stub
		

	}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_ValuationMeasures)){
			this.setValuationMeasures(new KeyStatMap(FIELD_ValuationMeasures,childElement));
		}
		if(childElement.getTagName().equals(FIELD_FinancialHighlights)){
			this.setValuationMeasures(new KeyStatMap(FIELD_FinancialHighlights,childElement));
		}
		if(childElement.getTagName().equals(FIELD_TradingInformation)){
			this.setValuationMeasures(new KeyStatMap(FIELD_TradingInformation,childElement));
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		rootElement.appendChild(this.getFinancialHighlights().toDomElement(doc));
		rootElement.appendChild(this.getTradingInformation().toDomElement(doc));
		rootElement.appendChild(this.getValuationMeasures().toDomElement(doc));

	}

}
