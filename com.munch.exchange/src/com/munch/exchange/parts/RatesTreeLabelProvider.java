package com.munch.exchange.parts;

import javax.inject.Inject;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.services.IBundleResourceLoader;



public class RatesTreeLabelProvider extends StyledCellLabelProvider {
	
	
	private Image rateContainerImage;
	private Image rateStocksImage;
	private Image rateFundsImage;
	private Image rateIndicesImage;
	private Image rateCommoditiesImage;
	private Image rateCurrenciesImage;
	private Image rateEconomicDatasImage;
	
	private Image rateImage;
	
	
	
	@Inject
	IBundleResourceLoader loader;
	
	
	
	
	private Image getRateContainerImage() {
		if(rateContainerImage==null){
			rateContainerImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER );
		}
		return rateContainerImage;
	}
	
	public Image getRateStocksImage() {
		if(rateStocksImage==null){
			rateStocksImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_STOCKS );
		}
		return rateStocksImage;
	}

	public Image getRateFundsImage() {
		if(rateFundsImage==null){
			rateFundsImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_FUNDS );
		}
		return rateFundsImage;
	}

	public Image getRateIndicesImage() {
		if(rateIndicesImage==null){
			rateIndicesImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_INDICES );
		}
		return rateIndicesImage;
	}

	public Image getRateCommoditiesImage() {
		if(rateCommoditiesImage==null){
			rateCommoditiesImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_COMMODITIES );
		}
		return rateCommoditiesImage;
	}

	public Image getRateCurrenciesImage() {
		if(rateCurrenciesImage==null){
			rateCurrenciesImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_CURRENCIES );
		}
		return rateCurrenciesImage;
	}
	
	public Image getEconomicDatasImage() {
		if(rateEconomicDatasImage==null){
			rateEconomicDatasImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER_ECONOMIC_DATAS );
		}
		return rateEconomicDatasImage;
	}

	public Image getRateImage() {
		if(rateImage==null){
			rateImage=loader.loadImage(getClass(),IImageKeys.RATE_COMMON );
		}
		return rateImage;
	}

	@Override
	public void update(ViewerCell cell) {
		Object element=cell.getElement();
		//Text
		if(element instanceof RateContainer){
			RateContainer rate=(RateContainer) element;
			if(rate.getLoadingState().isEmpty()){
				cell.setText( rate.getName()+ " ["+rate.getChilds().size()+"]");
			}
			else{
				cell.setText( rate.getName()+ ": "+rate.getLoadingState());
			}
			/*
			FontData[] datas=cell.getFont().getFontData();
			for(FontData data:datas){
				data.setStyle(SWT.BOLD);
			}
			cell.getFont().
			*/
					//cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		}
		else if(element instanceof Stock){
			Stock stock=(Stock) element;
			cell.setText(stock.getFullName()+" ["+stock.getIndustry()+", "+stock.getSector()+"]");
		}
		else if(element instanceof EconomicData){
			EconomicData economicData=(EconomicData) element;
			cell.setText(economicData.getName()+" ("+economicData.getId()+")");
		}
		else if(element instanceof ExchangeRate){
			ExchangeRate rate=(ExchangeRate) element;
			cell.setText(rate.getFullName());
		}
		
		//Image
		if(element instanceof RateContainer){
			RateContainer rate=(RateContainer) element;
			if(rate.getName().equals(RatesTreeContentProvider.STOCKS_CONTAINER)){
				cell.setImage(getRateStocksImage());
			}
			else if (rate.getName().equals(RatesTreeContentProvider.INDICES_CONTAINER)){
				cell.setImage(getRateIndicesImage());
			}
			else if (rate.getName().equals(RatesTreeContentProvider.FUNDS_CONTAINER)){
				cell.setImage(getRateFundsImage());
			}
			else if (rate.getName().equals(RatesTreeContentProvider.COMMODITIES_CONTAINER)){
				cell.setImage(getRateCommoditiesImage());
			}
			else if (rate.getName().equals(RatesTreeContentProvider.CURRENCIES_CONTAINER)){
				cell.setImage(getRateCurrenciesImage());
			}
			else if (rate.getName().equals(RatesTreeContentProvider.ECONOMICDATA_CONTAINER)){
				cell.setImage(getRateCurrenciesImage());
			}
			
		}
		else{
			cell.setImage(getRateImage());
		}
		
		
		super.update(cell);
	}

}
