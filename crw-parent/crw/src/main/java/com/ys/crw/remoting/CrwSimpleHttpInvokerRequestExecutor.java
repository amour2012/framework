package com.ys.crw.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import com.ys.crw.common.Constants;
import com.ys.crw.common.utils.SignUtils;

/**
 * @author oscar.wu
 *
 */
public class CrwSimpleHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {
	static final long SIGN_EXPIRE = Long.parseLong(System.getProperty("sign.expire", "300000")); //毫秒
	static final String SIGN_SECRET = System.getProperty("sign.secret", "f164c1a20ccb11eaa5d38284f0915c4e"); 
	
	@Override
	protected void writeRequestBody(HttpInvokerClientConfiguration config, HttpURLConnection con, ByteArrayOutputStream baos) throws IOException {
		String nonce = java.util.UUID.randomUUID().toString();
		long expireAt = System.currentTimeMillis() + SIGN_EXPIRE;
		String sign = SignUtils.computeSign(baos.toByteArray(), nonce, expireAt, SIGN_SECRET);
		con.setRequestProperty(Constants.SIGN_HEAD_NAME, sign);
		con.setRequestProperty(Constants.NONCE_HEAD_NAME, nonce);
		con.setRequestProperty(Constants.EXPIRE_AT_HEAD_NAME, expireAt + "");
		super.writeRequestBody(config, con, baos);
	}
	
	
	
	
}
