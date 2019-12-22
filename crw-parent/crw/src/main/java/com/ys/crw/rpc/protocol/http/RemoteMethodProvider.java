package com.ys.crw.rpc.protocol.http;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.Result;
import com.ys.crw.rpc.RpcException;
import com.ys.crw.rpc.RpcResult;

/**
 * @author oscar.wu
 *
 */
public class RemoteMethodProvider implements RemoteMethodInvoker {
	private static final Logger LOG = LoggerFactory.getLogger(RemoteMethodProvider.class);
	private Map<Method, Function> targets = new ConcurrentHashMap<>(); 

	@Override
	public Result invoke(RemoteInvocation invocation) {

		try{
			Method method = invocation.getType().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
			if(!targets.containsKey(method)){
				return new RpcResult(new RpcException(RpcException.FORBIDDEN_EXCEPTION));
			}
			Function function = targets.get(method);
			return function.invoke(invocation.getInvocation());
        } catch (Throwable e) {
        	String errorMessage = "Failed to invoke remote proxy method " + invocation.getMethodName() + ", cause: " + e.getMessage();
        	LOG.error(errorMessage, e);
            throw new RpcException(errorMessage, e);
        }
	}
	
	public void export(Function function){
		targets.put(function.getMethod(), function);
	}
	
	public void unexport(Function function){
		targets.remove(function.getMethod());
	}
}
