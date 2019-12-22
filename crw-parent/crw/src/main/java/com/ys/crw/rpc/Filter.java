package com.ys.crw.rpc;

/**
 * @author oscar.wu
 *
 */
public interface Filter {
	/**
	 * 
	 * @param function
	 * @param invocation
	 * @return
	 * @throws RpcException
	 */
	Result invoke(Function function, Invocation invocation) throws RpcException;
}
