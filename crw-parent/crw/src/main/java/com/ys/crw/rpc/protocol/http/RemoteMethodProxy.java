package com.ys.crw.rpc.protocol.http;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.ys.crw.common.Constants;
import com.ys.crw.common.URL;
import com.ys.crw.remoting.CrwSimpleHttpInvokerRequestExecutor;
import com.ys.crw.rpc.Invocation;
import com.ys.crw.rpc.Result;
import com.ys.crw.rpc.RpcException;
import com.ys.crw.rpc.protocol.AbstractFunction;

/**
 * @author oscar.wu
 *
 */
public class RemoteMethodProxy extends AbstractFunction {
	private static ConcurrentHashMap<String, RemoteMethodInvoker> remoteMethodInvokers = new ConcurrentHashMap<>();
	private RemoteMethodInvoker invoker;
	
	public RemoteMethodProxy(Method method, URL url){
		super(method, url);
		String fullPath = url.getFullPath();
		int readTimeout = url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
		int connectTimeout = url.getParameter(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
		String invokerKey = String.format("%s_%s_%s", fullPath, readTimeout, connectTimeout);
		invoker = remoteMethodInvokers.get(invokerKey);
		if(invoker == null){
			synchronized (remoteMethodInvokers) {
				invoker = remoteMethodInvokers.get(invokerKey);
				if(invoker == null){
					final HttpInvokerProxyFactoryBean httpProxyFactoryBean = new HttpInvokerProxyFactoryBean();
					httpProxyFactoryBean.setServiceUrl(fullPath);
					httpProxyFactoryBean.setServiceInterface(RemoteMethodInvoker.class);
					CrwSimpleHttpInvokerRequestExecutor httpInvokerRequestExecutor = new CrwSimpleHttpInvokerRequestExecutor();
					httpInvokerRequestExecutor.setReadTimeout(readTimeout);
					httpInvokerRequestExecutor.setConnectTimeout(connectTimeout);
					httpProxyFactoryBean.setHttpInvokerRequestExecutor(httpInvokerRequestExecutor);
					httpProxyFactoryBean.afterPropertiesSet();
					invoker = (RemoteMethodInvoker)httpProxyFactoryBean.getObject();
					remoteMethodInvokers.put(invokerKey, invoker);
				}
			}
		}
	}
	
	@Override
	public Result doInvoke(Invocation invocation) throws RpcException{
		Method method = getMethod();
		RemoteInvocation remoteInvocation = new RemoteInvocation(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), invocation);
		return invoker.invoke(remoteInvocation);
	}
}
