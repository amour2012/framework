package com.ys.crw.remoting;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author oscar.wu
 *
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 4976976874945297025L;
	
	private static DispatcherServlet INSTANCE;
	
	private static final Map<Integer, HttpHandler> handlers = new ConcurrentHashMap<>();
	
	public static void addHttpHandler(int port, HttpHandler processor){
		handlers.put(port, processor);
	}
	
	public static void removeHttpHandler(int port, HttpHandler processor){
		handlers.remove(port);
	}
	
	public static DispatcherServlet getInstance(){
		return INSTANCE;
	}
	
	public DispatcherServlet(){
		DispatcherServlet.INSTANCE = this;
	}
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpHandler handler = handlers.get(request.getLocalPort());
        if( handler == null ) {// service not found.
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Service not found.");
        } else {
            handler.handle(request, response);
        }
	}
}
