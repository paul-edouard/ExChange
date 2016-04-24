package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Bar;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.server.ejb.ib.ConnectionBean;

public enum HistoricalBarLoader implements IHistoricalDataHandler{
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(HistoricalBarLoader.class.getName());
	
	
	private EntityManager em;
	
	private List<IbContract> contracts;
	
	private long lastShortTermTrigger=0;
	
	private long currentShortTermTrigger=0;
	
	private LinkedList<Long> lastRequests=new LinkedList<>();
	

	
	
	private boolean isRunning=false;
	
	private HashMap<Long, Integer> longTermIdAttemptMap=new HashMap<>();
	
	
	
	
	/**
	 * Start the loading of the short time bars and
	 *  also the long term bar in order to fill the database
	 */
	public void run(){
		isRunning=true;
		
//		Clear the map search for the containers
		longTermIdAttemptMap.clear();
		List<IbBarContainer> containers=searchAllContainers();
		
		while(searchLongTermBar()){
			
//			Switch to the short term modus if new fresh data are required
			boolean shortTermModus=areShortTermBarRequired();
			
			
			for(IbBarContainer container:containers){
//				Break if the connection to TWS was broken
				if(!ConnectionBean.INSTANCE.isConnected())break;
				
				if(shortTermModus){
					loadShortTermBar(container);
				}
				else{
					loadLongTermBar(container);
				}
			
			}
			
//			Break if the connection to TWS was broken
			if(!ConnectionBean.INSTANCE.isConnected())break;
			
//			Refresh the container for the next attempt
			containers=searchAllContainers();
		}
		
		
		isRunning=false;
	}
	
	private synchronized boolean areShortTermBarRequired(){
		boolean shortTermModus=false;
		if(lastShortTermTrigger > currentShortTermTrigger){
			shortTermModus=true;
			currentShortTermTrigger=lastShortTermTrigger;
		}
		return shortTermModus;
	}
	
	
	private void loadShortTermBar(IbBarContainer container){
		log.info("Load short term bar: "+container.getType().toString());
		long from=Calendar.getInstance().getTimeInMillis();
		List<Bar> secondeBars = loadSecondeBar(container, from);
//		List<Bar> minuteBars = loadMinuteBar(container, from);
		HistoricalBarPersistance.saveBars(em, container, BarType.SECONDE, secondeBars);
		
		
//		em.
		
	}
	
	private void loadLongTermBar(IbBarContainer container){
		log.info("Load long term bar: "+container.getType().toString());
		long from=Calendar.getInstance().getTimeInMillis();
//		List<Bar> secondeBars = loadSecondeBar(container, from);
//		List<Bar> minuteBars = loadMinuteBar(container, from);
		
		longTermIdAttemptMap.put(container.getId(), longTermIdAttemptMap.get(container.getId())+1);
		
	}
	
	
	
	
	
	
	private List<IbBarContainer> searchAllContainers(){
		
		List<IbBarContainer> allContainers=new LinkedList<IbBarContainer>();
		for (IbContract exContract : contracts) {
			List<IbBarContainer> containers=BarMsgDrivenBean.getBarContainersOf(exContract, em);
			allContainers.addAll(containers);
		}
		
		for(IbBarContainer container:allContainers){
			
			if(longTermIdAttemptMap.containsKey(container.getId()))
				continue;
			
			longTermIdAttemptMap.put(container.getId(), 0);
		}
		
		return allContainers;
	}
	
	private boolean searchLongTermBar(){
		for(Integer value:longTermIdAttemptMap.values()){
			if(value<3)return true;
		}
		return false;
	}
	

	public synchronized void setContracts(List<IbContract> contracts) {
		this.contracts = contracts;
	}
	
	public synchronized void removeContract(int contractId) {
		for(IbContract contract:contracts){
			if(contract.getId()==contractId){
				contracts.remove(contract);
				break;
			}
		}
	}

	public synchronized void setEntityManager(EntityManager em) {
		if(this.em!=null)return;
		this.em = em;
	}


	public long getLastShortTermTrigger() {
		return lastShortTermTrigger;
	}

	public void setLastShortTermTrigger(long lastShortTermTrigger) {
//		log.info("Set last short term trigger");
		this.lastShortTermTrigger = lastShortTermTrigger;
		
		if(!this.isRunning){
			this.run();
		}
		
	}
	
	
//	###############################################
//	###               IB REQUEST                ###
//	###############################################
	
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query
	
	private static final long MIN_REQUEST_INTERVALL=10*60*1000;
	
	private static final long REQUEST_TIMOUT=2000;
	
	private long requestStartTime=0;
	
	private List<Bar> recievedBars=new LinkedList<>();
	
	private boolean requestFinished=false;
	
	private List<Bar> loadSecondeBar(IbBarContainer container, long from){
		int duration=1800;
		DurationUnit durationUnit=DurationUnit.SECOND;
		BarSize barSize=BarSize._1_secs;
		
		return loadBars(container, from, duration, durationUnit, barSize);
	}
	
	private List<Bar> loadMinuteBar(IbBarContainer container, long from){
		int duration=1;
		DurationUnit durationUnit=DurationUnit.DAY;
		BarSize barSize=BarSize._1_min;
		
		return loadBars(container, from, duration, durationUnit, barSize);
	}
	
	
	private List<Bar> loadBars(IbBarContainer container, long from, int duration, DurationUnit durationUnit, BarSize barSize ){
		requestFinished=false;
		recievedBars.clear();
		
//		Wait until the new request can be accepted
		waitUntilNewRequestIsAccepted();
		
//		Start the new request
		String date=FORMAT.format( new Date(from) );
		ConnectionBean.INSTANCE.controller().reqHistoricalData(container.getContract().getNewContract(),
				date, 									//The new Data
				duration,								//duration in (secondes)
				durationUnit,							//(secondes)
				barSize,								//bar Size
				container.getType(), 					//Ex MIDPOINT, BID, ASK
				false,									//RTh only
				this);
		
		requestStartTime=new Date().getTime();
		
		
//		Wait of the ib answer
		waitForIbAnswer();
		
		return recievedBars;
	}
	
	
	private void waitUntilNewRequestIsAccepted(){
		//Test if more than 60 historical data were starter in the last 60 minutes
		while(!acceptNewRequest()){
			log.info("New request denied! Please wait a little bit");
			//return recievedBars;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
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
		
		log.info("Diff: "+diff+ " min intervall: " +MIN_REQUEST_INTERVALL );
		
		
		if(diff > MIN_REQUEST_INTERVALL){
			lastRequests.remove(0);
			lastRequests.addLast(date.getTimeInMillis());
			return true;
		}
		
		return false;
	}
	
	
	private void waitForIbAnswer(){
		
		while( true ){
			
			if(requestFinished)
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
		return new Date().getTime()-requestStartTime>REQUEST_TIMOUT;
	}

	@Override
	public void historicalData(Bar bar, boolean hasGaps) {
		log.info("New bar recieved: "+bar.toString());
		recievedBars.add(bar);
	}


	@Override
	public void historicalDataEnd() {
		requestFinished=true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
