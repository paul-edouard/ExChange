package com.munch.exchange.server.ejb.ib.historicaldata;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.server.ejb.ib.Constants;

/**
 * Message-Driven Bean implementation class for: BarMsgDrivenBean
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty(propertyName = "destination", propertyValue =Constants.JMS_TOPIC_HISTORICAL_DATA),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		}, 
		mappedName = Constants.JMS_TOPIC_HISTORICAL_DATA)
//@TransactionManagement(TransactionManagementType.BEAN)
public class BarMsgDrivenBean implements MessageListener {
	
	
	private static final Logger log = Logger.getLogger(BarMsgDrivenBean.class.getName());
	
	@PersistenceContext
	private EntityManager em;

    /**
     * Default constructor. 
     */
    public BarMsgDrivenBean() {
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
	public void onMessage(Message message) {
		
		try {
			
			TextMessage msg = null;
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
//				log.info("Recieved Message from topic: " + msg.getText());
			} else {
				// log.info("Message of wrong type: "
				// + message.getClass().getName());
				return;
			}
			
			long time = msg
					.getLongProperty(HistoricalDataTimerBean.TIME_STRING);
			List<IbContract> allContracts = em.createNamedQuery(
					"IbContract.getAll", IbContract.class).getResultList();
			
			HistoricalBarLoader.INSTANCE.setEntityManager(em);
			HistoricalBarLoader.INSTANCE.setContracts(allContracts);
			HistoricalBarLoader.INSTANCE.setLastShortTermTrigger(time);
						

		} catch (JMSException e) {
			log.warning(e.toString());
		}

	}
	
	
	/**
	 * 
	 * Search all the bar of the contract
	 * 
	 */
	 public static List<IbBarContainer> getBarContainersOf(IbContract exContract,EntityManager em){
	    	List<IbBarContainer> containersInDB=exContract.getBars();
	    	List<IbBarContainer> AllAvailableContainers=IbContract.getAllAvailableIbBarContainers(exContract);
	    	for(IbBarContainer container: AllAvailableContainers){
	    		boolean containerIsSaved=false;
	    		for(IbBarContainer containerInDB : containersInDB){
	    			if(containerInDB.getType()==container.getType()){
	    				containerIsSaved=true;
	    			}
	    		}
	    		if(containerIsSaved)continue;
	    		
	    		em.persist(container);
	    		containersInDB.add(container);
	    	}
	    	
	    	return containersInDB;
	    }
	
	

}
