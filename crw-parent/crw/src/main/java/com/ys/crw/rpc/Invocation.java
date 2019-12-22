package com.ys.crw.rpc;

import java.util.Map;

/**
 * @author oscar.wu
 *
 */
public interface Invocation {

	/**
	 * get arguments.
	 * 
	 * @serial
	 * @return arguments.
	 */
	Object[] getArguments();

	/**
	 * get attachments.
	 * 
	 * @serial
	 * @return attachments.
	 */
	Map<String, String> getAttachments();
	
	/**
     * get attachment by key.
     * 
     * @serial
     * @return attachment value.
     */
	String getAttachment(String key);
	
	/**
     * get attachment by key with default value.
     * 
     * @serial
     * @return attachment value.
     */
	String getAttachment(String key, String defaultValue);

//    /**
//     * get the invoker in current context.
//     * 
//     * @transient
//     * @return invoker.
//     */
//    Invoker getInvoker();
}
