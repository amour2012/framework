package com.ys.crw.common.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author oscar.wu
 *
 */
public class ExtensionLoader<T> {

	private static final Logger LOG = LoggerFactory.getLogger(ExtensionLoader.class);
    
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();

    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

    private final Class<T> type;

    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();
    
    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();

    private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<String, Activate>();

    private volatile T cachedAdaptiveInstance;

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null)
            throw new IllegalArgumentException("Extension type == null");
        if(!type.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }
        
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    private ExtensionLoader(Class<T> type) {
        this.type = type;
        loadExension();
    }
    
    private boolean isMatchGroup(String group, String[] groups) {
        if (group == null || group.length() == 0) {
            return true;
        }
        if (groups != null && groups.length > 0) {
            for (String g : groups) {
                if (group.equals(g)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回指定名字的扩展。如果指定名字的扩展不存在，则抛异常
     *
     * @param name
     * @return
     */
	@SuppressWarnings("unchecked")
	public T getExtension(String name) {
		if (name == null || name.length() == 0)
		    throw new IllegalArgumentException("Extension name == null");
		Class<?> clazz = cachedClasses.get(name);
		return (T) EXTENSION_INSTANCES.get(clazz);
	}
    
	public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(new TreeSet<String>(cachedClasses.keySet()));
    }

    public T getAdaptiveExtension() {
    	return cachedAdaptiveInstance;
    }

    public List<T> getActivateExtension(String group) {
        List<T> exts = new ArrayList<T>();
        for (Map.Entry<String, Activate> entry : cachedActivates.entrySet()) {
            String name = entry.getKey();
            Activate activate = entry.getValue();
            if (isMatchGroup(group, activate.group())) {
            	T ext = getExtension(name);
            	exts.add(ext);
            }
        }
        return exts.stream().sorted(new Comparator<T>(){
        	@Override
        	public int compare(T o1, T o2) {
        		if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                if (o1.equals(o2)) {
                    return 0;
                }
                Activate a1 = o1.getClass().getAnnotation(Activate.class);
                Activate a2 = o2.getClass().getAnnotation(Activate.class);
                int n1 = a1 == null ? 0 : a1.order();
                int n2 = a2 == null ? 0 : a2.order();
                return n1 > n2 ? 1 : -1; 
        	}
        }).collect(Collectors.toList());
    }
    
    private void loadExension() {
    	List<T> instances = new ArrayList<>();
    	if(type == ExtensionFactory.class){
    		ServiceLoader<T> loader = ServiceLoader.load(type); 
    		for(T instance : loader){
    			instances.add(instance);
    		}
    	} else {
    		ExtensionFactory factory = ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
    		instances = factory.getExtension(type);
    	}
    	
    	String typeName = type.getSimpleName();
    	for(T instance : instances){
    		Class<?> clazz = instance.getClass();
            if (!type.isAssignableFrom(clazz)) {
                throw new IllegalStateException("Error when load extension class(interface: " +
                        type + ", class line: " + clazz.getName() + "), class " 
                        + clazz.getName() + "is not subtype of interface.");
            }
            
            EXTENSION_INSTANCES.putIfAbsent(clazz, instance);
            
            if (clazz.isAnnotationPresent(Adaptive.class)) {
                if(cachedAdaptiveInstance == null) {
                    cachedAdaptiveInstance = instance;
                } else if (!cachedAdaptiveInstance.getClass().equals(clazz)) {
                    throw new IllegalStateException("More than 1 adaptive class found: "
                            + cachedAdaptiveInstance.getClass().getName()
                            + ", " + clazz.getClass().getName());
                }
                continue;
            }
            
    		String clazzName = clazz.getSimpleName();
    		String name;
            if (clazzName.endsWith(typeName)) {
                name = clazzName.substring(0, clazzName.length() - typeName.length()).toLowerCase();
                if (!cachedNames.containsKey(instance.getClass())) {
                    cachedNames.put(instance.getClass(), name);
                }
                Class<?> c = cachedClasses.get(name);
                if (c == null) {
                	cachedClasses.put(name, clazz);
                } else if (c != clazz) {
                    throw new IllegalStateException("Duplicate extension " + type.getName() + " name " + name + " on " + c.getName() + " and " + clazz.getName());
                }
            } else {
                throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config");
            }
            
            // Activate
            Activate activate = clazz.getAnnotation(Activate.class);
            if (activate != null) {
                cachedActivates.put(name, activate);
            }
    	}
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + type.getName() + "]";
    }
    
}