package com.munch.exchange.parts;

import java.util.LinkedList;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.services.IExchangeRateProvider;

public class RatesTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	
	
	public static String STOCKS_CONTAINER="Stocks";
	public static String INDICES_CONTAINER="Indices";
	public static String FUNDS_CONTAINER="Funds";
	public static String COMMODITIES_CONTAINER="Commodities";
	public static String CURRENCIES_CONTAINER="Currencies";
	
	
	RateContainer root;
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	RateLoader loader=new RateLoader();

	public RatesTreeContentProvider() {
		super();
		// TODO Auto-generated constructor stub
		//System.out.println("RatesTreeContentProvider contructor called");
		
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof RateContainer){
			RateContainer cont=(RateContainer) parentElement;
			return cont.getChilds().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof RateContainer){
			RateContainer cont=(RateContainer) element;
			return !cont.getChilds().isEmpty();
			
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof RateContainer){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	/**
	 * reset the root structure
	 */
	public void resetRoot(){
		
		if(root!=null){
			root.getChilds().clear();
		}
		else{
			root=new RateContainer("ROOT", null);
		}
		loader.schedule();
	}
	
	
	public RateContainer getRoot() {
		if(root!=null)return root;
		
		resetRoot();
		return root;
	}
	
	public RateContainer addExChangeRate(ExchangeRate rate){
		
		RateContainer container=null;
		if(rate instanceof Stock){
			container=(RateContainer) root.getChild(STOCKS_CONTAINER);
			container.getChilds().add(rate);
			
		}
		else if(rate instanceof Indice){
			container=(RateContainer) root.getChild(INDICES_CONTAINER);
			container.getChilds().add(rate);
			
		}
		else if(rate instanceof Commodity){
			container=(RateContainer) root.getChild(COMMODITIES_CONTAINER);
			container.getChilds().add(rate);
			
		}
		else if(rate instanceof Fund){
			container=(RateContainer) root.getChild(FUNDS_CONTAINER);
			container.getChilds().add(rate);
		}
		else if(rate instanceof Currency){
			container=(RateContainer) root.getChild(CURRENCIES_CONTAINER);
			container.getChilds().add(rate);
			
		}
		
		return container;
	}
	
	public void deleteExChangeRate(ExchangeRate rate){
		RateContainer container=null;
		if(rate instanceof Stock){
			container=(RateContainer) root.getChild(STOCKS_CONTAINER);
			container.getChilds().remove(rate);
		}
		else if(rate instanceof Indice){
			container=(RateContainer) root.getChild(INDICES_CONTAINER);
			container.getChilds().remove(rate);
			
		}
		else if(rate instanceof Commodity){
			container=(RateContainer) root.getChild(COMMODITIES_CONTAINER);
			container.getChilds().remove(rate);
			
		}
		else if(rate instanceof Fund){
			container=(RateContainer) root.getChild(FUNDS_CONTAINER);
			container.getChilds().remove(rate);
		}
		else if(rate instanceof Currency){
			container=(RateContainer) root.getChild(CURRENCIES_CONTAINER);
			container.getChilds().remove(rate);
		}
	}
	
	
	
	

	public class RateContainer extends ExchangeRate{
		
		protected ExchangeRate parent;
		protected LinkedList<ExchangeRate> childs=new LinkedList<ExchangeRate>();
		protected String loadingState="";
		
		public RateContainer(String name, ExchangeRate parent) {
			super();
			this.name = name;
			this.parent = parent;
			if(this.parent!=null && parent instanceof RateContainer){
				RateContainer p=(RateContainer) this.parent;
				p.getChilds().add(this);
			}
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ExchangeRate getParent() {
			return parent;
		}
		public void setParent(ExchangeRate parent) {
			this.parent = parent;
		}
		public LinkedList<ExchangeRate> getChilds() {
			return childs;
		}
		
		public void setChilds(LinkedList<ExchangeRate> childs) {
			this.childs = childs;
		}
		public String getLoadingState() {
			return loadingState;
		}
		public void setLoadingState(String loadingState) {
			this.loadingState = loadingState;
		}
		
		public ExchangeRate getChild(String name){
			for(ExchangeRate rate:this.getChilds()){
				if(rate.getName().equals(name))
					return rate;
			}
			
			ExchangeRate child=new RateContainer(name,this);
			return child;
			
		}
		
		
		
	}
	
	private class LoadingStateChanger extends Job{
		
		RateContainer container;
		
		boolean stop=false;
		
		public LoadingStateChanger(RateContainer container) {
			super("Loading State Changer");
			setSystem(true);
			setPriority(SHORT);
			this.container=container;
			schedule();
		}
		
		

		public void Stop() {
			this.stop = true;
		}



		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try{
				int i=0;
			while(!stop){
				
				String loading_str="Loading";
				for(int j=0;j<i;j++){
					loading_str+=".";
				}
				
				container.setLoadingState(loading_str);
				eventBroker.post(IEventConstant.RATE_LOADING,container);
				
				i++;
				if(i>3)i=0;
				
				Thread.sleep(500);
				
			}
			}catch(InterruptedException e){
				
				return Status.CANCEL_STATUS;
			}
			
			return Status.CANCEL_STATUS;
			
		}
		
	}
	
	private class RateLoader extends Job{
		
		//RateContainer root;
		
		public RateLoader() {
			super("Rate Loader");
			setSystem(true);
			setPriority(SHORT);
			//this.root=root;
		}
		
		private void loadRateContainer(IProgressMonitor monitor, String container_name,Class<? extends ExchangeRate> clazz){
			monitor.subTask("Loading "+container_name);
			RateContainer container=(RateContainer) root.getChild(container_name);
			
			LoadingStateChanger changer=new LoadingStateChanger(container);
			
			LinkedList<ExchangeRate> stock_l=exchangeRateProvider.loadAll(clazz);
			container.setChilds(stock_l);
			changer.Stop();
			changer.wakeUp();
			try {
				changer.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			container.setLoadingState("");
			eventBroker.post(IEventConstant.RATE_LOADED,container);
			monitor.worked(20);
		}
		

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			monitor.beginTask("Loading rates", 100); 
			
			//Stocks
			loadRateContainer(monitor, STOCKS_CONTAINER,Stock.class);
			
			//Indices
			loadRateContainer(monitor, INDICES_CONTAINER,Indice.class);
			
			//Funds
			loadRateContainer(monitor, FUNDS_CONTAINER,Fund.class);
			
			//Commodities
			loadRateContainer(monitor, COMMODITIES_CONTAINER,Commodity.class);
			
			//Currencies
			loadRateContainer(monitor, CURRENCIES_CONTAINER,Currency.class);
			
			return Status.OK_STATUS;
		}
		
	}
	
	

}
