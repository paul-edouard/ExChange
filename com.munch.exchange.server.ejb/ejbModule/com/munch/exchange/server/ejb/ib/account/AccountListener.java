package com.munch.exchange.server.ejb.ib.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;


import com.ib.controller.ApiController.IAccountHandler;
import com.ib.controller.MarketValueTag;
import com.ib.controller.Position;

public class AccountListener implements IAccountHandler {
	
	private static final Logger log = Logger.getLogger(AccountListener.class.getName());
	
	
	String accountCode="";
	String timeStamp="";
	HashMap<MarginKey, String> keyValueMap=new HashMap<>();
	Position lastPosition=null;
	
	private MarginModel m_marginModel = new MarginModel();
	private MktValModel m_mktValModel = new MktValModel();
	

	public AccountListener(String accountCode) {
		super();
		this.accountCode = accountCode;
	}
	
	

	@Override
	public void accountValue(String account, String tag, String value,
			String currency) {
		
		if(!account.equals(accountCode))return;
		
		try {
			MarketValueTag mvTag = MarketValueTag.valueOf( tag);
			m_mktValModel.handle( account, currency, mvTag, value);
		}
		catch( Exception e) {
			m_marginModel.handle( tag, value, currency, account);
		}
		
	}

	@Override
	public void accountTime(String timeStamp) {
		this.timeStamp=timeStamp;
//		show("Margin of account: "+accountCode+"\n"+m_marginModel.toString());
	}

	@Override
	public void accountDownloadEnd(String account) {
		if(account!=accountCode)return;
//		show("Margin of account: "+accountCode+"\n"+m_marginModel.toString());
	}

	@Override
	public void updatePortfolio(Position position) {
		this.lastPosition=position;
	}
	
	
	
	private static class MktValModel  {
		private HashMap<String,MktValRow> m_map = new HashMap<String,MktValRow>();
		private ArrayList<MktValRow> m_list = new ArrayList<MktValRow>();
		
		void handle(String account, String currency, MarketValueTag mvTag, String value) {
			String key = account + currency;
			MktValRow row = m_map.get( key);
			if (row == null) {
				row = new MktValRow( account, currency);
				m_map.put( key, row);
				m_list.add( row);
			}
			row.set( mvTag, value);
		}

		void clear() {
			m_map.clear();
			m_list.clear();
		}
		
	}
	
	private static class MktValRow {
		String m_account;
		String m_currency;
		HashMap<MarketValueTag,String> m_map = new HashMap<MarketValueTag,String>();
		
		public MktValRow(String account, String currency) {
			m_account = account;
			m_currency = currency;
		}

		public String get(MarketValueTag tag) {
			return m_map.get( tag);
		}

		public void set(MarketValueTag tag, String value) {
			m_map.put( tag, value);
		}
	}	
	
	
//	#####################
//	##    MARGIN      ###
//	#####################
	
	
	private class MarginModel {
		HashMap<MarginKey,MarginRow> m_map = new HashMap<MarginKey,MarginRow>();
//		ArrayList<MarginRow> m_list = new ArrayList<MarginRow>();

//		void clear() {
//			m_map.clear();
//			m_list.clear();
//		}
		
		public void handle(String tag, String value, String currency, String account) {
			// useless
			if (tag.equals( "Currency")) {
				return;
			}
			
			int type = 0; // 0=whole acct; 1=securities; 2=commodities
			
			// "Securities" segment?
			if (tag.endsWith( "-S") ) { 
				tag = tag.substring( 0, tag.length() - 2);
				type = 1;
			}
			
			// "Commodities" segment?
			else if (tag.endsWith( "-C") ) { 
				tag = tag.substring( 0, tag.length() - 2);
				type = 2;
			}
			
			MarginKey key = new MarginKey( tag, currency);
			MarginRow row = m_map.get( key);
			
			if (row == null) {
				// don't add new rows with a value of zero
				if (isZero( value) ) {
					return;
				}
				
				row = new MarginRow(tag, currency);
				m_map.put( key, row);

			}
			
			switch( type) {
				case 0: row.m_val = value; break;
				case 1: row.m_secVal = value; break;
				case 2: row.m_comVal = value; break;
			}
			
//			SwingUtilities.invokeLater( new Runnable() {
//				@Override public void run() {
//					fireTableDataChanged();
//				}
//			});
		}
		
		public ArrayList<MarginRow> getRowList(){
			ArrayList<MarginRow> m_list = new ArrayList<MarginRow>();
			for(MarginRow row:m_map.values()){
				m_list.add(row);
			}
			Collections.sort( m_list);
			return m_list;
		}
		
		
		
		@Override
		public String toString() {
			String ret="MarginModel:\n";
			ArrayList<MarginRow> m_list=getRowList();
			
			ret+="Number of rows: "+m_list.size()+" map size: "+m_map.size();
			
			for(MarginRow row:getRowList()){
				ret+=row.toString()+"\n";
			}
			
			return ret;
		}
		
		
		

	}
	
	private static boolean isZero(String value) {
		try {
			return Double.parseDouble( value) == 0;
		}
		catch( Exception e) {
			return false;
		}
	}
	
	private static class MarginRow implements Comparable<MarginRow> {
		String m_tag;
		String m_currency;
		String m_val;
		String m_secVal;
		String m_comVal;

		MarginRow( String tag, String cur) {
			m_tag = tag;
			m_currency = cur;
		}

		@Override public int compareTo(MarginRow o) {
			return m_tag.compareTo( o.m_tag);
		}

		@Override
		public String toString() {
			return "MarginRow [m_tag=" + m_tag + ", m_currency=" + m_currency
					+ ", m_val=" + m_val + ", m_secVal=" + m_secVal
					+ ", m_comVal=" + m_comVal + "]";
		}
		
		
		
	}
	
	private static class MarginKey {
		String m_tag;
		String m_currency;
		
		public MarginKey(String key, String currency) {
			m_tag = key;
			m_currency = currency;
		}

		@Override public int hashCode() {
			int cur = m_currency != null ? m_currency.hashCode() : 0;
			return m_tag.hashCode() + cur;
		}

		@Override public boolean equals(Object obj) {
			MarginKey other = (MarginKey)obj;
			return m_tag.equals( other.m_tag) &&
				  (m_currency == null && other.m_currency == null || m_currency != null && m_currency.equals( other.m_currency) );
		}
	}
	
//	#####################
//	##    MARGIN      ###
//	#####################
	
	public void show(String string) {
		log.info(string);
		
	}
	

}
