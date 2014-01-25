package com.munch.exchange.parts;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.munch.exchange.IImageKeys;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.RatesTreeContentProvider.RateContainer;
import com.munch.exchange.services.IBundleResourceLoader;



public class RatesTreeLabelProvider implements ILabelProvider {
	
	
	private Image rateGroupImage;
	
	@Inject
	IBundleResourceLoader loader;
	
	
	private Image getRateContainerImage() {
		if(rateGroupImage==null){
			rateGroupImage=loader.loadImage(getClass(),IImageKeys.RATE_CONTAINER );
		}
		return rateGroupImage;
	}

	public RatesTreeLabelProvider() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		if(element instanceof RateContainer){
			return this.getRateContainerImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof RateContainer){
			RateContainer rate=(RateContainer) element;
			return rate.getName()+ " ["+rate.getChilds().size()+"]";
			
		}
		else if(element instanceof Stock){
			Stock stock=(Stock) element;
			return stock.getFullName()+" ["+stock.getIndustry()+", "+stock.getSector()+"]";
		}
		else if(element instanceof ExchangeRate){
			ExchangeRate rate=(ExchangeRate) element;
			
				return rate.getFullName();
			
			
		}
		return null;
	}

}
