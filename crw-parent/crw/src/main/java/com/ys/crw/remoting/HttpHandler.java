package com.ys.crw.remoting;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author oscar.wu
 *
 */
public interface HttpHandler {
	void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
