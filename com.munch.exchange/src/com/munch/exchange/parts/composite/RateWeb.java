package com.munch.exchange.parts.composite;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;

public class RateWeb extends Composite {
	
	private static Logger logger = Logger.getLogger(RateWeb.class);
	private Combo comboWebSites;
	private Button btnBack;
	private Button btnNext;
	private Browser browser;
	
	private LinkedHashMap<String, String> webSiteMap=new LinkedHashMap<String, String>();
	private Text textURI;
	private ProgressBar progressBar;
	private LinkedList<String> visitedWebSites=new LinkedList<String>();
	
	@Inject
	public RateWeb(Composite parent,ExchangeRate rate) {
		super(parent, SWT.NONE);
		createWebSiteMap(rate);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		GridLayout gl_composite = new GridLayout(5, false);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboWebSites = new Combo(composite, SWT.NONE);
		for(String key:webSiteMap.keySet()){
			comboWebSites.add(key);
			comboWebSites.setText(key);
		}
		
		comboWebSites.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				textURI.setText(webSiteMap.get(comboWebSites.getText()));
				browser.setUrl(textURI.getText());
			}
		});
		
		btnBack = new Button(composite, SWT.NONE);
		btnBack.setEnabled(false);
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int index=visitedWebSites.indexOf(browser.getUrl());
				if(index-1>=0){
					textURI.setText(visitedWebSites.get(index-1));
					browser.setUrl(textURI.getText());
				}
			}
		});
		btnBack.setText("<<");
		
		btnNext = new Button(composite, SWT.NONE);
		btnNext.setEnabled(false);
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int index=visitedWebSites.indexOf(browser.getUrl());
				if(index+1<visitedWebSites.size()-1){
					textURI.setText(visitedWebSites.get(index+1));
					browser.setUrl(textURI.getText());
				}
			}
		});
		btnNext.setText(">>");
		
		
		textURI = new Text(composite, SWT.BORDER);
		textURI.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//System.out.println("keyCode"+e.keyCode);
				if (e.keyCode == 13) 
				{browser.setUrl(textURI.getText());}
				//e.
			}
		});
		textURI.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(!textURI.getText().isEmpty()){
					progressBar.setVisible(true);
				}
			}
		});
		GridData gd_textURI = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textURI.widthHint = 215;
		textURI.setLayoutData(gd_textURI);
		
		progressBar = new ProgressBar(composite, SWT.NONE);
		GridData gd_progressBar = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
		gd_progressBar.widthHint = 72;
		progressBar.setLayoutData(gd_progressBar);
		
		
		
		
		
		
		browser = new Browser(this, SWT.NONE);
		browser.addTitleListener(new TitleListener() {
			public void changed(TitleEvent event) {
				System.out.println("Title Change: "+event.title+", URL"+browser.getUrl());
				if(!textURI.getText().equals(browser.getUrl())){
					textURI.setText(browser.getUrl());
				}
				
				if(!visitedWebSites.contains(browser.getUrl())){
					visitedWebSites.add(browser.getUrl());
				}
				if(visitedWebSites.size()<=1)return;
				
				int index=visitedWebSites.indexOf(browser.getUrl());
				if(index==0){
					btnNext.setEnabled(true);
					btnBack.setEnabled(false);
				}
				else if(index<visitedWebSites.size()-1) {
					btnNext.setEnabled(true);
					btnBack.setEnabled(true);
				}
				else{
					btnNext.setEnabled(false);
					btnBack.setEnabled(true);
				}
				
				
			}
		});
		browser.addProgressListener(new ProgressAdapter() {
			@Override
			public void changed(ProgressEvent event) {
				//System.out.println("Changed: "+event.current);
				progressBar.setSelection(event.current);
				progressBar.setMaximum(event.total);
			}
			@Override
			public void completed(ProgressEvent event) {
				System.out.println("completed");
				progressBar.setVisible(false);
				
				//if(visitedWebSites.size()>0)
			}
		});
		
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		textURI.setText(webSiteMap.get(comboWebSites.getText()));
		browser.setUrl(textURI.getText());
		
	}
	
	private void createWebSiteMap(ExchangeRate rate){
		
		webSiteMap.put("Google",getGoogleURL(rate));
		
		if(!(rate instanceof EconomicData)){
			webSiteMap.put("Yahoo Finance",getYahooURL(rate) );
			String dibaURL=getDiBaURL(rate);
			if(!dibaURL.isEmpty()){
				webSiteMap.put("DiBa",dibaURL );
			}
			
		}
		else{
			webSiteMap.put("St Louis Fed",getStLouisURL(rate) );
		}
		
		
	}
	
	
	private String getDiBaURL(ExchangeRate rate){
		
		if(rate.getISIN().isEmpty())return "";
		
		String baseUrl="https://wertpapiere.ing-diba.de/DE/Showpage.aspx?pageID=";
		if(rate instanceof Stock){
			baseUrl+="23&ISIN="+rate.getISIN()+"&";
		}
		else if(rate instanceof Indice){
			baseUrl+="45&ISIN="+rate.getISIN()+"&";
		}
		else if(rate instanceof Currency){
			baseUrl+="57&ISIN="+rate.getISIN()+"&";
		}
		else if(rate instanceof Commodity){
			baseUrl+="52&ISIN="+rate.getISIN()+"&";
		}
		else if(rate instanceof Fund){
			baseUrl+="40&ISIN="+rate.getISIN()+"&";
		}
		
		//https://wertpapiere.ing-diba.de/DE/Showpage.aspx?pageID=23&ISIN=DE0007664039&
		
		return baseUrl;
	}
	
	private String getStLouisURL(ExchangeRate rate){
		if(rate instanceof EconomicData){
			String baseUrl="http://research.stlouisfed.org/fred2/series/";
			try {
				return baseUrl + URLEncoder.encode(((EconomicData) rate).getId(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	private String getGoogleURL(ExchangeRate rate){
		
		String baseUrl="https://www.google.de/#q=";
		if(rate instanceof EconomicData){
			return baseUrl + ((EconomicData)rate).getId();
		}
		else if(!rate.getISIN().isEmpty()){
			return  baseUrl +rate.getISIN();
		}
		return baseUrl +rate.getSymbol();
	}
	
	private String getYahooURL(ExchangeRate rate){
		
		
		String baseUrl="http://finance.yahoo.com/q?s=";
		try {
			return baseUrl + URLEncoder.encode(rate.getSymbol(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
		
	}

}
