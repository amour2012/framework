package com.ys.crw.rpc;

import java.lang.reflect.Method;

/**
 * @author oscar.wu
 *
 */
public interface Function extends Node {
	Method getMethod();

	Result invoke(Invocation invocation) throws RpcException;
}
