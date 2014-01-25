package com.munch.exchange.services;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface IBundleResourceLoader {
	
	//TODO create a cache for the Pictures
	
	public Image loadImage(Class<?> clazz, String path);
	public ImageDescriptor loadImageDescriptor(Class<?> clazz, String path); 

}
