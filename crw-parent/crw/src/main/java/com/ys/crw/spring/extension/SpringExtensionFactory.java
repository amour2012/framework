package com.ys.crw.spring.extension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import com.ys.crw.common.extension.Activate;
import com.ys.crw.common.extension.ExtensionFactory;

/**
 * @author oscar.wu
 *
 */
public class SpringExtensionFactory implements ExtensionFactory {


private static final Map<ApplicationContext, ApplicationContext> contexts = new ConcurrentHashMap<>();
    
    public static void addApplicationContext(ApplicationContext context) {
        contexts.put(context, context);
    }

    public static void removeApplicationContext(ApplicationContext context) {
        contexts.remove(context);
    }

    @Override
    public <T> List<T> getExtension(Class<T> type) {
    	return contexts.keySet().stream().map(c -> c.getBeansOfType(type).values()).flatMap(b -> b.stream())
    		.filter(b -> b.getClass().isAnnotationPresent(Activate.class)).collect(Collectors.toList());
    }

}
