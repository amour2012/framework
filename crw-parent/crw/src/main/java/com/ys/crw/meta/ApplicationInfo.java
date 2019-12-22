package com.ys.crw.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author oscar.wu
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationInfo {
	private String region;
	private String name;
	private String host;
	private String path;
//	private int port;
	private boolean center;
	
	
	public int hashCode() {
        int h = region.hashCode();
        h = name.hashCode();
        h = 31 * h + host.hashCode();
        h = 31 * h + path.hashCode();
//        h = 31 * h + Integer.hashCode(port);
        h = 31 * h + Boolean.hashCode(center);
        return h;
    }
	
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ApplicationInfo)){
			return false;
		}
		ApplicationInfo app = (ApplicationInfo)obj;
		return app.getRegion().equals(region) && app.getName().equals(name) && app.getHost().equals(host) && app.getPath().equals(path) && app.center == center;
    }

}
