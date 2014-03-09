package com.munch.exchange.parts.composite;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;

public class RateWeb extends Composite {
	
	private static Logger logger = Logger.getLogger(RateWeb.class);
	
	@Inject
	public RateWeb(Composite parent,ExchangeRate rate) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		
		Browser browser = new Browser(this, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		browser.setUrl(getYahooURL(rate));
		
		
		
	}
	
	
	private String getYahooURL(ExchangeRate rate){
		
		if(rate instanceof EconomicData){
			
			String baseUrl="http://research.stlouisfed.org/fred2/series/";
			try {
				return baseUrl + URLEncoder.encode(((EconomicData) rate).getId(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//http://research.stlouisfed.org/fred2/series/UNRATE
		else{
		String baseUrl="http://finance.yahoo.com/q?s=";
		try {
			return baseUrl + URLEncoder.encode(rate.getSymbol(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return null;
		
	}

}
