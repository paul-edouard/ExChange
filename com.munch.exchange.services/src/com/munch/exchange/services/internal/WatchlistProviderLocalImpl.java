package com.munch.exchange.services.internal;

import java.io.File;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.watchlist.Watchlists;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IWatchlistProvider;

public class WatchlistProviderLocalImpl implements IWatchlistProvider {
	
	private String workspace;
	final private static String WatchlistsStr="Watchlists.xml";
	
	private static Logger logger = Logger.getLogger(WatchlistProviderLocalImpl.class);
	
	private Watchlists watchlists=null;
	
	
	@Override
	public void init(String workspace) {
		logger.info("Service initialisation, setting the workspace: "+workspace);
		this.workspace=workspace;
	}

	@Override
	public Watchlists load() {
		if(watchlists==null){
			String wachtlistsFileName=workspace+File.separator+WatchlistsStr;
			watchlists=new Watchlists();
			if(Xml.load(watchlists, wachtlistsFileName)){
				return watchlists;
			}
		}
		
		return watchlists;
	}

	@Override
	public boolean save() {
		if(watchlists==null)return false;
		if(watchlists==null || watchlists.getLists()==null)return false;
		String wachtlistsFileName=workspace+File.separator+WatchlistsStr;
		return Xml.save(watchlists, wachtlistsFileName);
	}

}
