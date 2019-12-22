package com.ys.crw.rpc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oscar.wu
 *
 */
public class RpcInvocation implements Invocation, Serializable {

	private static final long serialVersionUID = -6294566137907235934L;

	private Object[]             arguments;

    private Map<String, String>  attachments;
    
    public RpcInvocation() {
    }

    public RpcInvocation(Object[] arguments, Map<String, String> attachments) {
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
    }

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public Map<String, String> getAttachments() {
		return attachments;
	}

	@Override
	public String getAttachment(String key) {
		if (attachments == null) {
            return null;
        }
        return attachments.get(key);
	}

	@Override
	public String getAttachment(String key, String defaultValue) {
		if (attachments == null) {
            return defaultValue;
        }
        String value = attachments.get(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
	}
	
    public void setArguments(Object[] arguments) {
        this.arguments = arguments == null ? new Object[0] : arguments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
    }
    
    public void setAttachment(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<String, String>();
        }
        attachments.put(key, value);
    }

    public void setAttachmentIfAbsent(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<String, String>();
        }
        if (! attachments.containsKey(key)) {
        	attachments.put(key, value);
        }
    }

    public void addAttachments(Map<String, String> attachments) {
    	if (attachments == null) {
    		return;
    	}
    	if (this.attachments == null) {
    		this.attachments = new HashMap<String, String>();
        }
    	this.attachments.putAll(attachments);
    }

    public void addAttachmentsIfAbsent(Map<String, String> attachments) {
    	if (attachments == null) {
    		return;
    	}
    	for (Map.Entry<String, String> entry : attachments.entrySet()) {
    		setAttachmentIfAbsent(entry.getKey(), entry.getValue());
    	}
    }


    @Override
    public String toString() {
        return "RpcInvocation [arguments=" + Arrays.toString(arguments)
                + ", attachments=" + attachments + "]";
    }

}
