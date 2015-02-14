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
	static final String FIELD_UsePeriod="UsePeriod";
	
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
	public static final String FIELD_DocumentType="DocumentType";
	
	public static final String DocumentType_PDF="PDF";
	public static final String DocumentType_TXT="TXT";
	public static final String DocumentType_CSV="CSV";
	public static final String DocumentType_XLS="XLS";
	
	
	
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
	private boolean usePeriod=true;
	
	private String DocumentType=DocumentType_PDF;
	
	private HashMap<String, Long> keyValueMap=new HashMap<String, Long>();
	
	
	private LinkedList<SearchKeyValEl> allFoundElts=null;
	private String documentContent;
	
	
	
	public Calendar getNextExpectedFinancialDate() {
		return nextExpectedFinancialDate;
	}

	public void setNextExpectedFinancialDate(Calendar nextExpectedFinancialDate) {
		this.nextExpectedFinancialDate = nextExpectedFinancialDate;
	}
	
	
	public String getWebsite() {
		return website;
	}
	

	public  LinkedList<String> searchDocuments(String content){
		LinkedList<String> docs=new LinkedList<String>();
		String[] tockens=content.split("\n");
		
		//Period
		String[] ptockens=null;
		if(this.usePeriod)ptockens=selectedPeriod.toString().split("-");
		
		//Pattern
		String pattern=this.getPattern();
		
		for(int i=0;i<tockens.length;i++){
			String[] splits=tockens[i].split("\"");
			
			for(int j=0;j<splits.length;j++){
				//Test the searchPeriod
				boolean p_found=false;
				if(ptockens==null)p_found=true;
				else if(ptockens.length==0)p_found=true;
				else if(ptockens.length==1)p_found=splits[j].contains(ptockens[0]);
				else if(ptockens.length==2)
					p_found=(splits[j].contains(ptockens[0]) && splits[j].contains(ptockens[1])) || 
					(splits[j].contains(ptockens[0].toLowerCase()) && splits[j].contains(ptockens[1]));
				
				if(!p_found)continue;
				
				//Test the file type
				if(!splits[j].endsWith(this.getDocumentTypeSuffix()))continue;
				
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
	
	
	public String getDocumentTypeSuffix(){
		if(DocumentType.equals(DocumentType_PDF)){
			return ".pdf";
		}
		else if(DocumentType.equals(DocumentType_TXT)){
			return ".txt";
		}
		else if(DocumentType.equals(DocumentType_CSV)){
			return ".csv";
		}
		else if(DocumentType.equals(DocumentType_XLS)){
			return ".xlsx";
		}
		return "";
	}
	
	
	public void increaseFoundEltsWithValidRelations(){
		if(allFoundElts==null)return;
		//Create the element map
		HashMap<String , SearchKeyValEl> elMap=new HashMap<String , SearchKeyValEl>();
		for(SearchKeyValEl el:allFoundElts){
			elMap.put(el.sectorKey+"_"+el.fieldKey, el);
			logger.info("Key"+el.sectorKey+"_"+el.fieldKey);
		}
		
		//
		String outShares=Financials.FIELD_IncomeStatement+"_"+IncomeStatementPoint.FIELD_OutstandingShares;
		SearchKeyValEl outSharesEl=elMap.get(outShares);
		
		String earnPerShare=Financials.FIELD_IncomeStatement+"_"+IncomeStatementPoint.FIELD_EarningsPerShare;
		SearchKeyValEl earnPerShareEl=elMap.get(earnPerShare);
		
		String netIncome1=Financials.FIELD_IncomeStatement+"_"+IncomeStatementPoint.FIELD_NetIncome;
		SearchKeyValEl netIncomeEl1=elMap.get(netIncome1);
		
		String netIncome2=Financials.FIELD_CashFlow+"_"+CashFlowPoint.FIELD_NetIncome;
		SearchKeyValEl netIncomeEl2=elMap.get(netIncome2);
		
		//netIncome1 = outSharesKey * earnPerShare / 100
		if(outSharesEl!=null && earnPerShareEl!=null && netIncomeEl1==null){
			netIncomeEl1=new SearchKeyValEl(outSharesEl.periodType,
					IncomeStatementPoint.FIELD_NetIncome, Financials.FIELD_IncomeStatement);

			netIncomeEl1.value=outSharesEl.value * earnPerShareEl.value / 100;
			netIncomeEl1.wasCalculated=true;
			allFoundElts.add(netIncomeEl1);
			logger.info("netIncomeEl1: Calculated");
		}
		
		//netIncome2 = netIncome1
		if(netIncomeEl1!=null && netIncomeEl2==null){
			netIncomeEl2=new SearchKeyValEl(netIncomeEl1.periodType,
					CashFlowPoint.FIELD_NetIncome, Financials.FIELD_CashFlow);
			netIncomeEl2.value=netIncomeEl1.value;
			netIncomeEl2.wasCalculated=true;
			allFoundElts.add(netIncomeEl2);
			logger.info("netIncomeEl2: Calculated");
		}
	}
	
	//****************************************
	//***        Getter and Setter        ****
	//****************************************
	
	
	public boolean isUsePeriod() {
		return usePeriod;
	}

	public void setUsePeriod(boolean usePeriod) {
	this.usePeriod = usePeriod;
	}
	
	
	public LinkedList<SearchKeyValEl> getAllFoundElts() {
		return allFoundElts;
	}

	

	public void setAllFoundElts(LinkedList<SearchKeyValEl> allFoundElts) {
	this.allFoundElts = allFoundElts;
	}
	
	public String getDocumentContent() {
		return documentContent;
	}

	public void setDocumentContent(String lastContent) {
	this.documentContent = lastContent;
	}
	
	
	public String getDocumentType() {
		return DocumentType;
	}

	public void setDocumentType(String documentType) {
		changes.firePropertyChange(FIELD_DocumentType, this.DocumentType,
				this.DocumentType = documentType);
	}
	
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
	
	
	public void setPattern(String pattern){
		if(this.selectedPeriod.getType()==PeriodType.ANNUAL)
			setAnnualyPattern(pattern);
		else
			setQuaterlyPattern(pattern);
	}
	
	public String getPattern(){
		if(this.selectedPeriod.getType()==PeriodType.ANNUAL)
			return getAnnualyPattern();
		else
			return getQuaterlyPattern();
	}
	
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
	
	public void parseDocument(){
		allFoundElts=new LinkedList<SearchKeyValEl>();
		this.keyValueMap.clear();
		HashMap<String, SearchKeyValEl> map =getAllSearchKeyValEl(selectedPeriod.getOldPeriodString());
		if (map == null)
			return ;

		for (String key : map.keySet()) {
			SearchKeyValEl el = map.get(key);
			if(el.searchValue(documentContent)){
				allFoundElts.add(el);
				this.keyValueMap.put(el.getKey(), el.value);
			}
		}
		
		increaseFoundEltsWithValidRelations();
		
	}
	
	/*
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
	*/
	
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
					
					String regex_1="^-{0,1}[0-9]{1,3},[0-9]{3}.{0,1}[0-9]{0,2}";
					String regex_2="^-{0,1}[0-9]{1,3}.[0-9]{3},{0,1}[0-9]{0,2}";
					String regex_3="^-{0,1}[0-9]{1,3},[0-9]{0,2}";
					
					if(val_str.matches(regex_1)){
						val_str=val_str.replace(",", "");
					}
					else if(val_str.matches(regex_2)){
						val_str=val_str.replace(".", "");
						val_str=val_str.replace(",", ".");
					}
					else if(val_str.matches(regex_3)){
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
		
		if(rootElement.hasAttribute(FIELD_DocumentType)){
			this.setDocumentType(rootElement.getAttribute(FIELD_DocumentType));
		}
		if(rootElement.hasAttribute(FIELD_UsePeriod)){
			this.setUsePeriod(Boolean.getBoolean(rootElement.getAttribute(FIELD_UsePeriod)));
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
		
		rootElement.setAttribute(FIELD_DocumentType,String.valueOf(this.getDocumentType()));
		rootElement.setAttribute(FIELD_UsePeriod,String.valueOf(this.isUsePeriod()));
		
		
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
	
	
	
	public static void main(String[] args){
		
		//String regex="^[0-9]+,[0-9]{3}.[0-9]{1,2}";
		String regex="^-{0,1}[0-9]{1,3},[0-9]{3}[.0-9]{0,3}";
		
		String test="-333,123.3";
		
		System.out.println("Regex: "+regex);
		System.out.println("Test str: "+test);
		
		
		if(test.matches(regex)){
			System.out.println("Regex match!");
		}
		else
			System.out.println("Regex no match!");
		
	}
	
	
}
