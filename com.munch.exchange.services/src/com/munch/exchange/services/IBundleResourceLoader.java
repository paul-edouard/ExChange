package com.munch.exchange.services;

import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface IBundleResourceLoader {
	
	//TODO create a cache for the Pictures
	
	public URI getImageURI(Class<?> clazz, String path);
	public Image loadImage(Class<?> clazz, String path);
	public ImageDescriptor loadImageDescriptor(Class<?> clazz, String path); 

}
