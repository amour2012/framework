package com.ys.crw.meta;

import java.util.List;

/**
 * @author oscar.wu
 *
 */
public interface ApplicationInfoDataSource {
	String currentRegion();
	String currentAppName();
	List<ApplicationInfo> appInfos();
}
