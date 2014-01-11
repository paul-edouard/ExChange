package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EPSRevisions extends XmlParameterElement {
	
	
	static final String FIELD_UpLast7Days="UpLast7Days";
	static final String FIELD_UpLast30Days="UpLast30Days";
	static final String FIELD_DownLast30Days="DownLast30Days";
	static final String FIELD_DownLast90Days="DownLast90Days";
	
	
	private Estimation UpLast7Days=new Estimation(FIELD_UpLast7Days);
	private Estimation UpLast30Days=new Estimation(FIELD_UpLast30Days);
	private Estimation DownLast30Days=new Estimation(FIELD_DownLast30Days);
	private Estimation DownLast90Days=new Estimation(FIELD_DownLast90Days);
	
	public boolean update(EPSRevisions other){
		boolean isUpdated=false;
		if(!this.getTagName().equals(other.getTagName()))
			return isUpdated;
		
		//System.out.println(this);
		//System.out.println(other);
		
		if(this.getUpLast7Days().update(other.getUpLast7Days()))
			isUpdated=true;
		if(this.getUpLast30Days().update(other.getUpLast30Days()))
			isUpdated=true;
		if(this.getDownLast30Days().update(other.getDownLast30Days()))
			isUpdated=true;
		if(this.getDownLast90Days().update(other.getDownLast90Days()))
			isUpdated=true;
		
		//System.out.println("IsUpdated: "+isUpdated);
		
		return isUpdated;
		
	}
	
	public Estimation getUpLast7Days() {
		return UpLast7Days;
	}

	public void setUpLast7Days(Estimation upLast7Days) {
		changes.firePropertyChange(FIELD_UpLast7Days, this.UpLast7Days,
				this.UpLast7Days = upLast7Days);
	}

	public Estimation getUpLast30Days() {
		return UpLast30Days;
	}

	public void setUpLast30Days(Estimation upLast30Days) {
		changes.firePropertyChange(FIELD_UpLast30Days, this.UpLast30Days,
				this.UpLast30Days = upLast30Days);
	}

	public Estimation getDownLast30Days() {
		return DownLast30Days;
	}

	public void setDownLast30Days(Estimation downLast30Days) {
		changes.firePropertyChange(FIELD_DownLast30Days, this.DownLast30Days,
				this.DownLast30Days = downLast30Days);
	}

	public Estimation getDownLast90Days() {
		return DownLast90Days;
	}

	public void setDownLast90Days(Estimation downLast90Days) {
		changes.firePropertyChange(FIELD_DownLast90Days, this.DownLast90Days,
				this.DownLast90Days = downLast90Days);
	}

	@Override
	public String toString() {
		return "EPSRevisions [UpLast7Days=" + UpLast7Days + ", UpLast30Days="
				+ UpLast30Days + ", DownLast30Days=" + DownLast30Days
				+ ", DownLast90Days=" + DownLast90Days + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_UpLast7Days)){
			this.setUpLast7Days(new Estimation(FIELD_UpLast7Days,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_UpLast30Days)){
			this.setUpLast30Days(new Estimation(FIELD_UpLast30Days,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_DownLast30Days)){
			this.setDownLast30Days(new Estimation(FIELD_DownLast30Days,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_DownLast90Days)){
			this.setDownLast90Days(new Estimation(FIELD_DownLast90Days,childElement));
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getUpLast7Days().toDomElement(doc));
		rootElement.appendChild(this.getUpLast30Days().toDomElement(doc));
		rootElement.appendChild(this.getDownLast30Days().toDomElement(doc));
		rootElement.appendChild(this.getDownLast90Days().toDomElement(doc));

	}

}
