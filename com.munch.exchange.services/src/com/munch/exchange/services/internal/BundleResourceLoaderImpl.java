package com.munch.exchange.services.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.munch.exchange.services.IBundleResourceLoader;


public class BundleResourceLoaderImpl implements IBundleResourceLoader {

	@Override
	public Image loadImage(Class<?> clazz, String path) {
		ImageDescriptor imageDescript = loadImageDescriptor(clazz,path);
		return imageDescript.createImage();
	}
	
	@Override
	public ImageDescriptor loadImageDescriptor(Class<?> clazz, String path){
		Bundle bundle = FrameworkUtil.getBundle(clazz);
		
		//bundle.get
		
		URL url = FileLocator.find(bundle, new Path(path), null);
		ImageDescriptor imageDescript = ImageDescriptor.createFromURL(url);
		return imageDescript;
	}

}
