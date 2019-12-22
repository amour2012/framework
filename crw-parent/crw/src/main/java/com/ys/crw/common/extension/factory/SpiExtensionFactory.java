package com.ys.crw.common.extension.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.ys.crw.common.extension.ExtensionFactory;

/**
 * @author oscar.wu
 *
 */
public class SpiExtensionFactory implements ExtensionFactory {
	
	public <T> List<T> getExtension(Class<T> type){
		ServiceLoader<T> loader = ServiceLoader.load(type); 
		List<T> exts = new ArrayList<>();
    	for(T instance : loader){
    		exts.add(instance);
    	}
    	return exts;
	}
}
