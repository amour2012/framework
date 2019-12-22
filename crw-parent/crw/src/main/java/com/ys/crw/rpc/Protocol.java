package com.ys.crw.rpc;

import java.lang.reflect.Method;

import com.ys.crw.common.URL;

/**
 * @author oscar.wu
 *
 */
public interface Protocol {

    Exporter export(Method method, URL url, Object target) throws RpcException;

    Function refer(Method method, URL url) throws RpcException;
}
