package com.ys.crw.rpc;

/**
 * @author oscar.wu
 *
 */
public class RpcContext {
	private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};

	/**
	 * get context.
	 * 
	 * @return context
	 */
	public static RpcContext getContext() {
	    return LOCAL.get();
	}
	
	private String remoteAddress;
	
    public String getRemoteHost() {
    	return remoteAddress;
    }
    
    public void setRemoteHost(String remoteAddress) {
    	this.remoteAddress = remoteAddress;
    }
}
