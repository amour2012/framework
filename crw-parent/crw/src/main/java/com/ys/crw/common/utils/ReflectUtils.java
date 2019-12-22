package com.ys.crw.common.utils;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * @author oscar.wu
 *
 */
public class ReflectUtils {
	public static String getCodeBase(Class<?> cls) {
	    if (cls == null)
	        return null;
	    ProtectionDomain domain = cls.getProtectionDomain();
	    if (domain == null)
	        return null;
	    CodeSource source = domain.getCodeSource();
	    if (source == null)
	        return null;
	    URL location = source.getLocation();
	    if (location == null)
            return null;
	    return location.getFile();
	}
}
