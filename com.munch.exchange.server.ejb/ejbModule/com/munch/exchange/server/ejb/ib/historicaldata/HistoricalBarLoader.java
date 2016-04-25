package com.munch.exchange.server.ejb.ib.historicaldata;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.server.ejb.ib.ConnectionBean;

public class HistoricalBarLoader implements IHistoricalDataHandler{
	
	
	private static final Logger log = Logger.getLogger(HistoricalBarLoader.class.getName());

	private static HistoricalBarLoader INSTANCE=null;
	
	public static HistoricalBarLoader getINSTANCE() {
		return INSTANCE;
	}
	
	private EntityManager em;
	
	private List<IbContract> contracts;
	
	private long lastShortTermTrigger=0;
	
	private long currentShortTermTrigger=0;
	
	

	private boolean running=false;
	
	private HashMap<Long, Integer> longTermIdAttemptMap=new HashMap<>();
	

	public HistoricalBarLoader() {
		super();
		INSTANCE=this;
	}
	
	

	






	/**
	 * Start the loading of the short time bars and
	 *  also the long term bar in order to fill the database
	 */
	public void run(){
		log.info("Historical data loader is ready to run!");
		
		running=true;
		
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
		
		
		running=false;
		
		
		log.info("Historical data loader is done!");
		
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
		
		log.info("Start the loading of the second short term bar!");
		if(!loadShortTermTypedBars(container, BarType.SECOND)){
			return;
		}
		
		log.info("Start the loading of the minue short term bar!");
		if(!loadShortTermTypedBars(container, BarType.MINUTE)){
			return;
		}
		
		
	}
	
	private boolean loadShortTermTypedBars(IbBarContainer container, BarType barType){
		

//		Load the short term seconde bars
		long lastBarTimeInSeconde=HistoricalBarPersistance.getLastBarTime(em, container, barType);
		if(lastBarTimeInSeconde==0){
			log.warning("Load short term couldn't be started! no bar were found!");
			return true;
		}
		
//		Search the last recorded short bar time
		long from=HistoricalBarPersistance.getLastShortTermBarTime(em, container, barType);
		if(from==0){
			from=Calendar.getInstance().getTimeInMillis()/1000;
		}
		
		
//		Load the last bars
		List<Bar> loadedBars = loadTypeBar(container, barType, from*1000, true);
		if(loadedBars==null)return false;
	
		while(!loadedBars.isEmpty()){
			
			Bar firstLoadedBar=loadedBars.get(0);
			Bar lastLoadedBar=loadedBars.get(loadedBars.size()-1);
			
			log.info("Fist bar time loaded: "+FORMAT.format(firstLoadedBar.time()*1000)+
					", last bar time loaded: "+FORMAT.format(lastLoadedBar.time()*1000));
			
			log.info("lastBarTimeInSeconde: "+FORMAT.format(from*1000));
			
//			save the bar in the db
			HistoricalBarPersistance.saveBars(em, container, barType, loadedBars);
			
//			Save the last short time bar position in order to restart from save
			from=loadedBars.get(0).time();
			HistoricalBarPersistance.setLastShortTermBarTime(em, container, barType, from);
			
//			Load the new bars from the new start
			loadedBars = loadTypeBar(container, barType, from*1000, true);
			if(loadedBars==null)return false;
			
		}
		
//		Set the last short term bar to zero in order to restart from the current time
		HistoricalBarPersistance.setLastShortTermBarTime(em, container, barType,0);
		return true;
		
	}
	
	
	
	private void loadLongTermBar(IbBarContainer container){
//		log.info("Load long term bar: "+container.getType().toString());
//		
//		#########################################
//		##   Load the long term second bars   ##
//		#########################################
		
		Calendar start=Calendar.getInstance();
		long firstSecondBarTime=HistoricalBarPersistance.getFirstBarTime(em, container, BarType.SECOND);
//		log.info("First Seconde bar time: "+firstSecondeBarTime);
		firstSecondBarTime*=1000;
		if(firstSecondBarTime==0){
			firstSecondBarTime=Calendar.getInstance().getTimeInMillis();
		}
		
		List<Bar> secondeBars = loadTypeBar(container, BarType.SECOND, firstSecondBarTime, false);
//		TODO Please this could be null!!
		
		if(!secondeBars.isEmpty()){
			HistoricalBarPersistance.saveBars(em, container, BarType.SECOND, secondeBars);
			
			Calendar end=Calendar.getInstance();
			long time_s=(end.getTimeInMillis()-start.getTimeInMillis());
			log.info("Persist: "+container.getContract().getSymbol()+", "+
					container.getType().toString()+" "+secondeBars.size()+" "+
					BarType.SECOND.toString()+" bars from "+
					FORMAT.format( new Date(firstSecondBarTime) )+" in "+time_s+ " ms");
			
		}
		
		

		
//		#########################################
//		##   Load the long term minute bars   ##
//		#########################################
		
//		Load the long term minute bars
		start=Calendar.getInstance();
		long firstMinuteBarTime=HistoricalBarPersistance.getFirstBarTime(em, container, BarType.MINUTE);
		firstMinuteBarTime*=1000;
		if(firstMinuteBarTime==0){
			firstMinuteBarTime=Calendar.getInstance().getTimeInMillis();
		}
		
		List<Bar> minuteBars = loadTypeBar(container, BarType.MINUTE, firstMinuteBarTime, false);
		if(!minuteBars.isEmpty()){
			HistoricalBarPersistance.saveBars(em, container, BarType.MINUTE, minuteBars);
			
			Calendar end=Calendar.getInstance();
			long time_s=(end.getTimeInMillis()-start.getTimeInMillis());
			log.info("Persist: "+container.getContract().getSymbol()+", "+
					container.getType().toString()+" "+minuteBars.size()+" "+
					BarType.MINUTE.toString()+" bars from "+
					FORMAT.format( new Date(firstMinuteBarTime) )+" in "+time_s+ " ms");
			
		}
		
		
//		
		if(secondeBars.isEmpty() && minuteBars.isEmpty()){
			longTermIdAttemptMap.put(container.getId(), longTermIdAttemptMap.get(container.getId())+1);
		}
		
		
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
		if(this.em!=null){
			this.em.flush();
			this.em.clear();
		}
		
		List<IbContract> allContracts = em.createNamedQuery(
				"IbContract.getAll", IbContract.class).getResultList();
		this.setContracts(allContracts);
		this.em = em;
	}


	public long getLastShortTermTrigger() {
		return lastShortTermTrigger;
	}

	public synchronized void setLastShortTermTrigger(long lastShortTermTrigger) {
//		log.info("Set last short term trigger");
		this.lastShortTermTrigger = lastShortTermTrigger;
		
//		if(!this.running){
//			this.run();
//		}
		
	}
	
	public synchronized boolean isRunning() {
		return running;
	}
	
	
//	###############################################
//	###               IB REQUEST                ###
//	###############################################
	


	private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query
	
	private static final long MIN_REQUEST_INTERVALL=10*60*1000;
	
	private static final long REQUEST_TIMOUT=2000;
	
	private static final int MAX_SECONDE_DURATION_IN_SECONDE=1800;
	
	private static final int MAX_MINUTE_DURATION_IN_DAY=1;
	
	private static LinkedList<Long> lastRequests=new LinkedList<>();

	
	private long requestStartTime=0;
	
	private List<Bar> recievedBars=new LinkedList<>();
	
	private boolean requestFinished=false;
	
	private List<Bar> loadTypeBar(IbBarContainer container,BarType barType, long from, boolean removeDuplicated){
//		int duration=MAX_SECONDE_DURATION_IN_SECONDE;
		
		em.flush();
		em.clear();
		
		DurationUnit durationUnit=DurationUnit.SECOND;
		BarSize barSize=BarSize._1_secs;
		int max_duration=MAX_SECONDE_DURATION_IN_SECONDE;
		
		switch (barType) {
		case SECOND:
			durationUnit=DurationUnit.SECOND;
			barSize=BarSize._1_secs;
			max_duration=MAX_SECONDE_DURATION_IN_SECONDE;
			break;
		case MINUTE:
			durationUnit=DurationUnit.DAY;
			barSize=BarSize._1_min;
			max_duration=MAX_MINUTE_DURATION_IN_DAY;
			break;
			
		default:
			break;
		}
		
		
		List<Bar> bars=loadBars(container, from, max_duration, durationUnit, barSize);
		if(bars==null)return null;
		
		if(removeDuplicated){
			while(true){
				if(bars.isEmpty())break;
				
				long time=bars.get(0).time();
				if(HistoricalBarPersistance.containsBar(em, container, BarType.SECOND, time)){
					bars.remove(0);
					continue;
				}
				
				break;
			}
		}
		
		return bars;
	}
	
	
	
	private List<Bar> loadBars(IbBarContainer container, long from, int duration, DurationUnit durationUnit, BarSize barSize ){
		requestFinished=false;
		recievedBars.clear();
		
//		Wait until the new request can be accepted id return null the process have to be closed
		if(!waitUntilNewRequestIsAccepted()){
			return null;
		}
		
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
		
//		Sort the recieved bars
		Collections.sort(recievedBars, new Comparator<Bar>() {

			@Override
			public int compare(Bar arg0, Bar arg1) {
				if(arg0.time() > arg1.time())
					return 1;
				else if(arg0.time() < arg1.time()){
					return -1;
				}
				
				return 0;
			}
			
		});
		
		return recievedBars;
	}
	
	
	private boolean waitUntilNewRequestIsAccepted(){
		//Test if more than 60 historical data were starter in the last 60 minutes
		int i=0;
		while(!acceptNewRequest()){
//			log.info("New request denied! Please wait a little bit");
			
			if(!ConnectionBean.INSTANCE.isConnected())return false;
			if(i>1)return false;
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			i++;
		}
		
		return true;
		
	}
	
	private static synchronized boolean acceptNewRequest(){
		
		if(lastRequests.size()>2){
			long diff=lastRequests.getLast()-lastRequests.getFirst();
			Calendar cal=Calendar.getInstance();cal.setTimeInMillis(diff);
			//log.info("Number of request: "+lastRequests.size()+ " in the last: " +cal.get(Calendar.MINUTE)+"min "+cal.get(Calendar.SECOND)+"s ");
		}
		
		
		Calendar date=Calendar.getInstance();
		if(lastRequests.size()<60){
			lastRequests.add(date.getTimeInMillis());
			return true;
		}
		
		long first=lastRequests.getFirst();
		long diff=date.getTimeInMillis()-first;
		
//		log.info("Diff: "+diff+ " min intervall: " +MIN_REQUEST_INTERVALL );
		
		
		if(diff > MIN_REQUEST_INTERVALL){
			lastRequests.remove(0);
			lastRequests.addLast(date.getTimeInMillis());
			return true;
		}
		
		log.info("Please wait "+(MIN_REQUEST_INTERVALL-diff)/1000+" secondes until the next  historical data call!" );
		
		
		return false;
	}
	
	
	private void waitForIbAnswer(){
		
		while( true ){
			try {
				
				Thread.sleep(500);
				
			if(requestFinished){
				break;
			}
			if(isTimeOut() && recievedBars.isEmpty())
				break;
			
			
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
//		log.info("New bar recieved: "+bar.toString());
		
		requestStartTime=new Date().getTime();
		
		recievedBars.add(bar);
	}

	@Override
	public void historicalDataEnd() {
		requestFinished=true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
