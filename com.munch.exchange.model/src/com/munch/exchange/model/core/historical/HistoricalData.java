package com.munch.exchange.model.core.historical;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;

public class HistoricalData extends DatePointList<HistoricalPoint>  {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5430509341617898712L;
	
	
	@Override
	protected DatePoint createPoint() {
		return new HistoricalPoint();
	}
	
	
	
	public TimeSeries getTimeSeries(String field, int numberOfDays){
		 TimeSeries series = new TimeSeries(field);
		 
		 int maxDays=this.size();
		 for(int i=numberOfDays;i>0;i--){
			 if(maxDays-i>=0){
				 HistoricalPoint point=(HistoricalPoint)this.get(maxDays-i);
				 series.add(new Day(point.getDate().getTime()),point.get(field));
			 }
		 }
		 
		 return series;
	}
	
	/*
	public void sort(){
		java.util.Collections.sort(this);
	}
	

	
	@Override
	public String getTagName() {
		return this.getClass().getSimpleName();
	}
	
	
	
	public void init(Element Root){
		
		if(Root.getTagName().equals(this.getTagName())){
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Historical Point
					HistoricalPoint point=new HistoricalPoint();
					if(childElement.getTagName().equals(point.getTagName())){
						point.init(childElement);
						this.add(point);
					}
					
				}
			}
			
			
		}
	}
	
	
	
	public Element toDomElement(Document doc){
		Element e=doc.createElement(this.getTagName());
			
		for(HistoricalPoint point : this){
			Element h_p=point.toDomElement(doc);
			e.appendChild(h_p);
		}
		
		return e;
	  }
	*/

}
