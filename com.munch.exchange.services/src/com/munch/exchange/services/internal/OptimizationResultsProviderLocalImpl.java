package com.munch.exchange.services.internal;

import java.io.File;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.optimization.OptimizationResultsMap;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IOptimizationResultsProvider;

public class OptimizationResultsProviderLocalImpl implements
		IOptimizationResultsProvider {

	private static Logger logger = Logger.getLogger(OptimizationResultsProviderLocalImpl.class);
	
	final private static String OptResultsStr="OptimizationResults.xml";
	
	
	private File getLocalDivFile(ExchangeRate rate){
		return new File(rate.getDataPath()+File.separator+OptResultsStr);
	}
	
	
	@Override
	public boolean save(ExchangeRate rate) {
		if(rate==null)return false;
		
		String divFileStr=getLocalDivFile(rate).getAbsolutePath();
		
		logger.info("Writing file: "+divFileStr);
		return Xml.save(rate.getOptResultsMap(), divFileStr);
	}
	
	@Override
	public boolean load(ExchangeRate rate) {
		if(rate==null)return false;
		if(rate.getDataPath()==null)return false;
		if(rate.getDataPath().isEmpty())return false;
		
		File localDivFile=getLocalDivFile(rate);
		if(localDivFile.exists()){
			OptimizationResultsMap optResMap=new OptimizationResultsMap();
			if( Xml.load(optResMap, localDivFile.getAbsolutePath())){
				rate.setOptResultsMap(optResMap);
				logger.info("Optimization results localy found for "+rate.getFullName());
				
				return true;
			}
		}
		return false;

	}

	

}
