package com.ys.crw.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;

import com.ys.crw.common.Constants;
import com.ys.crw.common.utils.SignUtils;

/**
 * @author oscar.wu
 *
 */
public class CrwHttpComponentsHttpInvokerRequestExecutor extends HttpComponentsHttpInvokerRequestExecutor {
	static final long SIGN_EXPIRE = Long.parseLong(System.getProperty("sign.expire", "300000")); //毫秒
	static final String SIGN_SECRET = System.getProperty("sign.secret", "f164c1a20ccb11eaa5d38284f0915c4e"); 
	
	@Override
	protected void setRequestBody(
			HttpInvokerClientConfiguration config, HttpPost httpPost, ByteArrayOutputStream baos)
			throws IOException {
		String nonce = java.util.UUID.randomUUID().toString();
		long expireAt = System.currentTimeMillis() + SIGN_EXPIRE;
		String sign = SignUtils.computeSign(baos.toByteArray(), nonce, expireAt, SIGN_SECRET);
		httpPost.setHeader(Constants.SIGN_HEAD_NAME, sign);
		httpPost.setHeader(Constants.NONCE_HEAD_NAME, nonce);
		httpPost.setHeader(Constants.EXPIRE_AT_HEAD_NAME, expireAt + "");
		super.setRequestBody(config, httpPost, baos);
	}
}
