package com.ys.crw.rpc.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import com.ys.crw.common.URL;
import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.Invocation;
import com.ys.crw.rpc.Result;
import com.ys.crw.rpc.RpcException;
import com.ys.crw.rpc.RpcInvocation;
import com.ys.crw.rpc.RpcResult;

/**
 * @author oscar.wu
 *
 */
public abstract class AbstractFunction implements Function {
	private Method method;
	private URL url;
	
	private final Map<String, String> attachment;

    private volatile boolean available = true;

    private volatile boolean destroyed = false;
    
    public AbstractFunction(Method method, URL url){
    	this(method, url, null);
    }
	
	public AbstractFunction(Method method, URL url, Map<String, String> attachment){
		this.method = method;
		this.url = url;
		this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
	}

	public Method getMethod(){
		return method;
	}
	
	public URL getUrl(){
		return url;
	}
	
    public boolean isAvailable() {
        return available;
    }
    
    protected void setAvailable(boolean available) {
        this.available = available;
    }

    public void destroy() {
        if (isDestroyed()) {
            return;
        }
        destroyed = true;
        setAvailable(false);
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public String toString() {
        return getMethod().getDeclaringClass() + " " + getMethod() + " -> " + (getUrl() == null ? "" : getUrl().toString());
    }

    public Result invoke(Invocation inv) throws RpcException {
        if(destroyed) {
            throw new RpcException("Rpc invoker for service is DESTROYED, can not be invoked any more!");
        }
        RpcInvocation invocation = (RpcInvocation)inv;
        if (attachment != null && attachment.size() > 0) {
        	invocation.addAttachmentsIfAbsent(attachment);
        }
        
        try {
            return doInvoke(invocation);
        } catch (InvocationTargetException e) { // biz exception
            Throwable te = e.getTargetException();
            if (te == null) {
                return new RpcResult(e);
            } else {
                if (te instanceof RpcException) {
                    ((RpcException) te).setCode(RpcException.BIZ_EXCEPTION);
                }
                return new RpcResult(te);
            }
        } catch (RpcException e) {
            if (e.isBiz()) {
                return new RpcResult(e);
            } else {
                throw e;
            }
        } catch (Throwable e) {
            return new RpcResult(e);
        }
    }

    protected abstract Result doInvoke(Invocation invocation) throws Throwable;

}
