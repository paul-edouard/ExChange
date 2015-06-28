package com.munch.exchange.server.ejb.ib;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.munch.exchange.services.ejb.beans.HistoricalDataBeanRemote;

/**
 * Session Bean implementation class HistoricalDataBean
 */
@Stateless
@LocalBean
public class HistoricalDataBean implements HistoricalDataBeanRemote {

    /**
     * Default constructor. 
     */
    public HistoricalDataBean() {
        // TODO Auto-generated constructor stub
    }

}
