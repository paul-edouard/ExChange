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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.TimeBarSize;
import com.munch.exchange.model.core.ib.bar.BarComparator;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.server.ejb.ib.ConnectionBean;

public class HistoricalBarLoader implements IHistoricalDataHandler{
	
	
	private static final Logger log = Logger.getLogger(HistoricalBarLoader.class.getName());

	private static HistoricalBarLoader INSTANCE=null;
	
	private static boolean isDataFarmAvailable=true;
	
	public static synchronized void setDataFarmAvailibility(boolean isAvailable){
		isDataFarmAvailable=isAvailable;
	}
	
	public static HistoricalBarLoader getINSTANCE() {
		return INSTANCE;
	}
	
	private EntityManager em;
	
	private UserTransaction ut;
	
	private List<IbContract> contracts;
	
	private long lastShortTermTrigger=0;
	
	private long currentShortTermTrigger=0;
	
	

	private boolean running=false;
	
	private boolean isCurrentBarListReduced=false;
	
//	private HashMap<Long, Integer> longTermIdAttemptMap=new HashMap<>();
	

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
		
//		Test if the request is between 23h00 and 23h15 at this time there is no server response!
//		if(!isIbHDMSServerOnline()){
//			running=false;return;
//		}
		
//		Clear the map search for the containers
		numberOfParsingViolationAttempt=0;
		List<BarContainer> containers=searchAllContainers();

//		Switch to the short term modus if new fresh data are required
		boolean shortTermModus=areShortTermBarRequired();

		
		while( searchLongTermBar(containers) || shortTermModus){
			
			
			for(BarContainer container:containers){
				if(numberOfParsingViolationAttempt>=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT)
					break;
				
//				Break if the connection to TWS was broken
				if(!ConnectionBean.INSTANCE.isConnected())break;
				
//				Break id the HDMS server is not online
				if(!isIbHDMSServerOnline())break;
				
				if(shortTermModus){
					loadShortTermBar(container);
				}
				else{
					loadLongTermBar(container);
				}
				
				
			}
			
//			Break if the number of pasing attempt was reached
			if(numberOfParsingViolationAttempt>=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT)
				break;
			
//			Break if the connection to TWS was broken
			if(!ConnectionBean.INSTANCE.isConnected())break;
			
//			Break id the HDMS server is not online
			if(!isIbHDMSServerOnline())break;
			
//			Refresh the container for the next attempt
			containers=searchAllContainers();

//			Switch to the short term modus if new fresh data are required
			shortTermModus=areShortTermBarRequired();
		}
		
		
		running=false;
		
		
		log.info("Historical data loader is done!");
		
	}
	
	
	private synchronized boolean isIbHDMSServerOnline(){
		Calendar currentTime=Calendar.getInstance();
		if(currentTime.get(Calendar.HOUR_OF_DAY)==23){
			if(currentTime.get(Calendar.MINUTE)>=0 && currentTime.get(Calendar.MINUTE)<=15){
				log.info("Sorry at this time "+BarUtils.format(currentTime.getTimeInMillis())+" there is no server responce!");
				return false;
			}
		}
		
		return isDataFarmAvailable;
	}
	
	
//	###############################################
//	###             SHORT TERM BAR              ###
//	###############################################
	
	private synchronized boolean areShortTermBarRequired(){
		boolean shortTermModus=false;
		if(lastShortTermTrigger > currentShortTermTrigger){
			shortTermModus=true;
			currentShortTermTrigger=lastShortTermTrigger;
		}
		return shortTermModus;
	}
	
	
	private void loadShortTermBar(BarContainer container){
		log.info("Load short term bar: "+container.getContract().getSymbol()+
				", "+container.getType().toString());
		
//		log.info("Start the loading of the second short term bar!");
		if(!loadShortTermTypedBars(container, TimeBarSize.SECOND)){
			return;
		}
		
//		log.info("Start the loading of the minute short term bar!");
		if(!loadShortTermTypedBars(container, TimeBarSize.MINUTE)){
			return;
		}
		
		
	}
	
	private boolean loadShortTermTypedBars(BarContainer container, TimeBarSize barType){
		
		try {
		ut.begin();
		

//		Load the short term seconde bars
		long lastBarTimeInSeconde=HistoricalBarPersistance.getLastBarTime(em, container, barType);
		if(lastBarTimeInSeconde==0){
			log.warning("Load short term couldn't be started! no bar were found!");
			ut.commit();
			return true;
		}
		
//		log.info("Last bar found: "+FORMAT.format( new Date(lastBarTimeInSeconde*1000) ));
//		
		
//		Search the last recorded short bar time
		long from=HistoricalBarPersistance.getLastShortTermBarTime(em, container, barType);
		if(from==0){
			from=Calendar.getInstance().getTimeInMillis()/1000;
		}
		
//		Test if we are in the week end
		if(isWeekEndBreak(lastBarTimeInSeconde, from, barType)){
			log.info("This is the week end break! Please wait until sunday evening for new data!");
			ut.commit();
			return false;
		}
		
//		Load the last bars
		Calendar start=Calendar.getInstance();
		List<Bar> loadedBars = loadTypeBar(container, barType, from*1000, true);
		if(!ConnectionBean.INSTANCE.isConnected() ||
				numberOfParsingViolationAttempt>=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT || 
				!isIbHDMSServerOnline() ||
				loadedBars==null){
			numberOfParsingViolationAttempt=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT;
			log.warning("Loading error! no bar were found!");
			ut.commit();
			return false;
		}
	
		while(!loadedBars.isEmpty()){
			
			
//			save the bar in the db
			HistoricalBarPersistance.saveBars(em, container, barType, loadedBars);
			
			sendLoadingMessage("Persist short term bar: ", container, barType, start, from*1000, loadedBars);
			
//			If some of the bar were reduced then break the loop, the gab is closed
			if(isCurrentBarListReduced || loadedBars.isEmpty()){
				break;
			}
			
//			Save the last short time bar position in order to restart from save
			from=loadedBars.get(0).time();
			HistoricalBarPersistance.setLastShortTermBarTime(em, container, barType, from);
			
			ut.commit();
			
			ut.begin();
			
//			Load the new bars from the new start
			start=Calendar.getInstance();
			loadedBars = loadTypeBar(container, barType, from*1000, true);
			
			if(!ConnectionBean.INSTANCE.isConnected() ||
					numberOfParsingViolationAttempt>=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT || 
					!isIbHDMSServerOnline() || 
					loadedBars==null){
				ut.commit();
				return false;
			}
			
		}
		
//		Set the last short term bar to zero in order to restart from the current time
		HistoricalBarPersistance.setLastShortTermBarTime(em, container, barType,0);
		
		
		ut.commit();
		} catch (NotSupportedException | SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException | SystemException e) {
			
			e.printStackTrace();
			log.warning(e.toString());
			
			try {
				ut.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				
				e1.printStackTrace();
				log.warning(e.toString());
			}
			
			
		}
		
		
		
		return true;
		
	}
	
	private boolean isWeekEndBreak(long lastBarTimeInSeconde, long from, TimeBarSize barType){
		Calendar lastBarDate=Calendar.getInstance();
		lastBarDate.setTimeInMillis(lastBarTimeInSeconde*1000);
		
//		log.info("Day of week: "+lastBarDate.get(Calendar.DAY_OF_WEEK));
//		log.info("Hours of day: "+lastBarDate.get(Calendar.HOUR_OF_DAY));
//		log.info("minute: "+lastBarDate.get(Calendar.MINUTE));
//		log.info("second: "+lastBarDate.get(Calendar.SECOND));
		
		if(lastBarDate.get(Calendar.DAY_OF_WEEK)!=6)return false;
		if(lastBarDate.get(Calendar.HOUR_OF_DAY)!=22)return false;
		if(lastBarDate.get(Calendar.MINUTE)!=59)return false;
		if(barType==TimeBarSize.MINUTE){
			if(lastBarDate.get(Calendar.SECOND)!=0)return false;
		}
		else{
			if(lastBarDate.get(Calendar.SECOND)!=59)return false;
		}

		
		long SecondeDiff=from-lastBarTimeInSeconde;
		if(SecondeDiff < 0 )return false;
		
		long TwoDaysSecond=172800;
		
//		log.info("SecondeDiff: "+SecondeDiff);
		
		
		if(SecondeDiff < TwoDaysSecond )return true;
		
		
		return false;
	}
	
	
	private void sendLoadingMessage(String message, BarContainer container, TimeBarSize barType,Calendar start, long from, List<Bar> loadedBars){
		Calendar end=Calendar.getInstance();
		long time_s=(end.getTimeInMillis()-start.getTimeInMillis());
		log.info(message+container.getContract().getSymbol()+", "+
				container.getType().toString()+" "+loadedBars.size()+" "+
				barType.toString()+" bars from "+
				FORMAT.format( new Date(from) )+" in "+time_s+ " ms");
	}
	
//	###############################################
//	###             LONG TERM BAR               ###
//	###############################################
	
	private void loadLongTermBar(BarContainer container){
//		log.info("Load long term bar: "+container.getType().toString());
//		
//		#########################################
//		##   Load the long term second bars   ##
//		#########################################
		loadLongTermTypeBar(container, TimeBarSize.SECOND);
		
//		#########################################
//		##   Load the long term minute bars   ##
//		#########################################
		loadLongTermTypeBar(container, TimeBarSize.MINUTE);
		
		
	}
	
	
	private void loadLongTermTypeBar(BarContainer container, TimeBarSize bartype){
		
		if(!ConnectionBean.INSTANCE.isConnected())
			return;
		if(numberOfParsingViolationAttempt>=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT)
			return;
		
		
		try {
			ut.begin();
			
//			Test at first is the loading is finished
			if (HistoricalBarPersistance.isLongTermBarLoadingFinished(em, container, bartype)) {
				ut.commit();
				return;
			}
			
			// Search the first bar
			long firstSecondBarTime = HistoricalBarPersistance.getFirstBarTime(em, container, bartype);
			firstSecondBarTime *= 1000;
			if (firstSecondBarTime == 0) {
				firstSecondBarTime = Calendar.getInstance().getTimeInMillis();
			}
			
			List<Bar> typedBars = null;
			int nbOfAttempt = 0;
			while (nbOfAttempt < MAX_NUMBER_OF_LONG_TERM_ATTEMPT) {
				nbOfAttempt++;
				
				Calendar start = Calendar.getInstance();
				
				typedBars = loadTypeBar(container, bartype, firstSecondBarTime, false);
				
//				Return if one of the criterion is met
				if(!ConnectionBean.INSTANCE.isConnected()){
					ut.commit();
					return;
				}
				if(numberOfParsingViolationAttempt>=MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT){
					ut.commit();
					return;
				}
				if(!isIbHDMSServerOnline()){
					ut.commit();
					return;
				}
				
				
				// Error the request was wrong
				if (typedBars == null || typedBars.isEmpty() || isRequestTimoutReached) {
					
//					Remove one day from the first bar time and try again!
					firstSecondBarTime-=24L*60L*60L*1000L;
					
					log.warning("Retry loading bar from: " + container.getContract().getSymbol() + ", "
							+ container.getType().toString() + ", " + bartype.toString() + " from date: "
							+ FORMAT.format(new Date(firstSecondBarTime)));
					
					continue;
				}


				// Save the bar in the DB and return
				HistoricalBarPersistance.saveBars(em, container, bartype, typedBars);

				sendLoadingMessage("Persist long term bar: ", container, bartype, start, firstSecondBarTime, typedBars);

				ut.commit();
				
				return;
			}
			
//			Close the loading of Long term bar after the attempts
			log.warning("Warning by loading: " + container.getContract().getSymbol() + ", "
					+ container.getType().toString() + ", " + bartype.toString() + " no bar found before: "
					+ FORMAT.format(new Date(firstSecondBarTime)));
			
			HistoricalBarPersistance.setLongTermBarLoadingFinished(em, container, bartype, true);
			
			ut.commit();
			
			
		} catch (NotSupportedException | SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException | SystemException e) {

			e.printStackTrace();
			log.warning(e.toString());

			try {
				ut.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {

				e1.printStackTrace();
				log.warning(e.toString());
			}

		}
		
		
	}
	
	
	private List<BarContainer> searchAllContainers(){
		
		List<BarContainer> allContainers = new LinkedList<BarContainer>();

		try {
			ut.begin();

			for (IbContract exContract : contracts) {
				List<BarContainer> containers = BarMsgDrivenBean.getBarContainersOf(exContract, em);
				allContainers.addAll(containers);
			}			

			ut.commit();
		} catch (NotSupportedException | SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException | SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allContainers;
	}
	
	private boolean searchLongTermBar(List<BarContainer> containers){
		

		boolean searchActivated=false;
		
		try {
			ut.begin();
			
			for(BarContainer container:containers){
				if(!HistoricalBarPersistance.isLongTermBarLoadingFinished(em, container, TimeBarSize.SECOND)){
					searchActivated=true;
					break;
				}
				
				if(!HistoricalBarPersistance.isLongTermBarLoadingFinished(em, container, TimeBarSize.MINUTE)){
					searchActivated=true;
					break;
				}
		
				
			}
			

			ut.commit();
			
			
		} catch (NotSupportedException | SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException | SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return searchActivated;
		
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

	public synchronized void setEMandUT(EntityManager em, UserTransaction ut) {
		this.em = em;
		this.ut = ut;

		try {
			ut.begin();

			List<IbContract> allContracts = em.createNamedQuery("IbContract.getAll", IbContract.class).getResultList();
			this.setContracts(allContracts);

			ut.commit();
		} catch (NotSupportedException | SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException | SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
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
	
	private static final long MAX_NUMBER_OF_LONG_TERM_ATTEMPT=5;
	
	private static final long REQUEST_TIMOUT=10000;
	
	private static final long PARSING_VIOLATION_SLEEP=3000;
	
	private static final int MAX_SECONDE_DURATION_IN_SECOND=1800;
	
	private static final int MAX_MINUTE_DURATION_IN_DAY=1;
	
	private static final int MAX_MINUTE_DURATION_IN_SECOND=86400;
	
	private static final int MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT=15;
	
	private int numberOfParsingViolationAttempt=0;
	
	private static LinkedList<Long> lastRequests=new LinkedList<>();

	private long requestStartTime=0;
	
	private List<Bar> recievedBars=new LinkedList<>();
	
	private boolean requestFinished=false;
	
	private boolean isRequestTimoutReached=false;
	
	private List<Bar> loadTypeBar(BarContainer container,TimeBarSize barType, long from, boolean removeDuplicated){
//		int duration=MAX_SECONDE_DURATION_IN_SECONDE;
		
		em.flush();
		em.clear();
		
		isCurrentBarListReduced=false;
		
		DurationUnit durationUnit=DurationUnit.SECOND;
		BarSize barSize=BarSize._1_secs;
		int max_duration=MAX_SECONDE_DURATION_IN_SECOND;
		
		switch (barType) {
		case SECOND:
			durationUnit=DurationUnit.SECOND;
			barSize=BarSize._1_secs;
			max_duration=MAX_SECONDE_DURATION_IN_SECOND;
			break;
		case MINUTE:
//			durationUnit=DurationUnit.DAY;
			durationUnit=DurationUnit.SECOND;
			barSize=BarSize._1_min;
//			max_duration=MAX_MINUTE_DURATION_IN_DAY;
			max_duration=MAX_MINUTE_DURATION_IN_SECOND;
			break;
			
		default:
			break;
		}
		
		
		List<Bar> bars=loadBars(container, from, max_duration, durationUnit, barSize);
		if(bars==null)return null;
		
		
		if(removeDuplicated){
			LinkedList<Bar> barToDelete=new LinkedList<>();
			
//			for(Bar bar:bars){
//				long time=bar.time();
//				if(HistoricalBarPersistance.containsBar(em, container, barType, time)){
//					log.info("Bar to remove: "+bar.toString());
//					barToDelete.add(bar);
//				}
//			}
			
//			Fill the bar to delete list
			long start=Long.MAX_VALUE;
			long end = Long.MIN_VALUE;
			for(Bar bar:bars){
				long time=bar.time();
				if(time < start)
					start = time;
				if(time > end)
					end = time;
			}
			
			List<ExBar> savedBars=HistoricalBarPersistance.getBarsFromTo(em, container, barType, start, end);
//			log.info("Nb of saved bars: "+savedBars.size());
			for(Bar bar:bars){
				long time=bar.time();
//				log.info("bar Time: "+time);
				boolean isSaved=false;
				for(ExBar exBar:savedBars){
//					log.info("exBar Time: "+exBar.getTimeInSec());
					if(exBar.getTimeInSec()==time){
						isSaved=true;
						break;
					}
				}
				
				if(isSaved)
					barToDelete.add(bar);
			}
			
			
			
			
			
			if(barToDelete.size() > 0){
				log.info("Nb of bar to delete: "+barToDelete.size());
				isCurrentBarListReduced=true;
				bars.removeAll(barToDelete);
			}
			
//			while(true){
//				if(bars.isEmpty())break;
//				
//				long time=bars.get(0).time();
//				if(HistoricalBarPersistance.containsBar(em, container, barType, time)){
//					isCurrentBarListReduced=true;
//					bars.remove(0);
//					continue;
//				}
//				
//				break;
//			}
			
			
		}
		
		return bars;
	}
	
	
	
	private List<Bar> loadBars(BarContainer container, long from, int duration, DurationUnit durationUnit, BarSize barSize ){
		requestFinished=false;
		isRequestTimoutReached=false;
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
		if(!waitForIbAnswer()){
			return null;
		}
		
//		Sort the recieved bars
		Collections.sort(recievedBars, new BarComparator());
		
		
		if(!recievedBars.isEmpty())
			numberOfParsingViolationAttempt=0;
		
		return recievedBars;
	}
	
	
	private boolean waitUntilNewRequestIsAccepted(){
		//Test if more than 60 historical data were starter in the last 60 minutes
		while(!acceptNewRequest()){
//			log.info("New request denied! Please wait a little bit");
			
			if(!ConnectionBean.INSTANCE.isConnected())return false;
			if(numberOfParsingViolationAttempt>MAX_NUMBER_OF_PARSING_VIOLATION_ATTEMPT)return false;
			
			try {
				Thread.sleep(PARSING_VIOLATION_SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			numberOfParsingViolationAttempt++;
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
	
	
	private boolean waitForIbAnswer(){
		
		while( true ){
			try {
				
				Thread.sleep(500);
				
			if(requestFinished){
				return true;
			}
			if(isTimeOut() && recievedBars.isEmpty()){
				log.info("Request Time out is reached!");
				isRequestTimoutReached=true;
				return false;
			}
			
			
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		
		return false;
		
		
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
