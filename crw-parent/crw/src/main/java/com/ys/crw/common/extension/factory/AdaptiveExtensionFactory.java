package com.ys.crw.common.extension.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ys.crw.common.extension.Adaptive;
import com.ys.crw.common.extension.ExtensionFactory;
import com.ys.crw.common.extension.ExtensionLoader;

/**
 * @author oscar.wu
 *
 */
@Adaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {
	private volatile List<ExtensionFactory> factories;

	@Override
	public <T> List<T> getExtension(Class<T> type) {
		return getFactories().stream().map(f -> f.getExtension(type)).flatMap(p -> p.stream()).collect(Collectors.toList());
	}
	
	private List<ExtensionFactory> getFactories(){
		if(factories != null){
			return factories;
		}
		synchronized (this) {
			if(factories != null){
				return factories;
			}
			
	        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
	        List<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
	        for (String name : loader.getSupportedExtensions()) {
	            list.add(loader.getExtension(name));
	        }
	        factories = Collections.unmodifiableList(list);
	        return factories;
		}
	}
	

}
