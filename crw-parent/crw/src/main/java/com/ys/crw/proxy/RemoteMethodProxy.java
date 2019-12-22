package com.ys.crw.proxy;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

import com.ys.crw.common.URL;
import com.ys.crw.meta.ApplicationInfo;
import com.ys.crw.meta.Registry;
import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.Protocol;
import com.ys.crw.rpc.Result;
import com.ys.crw.rpc.RpcInvocation;

/**
 * @author oscar.wu
 *
 */
public class RemoteMethodProxy {
	private static final Logger LOG = LoggerFactory.getLogger(RemoteMethodProxy.class);
	private Protocol protocol;
	
	public RemoteMethodProxy(Protocol protocol){
		this.protocol = protocol;
	}
	
	public Object aroundInvoke(ProceedingJoinPoint point) throws Throwable {
		MethodInvocationProceedingJoinPoint methodPoint = (MethodInvocationProceedingJoinPoint)point;
		MethodSignature methodSignature = (MethodSignature)methodPoint.getSignature();
		Method method = methodSignature.getMethod();
		ApplicationInfo appInfo = Registry.getRemoteAppInfo(method, point.getArgs());
		if(appInfo != null){
			URL url = new URL(appInfo.getHost(), appInfo.getPath());
			Function function = protocol.refer(method, url);
			Result result = function.invoke(new RpcInvocation(point.getArgs(), null));
			if(result.hasException()){
				result.recreate();
			}
			return result.getValue();
		}
			
		return point.proceed();
	} 
}
