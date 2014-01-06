package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;
import com.munch.exchange.model.xml.XmlParameterElement;

public class EarningsHistory extends XmlParameterElement {
	
	
	static final String FIELD_Difference="Difference";
	static final String FIELD_Surprise="Surprise";
	static final String FIELD_EPSEst="EPSEst";
	static final String FIELD_EPSActual="EPSActual";
	
	private EstimationPointList Difference=new EstimationPointList(FIELD_Difference);
	private EstimationPointList Surprise=new EstimationPointList(FIELD_Surprise);
	private EstimationPointList EPSEst=new EstimationPointList(FIELD_EPSEst);
	private EstimationPointList EPSActual=new EstimationPointList(FIELD_EPSActual);
	
	
	private class EstimationPointList extends DatePointList<EstimationPoint>{

		private static final long serialVersionUID = -8818279385417606669L;
		
		public EstimationPointList(String tagName) {
			super(tagName);
		}

		@Override
		protected DatePoint createPoint() {
			return new EstimationPoint();
		}
	}
	

	public EstimationPointList getDifference() {
		return Difference;
	}

	public void setDifference(EstimationPointList difference) {
		changes.firePropertyChange(FIELD_Difference, this.Difference,
				this.Difference = difference);
	}

	public EstimationPointList getSurprise() {
		return Surprise;
	}

	public void setSurprise(EstimationPointList surprise) {
		changes.firePropertyChange(FIELD_Surprise, this.Surprise,
				this.Surprise = surprise);
	}

	public EstimationPointList getEPSEst() {
		return EPSEst;
	}

	public void setEPSEst(EstimationPointList ePSEst) {
		changes.firePropertyChange(FIELD_EPSEst, this.EPSEst, this.EPSEst = ePSEst);
	}

	public EstimationPointList getEPSActual() {
		return EPSActual;
	}

	public void setEPSActual(EstimationPointList ePSActual) {
		changes.firePropertyChange(FIELD_EPSActual, this.EPSActual,
				this.EPSActual = ePSActual);
	}

	
	@Override
	public String toString() {
		return "EarningsHistory [Difference=" + Difference + ", Surprise="
				+ Surprise + ", EPSEst=" + EPSEst + ", EPSActual=" + EPSActual
				+ "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		
		EstimationPointList difference=new EstimationPointList(FIELD_Difference);
		if(childElement.getTagName().equals(difference.getTagName())){
			difference.init(childElement);
			this.setDifference(difference);
		}
		
		EstimationPointList surprise=new EstimationPointList(FIELD_Surprise);
		if(childElement.getTagName().equals(surprise.getTagName())){
			surprise.init(childElement);
			this.setSurprise(surprise);
		}
		
		EstimationPointList ePSEst=new EstimationPointList(FIELD_EPSEst);
		if(childElement.getTagName().equals(ePSEst.getTagName())){
			ePSEst.init(childElement);
			this.setEPSEst(ePSEst);
		}
		
		EstimationPointList ePSActual=new EstimationPointList(FIELD_EPSActual);
		if(childElement.getTagName().equals(ePSActual.getTagName())){
			ePSActual.init(childElement);
			this.setEPSActual(ePSActual);
		}
	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getDifference().toDomElement(doc));
		rootElement.appendChild(this.getSurprise().toDomElement(doc));
		rootElement.appendChild(this.getEPSEst().toDomElement(doc));
		rootElement.appendChild(this.getEPSActual().toDomElement(doc));

	}

}
