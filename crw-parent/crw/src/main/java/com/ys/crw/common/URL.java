package com.ys.crw.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import lombok.Getter;

/**
 * @author oscar.wu
 *
 */
@Getter
public class URL {
	private final String protocol;
	private final String host;
	private final String path;
	private final int port;
	private final Map<String, String> parameters;
	
	public URL(String host, String path){
		this("", host, 0, path, new HashMap<String, String>());
	}
	
	public URL(String protocol, String host, int port, String path){
		this(protocol, host, port, path, new HashMap<String, String>());
	}
	
	public URL(String protocol, String host, int port, String path, Map<String, String> parameters){
		if(!StringUtils.isEmpty(host)){
			int index = host.indexOf("://");
			if(index > 0){
				if(StringUtils.isEmpty(protocol)){
					protocol = host.substring(0, index);
				}
				host = host.substring(index + 3);
			}
			
			index = host.indexOf(":");
			if(index > 0){
				if(port == 0 && index < host.length() - 1){
					port = Integer.parseInt(host.substring(index + 1));
				}
				host = host.substring(0, index);
			}
		}
		
		this.protocol = protocol;
		this.host = host;
		this.path = path;
		this.port = port;
		this.parameters = Collections.unmodifiableMap(parameters);
	}
	
	public String getAbsolutePath() {
        if (path != null && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
	}
	
	public int getParameter(String key, int defaultValue) {
        String value = parameters.get(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        return i;
    }
	
	public String getFullPath(){
		return String.format("%s://%s:%s%s", protocol, host, port, getAbsolutePath());
	}
}
