package com.munch.exchange.services.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.watchlist.Watchlists;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IHistoricalDataProvider;
import com.munch.exchange.services.internal.fred.FredObservations;
import com.munch.exchange.services.internal.fred.FredSeriesVintageDate;
import com.munch.exchange.services.internal.onvista.OnVistaTable;
import com.munch.exchange.services.internal.yql.YQLHistoricalData;

public class HistoricalDataProviderLocalImpl implements IHistoricalDataProvider {
	
	
	final private static String HistoricalDataStr="HistoricalData";
	
	
	private String getSavePath(ExchangeRate rate){
		File dir=new File(rate.getDataPath()+File.separator+HistoricalDataStr);
		if(dir.exists()){
			return dir.getAbsolutePath();
		}
		
		if(dir.mkdirs()){
			return dir.getAbsolutePath();
		}
		return "";
	}
	
	private HashMap<Integer,HistoricalData > splitHisData(ExchangeRate rate){
		HashMap<Integer,HistoricalData > map=new HashMap<Integer,HistoricalData >();
		
		int year_first=rate.getHistoricalData().getFirst().getDate().get(Calendar.YEAR);
		int year_last=rate.getHistoricalData().getLast().getDate().get(Calendar.YEAR);
		
		for(int i=year_first;i<=year_last;i++){
			map.put(i, new HistoricalData());
		}
		
		for(DatePoint point:rate.getHistoricalData()){
			int year_point=point.getDate().get(Calendar.YEAR);
			HistoricalData hisData=map.get(year_point);
			hisData.add(point);
		}
		
		return map;
	}
	
	public void clear(ExchangeRate rate){
		rate.getHistoricalData().clear();
		
		String savePathName=getSavePath(rate);
		File path=new File(savePathName);
		if(!path.isDirectory())return;
		
		File[] files=path.listFiles();
		for(int i=0;i<files.length;i++){
			files[i].delete();
		}
		
		
		
	}
	
	/**
	 * save all the historical data found
	 * @param rate
	 * @return
	 */
	public boolean saveAll(ExchangeRate rate) {
		if(rate==null)return false;
		
		HashMap<Integer,HistoricalData > map=splitHisData(rate);
		for(Integer i:map.keySet()){
			File f=new File(getSavePath(rate)+File.separator+String.valueOf(i)+".xml");
			System.out.println("Writing file: "+f.getAbsolutePath());
			if(!Xml.save(map.get(i), f.getAbsolutePath()))
				return false;
			
		}
		
		return true;
		
	}
	
	/**
	 * save only the historical data younger than the given year limit
	 * @param rate
	 * @param year
	 * @return
	 */
	
	private boolean saveFromYear(ExchangeRate rate,int year){
		if(rate==null)return false;
		
		HashMap<Integer,HistoricalData > map=splitHisData(rate);
		for(Integer i:map.keySet()){
			if(i<year)continue;
			File f=new File(getSavePath(rate)+File.separator+String.valueOf(i)+".xml");
			System.out.println("Writing file: "+f.getAbsolutePath());
			if(!Xml.save(map.get(i), f.getAbsolutePath()))
				return false;
			
		}
		
		return true;
		
	}
	
	private HistoricalData loadLocalData(ExchangeRate rate){

		File[] Xmlfiles = searchXmlFiles(rate);
		if(Xmlfiles.length==0)return null;
		
		HistoricalData hisDatas = new HistoricalData();
		for(int i=0;i<Xmlfiles.length;i++){
			HistoricalData hisDataYear = new HistoricalData();
			Xml.load(hisDataYear, Xmlfiles[i].getAbsolutePath());
			hisDatas.addAll(hisDataYear);hisDatas.sort();
		}
		
		return hisDatas;
	}
	
	
	private File[] searchXmlFiles(ExchangeRate rate){
		File localDir = new File(getSavePath(rate));
		File[] Xmlfiles = localDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});
		
		return Xmlfiles;
	}
	
	@Override
	public boolean hasLocalData(ExchangeRate rate){
		File[] Xmlfiles = searchXmlFiles(rate);
		if(Xmlfiles.length==0)return false;
		return Xmlfiles.length>1;
	}
	
	@Override
	public Calendar[] getIntervals(ExchangeRate rate){
		Calendar[] intervals=DateTool.splitIntervalInYears(rate.getStart(), rate.getEnd());
		return intervals;
	}
	
	
	@Override
	public boolean load(ExchangeRate rate) {
		if(rate==null)return false;
		if(rate.getDataPath()==null)return false;
		if(rate.getDataPath().isEmpty())return false;
		
		// load from local
		HistoricalData LocalhisData=loadLocalData(rate);
		if(LocalhisData!=null){
			System.out.println("The historical data localy found for \""+rate.getName()+" ("+rate.getSymbol()+")");
			rate.setHistoricalData(LocalhisData);
			update(rate);
			return true;
		}

		// try to load the data from YQL
		Calendar[] intervals=getIntervals(rate);
		HistoricalData hisDatas = new HistoricalData();
		for(int i=0;i<intervals.length;i=i+2){
			loadInterval(rate,hisDatas,intervals[i],intervals[i+1]);
		}
		if(rate instanceof EconomicData){
			EconomicData ed = (EconomicData) rate;
			//Set the vintage date
			FredSeriesVintageDate v=new FredSeriesVintageDate(ed.getId());
			LinkedList<Calendar> vintageDates=v.getVintageList();
			for(int i=1;i<=vintageDates.size();i++){
				Calendar vintageDate=vintageDates.get(vintageDates.size()-i);
				if(hisDatas.size()-i>0)
					hisDatas.get(hisDatas.size()-i).setVintageDate(vintageDate);
			}
		}
	
		return save(rate,hisDatas);
	}
	
	@Override
	public boolean save(ExchangeRate rate,HistoricalData hisDatas ){
		if(!hisDatas.isEmpty()){
			rate.setHistoricalData(hisDatas);
			if (saveAll(rate))
				return true;
		}
		return false;
	}
	
	@Override
	public void loadInterval(ExchangeRate rate, HistoricalData hisDatas,Calendar start,Calendar end){
		System.out.println("Interval:" + DateTool.dateToString(start) + " to "
				+ DateTool.dateToString(end));

		LinkedList<HistoricalPoint> points = new LinkedList<HistoricalPoint>();

		if (rate instanceof Commodity || rate instanceof Currency) {
			String id = "";
			if (rate instanceof Commodity)
				id = ((Commodity) rate).getOnVistaId();
			else
				id = ((Currency) rate).getOnVistaId();
			OnVistaTable table = new OnVistaTable(id, start, "Y1");
			points = table.getHisPointList();
		} else if (rate instanceof EconomicData) {
			EconomicData ed = (EconomicData) rate;
			FredObservations obs = new FredObservations(ed.getId(), start, end);
			points = obs.getObservations();
			//Set the vintage date
			/*
			FredSeriesVintageDate v=new FredSeriesVintageDate(ed.getId());
			LinkedList<Calendar> vintageDates=v.getVintageList();
			for(HistoricalPoint point:points){
				for(Calendar date:vintageDates){
					
				}
			}
			*/

		} else {
			YQLHistoricalData hisData = new YQLHistoricalData(rate.getSymbol(),
					start, end);
			points = hisData.getHisPointList();
		}

		if (points.isEmpty()) {
			System.out.println("No historical found for " + rate.getName()
					+ "(" + rate.getSymbol() + ")" + "in the interval:"
					+ DateTool.dateToString(start) + " to "
					+ DateTool.dateToString(end));

		} else {
			for (HistoricalPoint point : points) {
				if (!hisDatas.contains(point))
					hisDatas.add(point);
			}
			hisDatas.sort();
		}
	}

	@Override
	public boolean update(ExchangeRate rate) {
		boolean isUpdated = false;
		int yearLimit=Integer.MAX_VALUE;
		
		LinkedList<HistoricalPoint> points =new LinkedList<HistoricalPoint>();
		
		if(rate instanceof Commodity || rate instanceof Currency){
			String id="";
			if(rate instanceof Commodity)
				id=((Commodity) rate).getOnVistaId();
			else
				id=((Currency) rate).getOnVistaId();
			OnVistaTable table=new OnVistaTable(id, rate.getHistoricalData().getLast().getDate(),"Y1");
			points =table.getHisPointList();
		}
		else if(rate instanceof EconomicData){
			EconomicData ed=(EconomicData)rate;
			FredObservations obs=new FredObservations(ed.getId(),rate.getHistoricalData().getLast().getDate(), rate.getEnd());
			points=obs.getObservations();
			
			FredSeriesVintageDate v=new FredSeriesVintageDate(ed.getId());
			LinkedList<Calendar> vintageDates=v.getVintageList();
			for(int i=1;i<=vintageDates.size();i++){
				Calendar vintageDate=vintageDates.get(vintageDates.size()-i);
				if(points.size()-i>0)
					points.get(points.size()-i).setVintageDate(vintageDate);
			}
			
			
		}
		else{
			//Try to load the last 30 points
			Calendar start=rate.getHistoricalData().getLast().getDate();
			for(int i=30;i>1;i--){
				if(rate.getHistoricalData().size()>i){
					start=rate.getHistoricalData().get(rate.getHistoricalData().size()-i).getDate();
					break;
				}
			}
			
			YQLHistoricalData hisData = new YQLHistoricalData(rate.getSymbol(),start, rate.getEnd());
			points = hisData.getHisPointList();
			
		}
		
		for (HistoricalPoint point : points) {
			//System.out.println("Point: "+point);
			if (!rate.getHistoricalData().contains(point)) {
				int point_year = point.getDate().get(Calendar.YEAR);
				if (point_year < yearLimit)
					yearLimit = point_year;
				rate.getHistoricalData().addLast(point);
				rate.getHistoricalData().sort();
				System.out.println("Historical Point added: "+point);
				isUpdated = true;
			}
		}
		
		if(isUpdated){
			System.out.println("The ExchangeRate was updated: \""+rate.getName()+" ("+rate.getSymbol()+")");
			if(this.saveFromYear(rate, yearLimit)){
				System.out.println("The new Data were automaticaly saved!");
			}
			else{
				System.out.println("Error: cannot save the updated data!");
				return false;
			}
		}
		
		
		return false;
	}
	
	
	
	public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		ExchangeRate rate=provider.load("DAI.DE");
		Stock stock=(Stock) rate;
		
		HistoricalDataProviderLocalImpl dataProvider=new HistoricalDataProviderLocalImpl();
		
		dataProvider.load(stock);
		/*
		for(HistoricalPoint point:stock.getHistoricalData()){
			System.out.println(point);
		}
		*/

	}

}
	
	


