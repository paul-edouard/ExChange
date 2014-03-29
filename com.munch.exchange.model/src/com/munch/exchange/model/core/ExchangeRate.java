package com.munch.exchange.model.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.quote.RecordedQuote;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;


public abstract class ExchangeRate extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(ExchangeRate.class);
	
	static final String FIELD_Start="start";
	static final String FIELD_End="end";
	static final String FIELD_LastUpdate="LastUpdate";
	
	public static final String FIELD_Name="name";
	public static final String FIELD_UUID="uuid";
	public static final String FIELD_Symbol="symbol";
	public static final String FIELD_Data_Path="data_path";
	public static final String FIELD_Stock_Exchange="stockExchange";
	public static final String FIELD_Recorded_Quote="recorded_quote";
	
	public static final String FIELD_ISIN="ISIN";
	public static final String FIELD_WKN="WKN";
	
	
	protected Calendar start=Calendar.getInstance();
	protected Calendar end=Calendar.getInstance();
	protected Calendar lastUpdate=null;
	
	protected String name="";
	protected String symbol="";
	protected String dataPath="";
	protected String stockExchange="";
	
	protected String ISIN="";
	protected String WKN="";
	
	
	protected HistoricalData historicalData=new HistoricalData();
	protected RecordedQuote recordedQuote=null;
	
	protected String uuid=UUID.randomUUID().toString();
	
	
	
	
	
	public String getISIN() {
		return ISIN;
	}


	public void setISIN(String iSIN) {
		changes.firePropertyChange(FIELD_ISIN, this.ISIN, this.ISIN = iSIN);
	}


	public String getWKN() {
		return WKN;
	}


	public void setWKN(String wKN) {
		changes.firePropertyChange(FIELD_WKN, this.WKN, this.WKN = wKN);
	}


	public Calendar getStart() {
		return start;
	}


	public void setStart(Calendar start) {
		changes.firePropertyChange(FIELD_Start, this.start, this.start = start);
	}


	public Calendar getEnd() {
		return end;
	}


	public void setEnd(Calendar end) {
		changes.firePropertyChange(FIELD_End, this.end, this.end = end);
	}
	
	public String getFullName(){
		return this.getName()+" ("+this.getSymbol()+")";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		changes.firePropertyChange(FIELD_Name, this.name,
				this.name = name);
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		changes.firePropertyChange(FIELD_Symbol, this.symbol, this.symbol = symbol);
	}
	
	
	public HistoricalData getHistoricalData() {
		return historicalData;
	}
	
	public void setHistoricalData(HistoricalData historicalData) {
		this.historicalData.clear();
		this.historicalData.addAll(historicalData);
		//this.historicalData = historicalData;
	}
	
	
	public RecordedQuote getRecordedQuote() {
		if(this.recordedQuote!=null)return recordedQuote;
		
		this.recordedQuote=new RecordedQuote();
		this.recordedQuote.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//logger.info("Recorded Quote from "+name+ ", has Changed: "+evt.getPropertyName()+", value: "+evt.getNewValue());
				if(evt.getPropertyName().equals(RecordedQuote.FIELD_LastQuoteChanged)){
					 HistoricalPoint point=recordedQuote.createLastHistoricalPoint();
					 if(point!=null){
						 historicalData.setLastHisPointFromQuote(point);
					 }
				}
				
			}
		});
		return recordedQuote;
	}
	public void setRecordedQuote(RecordedQuote nrecordedQuote) {
		this.recordedQuote.clear();
		this.recordedQuote.addAll(nrecordedQuote);
		
		changes.firePropertyChange(FIELD_Recorded_Quote, null, this.recordedQuote);
		
	}
	
	public String getDataPath() {
		return dataPath;
	}
	public void setDataPath(String dataPath) {
		changes.firePropertyChange(FIELD_Data_Path, this.dataPath, this.dataPath = dataPath);
		//this.dataPath = dataPath;
	}
	
	
	
	public String getUUID() {
		return uuid;
	}


	public void setUUID(String uuid) {
	changes.firePropertyChange(FIELD_UUID, this.uuid, this.uuid = uuid);
	}
	

	public String getStockExchange() {
		return stockExchange;
	}
	public void setStockExchange(String stockExchange) {
		changes.firePropertyChange(FIELD_Stock_Exchange, this.stockExchange, this.stockExchange = stockExchange);
		
	}
	
	
	
	

	public Calendar getLastUpdate() {
		return lastUpdate;
	}


	public void setLastUpdate(Calendar lastUpdate) {
	changes.firePropertyChange(FIELD_LastUpdate, this.lastUpdate, this.lastUpdate = lastUpdate);}
	


	@Override
	public String toString() {
		return "ExchangeRate [name=" + name + ", symbol=" + symbol
				+ ", dataPath=" + dataPath + ", stockExchange=" + stockExchange
				+ ", historicalData=" + historicalData + ", recordedQuote="
				+ recordedQuote + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		/*
		if (!super.equals(obj)) {
			return false;
		}
		*/
		if (!(obj instanceof ExchangeRate)) {
			return false;
		}
		ExchangeRate other = (ExchangeRate) obj;
		if (symbol == null) {
			if (other.symbol != null) {
				return false;
			}
		} else if (!symbol.equals(other.symbol)) {
			return false;
		}
		return true;
	}
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	protected void initAttribute(Element rootElement){
		
		this.setEnd(DateTool.StringToDate(rootElement.getAttribute(FIELD_End)));
		this.setStart(DateTool.StringToDate(rootElement.getAttribute(FIELD_Start)));
		if(rootElement.hasAttribute(FIELD_LastUpdate))
			this.setLastUpdate(DateTool.StringToDate(rootElement.getAttribute(FIELD_LastUpdate)));
		
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setSymbol(rootElement.getAttribute(FIELD_Symbol));
		this.setUUID(rootElement.getAttribute(FIELD_UUID));
		
		if(rootElement.hasAttribute(FIELD_ISIN))
			this.setISIN(rootElement.getAttribute(FIELD_ISIN));
		if(rootElement.hasAttribute(FIELD_WKN))
			this.setWKN(rootElement.getAttribute(FIELD_WKN));
		
		this.setStockExchange(rootElement.getAttribute(FIELD_Stock_Exchange));
	}
	protected void initChild(Element childElement){}
	
	protected void setAttribute(Element rootElement){
		
		rootElement.setAttribute(FIELD_End,DateTool.dateToString( this.getEnd()));
		rootElement.setAttribute(FIELD_Start,DateTool.dateToString( this.getStart()));
		if(this.getLastUpdate()!=null)
			rootElement.setAttribute(FIELD_LastUpdate,DateTool.dateToString( this.getLastUpdate()));
		
		rootElement.setAttribute(FIELD_Name, this.getName());
		rootElement.setAttribute(FIELD_Symbol, this.getSymbol());
		rootElement.setAttribute(FIELD_UUID, this.getUUID());
		
		rootElement.setAttribute(FIELD_ISIN, this.getISIN());
		rootElement.setAttribute(FIELD_WKN, this.getWKN());
		
		rootElement.setAttribute(FIELD_Stock_Exchange, this.getStockExchange());
	}
	protected void appendChild(Element rootElement,Document doc){
	}
	
	

}
