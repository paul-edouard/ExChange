package com.munch.exchange.model.core.financials;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.financials.Period.PeriodType;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.Parameter;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ReportReaderConfiguration extends XmlParameterElement {
	
	
	private static Logger logger = Logger.getLogger(ReportReaderConfiguration.class);
	
	static final String FIELD_Website="Website";
	//static final String FIELD_SelectedPeriodType="SelectedPeriodType";
	static final String FIELD_SelectedPeriod="SelectedPeriod";
	
	static final String FIELD_QuaterlyReportWebsite="QuaterlyReportWebsite";
	//static final String FIELD_QuaterlyReportWebsites="QuaterlyReportWebsites";
	static final String FIELD_AnnualyReportWebsite="AnnualyReportWebsite";
	//static final String FIELD_AnnualyReportWebsites="AnnualyReportWebsites";
	
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
	
	//private String quaterlyReportWebsite;
	private LinkedList<String> quaterlyReportWebsites=new LinkedList<String>();
	
	//private String annualyReportWebsite;
	private LinkedList<String> annualyReportWebsites=new LinkedList<String>();
	
	
	private String quaterlyPattern;
	private String annualyPattern;
	
	private String quaterlySearchPeriod;
	private String annualySearchPeriod;
	
	private boolean quaterlySearchPeriodActivated;
	private boolean annualySearchPeriodActivated;
	
	private Calendar nextExpectedFinancialDate=null;
	private Period selectedPeriod=null;
	
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
	
	
	
	
	public static LinkedList<String> searchDocuments(String content, String pattern,String searchPeriod){
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
				if(ptockens.length==2)
					p_found=(splits[j].contains(ptockens[0]) && splits[j].contains(ptockens[1])) || 
					(splits[j].contains(ptockens[0].toLowerCase()) && splits[j].contains(ptockens[1]));
				
				if(!p_found)continue;
				
				
				//Test the pattern
				if(splits[j].matches(pattern)){
					if(!docs.contains(splits[j]))
						docs.add(splits[j]);
					
				}
			}
		}
		
		return docs;
		
		/*
		String[] docl=new String[docs.size()];
		for(int i=0;i<docs.size();i++){
			docl[i]=docs.get(i);
		}
		
		return docl;
		*/
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
	
	
	//****************************************
	//***        Getter and Setter        ****
	//****************************************
	
	
	public Period getSelectedPeriod() {
		if(selectedPeriod==null){
			selectedPeriod=new Period();
		}
		return selectedPeriod;
	}

	public void setSelectedPeriod(Period selectedPeriod) {
	changes.firePropertyChange(FIELD_SelectedPeriod, this.selectedPeriod, this.selectedPeriod = selectedPeriod);}
	

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
	
	
	public void setQuaterlyReportWebsites(LinkedList<String> quaterlyReportWebsites) {
	changes.firePropertyChange(FIELD_QuaterlyReportWebsite, this.quaterlyReportWebsites, this.quaterlyReportWebsites = quaterlyReportWebsites);}
	

	public LinkedList<String> getReportWebsites() {
		if(this.selectedPeriod.getType()==PeriodType.ANNUAL)
			return annualyReportWebsites;
		else{
			return quaterlyReportWebsites;
		}
		
	}

	public void setAnnualyReportWebsites(LinkedList<String> annualyReportWebsites) {
	changes.firePropertyChange(FIELD_AnnualyReportWebsite, this.annualyReportWebsites, this.annualyReportWebsites = annualyReportWebsites);}
	
	
	
	//****************************************
	//***            SearchKeyValEl       ****
	//****************************************
	

	

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
	
	public SearchKeyValEl getSearchKeyValEl(String periodType,String fieldKey,String sectorKey){
		HashMap<String, SearchKeyValEl> map=getAllSearchKeyValEl(periodType);
		if(map.containsKey(fieldKey+"_"+sectorKey)){
			return map.get(fieldKey+"_"+sectorKey);
		}
		return new SearchKeyValEl(periodType,fieldKey,sectorKey);
	}
	
	public void updateSearchKeyValEl(SearchKeyValEl el){
		this.setParam(el.getKey(), el.getContent());
	}
	
	
	//****************************************
	//***            PARSE Document       ****
	//****************************************
	
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
	
	
	public class SearchKeyValEl{
		public String fieldKey;
		public String sectorKey;
		public String periodType;
		
		public String activation="";
		public String startLineWith="";
		public int position=0;
		public int factor=1;
		public boolean wasCalculated=false;
		
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
			
			
			
			
			value=Long.MIN_VALUE;
			boolean isActivated=false;
			
			
			
			String[] allLines=content.split("\n");
			for(int i=0;i<allLines.length;i++){
				String line=allLines[i];
				if(this.activation.isEmpty()){
					break;
				}
				
				if(line.contains(this.activation)){
					isActivated=true;
				}
				
				if(!isActivated)continue;
				
				// logger.info("Activated: "+line);
				String[] startLineTockens = startLineWith.split(":");
				for (int j = 0; j < startLineTockens.length; j++) {

					if (line.startsWith(startLineTockens[j])) {
						// logger.info("Activated: "+line);

						String newLine = line.replaceFirst(this.startLineWith,
								"");
						while (newLine.contains("  ")) {
							newLine = newLine.replaceAll("  ", " ");
						}

						String[] tockens = newLine.split(" ");
						LinkedList<Long> longs = getLongsFromTockens(tockens);
						// logger.info("Number of tockens: "+tockens.length);
						if (longs.size() > this.position) {
							this.value = longs.get(this.position);
							// foundString=string.v;

						}
						isActivated = false;
					}
				}
				
				if(!isActivated)break;
				
			}
			
			return value!=Long.MIN_VALUE;
			
		}
		
		
		
		
		private LinkedList<Long> getLongsFromTockens(String[] tockens){
			LinkedList<Long> longs=new LinkedList<Long>();
			for(int i=0;i<tockens.length;i++){
				long value=Long.MIN_VALUE;
				
				try{
					String val_str=tockens[i];
					val_str=val_str.replace("(", "-").replace(")", "");
					if(val_str.contains(".") && val_str.contains(",")){
						if(val_str.indexOf(",")<val_str.indexOf(".")){
							val_str=val_str.replace(",", "");
						}
						else{
							val_str=val_str.replace(".", "");
							val_str=val_str.replace(",", ".");
						}
						
					}
					else if(val_str.contains(",")){
						val_str=val_str.replace(",", ".");
					}
					
					value=(long) (this.factor*Double.parseDouble(val_str));
				}
				catch(Exception e){
					continue;
				}
				
				if(value!=Long.MIN_VALUE){
					longs.add(value);
				}
				
			}
			
			
			return longs;
			
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
	
	
	
	//****************************************
	//***             XML                 ****
	//****************************************
	
	@Override
	protected void initAttribute(Element rootElement) {
		this.setWebsite((rootElement.getAttribute(FIELD_Website)));
		
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
		
		
		
		quaterlyReportWebsites.clear();
		annualyReportWebsites.clear();
		
	}

	@Override
	protected void initChild(Element childElement) {
		Period p=new Period();
		
		if(childElement.getTagName().equals(p.getTagName())){
			p.init(childElement);
			this.setSelectedPeriod(p);
		}
		else if(childElement.getTagName().equals(FIELD_AnnualyReportWebsite)){
			annualyReportWebsites.add(childElement.getTextContent());
		}
		else if(childElement.getTagName().equals(FIELD_QuaterlyReportWebsite)){
			quaterlyReportWebsites.add(childElement.getTextContent());
		}
	}
	
	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_Website,this.getWebsite());
		
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
		rootElement.appendChild(this.getSelectedPeriod().toDomElement(doc));
		
		for(String site:annualyReportWebsites){
			Element el=doc.createElement(FIELD_AnnualyReportWebsite);
			el.setTextContent(site);
			rootElement.appendChild(el);
		}
		
		for(String site:quaterlyReportWebsites){
			Element el=doc.createElement(FIELD_QuaterlyReportWebsite);
			el.setTextContent(site);
			rootElement.appendChild(el);
		}
		
		
	}
	
	
	
	
	
}
