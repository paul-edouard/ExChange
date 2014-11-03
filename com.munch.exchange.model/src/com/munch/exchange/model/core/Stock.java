package com.munch.exchange.model.core;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.analystestimation.AnalystEstimation;
import com.munch.exchange.model.core.divident.HistoricalDividend;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.keystat.KeyStatistics;
import com.munch.exchange.model.core.neuralnetwork.NNetwork;
import com.munch.exchange.model.tool.DateTool;

public class Stock extends ExchangeRate {
	
	
	static final String FIELD_Sector="sector";
	static final String FIELD_Industry="industry";
	
	static final String FIELD_Parent_Name="parentName";
	static final String FIELD_Parent_Symbol="parentSymbol";
	
	static final String FIELD_Financials="Financials";
	static final String FIELD_AnalystEstimation="AnalystEstimation";
	static final String FIELD_KeyStatistics="KeyStatistics";
	
	static final String FIELD_NeuralNetwork="NeuralNetwork";
	
	//Data from YQLStocks
	private String sector,industry="";
	//TODO Record Full time Employees
	
	private String parentSymbol,parentName="";
	
	protected HistoricalDividend historicalDividend=new HistoricalDividend();
	//protected HistoricalIncomeStatement historicalIncomeStatement= new HistoricalIncomeStatement();
	
	protected Financials Financials=new Financials();
	
	protected AnalystEstimation AnalystEstimation =new AnalystEstimation();
	
	protected KeyStatistics KeyStatistics=new KeyStatistics();
	
	protected NNetwork neuralNetwork=new NNetwork();
	
	
	
	
	public NNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	public void setNeuralNetwork(NNetwork neuralNetwork) {
	changes.firePropertyChange(FIELD_NeuralNetwork, this.neuralNetwork, this.neuralNetwork = neuralNetwork);}
	

	public KeyStatistics getKeyStatistics() {
		return KeyStatistics;
	}

	public void setKeyStatistics(KeyStatistics keyStatistics) {
		changes.firePropertyChange(FIELD_KeyStatistics, this.KeyStatistics,
				this.KeyStatistics = keyStatistics);
	}

	public AnalystEstimation getAnalystEstimation() {
		return AnalystEstimation;
	}

	public void setAnalystEstimation(AnalystEstimation analystEstimation) {
		changes.firePropertyChange(FIELD_AnalystEstimation, this.AnalystEstimation,
				this.AnalystEstimation = analystEstimation);
	}



	public String getSector() {
		return sector;
	}

	
	
	public void setSector(String sector) {
		changes.firePropertyChange(FIELD_Sector, this.sector, this.sector = sector);
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		changes.firePropertyChange(FIELD_Industry, this.industry, this.industry = industry);
	}


	public HistoricalDividend getHistoricalDividend() {
		return historicalDividend;
	}


	public void setHistoricalDividend(HistoricalDividend historicalDividend) {
		//changes.firePropertyChange(FIELD, this.historicalDividend, this.historicalDividend = historicalDividend);
		this.historicalDividend = historicalDividend;
	}
	

	public Financials getFinancials() {
		return Financials;
	}

	public void setFinancials(Financials financials) {
		changes.firePropertyChange(FIELD_Financials, Financials,
				Financials = financials);
	}



	public boolean isParentUpdateNeeded(){
		if(!this.getParentSymbol().isEmpty() && !this.getParentSymbol().equals(this.getSymbol())){
			if(this.getParentName()==null || this.getParentName().isEmpty())
				return true;
			else if(this.getSector()==null || this.getSector().isEmpty()){
				return true;
			}
			else if(this.getIndustry()==null || this.getIndustry().isEmpty()){
				return true;
			}
		}
		return false;
	}
	
	public String getParentSymbol() {
		if(parentSymbol==null || parentSymbol.isEmpty())
			return this.symbol;
		return parentSymbol;
	}

	public void setParentSymbol(String parentSymbol) {
		changes.firePropertyChange(FIELD_Parent_Symbol, this.parentSymbol, this.parentSymbol = parentSymbol);
		//this.parentSymbol = parentSymbol;
	}


	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		changes.firePropertyChange(FIELD_Parent_Name, this.parentName, this.parentName = parentName);
		//this.parentName = parentName;
	}


	@Override
	protected void initAttribute(Element rootElement) {
		
		
		this.setSector(rootElement.getAttribute(FIELD_Sector));
		this.setIndustry(rootElement.getAttribute(FIELD_Industry));
		
		this.setParentName(rootElement.getAttribute(FIELD_Parent_Name));
		this.setParentSymbol(rootElement.getAttribute(FIELD_Parent_Symbol));
		
		super.initAttribute(rootElement);
	}
	
	
	@Override
	protected void setAttribute(Element rootElement) {
		
	
		
		rootElement.setAttribute(FIELD_Sector,this.getSector());
		rootElement.setAttribute(FIELD_Industry,this.getIndustry());
		
		rootElement.setAttribute(FIELD_Parent_Symbol,this.getParentSymbol());
		rootElement.setAttribute(FIELD_Parent_Name,this.getParentName());
		
		super.setAttribute(rootElement);
	}


	@Override
	public String toString() {
		return "Stock [start=" + DateTool.dateToString(start) + ", end=" + DateTool.dateToString(end) + ", sector=" + sector
				+ ", industry=" + industry + ", parentSymbol=" + parentSymbol
				+ ", parentName=" + parentName + ", historicalDividend="
				+ historicalDividend + ", name=" + name + ", symbol=" + symbol
				+ ", historicalData=" + historicalData + ", recordedQuote="
				+ recordedQuote + ", changes=" + changes + ", parameter="
				+ parameter + "]";
	}


	

}
