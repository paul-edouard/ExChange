package com.munch.exchange.server.ejb.ib;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;
import com.munch.exchange.services.ejb.interfaces.ChartIndicatorBeanRemote;

/**
 * Session Bean implementation class ChartIndicatorBean
 */
@Stateless
@LocalBean
public class ChartIndicatorBean implements ChartIndicatorBeanRemote{
	
	
	private static final Logger log = Logger.getLogger(ChartIndicatorBean.class.getName());
	
	@PersistenceContext
	private EntityManager em;
	
	
    /**
     * Default constructor. 
     */
    public ChartIndicatorBean() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void update(IbChartIndicatorGroup group) {
		//System.out.println("Group to update:"+group.getId());
		em.merge(group);
	}

	@Override
	public void removeGroup(int id) {
		em.remove(getGroup(id));
	}

	@Override
	public IbChartIndicatorGroup getGroup(int id) {
		IbChartIndicatorGroup group=em.find(IbChartIndicatorGroup.class, id);
		return group;
	}

	@Override
	public List<IbChartSignalOptimizedParameters> updateOptimizedParameters(IbChartSignal signal) {
		
		IbChartSignal savedSignal = em.find(IbChartSignal.class, signal.getId());
		for(IbChartSignalOptimizedParameters savedParams:savedSignal.getAllOptimizedSet()){
			//Try to find the saved parameters to remove
			boolean paramFound=false;
			for(IbChartSignalOptimizedParameters params:signal.getAllOptimizedSet()){
				if(IbChartParameter.areAllValuesEqual(savedParams.getParameters(),params.getParameters() )){
					paramFound=true;break;
				}
			}
			
			if(!paramFound){
				em.remove(savedParams);
			}
			
		}
		
		//Save the chart signal
		savedSignal = em.merge(signal);
		
		//Make a copy of all parameters
		List<IbChartSignalOptimizedParameters> listCopy=new LinkedList<IbChartSignalOptimizedParameters>();
		for(IbChartSignalOptimizedParameters params:savedSignal.getAllOptimizedSet()){
			IbChartSignalOptimizedParameters cp=params.copy();
			cp.setParent(null);
			listCopy.add(cp);
		}
		
		return listCopy;
	}

	@Override
	public IbChartSignal getSignal(int id) {
		System.out.println("Try to get the signal!");
		IbChartSignal signal = em.find(IbChartSignal.class, id);
		System.out.println("Signal: "+signal.getId());
		return (IbChartSignal)signal.copy();
	}

	
	

}
