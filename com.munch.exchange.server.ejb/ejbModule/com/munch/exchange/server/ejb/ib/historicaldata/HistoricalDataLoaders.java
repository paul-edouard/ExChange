package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExContractBars;
import com.munch.exchange.model.core.ib.bar.ExSecondeBar;
import com.munch.exchange.server.ejb.ib.ConnectionBean;

public enum HistoricalDataLoaders {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(HistoricalDataLoaders.class.getName());
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query

	
	private HashMap<Long, BarLoader> loaderMap=new HashMap<Long, BarLoader>();
	private boolean isLoading=false;
	
	
	
	public void init(List<ExContractBars> allBars, long time){
		
		//Add new Contracts
		for(ExContractBars c : allBars){			
			if(!loaderMap.containsKey(c.getId())){
				log.info("Create a new Bar Loader");
				loaderMap.put(c.getId(), new BarLoader(c));
			}
		}
		
		//Remove old loaders
		List<Long> toDeleteList=new LinkedList<>();
		for(Long i:loaderMap.keySet()){
			boolean isValid=false;
			for(ExContractBars c : allBars){
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
	
	
	public class BarLoader implements IHistoricalDataHandler{
		

		private static final long TIMOUT=2000;
		
		private boolean isLoading=false;
		private ExContractBars bars;
		private long time;
		
		private long requestStartTime=0;
		private List<Bar> recievedBars=new LinkedList<>();
		private boolean finished=false;
		
		public BarLoader(ExContractBars bars ){
			
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

		public ExContractBars getBars() {
			return bars;
		}

		public List<Bar> loadLastBars(long last,BarSize barSize){
			return loadBarsFromTo(last, this.time, barSize);
			
			/*
			recievedBars.clear();
			finished=false;
			
			String date=FORMAT.format( new Date(this.time) );
			int duration=(int) (this.time-last)/1000;
			log.info("Date: "+date);
			ConnectionBean.INSTANCE.controller().reqHistoricalData(bars.getContract().getNewContract(),
					date, 							//The new Data
					duration,						//duration in secondes
					DurationUnit.SECOND,			//secondes
					barSize,						//bar Size
					bars.getType(), 				//Ex MIDPOINT, BID, ASK
					false,							//RTh only
					this);
			
			requestStartTime=new Date().getTime();
			log.info("Search for Historical data Finished!");
			
			//Wait of the ib answer
			waitForIbAnswer();
			
			
			return recievedBars;
			*/
			
		}
		
		public List<Bar> loadBarsFromTo(long from, long to,BarSize barSize){
			recievedBars.clear();
			finished=false;
			
			/*
			String date=FORMAT.format( new Date(to) );
			int duration=(int) (to-from)/1000;
			log.info("Date: "+date);
			ConnectionBean.INSTANCE.controller().reqHistoricalData(bars.getContract().getNewContract(),
					date, 							//The new Data
					duration,						//duration in secondes
					DurationUnit.SECOND,			//secondes
					barSize,						//bar Size
					bars.getType(), 				//Ex MIDPOINT, BID, ASK
					false,							//RTh only
					this);
			
			requestStartTime=new Date().getTime();
			log.info("Search for Historical data Finished!");
			*/
			recievedBars=createDummyBars(from, to, barSize);
			
			
			//Wait of the ib answer
			waitForIbAnswer();
			
			
			return recievedBars;
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
			
			while( (!isTimeOut() && recievedBars.isEmpty()) || !finished ){
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
			recievedBars.add(bar);
			
			/*
			log.info("New bar recieved: "+bar.toString());
			ExSecondeBar exBar=new ExSecondeBar(bar);
			
			log.info("1 Trie to load Bars: ");
			List<ExBar> allbars=bars.getAllBars();
			if(allbars==null){
				allbars=new LinkedList<ExBar>();
			}
			try{
			log.info("2 Trie to load Bars: ");
			log.info("Number of bars: "+allbars.size());
			allbars.add(exBar);
			bars.setAllBars(allbars);
			exBar.setRoot(bars);
			exBar.setParent(bars);
			
			}catch(Exception ex){
			log.warning(ex.toString());
			
			}
			*/
			
			//em.persist(exBar);
			return ;
			
			
		}

		@Override
		public void historicalDataEnd() {
			log.info("historicalDataEnd");
			
			finished=true;
			
			/*
			try{
				//ut.begin();
				//em.persist(bars);
				//ut.commit();
			}catch(Exception ex){
				log.warning(ex.toString());
				
				try{
					ut.rollback();
				}
				catch(IllegalStateException | SecurityException | SystemException e){
					log.warning(e.toString());
					
				}
				
				//em.getTransaction().rollback();
			}
			*/
			
		}
		
	}

}
