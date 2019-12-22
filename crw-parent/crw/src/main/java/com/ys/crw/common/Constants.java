package com.ys.crw.common;

import java.util.regex.Pattern;

/**
 * @author oscar.wu
 *
 */
public class Constants {
	public static final String SIGN_HEAD_NAME = "Crw-Sign";
	public static final String NONCE_HEAD_NAME = "Crw-Nonce";
	public static final String EXPIRE_AT_HEAD_NAME = "Crw-ExpireAt";
	public static final String SIGN_ERROR_MESSAGE = "Signature Error";
	
	public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
	public static final String CONNECT_TIMEOUT_KEY = "connect.timeout";
	public static final String TIMEOUT_KEY = "timeout";
	
	public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
	
    public static final String  PROVIDER = "provider";
    public static final String  CONSUMER = "consumer";
}
