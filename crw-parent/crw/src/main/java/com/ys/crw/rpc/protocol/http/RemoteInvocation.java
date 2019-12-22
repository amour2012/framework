package com.ys.crw.rpc.protocol.http;

import java.io.Serializable;

import com.ys.crw.rpc.Invocation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author oscar.wu
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoteInvocation implements Serializable {

	private static final long serialVersionUID = 745742620793219806L;
	
	private Class<?> type;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Invocation invocation;
}
