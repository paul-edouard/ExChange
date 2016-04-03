package com.munch.exchange.parts.overview;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jfree.chart.plot.RainbowPalette;
import org.jfree.util.Log;

import com.ib.controller.Types.SecType;
import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.Commodity;
import com.munch.exchange.model.core.Currency;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Fund;
import com.munch.exchange.model.core.Indice;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;
import com.munch.exchange.services.ejb.interfaces.IIBNeuralProvider;

public class RatesTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	
	
	public static String STOCKS_CONTAINER="Stocks";
	public static String INDICES_CONTAINER="Indices";
	public static String FUNDS_CONTAINER="Funds";
	public static String COMMODITIES_CONTAINER="Commodities";
	public static String CURRENCIES_CONTAINER="Currencies";
	public static String ECONOMICDATA_CONTAINER="Economic Datas";
	
	

	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private IIBContractProvider contractProvider;
	
	@Inject
	private IIBNeuralProvider neuralProvider;
	
	
	RootContainer rootContainer;
	
	RateLoader loader=new RateLoader();

	@Inject
	public RatesTreeContentProvider() {
		super();
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
			return cont.getChildren().toArray();
		}
		else if(parentElement instanceof RootContainer){
			RootContainer root=(RootContainer) parentElement;
			return root.getChildren();
		}
		else if(parentElement instanceof ExContractContainer){
			ExContractContainer root=(ExContractContainer) parentElement;
			return root.getChildren().toArray();
		}
		else if(parentElement instanceof IbContract){
			IbContract contract=(IbContract) parentElement;
			return contract.getNeuralConfigurations().toArray();
		}
		else if(parentElement instanceof NeuralConfiguration){
			NeuralConfiguration config=(NeuralConfiguration) parentElement;
			return config.getIsolatedArchitectures().toArray();
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
			return !cont.getChildren().isEmpty();
		}
		else if(element instanceof RootContainer){
			return true;
		}
		else if(element instanceof ExContractContainer){
			ExContractContainer cont=(ExContractContainer) element;
			return !cont.getChildren().isEmpty();
		}
		else if(element instanceof IbContract){
			IbContract contract=(IbContract) element;
			return !contract.getNeuralConfigurations().isEmpty();
		}
		else if(element instanceof NeuralConfiguration){
			NeuralConfiguration config=(NeuralConfiguration) element;
			return !config.getIsolatedArchitectures().isEmpty();
		}
		
		
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof RateContainer
				|| inputElement instanceof RootContainer
				|| inputElement instanceof ExContractContainer
				|| inputElement instanceof IbContract){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	/**
	 * reset the root structure
	 */
	public void resetRoot(){
		
		if(rootContainer!=null){
			rootContainer.getRateRoot().getChildren().clear();
			rootContainer.getExContractRoot().getChildren().clear();
			//rateRoot.getChilds().clear();
		}
		else{
			rootContainer =new RootContainer();
			//rateRoot=new RateContainer("ROOT", null);
		}
		
		relaodContracts();
		
		loader.schedule();
	}
	
	public void relaodContracts(){
		rootContainer.loadAllContracts();
	}
	
	
	
	public RootContainer getRoot() {
		if(rootContainer!=null)return rootContainer;
		
		resetRoot();
		return rootContainer;
	}
	
	public RateContainer addExChangeRate(ExchangeRate rate){
		
		RateContainer container=null;
		if(rate instanceof Stock){
			container=(RateContainer) rootContainer.getRateRoot().getChild(STOCKS_CONTAINER);
			container.getChildren().add(rate);
			
		}
		else if(rate instanceof Indice){
			container=(RateContainer) rootContainer.getRateRoot().getChild(INDICES_CONTAINER);
			container.getChildren().add(rate);
			
		}
		else if(rate instanceof Commodity){
			container=(RateContainer) rootContainer.getRateRoot().getChild(COMMODITIES_CONTAINER);
			container.getChildren().add(rate);
			
		}
		else if(rate instanceof Fund){
			container=(RateContainer) rootContainer.getRateRoot().getChild(FUNDS_CONTAINER);
			container.getChildren().add(rate);
		}
		else if(rate instanceof Currency){
			container=(RateContainer) rootContainer.getRateRoot().getChild(CURRENCIES_CONTAINER);
			container.getChildren().add(rate);
		}
		else if(rate instanceof EconomicData){
			container=(RateContainer) rootContainer.getRateRoot().getChild(ECONOMICDATA_CONTAINER);
			container.getChildren().add(rate);
		}
		
		return container;
	}
	
	public void deleteExChangeRate(ExchangeRate rate){
		RateContainer container=null;
		if(rate instanceof Stock){
			container=(RateContainer) rootContainer.getRateRoot().getChild(STOCKS_CONTAINER);
			container.getChildren().remove(rate);
		}
		else if(rate instanceof Indice){
			container=(RateContainer) rootContainer.getRateRoot().getChild(INDICES_CONTAINER);
			container.getChildren().remove(rate);
			
		}
		else if(rate instanceof Commodity){
			container=(RateContainer) rootContainer.getRateRoot().getChild(COMMODITIES_CONTAINER);
			container.getChildren().remove(rate);
			
		}
		else if(rate instanceof Fund){
			container=(RateContainer) rootContainer.getRateRoot().getChild(FUNDS_CONTAINER);
			container.getChildren().remove(rate);
		}
		else if(rate instanceof Currency){
			container=(RateContainer) rootContainer.getRateRoot().getChild(CURRENCIES_CONTAINER);
			container.getChildren().remove(rate);
		}
		else if(rate instanceof EconomicData){
			container=(RateContainer) rootContainer.getRateRoot().getChild(ECONOMICDATA_CONTAINER);
			container.getChildren().remove(rate);
		}
	}
	
	
	public class RootContainer{
		protected RateContainer rateRoot;
		protected ExContractContainer exContractRoot;
		
		public RootContainer(){
			rateRoot=new RateContainer("Rates", this);
			exContractRoot =new ExContractContainer();
			exContractRoot.setParent(this);
			exContractRoot.setLongName("Contracts");
		}
		
		public RateContainer getRateRoot() {
			return rateRoot;
		}
		public void setRateRoot(RateContainer rateRoot) {
			this.rateRoot = rateRoot;
		}
		public ExContractContainer getExContractRoot() {
			return exContractRoot;
		}
		public void setExContractRoot(ExContractContainer exContractRoot) {
			this.exContractRoot = exContractRoot;
		}
		
		void loadAllContracts(){
			List<IbContract> contracts=contractProvider.getAllContracts();
			exContractRoot.getChildren().clear();
			for(IbContract contract:contracts){
				//System.out.println("Add new contract: "+contract.getId());
				exContractRoot.addExContract(contract);
				neuralProvider.loadNeuralConfigurations(contract);
				for(NeuralConfiguration config:contract.getNeuralConfigurations()){
					neuralProvider.loadIsolatedNeuralArchitecture(config);
				}
				
			}
		}
		
		public Object[] getChildren(){
			Object[] children=new Object[2];
			children[0]=exContractRoot;
			children[1]=rateRoot;
			return children;
		}
		
		public IbContract searchIbContract(int contractId){
			for(IbContract contract :exContractRoot.getChildren() ){
				if(contract instanceof ExContractContainer){
					ExContractContainer container=(ExContractContainer) contract;
					for(IbContract c :container.getChildren() ){
						//System.out.println("Contract Id: "+c.getId());
						if(contractId==c.getId())return c;
					}
				}
			}
			
			
			return null;
		}
		
	}
	
	public void addContract(IbContract contract){
		rootContainer.getExContractRoot().addExContract(contract);
	}
	
	public void removeContract(IbContract contract){
		rootContainer.getExContractRoot().removeContract(contract);
	}
	
	
	public class ExContractContainer extends IbContract{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7657341323960133574L;
		
		protected Object parent;
		protected LinkedList<IbContract> children=new LinkedList<IbContract>();
		public Object getParent() {
			return parent;
		}
		public void setParent(Object parent) {
			this.parent = parent;
		}
		public LinkedList<IbContract> getChildren() {
			return children;
		}
		public void setChilds(LinkedList<IbContract> children) {
			this.children = children;
		}
		
		
		private ExContractContainer getChild(SecType type){
			for(IbContract child:children){
				if(child.getSecType()==type && child instanceof ExContractContainer)
					return (ExContractContainer)child;
			}
			
			ExContractContainer newChild=new ExContractContainer();
			newChild.setSecType(type);
			newChild.setLongName(type.getApiString());
			children.add(newChild);
			
			return newChild;
			
		}
		
		public void addExContract(IbContract contract){
			SecType type=contract.getSecType();
			ExContractContainer childContainer=this.getChild(type);
			childContainer.getChildren().add(contract);
		}
		
		
		public void removeContract(IbContract contract){
			SecType type=contract.getSecType();
			ExContractContainer childContainer=this.getChild(type);
			
			System.out.println("Try to remove contract: "+contract.getLongName());
			
			if(childContainer !=null){
				if(childContainer.getChildren().size()==1){
					LinkedList<IbContract> rooTchildren=this.getChildren();
					rooTchildren.remove(childContainer);
					System.out.println("Number of root children: "+rooTchildren.size());
					this.children=rooTchildren;
				}
				else{
					childContainer.getChildren().remove(contract);
				}
			}
		}
		
		
	}
	
	
	public class RateContainer extends ExchangeRate{
		
		protected Object parent;
		protected LinkedList<ExchangeRate> children=new LinkedList<ExchangeRate>();
		protected String loadingState="";
		
		public RateContainer(String name, Object parent) {
			super();
			this.name = name;
			this.parent = parent;
			if(this.parent!=null && parent instanceof RateContainer){
				RateContainer p=(RateContainer) this.parent;
				p.getChildren().add(this);
			}
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Object getParent() {
			return parent;
		}
		public void setParent(Object parent) {
			this.parent = parent;
		}
		public LinkedList<ExchangeRate> getChildren() {
			return children;
		}
		
		public void setChilds(LinkedList<ExchangeRate> childs) {
			this.children = childs;
		}
		public String getLoadingState() {
			return loadingState;
		}
		public void setLoadingState(String loadingState) {
			this.loadingState = loadingState;
		}
		
		public ExchangeRate getChild(String name){
			for(ExchangeRate rate:this.getChildren()){
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
				//eventBroker.post(IEventConstant.RATE_LOADING,container);
				
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
			RateContainer container=(RateContainer) rootContainer.getRateRoot().getChild(container_name);
			
			LoadingStateChanger changer=new LoadingStateChanger(container);
			
			LinkedList<ExchangeRate> stock_l=exchangeRateProvider.loadAll(clazz);
			for(ExchangeRate rate:stock_l){
				eventBroker.post(IEventConstant.RATE_LOADED,rate);
			}
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
			
			//Economic Data
			loadRateContainer(monitor, ECONOMICDATA_CONTAINER,EconomicData.class);
			
			return Status.OK_STATUS;
		}
		
	}
	
	

}
