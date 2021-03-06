package com.munch.exchange.parts.composite;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.services.IBundleResourceLoader;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IQuoteProvider;

public class RateTitle extends Composite {
	
	private static Logger logger = Logger.getLogger(RateTitle.class);
	
	ExchangeRate rate;
	
	IExchangeRateProvider exchangeRateProvider;
	
	IBundleResourceLoader loader;
	
	IQuoteProvider quoteProvider;
	
	private Label lblFulleName;
	private Label lblQuote;
	private Label lblChange;
	private Label lblLasttradedate;
	
	private Image upImage;
	private Image downImage;
	private Label labelIcon;
	private Label lblIsin;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@Inject
	public RateTitle(Composite parent,ExchangeRate rate,IBundleResourceLoader loader,
			IExchangeRateProvider exchangeRateProvider, IQuoteProvider quoteProvider) {
		super(parent,  SWT.NONE);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		this.rate=rate;
		this.loader=loader;
		this.exchangeRateProvider=exchangeRateProvider;
		this.quoteProvider=quoteProvider;
		
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.horizontalSpacing = 10;
		setLayout(gridLayout);
		
		lblFulleName = new Label(this, SWT.NONE);
		lblFulleName.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		lblFulleName.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFulleName.setBackground(SWTResourceManager.getColor(0, 0, 255));
		lblFulleName.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		//lblFulleName.setText("None");
		
		lblQuote = new Label(this, SWT.NONE);
		lblQuote.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		lblQuote.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblQuote.setBackground(SWTResourceManager.getColor(0, 0, 255));
		lblQuote.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		
		labelIcon = new Label(this, SWT.NONE);
		labelIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		//lblQuote.setText("Quote");
		
		lblChange = new Label(this, SWT.NONE);
		lblChange.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblChange.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		lblChange.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblChange.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		lblIsin = new Label(this, SWT.NONE);
		lblIsin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				InputDialog in_dia=new InputDialog(RateTitle.this.getShell(), "Enter ISIN", "Please enter the ISIN", "", new IInputValidator() {
					
					@Override
					public String isValid(String newText) {
						String symbol=RateTitle.this.exchangeRateProvider.getYahooSymbolFromIsin(newText);
						if(symbol.isEmpty())return "ISIN no found on Yahoo!";
						if(!symbol.equals(RateTitle.this.rate.getSymbol())){
							return "ISIN and Yahoo symbol doesn't match!\nSymbol found: "+symbol;
						}
						
						
						return null;
					}
				});
				
				if (in_dia.open() == Window.OK) {
					RateTitle.this.rate.setISIN(in_dia.getValue());
					RateTitle.this.exchangeRateProvider.save(RateTitle.this.rate);
					lblIsin.setText(RateTitle.this.rate.getISIN());
					lblIsin.pack();
				}
				
			}
		});
		lblIsin.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				lblIsin.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD| SWT.ITALIC));
			}
			@Override
			public void mouseExit(MouseEvent e) {
				lblIsin.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
			}
			
		});
		lblIsin.setText("ISIN");
		lblIsin.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblIsin.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblIsin.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblLasttradedate = new Label(this, SWT.NONE);
		lblLasttradedate.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblLasttradedate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblLasttradedate.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLasttradedate.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		//lblLasttradedate.setText("LastTradeDate");
		
		searchLastQuote();
		
		setLabelValues();
		
	}
	
	/**
	 * if not loaded the quote will be loaded
	 */
	private void searchLastQuote(){
		if(rate.getRecordedQuote().isEmpty() && !(rate instanceof EconomicData)){
			quoteProvider.load(rate);
		}
	}
	
	
	@Inject
	private void quoteLoaded(@Optional  @UIEventTopic(IEventConstant.QUOTE_LOADED) String rate_uuid/*,@Optional Shell shell*/ ){
		
		if(this.isDisposed())return;
		//logger.info("Message recieved: Quote loaded!");
		/*
		if(shell==null)return;
		if(shell.getDisplay().isDisposed())return;
		*/
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
	private void quoteUpdate(@Optional  @UIEventTopic(IEventConstant.QUOTE_UPDATE) String rate_uuid /*,@Optional Shell shell*/){
		
		if(this.isDisposed())return;
		
		/*
		if(shell==null)return;
		if(shell.getDisplay().isDisposed())return;
		//logger.info("Message recieved: Quote update!");
		 */
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
	private void historicalDataLoaded(@Optional  @UIEventTopic(IEventConstant.HISTORICAL_DATA_LOADED) String rate_uuid ){
		
		
		if(this.isDisposed())return;
		if(rate_uuid==null || rate_uuid.isEmpty())return;
		
		
		ExchangeRate incoming=exchangeRateProvider.load(rate_uuid);
		if(incoming==null || rate==null || lblFulleName==null || lblQuote==null)return;
		if(!incoming.getUUID().equals(rate.getUUID()))return;
		
		
		if(rate instanceof EconomicData){
			setLabelValuesEconomicData();
		}
	}
	
	
	
	
	
	public  void setLabelValues(){
		
		
		lblFulleName.setText(rate.getFullName());
		
		if(rate.getISIN().isEmpty()){
			lblIsin.setText("Set ISIN");
		}
		else{
			lblIsin.setText(rate.getISIN());
		}
		
		if(rate instanceof EconomicData){
			setLabelValuesEconomicData();
		}
		else{
			setLabelValueCommonRate();
		}
		
	}
	
	/**
	 * set the label values for Economic data
	 */
	private void setLabelValuesEconomicData(){
		
		if(rate.getHistoricalData().isEmpty())return;
		
		//logger.info("HISTORICAL DATA LOADED 2: "+rate.getFullName());
		
		HistoricalPoint lastPoint=(HistoricalPoint)rate.getHistoricalData().getLast();
		
		// Quote
		lblQuote.setText(String.valueOf(lastPoint.getClose()));
		//lblQuote.update();
		
		//logger.info("HISTORICAL DATA LOADED 3: "+String.valueOf(lastPoint.getClose()));
		
		if(rate.getHistoricalData().size()==1)return;
		
		HistoricalPoint lastPoint_1=(HistoricalPoint)rate.getHistoricalData().get(rate.getHistoricalData().size()-2);
		
		// Change
		float per = (lastPoint.getClose()- lastPoint_1.getClose())* 100 / lastPoint_1.getClose();
		lblChange.setText(String.valueOf(lastPoint.getClose()- lastPoint_1.getClose()) + " ("
				+ String.format("%.2f", per) + "%)");
		if (per > 0) {
			labelIcon.setImage(this.getUpImage());
			lblChange.setForeground(SWTResourceManager
					.getColor(SWT.COLOR_GREEN));
		} else if (per < 0) {
			labelIcon.setImage(this.getDownImage());
			lblChange.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		} else {
			labelIcon.setImage(null);
		}
		
		// Date Time
		Calendar date_paris = Calendar.getInstance();
		date_paris
						.setTimeInMillis(lastPoint.getDate().getTimeInMillis() + 3600 * 1000 * 6);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		lblLasttradedate.setText(format.format(date_paris.getTime()));
		
		//Force Redrawing
		this.layout();
		
		
		
	}
	
	/**
	 * set the values for common rate
	 * 
	 */
	private void  setLabelValueCommonRate(){
		
		if(rate.getRecordedQuote().isEmpty())return;
			
		QuotePoint point = (QuotePoint) rate.getRecordedQuote().getLast();

		// Quote
		lblQuote.setText(String.valueOf(point.getLastTradePrice()));
		lblQuote.setVisible(true);

		// Change
		float per = point.getChange() * 100 / point.getLastTradePrice();
		lblChange.setText(String.valueOf(point.getChange()) + " ("
				+ String.format("%.2f", per) + "%)");
		if (per > 0) {
			labelIcon.setImage(this.getUpImage());
			lblChange.setForeground(SWTResourceManager
					.getColor(SWT.COLOR_GREEN));
		} else if (per < 0) {
			labelIcon.setImage(this.getDownImage());
			lblChange.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		} else {
			labelIcon.setImage(null);
		}
		labelIcon.setVisible(true);

		// Date Time
		Calendar date_paris = Calendar.getInstance();
		date_paris
				.setTimeInMillis(point.getLastTradeDate().getTimeInMillis() + 3600 * 1000 * 6);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		lblLasttradedate.setText(format.format(date_paris.getTime()));
		lblLasttradedate.setVisible(true);
		
		
		//Force Redrawing
		this.layout();
			
	}
	

	public Image getUpImage() {
		if(upImage==null){
			upImage=loader.loadImage(getClass(),IImageKeys.QUOTE_UP );
		}
		return upImage;
	}

	public Image getDownImage() {
		if(downImage==null){
			downImage=loader.loadImage(getClass(),IImageKeys.QUOTE_DOWN );
		}
		return downImage;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}
