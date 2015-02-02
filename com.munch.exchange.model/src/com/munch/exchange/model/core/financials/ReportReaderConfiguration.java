package com.munch.exchange.model.core.financials;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.Parameter;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ReportReaderConfiguration extends XmlParameterElement {
	
	
	private static Logger logger = Logger.getLogger(ReportReaderConfiguration.class);
	
	static final String FIELD_Website="Website";
	static final String FIELD_SelectedPeriodType="SelectedPeriodType";
	
	static final String FIELD_QuaterlyReportWebsite="QuaterlyReportWebsite";
	static final String FIELD_AnnualyReportWebsite="AnnualyReportWebsite";
	
	static final String FIELD_QuaterlyPattern="QuaterlyPattern";
	static final String FIELD_AnnualyPattern="AnnualyPattern";
	
	static final String FIELD_QuaterlyDocuments="QuaterlyDocuments";
	static final String FIELD_AnnualyDocuments="AnnualyDocuments";
	
	static final String FIELD_QuaterlySearchPeriod="QuaterlySearchPeriod";
	static final String FIELD_AnnualySearchPeriod="AnnualySearchPeriod";
	
	static final String FIELD_QuaterlySearchPeriodActivated="QuaterlySearchPeriodActivated";
	static final String FIELD_AnnualySearchPeriodActivated="AnnualySearchPeriodActivated";
	
	public static final String FIELD_NextExpectedFinancialDate = "NextExpectedFinancialDate";
	
	
	private String website;
	//private String selectedPeriodType;
	
	private String quaterlyReportWebsite;
	private String annualyReportWebsite;
	
	private String quaterlyPattern;
	private String annualyPattern;
	
	private String quaterlySearchPeriod;
	private String annualySearchPeriod;
	
	private boolean quaterlySearchPeriodActivated;
	private boolean annualySearchPeriodActivated;
	
	private Calendar nextExpectedFinancialDate=null;
	
	private HashMap<String, Long> keyValueMap=new HashMap<String, Long>();
	
	
	public Calendar getNextExpectedFinancialDate() {
		return nextExpectedFinancialDate;
	}

	public void setNextExpectedFinancialDate(Calendar nextExpectedFinancialDate) {
		this.nextExpectedFinancialDate = nextExpectedFinancialDate;
	}
	
	
	public String getWebsite() {
		return website;
	}
	
	public String getSelectedPeriodType(){
		return this.getStringParam(FIELD_SelectedPeriodType);
	}
	public void setSelectedPeriodType(String selectedPeriodType){
		this.setParam(FIELD_SelectedPeriodType, selectedPeriodType);
	}
	
	public static String[] searchDocuments(String content, String pattern,String searchPeriod){
		LinkedList<String> docs=new LinkedList<String>();
		String[] tockens=content.split("\n");
		String[] ptockens=searchPeriod.split("-");
		
		for(int i=0;i<tockens.length;i++){
			String[] splits=tockens[i].split("\"");
			
			for(int j=0;j<splits.length;j++){
				//Test the searchPeriod
				boolean p_found=false;
				if(ptockens.length==0)p_found=true;
				if(ptockens.length==1)p_found=splits[j].contains(ptockens[0]);
				if(ptockens.length==2)p_found=(splits[j].contains(ptockens[0]) && splits[j].contains(ptockens[1]));
				
				if(!p_found)continue;
				
				
				//Test the pattern
				if(splits[j].matches(pattern)){
					if(!docs.contains(splits[j]))
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
	

	public String getQuaterlySearchPeriod() {
		return quaterlySearchPeriod;
	}

	public void setQuaterlySearchPeriod(String quaterlySearchPeriod) {
	changes.firePropertyChange(FIELD_QuaterlySearchPeriod, this.quaterlySearchPeriod, this.quaterlySearchPeriod = quaterlySearchPeriod);}
	

	public String getAnnualySearchPeriod() {
		return annualySearchPeriod;
	}

	public void setAnnualySearchPeriod(String annualySearchPeriod) {
	changes.firePropertyChange(FIELD_AnnualySearchPeriod, this.annualySearchPeriod, this.annualySearchPeriod = annualySearchPeriod);}
	

	public boolean isQuaterlySearchPeriodActivated() {
		return quaterlySearchPeriodActivated;
	}

	public void setQuaterlySearchPeriodActivated(boolean quaterlySearchPeriodActivated) {
	changes.firePropertyChange(FIELD_QuaterlySearchPeriodActivated, this.quaterlySearchPeriodActivated, this.quaterlySearchPeriodActivated = quaterlySearchPeriodActivated);}
	

	public boolean isAnnualySearchPeriodActivated() {
		return annualySearchPeriodActivated;
	}

	public void setAnnualySearchPeriodActivated(boolean annualySearchPeriodActivated) {
	changes.firePropertyChange(FIELD_AnnualySearchPeriodActivated, this.annualySearchPeriodActivated, this.annualySearchPeriodActivated = annualySearchPeriodActivated);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setWebsite((rootElement.getAttribute(FIELD_Website)));
		
		this.setAnnualyReportWebsite((rootElement.getAttribute(FIELD_AnnualyReportWebsite)));
		this.setQuaterlyReportWebsite((rootElement.getAttribute(FIELD_QuaterlyReportWebsite)));
		
		this.setAnnualyPattern((rootElement.getAttribute(FIELD_AnnualyPattern)));
		this.setQuaterlyPattern((rootElement.getAttribute(FIELD_QuaterlyPattern)));
		
		this.setAnnualySearchPeriod((rootElement.getAttribute(FIELD_AnnualySearchPeriod)));
		this.setQuaterlySearchPeriod((rootElement.getAttribute(FIELD_QuaterlySearchPeriod)));
		
		this.setAnnualySearchPeriodActivated(Boolean.parseBoolean(rootElement.getAttribute(FIELD_AnnualySearchPeriodActivated)));
		this.setQuaterlySearchPeriodActivated(Boolean.parseBoolean(rootElement.getAttribute(FIELD_QuaterlySearchPeriodActivated)));
		
		if(rootElement.hasAttribute(FIELD_NextExpectedFinancialDate)){
		this.setNextExpectedFinancialDate(DateTool.StringToDate(
				rootElement.getAttribute(FIELD_NextExpectedFinancialDate)));
		}
		
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
		
		rootElement.setAttribute(FIELD_AnnualySearchPeriod,this.getAnnualySearchPeriod());
		rootElement.setAttribute(FIELD_QuaterlySearchPeriod,this.getQuaterlySearchPeriod());
		
		rootElement.setAttribute(FIELD_AnnualySearchPeriodActivated,String.valueOf(this.isAnnualySearchPeriodActivated()));
		rootElement.setAttribute(FIELD_QuaterlySearchPeriodActivated,String.valueOf(this.isQuaterlySearchPeriodActivated()));
		
		rootElement.setAttribute(FIELD_NextExpectedFinancialDate,DateTool.dateToString(this.getNextExpectedFinancialDate()));
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub
	}
	
	private HashMap<String, SearchKeyValEl> getAllSearchKeyValEl(String periodType){
		HashMap<String, SearchKeyValEl> map=new HashMap<String, SearchKeyValEl>();
		
		for(Parameter param:this.getParameter().getChilds()){
			if(param.getKey().startsWith(periodType)){
				SearchKeyValEl el=new SearchKeyValEl(param.getKey(),(String)param.getValue());
				if(this.keyValueMap.containsKey(el.getKey())){
					el.value=this.keyValueMap.get(el.getKey());
				}
				map.put(el.fieldKey+"_"+el.sectorKey, el);
			}
		}
		
		return map;
	}
	
	public HashMap<String, SearchKeyValEl> getAllQuaterlySearchKeyValEl(){
		return getAllSearchKeyValEl(FinancialPoint.PeriodeTypeQuaterly);
	}
	public HashMap<String, SearchKeyValEl> getAllAnnualySearchKeyValEl(){
		return getAllSearchKeyValEl(FinancialPoint.PeriodeTypeAnnual);
	}
	
	public SearchKeyValEl getQuaterlySearchKeyValEl(String fieldKey,String sectorKey){
		HashMap<String, SearchKeyValEl> map=getAllQuaterlySearchKeyValEl();
		if(map.containsKey(fieldKey+"_"+sectorKey)){
			return map.get(fieldKey+"_"+sectorKey);
		}
		return new SearchKeyValEl(FinancialPoint.PeriodeTypeQuaterly,fieldKey,sectorKey);
	}
	
	public SearchKeyValEl getAnnualySearchKeyValEl(String fieldKey,String sectorKey){
		HashMap<String, SearchKeyValEl> map=getAllAnnualySearchKeyValEl();
		if(map.containsKey(fieldKey+"_"+sectorKey)){
			return map.get(fieldKey+"_"+sectorKey);
		}
		return new SearchKeyValEl(FinancialPoint.PeriodeTypeAnnual,fieldKey,sectorKey);
	}
	
	public void updateSearchKeyValEl(SearchKeyValEl el){
		this.setParam(el.getKey(), el.getContent());
	}
	
	private LinkedList<SearchKeyValEl> parseDocument(String document,String periodType){
		
		LinkedList<SearchKeyValEl> allFoundElts=new LinkedList<SearchKeyValEl>();
		
		this.keyValueMap.clear();
		HashMap<String, SearchKeyValEl> map =getAllSearchKeyValEl(periodType);
		if (map == null)
			return allFoundElts;

		for (String key : map.keySet()) {
			SearchKeyValEl el = map.get(key);
			if(el.searchValue(document)){
				allFoundElts.add(el);
				this.keyValueMap.put(el.getKey(), el.value);
			}
		}
		
		return allFoundElts;
	}
	
	public LinkedList<SearchKeyValEl> parseQuaterlyDocument(String document){
		return parseDocument(document,FinancialPoint.PeriodeTypeQuaterly);
	}
	public LinkedList<SearchKeyValEl> parseAnnualyDocument(String document){
		return parseDocument(document,FinancialPoint.PeriodeTypeAnnual);
	}
	
	
	//this.setParam(key, docs);
	
	//FinancialPoint.PeriodeTypeQuaterly
	
	public class SearchKeyValEl{
		public String fieldKey;
		public String sectorKey;
		public String periodType;
		
		public String activation="";
		public String startLineWith="";
		public int position=0;
		public int factor=1;
		
		public long value=Long.MIN_VALUE;
		private String foundString="";
		
		public SearchKeyValEl(String periodType,String fieldKey,String sectorKey){
			this.fieldKey=fieldKey;
			this.periodType=periodType;
			this.sectorKey=sectorKey;
		}
		
		public SearchKeyValEl(String key,String content){
			String[] keys=key.split("_");
			String[] contents=content.split(";");
			
			if(keys.length!=3)return;
			if(contents.length!=4)return;
			
			periodType=keys[0];
			fieldKey=keys[1];
			sectorKey=keys[2];
			
			activation=contents[0];
			startLineWith=contents[1];
			position=Integer.valueOf(contents[2]);
			factor=Integer.valueOf(contents[3]);
			
		}
		
		
		public String getKey(){
			return periodType+"_"+fieldKey+"_"+sectorKey;
		}

		public String getContent() {
			return activation+";"+startLineWith+";"+String.valueOf(position)+";"+String.valueOf(factor);
		}
		
		public boolean searchValue(String content){
			//TODO
			//logger.info("Start: "+this.toString());
			value=Long.MIN_VALUE;
			boolean isActivated=false;
			String[] allLines=content.split("\n");
			for(int i=0;i<allLines.length;i++){
				String line=allLines[i];
				if(line.contains(this.activation))isActivated=true;
				
				if(!isActivated)continue;
				
				logger.info("Activated: "+line);
				
				if(line.startsWith(this.startLineWith)){
					String newLine=line.replaceFirst(this.startLineWith, "");
					while(newLine.contains("  ")){
						newLine=newLine.replaceAll("  ", " ");
					}
					
					String[] tockens=newLine.split(" ");
					logger.info("Number of tockens: "+tockens.length);
					if(tockens.length>this.position && !tockens[this.position].isEmpty()){
						foundString=tockens[this.position];
						this.value=(long) (this.factor*Double.parseDouble(foundString.replace(",", "")));
					}
					isActivated=false;
				}
				
				if(!isActivated)break;
				
			}
			
			return value!=Long.MIN_VALUE;
			
		}

		@Override
		public String toString() {
			return "SearchKeyValEl [fieldKey=" + fieldKey + ", sectorKey="
					+ sectorKey + ", periodType=" + periodType
					+ ", activation=" + activation + ", startLineWith="
					+ startLineWith + ", position=" + position + ", factor="
					+ factor + ", value=" + value + ", foundString="
					+ foundString + "]";
		}
		
		
		
	}
	

}
