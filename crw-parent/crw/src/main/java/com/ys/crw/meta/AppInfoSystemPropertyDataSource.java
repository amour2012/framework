package com.ys.crw.meta;

import java.util.List;

import com.alibaba.fastjson.JSONArray;

/**
 * @author oscar.wu
 *
 */
public class AppInfoSystemPropertyDataSource implements ApplicationInfoDataSource {
	public static final String CURRENT_REGION_PROPERTY_KEY = "crw.current.region";
	public static final String CURRENT_APP_NAME_PROPERTY_KEY = "crw.current.appname";
	public static final String APP_INFOS_PROPERTY_KEY = "crw.appinfos";
	
	@Override
	public String currentRegion(){
		return System.getProperty("crw.current.region");
	}

	@Override
	public String currentAppName() {
		return System.getProperty("crw.current.appname");
	}

	@Override
	public List<ApplicationInfo> appInfos() {
		return JSONArray.parseArray(System.getProperty("crw.appinfos"), ApplicationInfo.class);
	}

}
