package com.ys.crw.rpc.protocol.http;

import com.ys.crw.rpc.Result;

/**
 * @author oscar.wu
 *
 */
public interface RemoteMethodInvoker {
	public abstract Result invoke(RemoteInvocation invocation);
}
