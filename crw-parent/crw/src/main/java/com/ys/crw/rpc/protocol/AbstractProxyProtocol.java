package com.ys.crw.rpc.protocol;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ys.crw.common.Constants;
import com.ys.crw.common.URL;
import com.ys.crw.common.extension.ExtensionLoader;
import com.ys.crw.rpc.Exporter;
import com.ys.crw.rpc.Filter;
import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.Invocation;
import com.ys.crw.rpc.Result;
import com.ys.crw.rpc.RpcException;
import com.ys.crw.rpc.RpcResult;
import com.ys.crw.rpc.support.ProtocolUtils;

/**
 * @author oscar.wu
 *
 */
public abstract class AbstractProxyProtocol extends AbstractProtocol {
	private final List<Class<?>> rpcExceptions = new CopyOnWriteArrayList<Class<?>>();;

    public AbstractProxyProtocol() {
    }

    public AbstractProxyProtocol(Class<?>... exceptions) {
        for (Class<?> exception : exceptions) {
            addRpcException(exception);
        }
    }

    public void addRpcException(Class<?> exception) {
        this.rpcExceptions.add(exception);
    }


	public Exporter export(Method method, URL url, Object target) throws RpcException {
		final Function function = new AbstractFunction(method, url, null){
			@Override
			protected Result doInvoke(Invocation invocation) throws Throwable {
				return new RpcResult(method.invoke(target, invocation.getArguments()));
			}
		}; 
        final String exporterKey = ProtocolUtils.serviceKey(function.getUrl()) + "/" + method.getDeclaringClass().getName() + "." + method.toString();
        Exporter exporter = exporterMap.get(exporterKey);
        if (exporter != null) {
        	return exporter;
        }
        final Runnable runnable = doExport(buildInvokerChain(function, Constants.PROVIDER));
        exporter = new AbstractExporter(function) {
            public void unexport() {
                super.unexport();
                exporterMap.remove(exporterKey);
                if (runnable != null) {
                    try {
                        runnable.run();
                    } catch (Throwable t) {
                    	LOG.warn(t.getMessage(), t);
                    }
                }
            }
        };
        exporterMap.put(exporterKey, exporter);
        return exporter;
    }

    public Function refer(final Method method, URL url) throws RpcException {
        final Function tagert = doRefer(method, url);
        Function function = new AbstractFunction(method, url) {
            @Override
            protected Result doInvoke(Invocation invocation) throws Throwable {
                try {
                    Result result = tagert.invoke(invocation);
                    Throwable e = result.getException();
                    if (e != null) {
                        for (Class<?> rpcException : rpcExceptions) {
                            if (rpcException.isAssignableFrom(e.getClass())) {
                                throw getRpcException(method, url, invocation, e);
                            }
                        }
                    }
                    return result;
                } catch (RpcException e) {
                    if (e.getCode() == RpcException.UNKNOWN_EXCEPTION) {
                        e.setCode(getErrorCode(e.getCause()));
                    }
                    throw e;
                } catch (Throwable e) {
                    throw getRpcException(method, url, invocation, e);
                }
            }
        };
        function = buildInvokerChain(function, Constants.CONSUMER);
        functions.put(function, function);
        return function;
    }

    protected RpcException getRpcException(Method method, URL url, Invocation invocation, Throwable e) {
        RpcException re = new RpcException("Failed to invoke remote service: " + method.getDeclaringClass() + ", method: "
                + method.getName() + ", cause: " + e.getMessage(), e);
        re.setCode(getErrorCode(e));
        return re;
    }

    protected int getErrorCode(Throwable e) {
        return RpcException.UNKNOWN_EXCEPTION;
    }

    protected abstract Runnable doExport(Function function) throws RpcException;

    protected abstract Function doRefer(Method method, URL url) throws RpcException;
    
    private static Function buildInvokerChain(final Function function, String group) {
    	Function last = function;
        List<Filter> filters = ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(group);
        for (int i = filters.size() - 1; i >= 0; i--) {
            final Filter filter = filters.get(i);
            final Function next = last;
            last = new Function() {
				@Override
				public boolean isAvailable() {
					return function.isAvailable();
				}
				
				@Override
				public URL getUrl() {
					return function.getUrl();
				}
				
				@Override
				public void destroy() {
					function.destroy();					
				}
				
				@Override
				public Result invoke(Invocation invocation) throws RpcException {
					return filter.invoke(next, invocation);
				}
				
				@Override
				public Method getMethod() {
					return function.getMethod();
				}
			};
        }

        return last;
    }
}
