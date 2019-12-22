package com.ys.crw.rpc.protocol;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.crw.rpc.Exporter;
import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.Protocol;

/**
 * @author oscar.wu
 *
 */
public abstract class AbstractProtocol implements Protocol {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractProtocol.class);

	protected final Map<String, Exporter> exporterMap = new ConcurrentHashMap<String, Exporter>();

    protected final Map<Function, Function> functions = new ConcurrentHashMap<>();
	
	public void destroy() {
	    for (Function function : functions.keySet()){
	        if (function == null) {
	        	continue;
	        }
        	functions.remove(function);
            try {
                if (LOG.isInfoEnabled()) {
                	LOG.info("Destroy reference: " + function.getUrl());
                }
                function.destroy();
            } catch (Throwable t) {
            	LOG.warn(t.getMessage(), t);
            }
	    }
	    for (String key : new ArrayList<String>(exporterMap.keySet())) {
            Exporter exporter = exporterMap.remove(key);
            if (exporter == null) {
            	continue;
            }
            try {
                if (LOG.isInfoEnabled()) {
                	LOG.info("Unexport service: " + exporter.getFunction().getUrl());
                }
                exporter.unexport();
            } catch (Throwable t) {
            	LOG.warn(t.getMessage(), t);
            }
        }
	}

}
