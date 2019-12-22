package com.ys.crw.rpc.filter;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.crw.common.Constants;
import com.ys.crw.common.extension.Activate;
import com.ys.crw.common.utils.ReflectUtils;
import com.ys.crw.common.utils.StringUtils;
import com.ys.crw.rpc.Filter;
import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.Invocation;
import com.ys.crw.rpc.Result;
import com.ys.crw.rpc.RpcContext;
import com.ys.crw.rpc.RpcException;
import com.ys.crw.rpc.RpcResult;

/**
 * @author oscar.wu
 *
 */
@Activate(group = Constants.PROVIDER)
public class ExceptionFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionFilter.class);


	@Override
	public Result invoke(Function function, Invocation invocation) throws RpcException {
		try {
            Result result = function.invoke(invocation);
            if (result.hasException()) {
                try {
                    Throwable exception = result.getException();

                    // 如果是checked异常，直接抛出
                    if (! (exception instanceof RuntimeException) && (exception instanceof Exception)) {
                        return result;
                    }
                    // 在方法签名上有声明，直接抛出
                    Method method = function.getMethod();
                    Class<?>[] exceptionClassses = method.getExceptionTypes();
                    for (Class<?> exceptionClass : exceptionClassses) {
                        if (exception.getClass().equals(exceptionClass)) {
                            return result;
                        }
                    }

                    // 未在方法签名上定义的异常，在服务器端打印ERROR日志
                    LOG.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
                            + ". method: " + function.getMethod().getName() 
                            + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

                    // 异常类和接口类在同一jar包里，直接抛出
                    String serviceFile = ReflectUtils.getCodeBase(function.getMethod().getDeclaringClass());
                    String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                    if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)){
                        return result;
                    }
                    // 是JDK自带的异常，直接抛出
                    String className = exception.getClass().getName();
                    if (className.startsWith("java.") || className.startsWith("javax.")) {
                        return result;
                    }
                    // 是框架本身的异常，直接抛出
                    if (exception instanceof RpcException) {
                        return result;
                    }

                    // 否则，包装成RuntimeException抛给客户端
                    return new RpcResult(new RuntimeException(StringUtils.toString(exception)));
                } catch (Throwable e) {
                    LOG.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost()
                    		+ ". method: " + function.getMethod().getName() 
                            + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
                    return result;
                }
            }
            return result;
        } catch (RuntimeException e) {
        	LOG.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
        			+ ". method: " + function.getMethod().getName() 
                    + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
            throw e;
        }
	}

}
