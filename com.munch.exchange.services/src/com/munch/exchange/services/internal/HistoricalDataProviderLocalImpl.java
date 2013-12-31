package com.munch.exchange.services.internal;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IHistoricalDataProvider;
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
		
		for(HistoricalPoint point:rate.getHistoricalData()){
			int year_point=point.getDate().get(Calendar.YEAR);
			HistoricalData hisData=map.get(year_point);
			hisData.add(point);
		}
		
		return map;
	}
	
	private boolean saveAll(ExchangeRate rate) {
		if(rate==null)return false;
		
		HashMap<Integer,HistoricalData > map=splitHisData(rate);
		for(Integer i:map.keySet()){
			File f=new File(getSavePath(rate)+File.separator+String.valueOf(i)+"xml");
			System.out.println("Writing file: "+f.getAbsolutePath());
			if(!Xml.save(map.get(i), f.getAbsolutePath()))
				return false;
			
		}
		
		return true;
		
	}
	
	@Override
	public boolean load(ExchangeRate rate) {
		if(rate.getDataPath().isEmpty())return false;
		
		// TODO load from local
		// File localDir=new File(getSavePath(rate));

		// try to load the data from YQL
		//TODO load only one year!!
		
		YQLHistoricalData hisData = new YQLHistoricalData(rate.getSymbol(),
				rate.getStart()/*, rate.getEnd()*/);
		
		LinkedList<HistoricalPoint> points =hisData.getHisPointList();
		if (points.isEmpty()) {
			System.out.println("No historical found for the exchange rate: "
					+ rate.getName() + "(" + rate.getSymbol() + ")");
			return false;
		} else {
			HistoricalData hisDatas = new HistoricalData();
			hisDatas.addAll(points);
			hisDatas.sort();
			rate.setHistoricalData(hisDatas);
			if (saveAll(rate))
				return true;

		}
		
		
		return false;
	}

	@Override
	public boolean update(ExchangeRate rate) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		
		ExchangeRate rate=provider.load("DAI.DE");
		Stock stock=(Stock) rate;
		
		HistoricalDataProviderLocalImpl dataProvider=new HistoricalDataProviderLocalImpl();
		
		dataProvider.load(stock);
		for(HistoricalPoint point:stock.getHistoricalData()){
			System.out.println(point);
		}

	}

}
	
	


