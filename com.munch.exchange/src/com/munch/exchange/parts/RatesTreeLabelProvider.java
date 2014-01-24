package com.munch.exchange.parts;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.RatesTreeContentProvider.RateContainer;

public class RatesTreeLabelProvider implements ILabelProvider {

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
		// TODO Auto-generated method stub
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
