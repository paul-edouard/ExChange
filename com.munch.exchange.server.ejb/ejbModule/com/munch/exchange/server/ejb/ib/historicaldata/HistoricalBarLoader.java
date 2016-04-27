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
	
	private UserTransaction ut;
	
	private List<IbContract> contracts;
	
	private long lastShortTermTrigger=0;
	
	private long currentShortTermTrigger=0;
	
	

	private boolean running=false;
	
	private static final int maxNumberOfParsingViolationAttempt=10;
	
	private int numberOfParsingViolationAttempt=0;
	
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
		
//		Clear the map search for the containers
		numberOfParsingViolationAttempt=0;
		List<IbBarContainer> containers=searchAllContainers();

//		Switch to the short term modus if new fresh data are required
		boolean shortTermModus=areShortTermBarRequired();

		
		while( searchLongTermBar(containers) || shortTermModus){
			
			
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
			
//			Break if the number of pasing attempt was reached
			if(numberOfParsingViolationAttempt>maxNumberOfParsingViolationAttempt)break;
			
			
//			Break if the connection to TWS was broken
			if(!ConnectionBean.INSTANCE.isConnected())break;
			
//			Refresh the container for the next attempt
			containers=searchAllContainers();

//			Switch to the short term modus if new fresh data are required
			shortTermModus=areShortTermBarRequired();
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
		
		log.info("Start the loading of the minute short term bar!");
		if(!loadShortTermTypedBars(container, BarType.MINUTE)){
			return;
		}
		
		
	}
	
	private boolean loadShortTermTypedBars(IbBarContainer container, BarType barType){
		
		try {
		ut.begin();
		

//		Load the short term seconde bars
		long lastBarTimeInSeconde=HistoricalBarPersistance.getLastBarTime(em, container, barType);
		if(lastBarTimeInSeconde==0){
			log.warning("Load short term couldn't be started! no bar were found!");
			ut.commit();
			return true;
		}
		
//		Search the last recorded short bar time
		long from=HistoricalBarPersistance.getLastShortTermBarTime(em, container, barType);
		if(from==0){
			from=Calendar.getInstance().getTimeInMillis()/1000;
		}
		
		
//		Load the last bars
		Calendar start=Calendar.getInstance();
		List<Bar> loadedBars = loadTypeBar(container, barType, from*1000, true);
		if(loadedBars==null){
			ut.commit();
			return false;
		}
	
		while(!loadedBars.isEmpty()){
			
			
//			save the bar in the db
			HistoricalBarPersistance.saveBars(em, container, barType, loadedBars);
			
//			If some of the bar were reduced then break the loop, the gab is closed
			if(isCurrentBarListReduced)
				break;
			
//			Save the last short time bar position in order to restart from save
			from=loadedBars.get(0).time();
			HistoricalBarPersistance.setLastShortTermBarTime(em, container, barType, from);
			
			
			Calendar end=Calendar.getInstance();
			long time_s=(end.getTimeInMillis()-start.getTimeInMillis());
			log.info("Persist short term bar: "+container.getContract().getSymbol()+", "+
					container.getType().toString()+" "+loadedBars.size()+" "+
					barType.toString()+" bars from "+
					FORMAT.format( new Date(from*1000) )+" in "+time_s+ " ms");
			
			ut.commit();
			ut.begin();
			
//			Load the new bars from the new start
			start=Calendar.getInstance();
			loadedBars = loadTypeBar(container, barType, from*1000, true);
			if(loadedBars==null){
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
	
	
	
	private void loadLongTermBar(IbBarContainer container){
//		log.info("Load long term bar: "+container.getType().toString());
//		
//		#########################################
//		##   Load the long term second bars   ##
//		#########################################
		loadLongTermTypeBar(container, BarType.SECOND);
		
//		#########################################
//		##   Load the long term minute bars   ##
//		#########################################
		loadLongTermTypeBar(container, BarType.MINUTE);
		
		
	}
	
	
	private void loadLongTermTypeBar(IbBarContainer container, BarType bartype){
		
		List<Bar> typedBars=null;
		
		try {
			ut.begin();
		
		
		if(HistoricalBarPersistance.isLongTermBarLoadingFinished(em, container, bartype)){
			ut.commit();return;
		}
			
			
		Calendar start=Calendar.getInstance();
		
//		Search the first bar
		long firstSecondBarTime=HistoricalBarPersistance.getFirstBarTime(em, container, bartype);
		firstSecondBarTime*=1000;
		if(firstSecondBarTime==0){
			firstSecondBarTime=Calendar.getInstance().getTimeInMillis();
		}
		
		typedBars = loadTypeBar(container, bartype, firstSecondBarTime, false);
//		Error the request was wrong
		if(typedBars==null){
			ut.commit();return;
		}
		
//		The last long term bar was reached!
		if(typedBars.isEmpty()){
			HistoricalBarPersistance.setLongTermBarLoadingFinished(em, container, bartype, true);
			ut.commit();
			return;
		}
		
//		Save the bar in the DB
		HistoricalBarPersistance.saveBars(em, container,bartype, typedBars);
			
		Calendar end=Calendar.getInstance();
		long time_s=(end.getTimeInMillis()-start.getTimeInMillis());
		log.info("Persist long term bar: "+container.getContract().getSymbol()+", "+
				container.getType().toString()+" "+typedBars.size()+" "+
				bartype.toString()+" bars from "+
				FORMAT.format( new Date(firstSecondBarTime) )+" in "+time_s+ " ms");
			
		
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
		
		
	}
	
	
	private List<IbBarContainer> searchAllContainers(){
		
		List<IbBarContainer> allContainers = new LinkedList<IbBarContainer>();

		try {
			ut.begin();

			for (IbContract exContract : contracts) {
				List<IbBarContainer> containers = BarMsgDrivenBean.getBarContainersOf(exContract, em);
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
	
	private boolean searchLongTermBar(List<IbBarContainer> containers){
		

		boolean searchActivated=false;
		
		try {
			ut.begin();
			
			for(IbBarContainer container:containers){
				if(!HistoricalBarPersistance.isLongTermBarLoadingFinished(em, container, BarType.SECOND)){
					searchActivated=true;
					break;
				}
				
				if(!HistoricalBarPersistance.isLongTermBarLoadingFinished(em, container, BarType.MINUTE)){
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
	
	private static final long REQUEST_TIMOUT=30000;
	
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
		
		isCurrentBarListReduced=false;
		
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
					isCurrentBarListReduced=true;
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
		if(!waitForIbAnswer()){
			return null;
		}
		
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
		
		
		if(!recievedBars.isEmpty())
			numberOfParsingViolationAttempt=0;
		
		return recievedBars;
	}
	
	
	private boolean waitUntilNewRequestIsAccepted(){
		//Test if more than 60 historical data were starter in the last 60 minutes
		while(!acceptNewRequest()){
//			log.info("New request denied! Please wait a little bit");
			
			if(!ConnectionBean.INSTANCE.isConnected())return false;
			if(numberOfParsingViolationAttempt>maxNumberOfParsingViolationAttempt)return false;
			
			try {
				Thread.sleep(2000);
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
