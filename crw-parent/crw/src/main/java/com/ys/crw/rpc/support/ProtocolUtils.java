package com.ys.crw.rpc.support;

import com.ys.crw.common.URL;

/**
 * @author oscar.wu
 *
 */
public class ProtocolUtils {
	public static String serviceKey(URL url) {
        return serviceKey(url.getPort(), url.getPath());
    }
	
	public static String serviceKey(int port, String serviceName) {
        StringBuilder buf = new StringBuilder();
        buf.append(serviceName);
        buf.append(":");
        buf.append(port);
        return buf.toString();
    }
}
