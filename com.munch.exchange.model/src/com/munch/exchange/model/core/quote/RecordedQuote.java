package com.munch.exchange.model.core.quote;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.tool.DateTool;

public class RecordedQuote extends DatePointList<QuotePoint> {

	
	private static final long serialVersionUID = -5786399869426289076L;
	
	
	public static final String FIELD_IsUpdated="IsUpdated";
	
	private boolean isUpdated=false;
	
	
	
	@Override
	protected DatePoint createPoint() {
		return new QuotePoint();
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
	this.isUpdated = isUpdated;
	}
	
	public HistoricalPoint createLastHistoricalPoint(){
		QuotePoint point=(QuotePoint)this.getLast();
		if(point==null)return null;
		
		HistoricalPoint hist_p=new HistoricalPoint();
		hist_p.setDate(point.getDate());
		hist_p.setAdjClose(point.getLastTradePrice());
		hist_p.setClose(point.getLastTradePrice());
		hist_p.setHigh(point.getDaysHigh());
		hist_p.setLow(point.getDaysLow());
		hist_p.setOpen(point.getLastTradePrice());
		hist_p.setVolume(point.getAverageDailyVolume());
		return hist_p;
		
	}
	
	
	public TimeSeries getTimeSeries(String field){
		 TimeSeries series = new TimeSeries(field);
		 RecordedQuote dayQuotes=lastQuotes();
		 
		 
		 for(DatePoint point:dayQuotes){
			 QuotePoint quote=(QuotePoint) point;
			 series.add(new Millisecond(quote.getDate().getTime()),quote.get(field));
		 }
		 
		 return series;
	} 
	
	
	 private RecordedQuote lastQuotes(){
		RecordedQuote dayQuotes=new RecordedQuote();
		if(this.isEmpty())return dayQuotes;
		
		String lastDay=DateTool.dateToDayString(this.getLast().getDate());
		
		for(DatePoint point:this){
			String Day=DateTool.dateToDayString(point.getDate());
			if(lastDay.equals(Day)){
				dayQuotes.add(point);
			}
		}
		dayQuotes.sort();
		
		return dayQuotes;
	}


}
