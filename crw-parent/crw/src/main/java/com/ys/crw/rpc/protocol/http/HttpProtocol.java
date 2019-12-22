package com.ys.crw.rpc.protocol.http;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.ys.crw.common.URL;
import com.ys.crw.remoting.CrwHttpInvokerServiceExporter;
import com.ys.crw.remoting.DispatcherServlet;
import com.ys.crw.remoting.HttpHandler;
import com.ys.crw.rpc.Function;
import com.ys.crw.rpc.RpcException;
import com.ys.crw.rpc.protocol.AbstractProxyProtocol;

/**
 * @author oscar.wu
 *
 */
public class HttpProtocol extends AbstractProxyProtocol {
	public static final int DEFAULT_PORT = 8080;
	
	private final Map<String, HttpInvokerServiceExporter> exporters = new ConcurrentHashMap<>();
	private RemoteMethodProvider provider;
	

	private class InternalHandler implements HttpHandler {
		
		public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			String uri = request.getRequestURI();
			HttpInvokerServiceExporter exporter = exporters.get(uri);
			if (!request.getMethod().equalsIgnoreCase("POST") || exporter == null) {
                response.setStatus(500);
            } else {
            	try{
            		exporter.handleRequest(request, response);
            	} catch(Throwable e){
            		throw new ServletException(e);
            	}
            }
		}
	}
	
	@Override
	public Runnable doExport(Function function) throws RpcException {
		final String path = function.getUrl().getAbsolutePath();
		if(provider == null){
			synchronized (this) {
				if(provider == null){
					provider = new RemoteMethodProvider();
					DispatcherServlet.addHttpHandler(DEFAULT_PORT, new InternalHandler());
					CrwHttpInvokerServiceExporter exporter = new CrwHttpInvokerServiceExporter();
					exporter.setServiceInterface(RemoteMethodInvoker.class);
					exporter.setService(provider);
					exporters.put(path, exporter);
					try {
						exporter.afterPropertiesSet();
			        } catch (Exception e) {
			            throw new RpcException(e.getMessage(), e);
			        }
				}
			}
		}
		
		provider.export(function);
		
        return new Runnable() {
            public void run() {
            	exporters.remove(path);
            	provider.unexport(function);
            }
        };
	}


	@Override
	public Function doRefer(Method method, URL url) throws RpcException {
		RemoteMethodProxy invoker = new RemoteMethodProxy(method, url);
		return invoker;
	}

}
