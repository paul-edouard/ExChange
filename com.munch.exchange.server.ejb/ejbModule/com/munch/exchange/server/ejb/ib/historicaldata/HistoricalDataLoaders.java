package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;

import com.ib.controller.Bar;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;
import com.munch.exchange.server.ejb.ib.ConnectionBean;

public enum HistoricalDataLoaders {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(HistoricalDataLoaders.class.getName());
	
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query

	
	private HashMap<Long, BarLoader> loaderMap=new HashMap<Long, BarLoader>();
	private boolean isLoading=false;
	
	private LinkedList<Long> lastRequests=new LinkedList<>();
	private long minRequestIntervall=10*60*1000;
	
	private synchronized boolean acceptNewRequest(){
		
		if(lastRequests.size()>2){
			long diff=lastRequests.getLast()-lastRequests.getFirst();
			Calendar cal=Calendar.getInstance();cal.setTimeInMillis(diff);
			//log.info("Number of request: "+lastRequests.size()+ " in the last: " +cal.get(Calendar.MINUTE)+"min "+cal.get(Calendar.SECOND)+"s ");
		}
		
		
		Calendar date=Calendar.getInstance();
		if(lastRequests.size()<=60){
			lastRequests.add(date.getTimeInMillis());
			return true;
		}
		
		long first=lastRequests.getFirst();
		long diff=date.getTimeInMillis()-first;
		
		log.info("Diff: "+diff+ " min intervall: " +minRequestIntervall );
		
		
		if(diff > minRequestIntervall){
			lastRequests.remove(0);
			lastRequests.addLast(date.getTimeInMillis());
			return true;
		}
		
		return false;
	}
	
	
	public void init(List<IbBarContainer> allBars, long time){
		
		//Add new Contracts
		for(IbBarContainer c : allBars){			
			if(!loaderMap.containsKey(c.getId())){
				log.info("Create a new Bar Loader");
				loaderMap.put(c.getId(), new BarLoader(c));
			}
		}
		
		//Remove old loaders
		List<Long> toDeleteList=new LinkedList<>();
		for(Long i:loaderMap.keySet()){
			boolean isValid=false;
			for(IbBarContainer c : allBars){
				if(i==c.getId()){
					isValid=true;
					break;
				}
			}
			
			if(!isValid){
				toDeleteList.add(i);
			}	
		}
		
		for(Long i:toDeleteList)
			loaderMap.remove(i);
		
		
		//Reset the time
		for(BarLoader loader:loaderMap.values())
			loader.setTime(time);
	}
	
	
	public synchronized boolean isLoading() {
		return isLoading;
	}


	public synchronized void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public Collection<BarLoader> getLoaders(){
		return loaderMap.values();
	}
	
	public BarLoader getLoaderFrom(IbBarContainer c){
		if(loaderMap.containsKey(c.getId())){
			return loaderMap.get(c.getId());
		}
		return null;
	}
		
	public class BarLoader implements IHistoricalDataHandler{
		

		private static final long TIMOUT=2000;
		
		private boolean isLoading=false;
		private IbBarContainer bars;
		private long time;
		
		private long requestStartTime=0;
		private List<Bar> recievedBars=new LinkedList<>();
		private boolean finished=false;
		
		public BarLoader(IbBarContainer bars ){
			
			//try{
			this.bars=bars;
			
			//init();
			/*
			List<ExBar> allbars=bars.getAllBars();
			if(allbars==null){
				allbars=new LinkedList<ExBar>();
				bars.setAllBars(allbars);
			}
			
			log.info("Number of bars: "+bars.getAllBars().size());
			//bars.getAllBars().size()
			}catch(Exception ex){
				log.warning(ex.toString());
				ex.printStackTrace();
				//em.getTransaction().rollback();
			}
			*/
			
		}
		
		
		public synchronized boolean isLoading() {
			return isLoading;
		}


		public synchronized void setLoading(boolean isLoading) {
			this.isLoading = isLoading;
		}
		
		
		public void setTime( long time){
			this.time=time;
		}
		
		
		/*
		private void init(){
			
			
			if(contract.getBars()==null)
				contract.setBars(new LinkedList<ExContractBars>());
			
			log.info("Number of bars: "+contract.getBars().size());
			
			if(contract.getBars().size()==0){
				ExContractBars contractBars=new ExContractBars();
				contractBars.setContract(contract);
				contractBars.setType(WhatToShow.MIDPOINT);
				
				contract.getBars().add(contractBars);
				
				try{
					
					//em.persist(contract);
					
					
				}catch(Exception ex){
					log.warning(ex.toString());
					//em.getTransaction().rollback();
				}
				
			}
			
		}
		*/
		
		public long getTime() {
			return time;
		}

		public IbBarContainer getBars() {
			return bars;
		}

		public List<Bar> loadLastBars(long last,BarSize barSize){
			return loadBarsFromTo(last, this.time, barSize);
		}
		
		public List<Bar> loadBarsFromTo(long from, long to,BarSize barSize){
			recievedBars.clear();
			
			//Test if more than 60 historical data were starter in the last 60 minutes
			while(!acceptNewRequest()){
				log.info("New request denied!");
				//return recievedBars;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
			finished=false;
			
			
			String date=FORMAT.format( new Date(to) );
			//int duration=(int) (to-from)/1000;
			//log.info("Date: "+date);
			ConnectionBean.INSTANCE.controller().reqHistoricalData(bars.getContract().getNewContract(),
					date, 									//The new Data
					calculateDuration(from, to, barSize),	//duration in secondes
					getDurationUnit(barSize),				//secondes
					barSize,								//bar Size
					bars.getType(), 						//Ex MIDPOINT, BID, ASK
					false,									//RTh only
					this);
			
			requestStartTime=new Date().getTime();
			//log.info("Search for Historical data send!");
			
			//recievedBars=createDummyBars(from, to, barSize);
			
			
			//Wait of the ib answer
			waitForIbAnswer();
			
			
			return recievedBars;
		}
		
		private int calculateDuration(long from, long to,BarSize barSize){
			long diff=to-from;
			
			//log.info("1. Duration: "+ diff);
			
			if(barSize==BarSize._1_secs){
				diff/=1000;
			}
			else if(barSize==BarSize._1_min){
				diff/=1000;
			}
			else if(barSize==BarSize._1_hour){
				long period=1000*60*60*24;
				diff=diff/period;
				if(diff==0)
					diff=1;
			}
			else if(barSize==BarSize._1_day){
				long period=1000*60*60*24;
				//log.info("2. Periode: "+ period);
				diff=diff/period;
				if(diff==0)
					diff=1;
			}
			
			//log.info("2. Duration: "+ diff);
			//log.info("3. Duration: "+(int) diff);
			
			return (int) diff;
		}
		
		private DurationUnit getDurationUnit(BarSize barSize){
			if(barSize==BarSize._1_secs){
				return DurationUnit.SECOND;
			}
			else if(barSize==BarSize._1_min){
				return DurationUnit.SECOND;
			}
			else if(barSize==BarSize._1_hour){
				return DurationUnit.DAY;
			}
			else if(barSize==BarSize._1_day){
				return DurationUnit.DAY;
			}
			
			return DurationUnit.SECOND;
		}
		
		
		
		private List<Bar> createDummyBars(long from, long to,BarSize barSize){
			//log.info("Creation of dummy bars: "+barSize.toString());
			List<Bar> dummyBars=new LinkedList<>();
			
			Calendar cal=Calendar.getInstance();
			Calendar calTo=Calendar.getInstance();
			calTo.setTimeInMillis(to);
			
			if(from==0){
				cal.add(Calendar.HOUR_OF_DAY, -2);
			}
			else{
				cal.setTimeInMillis(from);
			}
			cal.set(Calendar.MILLISECOND, 0);
			
			int field=0;
			if(barSize==BarSize._1_day){
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				
				field=Calendar.DAY_OF_YEAR;
			}
			else if(barSize==BarSize._1_hour){
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				
				field=Calendar.HOUR_OF_DAY;
			}
			else if(barSize==BarSize._1_min){
				cal.set(Calendar.SECOND, 0);
				
				field=Calendar.MINUTE;
			}
			else{
				field=Calendar.SECOND;
			}
			
			cal.add(field, 1);
			Random r =new Random();
			while(cal.before(calTo) || cal.equals(calTo)){
				double value=100*r.nextDouble();
				Bar new_bar=new Bar(cal.getTimeInMillis(), value+10*r.nextDouble(), value-10*r.nextDouble(), value, value, 0, 0, 1);
				dummyBars.add(new_bar);
				cal.add(field, 1);
				
			}
			//log.info("Nb. Of new Bars: "+dummyBars.size());
			finished=true;
			
			return dummyBars;
		}
		
		
		private void waitForIbAnswer(){
			
			while( true ){
				
				if(finished)
					break;
				if(isTimeOut() && recievedBars.isEmpty())
					break;
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			
		}
		
		private boolean isTimeOut(){
			return new Date().getTime()-requestStartTime>TIMOUT;
		}

		@Override
		public void historicalData(Bar bar, boolean hasGaps) {
			//log.info("New bar recieved: "+bar.toString());
			recievedBars.add(bar);
			
			//em.persist(exBar);
			return ;
			
			
		}

		@Override
		public void historicalDataEnd() {
			//log.info("historicalDataEnd");
			finished=true;
		}
		
	}

}
