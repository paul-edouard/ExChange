package com.munch.exchange.model.core.financials;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class ReportReaderConfiguration extends XmlParameterElement {
	
	
	static final String FIELD_Website="Website";
	static final String FIELD_SelectedPeriodType="SelectedPeriodType";
	
	static final String FIELD_QuaterlyReportWebsite="QuaterlyReportWebsite";
	static final String FIELD_AnnualyReportWebsite="AnnualyReportWebsite";
	
	static final String FIELD_QuaterlyPattern="QuaterlyPattern";
	static final String FIELD_AnnualyPattern="AnnualyPattern";
	
	static final String FIELD_QuaterlyDocuments="QuaterlyDocuments";
	static final String FIELD_AnnualyDocuments="AnnualyDocuments";
	
	
	private String website;
	//private String selectedPeriodType;
	
	private String quaterlyReportWebsite;
	private String annualyReportWebsite;
	
	private String quaterlyPattern;
	private String annualyPattern;
	
	
	
	public String getWebsite() {
		return website;
	}
	
	public String getSelectedPeriodType(){
		return this.getStringParam(FIELD_SelectedPeriodType);
	}
	public void setSelectedPeriodType(String selectedPeriodType){
		this.setParam(FIELD_SelectedPeriodType, selectedPeriodType);
	}
	
	public static String[] searchDocuments(String content, String pattern){
		LinkedList<String> docs=new LinkedList<String>();
		String[] tockens=content.split("\n");
		for(int i=0;i<tockens.length;i++){
			String[] splits=tockens[i].split("\"");
			
			for(int j=0;j<splits.length;j++){
				if(splits[j].matches(pattern)){
					docs.add(splits[j]);
					
				}
			}
		}
		String[] docl=new String[docs.size()];
		for(int i=0;i<docs.size();i++){
			docl[i]=docs.get(i);
		}
		
		return docl;
		
	}
	
	
	private String[] getLastDocuments(String key){
		String docs=this.getStringParam(key);
		return docs.split(";");
	}
	
	public String[] getLastQuaterlyDocuments(){
		return getLastDocuments(FIELD_QuaterlyDocuments);
	}
	public String[] getLastAnnualyDocuments(){
		return getLastDocuments(FIELD_AnnualyDocuments);
	}
	
	public void setLastDocuments(String[] documents,String key){
		String docs="";
		for(int i =0;i<documents.length;i++){
			if(i==docs.length()-1)
				docs+=documents[i];
			else
				docs+=documents[i]+";";
		}
		this.setParam(key, docs);
	}
	
	public void setLastQuaterlyDocuments(String[] documents){
		setLastDocuments(documents,FIELD_QuaterlyDocuments);
	}
	public void setLastAnnualyDocuments(String[] documents){
		setLastDocuments(documents,FIELD_AnnualyDocuments);
	}
	
	
	public String getQuaterlyReportWebsite() {
		return quaterlyReportWebsite;
	}

	public void setQuaterlyReportWebsite(String quaterlyReportWebsite) {
	changes.firePropertyChange(FIELD_QuaterlyReportWebsite, this.quaterlyReportWebsite, this.quaterlyReportWebsite = quaterlyReportWebsite);}
	

	public String getAnnualyReportWebsite() {
		return annualyReportWebsite;
	}

	public void setAnnualyReportWebsite(String annualyReportWebsite) {
	changes.firePropertyChange(FIELD_AnnualyReportWebsite, this.annualyReportWebsite, this.annualyReportWebsite = annualyReportWebsite);}
	

	public void setWebsite(String website) {
	changes.firePropertyChange(FIELD_Website, this.website, this.website = website);}
	
	

	public String getQuaterlyPattern() {
		return quaterlyPattern;
	}

	public void setQuaterlyPattern(String quaterlyPattern) {
	changes.firePropertyChange(FIELD_QuaterlyPattern, this.quaterlyPattern, this.quaterlyPattern = quaterlyPattern);}
	

	public String getAnnualyPattern() {
		return annualyPattern;
	}

	public void setAnnualyPattern(String annualyPattern) {
	changes.firePropertyChange(FIELD_AnnualyPattern, this.annualyPattern, this.annualyPattern = annualyPattern);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setWebsite((rootElement.getAttribute(FIELD_Website)));
		
		this.setAnnualyReportWebsite((rootElement.getAttribute(FIELD_AnnualyReportWebsite)));
		this.setQuaterlyReportWebsite((rootElement.getAttribute(FIELD_QuaterlyReportWebsite)));
		
		this.setAnnualyPattern((rootElement.getAttribute(FIELD_AnnualyPattern)));
		this.setQuaterlyPattern((rootElement.getAttribute(FIELD_QuaterlyPattern)));
		
		
	}

	@Override
	protected void initChild(Element childElement) {
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_Website,this.getWebsite());
		
		rootElement.setAttribute(FIELD_AnnualyReportWebsite,this.getAnnualyReportWebsite());
		rootElement.setAttribute(FIELD_QuaterlyReportWebsite,this.getQuaterlyReportWebsite());
		
		rootElement.setAttribute(FIELD_AnnualyPattern,this.getAnnualyPattern());
		rootElement.setAttribute(FIELD_QuaterlyPattern,this.getQuaterlyPattern());
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub
		
	}

}
