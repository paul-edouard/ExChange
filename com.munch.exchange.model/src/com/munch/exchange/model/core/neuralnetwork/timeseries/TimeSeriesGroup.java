package com.munch.exchange.model.core.neuralnetwork.timeseries;

import java.util.Calendar;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.financials.FinancialPoint;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.PeriodType;
import com.munch.exchange.model.core.neuralnetwork.ValuePoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class TimeSeriesGroup extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(TimeSeriesGroup.class);
	
	static final String ROOT="ROOT";
	static final String BASE="this";
	
	static final String FIELD_Name="Name";
	static final String FIELD_Level="Level";
	static final String FIELD_ReferencedRateUUID="ReferencedRateUUID";
	
	
	public static final String GROUP_RATE="Rate";
	public static final String GROUP_FINANCIAL="Financial";
	public static final String GROUP_INDICATOR="Indicator";
	public static final String GROUP_TARGET_OUTPUT="Target Output";
	
	
	
	private String name;
	private int level;
	private String referencedRateUUID="";
	private TimeSeriesGroup parent;
	
	
	LinkedList<TimeSeriesGroup> subGroups=new LinkedList<TimeSeriesGroup>();
	LinkedList<TimeSeries> timeSeriesList=new LinkedList<TimeSeries>();
	
	public TimeSeriesGroup(){
		
	}
	
	public TimeSeriesGroup(TimeSeriesGroup parent,String name, boolean addToParentchildren){
		this.parent=parent;
		this.name=name;
		this.level=0;
		
		if(this.parent!=null)
			this.level=this.parent.level+1;
		
		if(this.parent!=null && addToParentchildren){
			this.parent.getSubGroups().add(this);
			
			if(!this.parent.getName().equals(ROOT) &&
					!this.parent.getReferencedRateUUID().isEmpty()){
				this.referencedRateUUID=this.parent.getReferencedRateUUID();
			}
			
		}
		
	}
	
	private TimeSeriesGroup searchRoot(){
		TimeSeriesGroup s_parent=this.parent;
		TimeSeriesGroup root=this;
		while(s_parent!=null){
			root=s_parent;
			s_parent=s_parent.parent;
		}
		return root;
	}
	
	private boolean isBranched(ExchangeRate rate){
		TimeSeriesGroup root=searchRoot();
		for(TimeSeriesGroup c:root.getSubGroups()){
			if(c.getReferencedRateUUID().equals(rate.getUUID()))
				return true;
		}
		
		return false;
	}
	
	public void addNewBranch(ExchangeRate rate){
		
		if(isBranched(rate))return;
		
		TimeSeriesGroup root=searchRoot();
		//Base
		TimeSeriesGroup baseGroup=new TimeSeriesGroup(root,rate.getFullName(), true);
		baseGroup.setReferencedRateUUID(rate.getUUID());
		
		//Stock
		if(rate instanceof Stock){
			addStockGroups((Stock) rate,baseGroup);
		}
		//Rate
		else{
			addRateGroups(rate, baseGroup);
		}
		
	}
	
	public void searchLastAvailableInputPointDate(ExchangeRate rate, Configuration configuration){
		for(TimeSeriesGroup subGoup:subGroups){
			subGoup.searchLastAvailableInputPointDate(rate, configuration);
		}
		
		if(!rate.getUUID().equals(referencedRateUUID)){
			logger.info("Error: searchLastAvailableInputPointDate, the wrong rate is used! Expected is "+referencedRateUUID);
			return ;
		}
		//Rate Series
		if(this.name.equals(GROUP_RATE)){
			if(configuration.getPeriod()==PeriodType.DAY){
				LinkedList<HistoricalPoint> hisPointList=rate.getHistoricalData().getNoneEmptyPoints();
				for(TimeSeries series:this.timeSeriesList){
					int nbOfValues=series.getNumberOfPastValues();
					configuration.setLastInputPointDate(hisPointList.get(nbOfValues-1).getDate());
				}
			}
		}
		//Financial series
		else if(this.name.equals(GROUP_FINANCIAL) && rate instanceof Stock){
			Stock stock=(Stock)rate;
			LinkedList<Calendar> financialsDates=stock.getFinancials().getDateList(FinancialPoint.PeriodeTypeQuaterly);
			for(TimeSeries series:this.timeSeriesList){
				int nbOfValues=series.getNumberOfPastValues();
				Calendar date=financialsDates.get(financialsDates.size()-nbOfValues-1);
				//stock.getFinancials().getEffectiveDate(FinancialPoint.PeriodeTypeQuaterly, date)
				configuration.setLastInputPointDate(
						stock.getFinancials().getEffectiveDate(FinancialPoint.PeriodeTypeQuaterly, date));
			}
		}
		//Target Output
		else if(this.name.equals(GROUP_TARGET_OUTPUT) &&
				configuration.getOutputPointList()!=null &&
				 configuration.getOutputPointList().isEmpty()){
			
			for(TimeSeries series:this.timeSeriesList){
				int nbOfValues=series.getNumberOfPastValues();
				configuration.setLastInputPointDate(configuration.getOutputPointList().get(nbOfValues).getDate());
			}
			
			//Last output point
			configuration.setLastInputPointDate(configuration.getOutputPointList().getFirst().getDate());
			
		}
		//Indicators
		else if(this.name.equals(GROUP_INDICATOR)){
			logger.info("Please implements this section! searchLastAvailableInputPointDate");
			//TODO
		}
		
		
		
	}
	
	public void createInputValueLists(ExchangeRate rate, Configuration configuration){
		for(TimeSeriesGroup subGoup:subGroups){
			subGoup.createInputValueLists(rate, configuration);
		}
		
		if(!rate.getUUID().equals(referencedRateUUID)){
			logger.info("Error: createInputValueLists, the wrong rate is used! Expected is "+referencedRateUUID);
			return ;
		}
		//Rate Series
		if(this.name.equals(GROUP_RATE)){
			createRateInputValueLists(rate,configuration);
		}
		//Financial series
		else if(this.name.equals(GROUP_FINANCIAL) && rate instanceof Stock){
			createFinancialInputValueLists((Stock)rate,configuration );
		}
		//Target Output
		else if(this.name.equals(GROUP_TARGET_OUTPUT) &&
						configuration.getOutputPointList()!=null &&
						 configuration.getOutputPointList().isEmpty()){
			createTargetOutputInputValueLists((Stock)rate,configuration);
		}
		//
		else if(this.name.equals(GROUP_INDICATOR)){
			createIndicatorInputValueLists(rate,configuration);
		}
		
	}
	
	private void createRateInputValueLists(ExchangeRate rate, Configuration configuration){
		if(configuration.getPeriod()==PeriodType.DAY){
			LinkedList<HistoricalPoint> hisPointList=rate.getHistoricalData().getNoneEmptyPoints();
			for(TimeSeries series:this.timeSeriesList){
				series.getInputValues().clear();
				for(HistoricalPoint his_point:hisPointList){
					ValuePoint point=new ValuePoint(his_point.getDate(),his_point.get(series.getName()));
					//if(point.getDate().before(configuration.getLastInputPointDate()))continue;
					//======================================
					//==Set the next value date: + 1 DAY  ==
					//======================================
					Calendar expectedNextValue=Calendar.getInstance();
					expectedNextValue.setTimeInMillis(his_point.getDate().getTimeInMillis()+PeriodType.DAY.getPeriod());
					point.setNextValueDate(expectedNextValue);
					
					series.getInputValues().add(point);
				}
				
				logger.info("Series: "+series.getName()
						+", first point: "+DateTool.dateToDayString(series.getInputValues().getFirst().getDate())
						+", last point: "+DateTool.dateToDayString(series.getInputValues().getLast().getDate())
						+", Size:"+series.getInputValues().size());
				
			}
		}
				
	}
	
	private void createFinancialInputValueLists(Stock stock, Configuration configuration){
		LinkedList<Calendar> financialsDates=stock.getFinancials().getDateList(FinancialPoint.PeriodeTypeQuaterly);
		LinkedList<HistoricalPoint> hisPointList=stock.getHistoricalData().getNoneEmptyPoints();
		
		for(TimeSeries series:this.timeSeriesList){
			//series.adaptInputValuesToMasterValuePointList(masterValuePointList);
			series.getInputValues().clear();
			series.getLowFrequencyValues().clear();
			
			//Save the input value dates
			for(HistoricalPoint his_point:hisPointList){
				ValuePoint point=new ValuePoint(his_point.getDate(), 0);
				series.getInputValues().add(point);
			}
			
			
			String key=series.getName().split(":")[1];
			String sectorKey=series.getName().split(":")[0];
			
			int pos=financialsDates.size()-2;
			for(int k=financialsDates.size()-1;k>=0;k--){
				Calendar date=financialsDates.get(k);
				double finVal=stock.getFinancials().getValue(FinancialPoint.PeriodeTypeQuaterly, date, key, sectorKey);
				//logger.info("Fin val: "+finVal);
				ValuePoint point=new ValuePoint(
						stock.getFinancials().getEffectiveDate(FinancialPoint.PeriodeTypeQuaterly, date),finVal);
				
				//======================================
				//==Set the next value date:          ==
				//======================================
				if(pos>=0){
					Calendar expectedNextValue=Calendar.getInstance();
					Calendar nextDate=financialsDates.get(pos);
					Calendar nextEffectivDate=stock.getFinancials().getEffectiveDate(FinancialPoint.PeriodeTypeQuaterly, nextDate);
					expectedNextValue.setTimeInMillis(nextEffectivDate.getTimeInMillis());
					point.setNextValueDate(expectedNextValue);
				}
				else{
					point.setNextValueDate(stock.getFinancials().getNextExpectedFinancialDate(FinancialPoint.PeriodeTypeQuaterly));
				}
				pos--;
				
				//logger.info("Financial Point: "+String.valueOf(point));
				
				series.getLowFrequencyValues().add(point);
				
			}
			
			
			logger.info("Series: "+series.getName()
					+", first point: "+DateTool.dateToDayString(series.getLowFrequencyValues().getFirst().getDate())
					+", last point: "+DateTool.dateToDayString(series.getLowFrequencyValues().getLast().getDate())
					+", Size:"+series.getLowFrequencyValues().size());
		}
	}
	
	private void createTargetOutputInputValueLists(Stock stock, Configuration configuration){
		for(TimeSeries series:this.timeSeriesList){
			series.getInputValues().clear();
			ValuePoint lastPoint=null;
			for(ValuePoint point:configuration.getOutputPointList()){
				if(lastPoint!=null){
					ValuePoint outputPoint=new ValuePoint(point.getDate(), lastPoint.getValue());
					//if(point.getDate().before(configuration.getLastInputPointDate()))continue;
					//======================================
					//==Set the next value date: + 1 DAY  ==
					//======================================
					Calendar expectedNextValue=Calendar.getInstance();
					expectedNextValue.setTimeInMillis(point.getDate().getTimeInMillis()+PeriodType.DAY.getPeriod());
					outputPoint.setNextValueDate(expectedNextValue);
					
					series.getInputValues().add(outputPoint);
				}
				lastPoint=point;
			}
			
			//Create the last point
			Calendar lastValueDate=Calendar.getInstance();
			lastValueDate.setTimeInMillis(lastPoint.getDate().getTimeInMillis()+PeriodType.DAY.getPeriod());
			ValuePoint lastOutputPoint=new ValuePoint(lastValueDate, lastPoint.getValue());
			
			Calendar expectedNextValue=Calendar.getInstance();
			expectedNextValue.setTimeInMillis(lastValueDate.getTimeInMillis()+PeriodType.DAY.getPeriod());
			lastOutputPoint.setNextValueDate(expectedNextValue);
			
			
			series.getInputValues().add(lastOutputPoint);
			
			logger.info("Series: "+series.getName()
					+", first point: "+DateTool.dateToDayString(series.getInputValues().getFirst().getDate())
					+", last point: "+DateTool.dateToDayString(series.getInputValues().getLast().getDate())
					+", Size:"+series.getInputValues().size());
		}
	}
	
	private void createIndicatorInputValueLists(ExchangeRate rate, Configuration configuration){
		//TODO
		
		logger.info("Please implements this section! createIndicatorInputValueLists");
	}
	
	
	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/
	
	public TimeSeriesGroup createCopy(){
		TimeSeriesGroup copy=new TimeSeriesGroup();
		copy.name=this.name;
		copy.level=this.level;
		copy.referencedRateUUID=this.referencedRateUUID;
		
		for(TimeSeriesGroup child:subGroups){
			TimeSeriesGroup child_copy=child.createCopy();
			child_copy.parent=copy;
			copy.subGroups.add(child_copy);
		}
		for(TimeSeries series:timeSeriesList){
			TimeSeries series_copy=series.createCopy();
			series_copy.setParentGroup(copy);
			copy.timeSeriesList.add(series_copy);
		}
		
		return copy;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);
	}
	

	public TimeSeriesGroup getParent() {
		return parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
	changes.firePropertyChange(FIELD_Level, this.level, this.level = level);}
	

	public String getReferencedRateUUID() {
		return referencedRateUUID;
	}

	public void setReferencedRateUUID(String referencedRateUUID) {
	changes.firePropertyChange(FIELD_ReferencedRateUUID, this.referencedRateUUID, this.referencedRateUUID = referencedRateUUID);}
	

	public LinkedList<TimeSeriesGroup> getSubGroups() {
		return subGroups;
	}

	public LinkedList<TimeSeries> getTimeSeriesList() {
		return timeSeriesList;
	}
	
	public LinkedList<TimeSeries> getAllTimeSeries(){
		LinkedList<TimeSeries> list=new LinkedList<TimeSeries>();
		list.addAll(timeSeriesList);
		for(TimeSeriesGroup subGroup:subGroups)
			list.addAll(subGroup.getAllTimeSeries());
			
		return list;
	}
	
	
	public void addTimeSeries(TimeSeries serie){
		timeSeriesList.add(serie);
		serie.setParentGroup(this);
		}

	/***********************************
	 *		       XML                 *
	 ***********************************/

	@Override
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setLevel(Integer.parseInt(rootElement.getAttribute(FIELD_Level)));
		this.setReferencedRateUUID(rootElement.getAttribute(FIELD_ReferencedRateUUID));
		
		subGroups.clear();
		timeSeriesList.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		TimeSeriesGroup group=new TimeSeriesGroup(this,ROOT,false);
		TimeSeries serie=new TimeSeries();
		if(childElement.getTagName().equals(group.getTagName())){
			group.init(childElement);
			subGroups.add(group);
		}
		else if(childElement.getTagName().equals(serie.getTagName())){
			serie.init(childElement);
			timeSeriesList.add(serie);
		}
		

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_Level,String.valueOf(this.getLevel()));
		rootElement.setAttribute(FIELD_ReferencedRateUUID,this.getReferencedRateUUID());

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(TimeSeriesGroup group:subGroups){
			rootElement.appendChild(group.toDomElement(doc));
		}
		
		for(TimeSeries serie:timeSeriesList){
			rootElement.appendChild(serie.toDomElement(doc));
		}

	}
	
	/***********************************
	 *		       STATIC              *
	 ***********************************/
	
	public static TimeSeriesGroup createRoot(Stock stock){
		TimeSeriesGroup root=new TimeSeriesGroup(null,ROOT, false);
		root.setName(ROOT);
		root.setLevel(0);
		//root.setReferencedRateUUID(stock.getUUID());
		
		//Base
		TimeSeriesGroup baseGroup=new TimeSeriesGroup(root,"this", true);
		baseGroup.setReferencedRateUUID(stock.getUUID());
		
		addStockGroups(stock,baseGroup);
		
		new TimeSeriesGroup(baseGroup,GROUP_TARGET_OUTPUT, true);
		
		return root;
	}
	
	public static void addStockGroups(Stock stock,TimeSeriesGroup baseGroup){
		new TimeSeriesGroup(baseGroup,GROUP_RATE, true);
		new TimeSeriesGroup(baseGroup,GROUP_FINANCIAL, true);
		new TimeSeriesGroup(baseGroup,GROUP_INDICATOR, true);
	}
	
	public static void addRateGroups(ExchangeRate rate,TimeSeriesGroup baseGroup){
		new TimeSeriesGroup(baseGroup,GROUP_INDICATOR, true);
	}
	
	public static LinkedList<String> getAvailableSerieNames(TimeSeriesGroup group){
		LinkedList<String> serieNames=new LinkedList<String>();
		
		if(group.getName().equals(GROUP_RATE)){
			serieNames.add(DatePoint.FIELD_Close);
			serieNames.add(DatePoint.FIELD_High);
			serieNames.add(DatePoint.FIELD_Low);
			serieNames.add(DatePoint.FIELD_Open);
			serieNames.add(DatePoint.FIELD_Volume);
			serieNames.add(DatePoint.FIELD_Adj_Close);
		}
		else if(group.getName().equals(GROUP_FINANCIAL)){
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_EarningsPerShare);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_EBIT);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_TotalRevenue);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_NetIncome);
			
		}
		else if(group.getName().equals(GROUP_TARGET_OUTPUT)){
			serieNames.add("Desired Output");
		}
		
		return serieNames;
	}


}
