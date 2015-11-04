package com.munch.exchange.server.ejb.ib;

import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
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

}
