package com.ys.crw.remoting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.util.NestedServletException;

import com.ys.crw.common.Constants;
import com.ys.crw.common.utils.SignUtils;

/**
 * @author oscar.wu
 *
 */
public class CrwHttpInvokerServiceExporter extends HttpInvokerServiceExporter {
	private static final Logger LOG = LoggerFactory.getLogger(CrwHttpInvokerServiceExporter.class);
	
	private static final ThreadLocal<Boolean> IS_SIGN_ERROR = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			RemoteInvocation invocation = readRemoteInvocation(request);
			RemoteInvocationResult result;
			if(IS_SIGN_ERROR.get()){
				result = new RemoteInvocationResult(new RemotingException(Constants.SIGN_ERROR_MESSAGE));
			} else {
				result = invokeAndCreateResult(invocation, getProxy());
			}
			writeRemoteInvocationResult(request, response, result);
		}
		catch (ClassNotFoundException ex) {
			throw new NestedServletException("Class not found during deserialization", ex);
		}
	}
	
	
	@Override
	protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		try{
			while((len = is.read(buffer)) > -1){
				baos.write(buffer, 0, len);
			}
			baos.flush();
		} catch(IOException e){
			LOG.error(e.getMessage(), e);
		}
		
		IS_SIGN_ERROR.set(false);
		String nonce = request.getHeader(Constants.NONCE_HEAD_NAME);
		String expireAt = request.getHeader(Constants.EXPIRE_AT_HEAD_NAME);
		try{
			if(Long.parseLong(expireAt) > System.currentTimeMillis()){
				String sign = SignUtils.computeSign(baos.toByteArray(), nonce, expireAt, CrwSimpleHttpInvokerRequestExecutor.SIGN_SECRET);
				IS_SIGN_ERROR.set(!sign.equals(request.getHeader(Constants.SIGN_HEAD_NAME)));
			}
		} catch(Exception ex){
			LOG.error("compute sign error", ex);
		}
		
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
