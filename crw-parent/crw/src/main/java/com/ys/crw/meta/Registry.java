package com.ys.crw.meta;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author oscar.wu
 *
 */
public class Registry {
	private static ApplicationInfo currentApp;
	private static String currentRegion;
	private static String currentAppName;
	private static Map<String, RemoteMethodDefinition> methods = new ConcurrentHashMap<>();
	private static Map<ApplicationInfo, ApplicationInfo> allAppInfos = new ConcurrentHashMap<>();
	
	public static void setCurrentApplication(String region, String appName){
		currentRegion = region;
		currentAppName = appName;
		Optional<ApplicationInfo> opt = allAppInfos.keySet().stream().filter(a -> a.getRegion().equals(region) && a.getName().equals(appName)).findAny();
		if(opt.isPresent()){
			currentApp = opt.get();
		}
	}
	public static void register(List<ApplicationInfo> appInfos){
		if(appInfos == null){
			return;
		}
		appInfos.stream().forEach(a -> register(a));
	}
	public static void register(ApplicationInfo appInfo){
		allAppInfos.put(appInfo, appInfo);
		if(appInfo.getRegion().equals(currentRegion) && appInfo.getName().equals(currentAppName)){
			currentApp = appInfo;
		}
	}
	public static void register(SupportRemote methodAnnotation, Method method, int regionParamIndex){
		if(methodAnnotation == null || method == null){
			return;
		}
		RemoteMethodDefinition defintiion = new RemoteMethodDefinition(method, regionParamIndex, regionParamIndex >= 0 ? false : methodAnnotation.center());
		methods.put(methodKey(method), defintiion);
	}
	
	public static ApplicationInfo getRemoteAppInfo(Method method, Object[] args){
		if(method == null){
			return null;
		}
		RemoteMethodDefinition definition = methods.get(methodKey(method));
		if(definition == null){
			return null;
		}
		// 如果指定了分区参数,则返回指定分区的应用
		if(definition.getRegionParamIndex() >= 0){
			String region = (String)args[definition.getRegionParamIndex()];
			return allAppInfos.keySet().stream().filter(a -> a.getRegion().endsWith(region) && a.getName().equals(currentAppName)).findFirst().get();
		}
		// 非中心区域访问中心区域应用,优先返回相同应用
		if(definition.isCenter() && (currentApp == null || !currentApp.isCenter())){
			ApplicationInfo appInfo = allAppInfos.keySet().stream().filter(a -> a.isCenter() && a.getName().equals(currentAppName)).findFirst().get();
			if(appInfo != null){
				return appInfo;
			}
			return allAppInfos.keySet().stream().filter(a -> a.isCenter()).findFirst().get();
		}
		return null;
	}
	
	public static boolean needToExport(Method method){
		if(method == null){
			return false;
		}
		if(currentApp == null){
			return false;
		}
		RemoteMethodDefinition definition = methods.get(methodKey(method));
		if(definition.isCenter() && currentApp.isCenter() || !definition.isCenter() && !currentApp.isCenter()){
			return true;
		}
		return false;
	}
	
	private static String methodKey(Method method){
		return method.toGenericString();
	}
}
