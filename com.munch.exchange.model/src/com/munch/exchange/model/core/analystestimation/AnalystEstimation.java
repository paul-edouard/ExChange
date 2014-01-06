package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class AnalystEstimation extends XmlParameterElement {
	
	
	static final String FIELD_EarningsHistory="EarningsHistory";
	static final String FIELD_GrowthEst="GrowthEst";
	static final String FIELD_RevenueEst="RevenueEst";
	static final String FIELD_EPSTrends="EPSTrends";
	static final String FIELD_EPSRevisions="EPSRevisions";
	static final String FIELD_EarningsEst="EarningsEst";
	
	
	private EarningsHistory EarningsHistory=new EarningsHistory();
	private GrowthEst GrowthEst=new GrowthEst();
	private RevenueEst RevenueEst=new RevenueEst();
	private EPSTrends EPSTrends=new EPSTrends();
	private EPSRevisions EPSRevisions=new EPSRevisions();
	private EarningsEst EarningsEst=new EarningsEst();
	


	public EarningsHistory getEarningsHistory() {
		return EarningsHistory;
	}

	public void setEarningsHistory(EarningsHistory earningsHistory) {
		changes.firePropertyChange(FIELD_EarningsHistory, this.EarningsHistory,
				this.EarningsHistory = earningsHistory);
	}

	public GrowthEst getGrowthEst() {
		return GrowthEst;
	}

	public void setGrowthEst(GrowthEst growthEst) {
		changes.firePropertyChange(FIELD_GrowthEst, this.GrowthEst,
				this.GrowthEst = growthEst);
	}

	public RevenueEst getRevenueEst() {
		return RevenueEst;
	}

	public void setRevenueEst(RevenueEst revenueEst) {
		changes.firePropertyChange(FIELD_RevenueEst, this.RevenueEst,
				this.RevenueEst = revenueEst);
	}

	public EPSTrends getEPSTrends() {
		return EPSTrends;
	}

	public void setEPSTrends(EPSTrends ePSTrends) {
		changes.firePropertyChange(FIELD_EPSTrends, this.EPSTrends,
				this.EPSTrends = ePSTrends);
	}

	public EPSRevisions getEPSRevisions() {
		return EPSRevisions;
	}

	public void setEPSRevisions(EPSRevisions ePSRevisions) {
		changes.firePropertyChange(FIELD_EPSRevisions, this.EPSRevisions,
				this.EPSRevisions = ePSRevisions);
	}

	public EarningsEst getEarningsEst() {
		return EarningsEst;
	}

	public void setEarningsEst(EarningsEst earningsEst) {
		changes.firePropertyChange(FIELD_EarningsEst, this.EarningsEst,
				this.EarningsEst = earningsEst);
	}

	@Override
	public String toString() {
		return "AnalystEstimation [EarningsHistory=" + EarningsHistory
				+ ", GrowthEst=" + GrowthEst + ", RevenueEst=" + RevenueEst
				+ ", EPSTrends=" + EPSTrends + ", EPSRevisions=" + EPSRevisions
				+ ", EarningsEst=" + EarningsEst + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		EarningsHistory earningsHistory=new EarningsHistory();
		if(childElement.getTagName().equals(earningsHistory.getTagName())){
			earningsHistory.init(childElement);
			this.setEarningsHistory(earningsHistory);
		}
		
		GrowthEst growthEst=new GrowthEst();
		if(childElement.getTagName().equals(growthEst.getTagName())){
			growthEst.init(childElement);
			this.setGrowthEst(growthEst);
		}
		
		RevenueEst revenueEst=new RevenueEst();
		if(childElement.getTagName().equals(revenueEst.getTagName())){
			revenueEst.init(childElement);
			this.setRevenueEst(revenueEst);
		}
		
		EPSTrends ePSTrends=new EPSTrends();
		if(childElement.getTagName().equals(ePSTrends.getTagName())){
			ePSTrends.init(childElement);
			this.setEPSTrends(ePSTrends);
		}
		
		EPSRevisions ePSRevisions=new EPSRevisions();
		if(childElement.getTagName().equals(ePSRevisions.getTagName())){
			ePSRevisions.init(childElement);
			this.setEPSRevisions(ePSRevisions);
		}
		
		EarningsEst earningsEst=new EarningsEst();
		if(childElement.getTagName().equals(earningsEst.getTagName())){
			earningsEst.init(childElement);
			this.setEarningsEst(earningsEst);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getEarningsHistory().toDomElement(doc));
		rootElement.appendChild(this.getGrowthEst().toDomElement(doc));
		rootElement.appendChild(this.getRevenueEst().toDomElement(doc));
		rootElement.appendChild(this.getEPSTrends().toDomElement(doc));
		rootElement.appendChild(this.getEPSRevisions().toDomElement(doc));
		rootElement.appendChild(this.getEarningsEst().toDomElement(doc));

	}

}
