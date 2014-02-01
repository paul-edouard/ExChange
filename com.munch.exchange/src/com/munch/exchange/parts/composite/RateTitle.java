package com.munch.exchange.parts.composite;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.services.IExchangeRateProvider;

public class RateTitle extends Composite {
	
	private static Logger logger = Logger.getLogger(RateTitle.class);
	
	ExchangeRate rate;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	
	private Label lblFulleName;
	private Label lblQuote;
	private Label lblChange;
	private Label lblLasttradedate;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public RateTitle(Composite parent,ExchangeRate rate) {
		super(parent,  SWT.NONE);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		this.rate=rate;
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 10;
		setLayout(gridLayout);
		
		lblFulleName = new Label(this, SWT.NONE);
		lblFulleName.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		lblFulleName.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFulleName.setBackground(SWTResourceManager.getColor(0, 0, 255));
		lblFulleName.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblFulleName.setText("None");
		
		lblQuote = new Label(this, SWT.NONE);
		lblQuote.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		lblQuote.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblQuote.setBackground(SWTResourceManager.getColor(0, 0, 255));
		lblQuote.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblQuote.setText("Quote");
		
		lblChange = new Label(this, SWT.NONE);
		lblChange.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblChange.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		lblChange.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblChange.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblChange.setText("Change");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblLasttradedate = new Label(this, SWT.NONE);
		lblLasttradedate.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblLasttradedate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblLasttradedate.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLasttradedate.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblLasttradedate.setText("LastTradeDate");
		
		setLabelValues();
		
	}
	
	@Inject
	private void quoteLoaded(@Optional  @UIEventTopic(IEventConstant.QUOTE_LOADED) String rate_uuid ){
		//logger.info("Message recieved: Quote loaded!");
		
		if(rate_uuid==null || rate_uuid.isEmpty()){
			return;
		}
		
		ExchangeRate incoming=exchangeRateProvider.load(rate_uuid);
		if(incoming==null || rate==null || lblFulleName==null || lblQuote==null){
			return;
		}
		
		if(!incoming.getUUID().equals(rate.getUUID())){
			return;
		}
		
		setLabelValues();
	
	}
	
	@Inject
	private void quoteUpdate(@Optional  @UIEventTopic(IEventConstant.QUOTE_UPDATE) String rate_uuid ){
		//logger.info("Message recieved: Quote update!");
		if(rate_uuid==null || rate_uuid.isEmpty()){
			return;
		}
		
		ExchangeRate incoming=exchangeRateProvider.load(rate_uuid);
		if(incoming==null || rate==null || lblFulleName==null || lblQuote==null){
			return;
		}
		
		if(!incoming.getUUID().equals(rate.getUUID())){
			return;
		}
		
		setLabelValues();
	}
	
	
	
	
	
	public  void setLabelValues(){
		
		lblFulleName.setText(rate.getFullName());
		
		if(!rate.getRecordedQuote().isEmpty()){
			
			QuotePoint point=(QuotePoint) rate.getRecordedQuote().getLast();
			
			//Quote
			lblQuote.setText(String.valueOf(point.getLastTradePrice()));
			
			//Change
			float per=point.getChange()*100/point.getLastTradePrice();
			lblChange.setText(String.valueOf(point.getChange())+" ("+String.format("%.2f", per)+"%)");
			
			//Date Time
			Calendar date_paris=Calendar.getInstance();
			date_paris.setTimeInMillis(point.getLastTradeDate().getTimeInMillis()+3600*1000*6);
			
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
			lblLasttradedate.setText(format.format(date_paris.getTime()));
			lblLasttradedate.setRedraw(true);
			lblLasttradedate.redraw();
			lblLasttradedate.update();
			
			
		}
		
		//this.redraw();
		//this.update();
		
	}
	
	
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}
