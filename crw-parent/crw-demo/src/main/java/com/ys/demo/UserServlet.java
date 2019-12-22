package com.ys.demo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author oscar.wu
 *
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 978255734938082702L;

	private static Logger LOG = Logger.getLogger(UserServlet.class);
	
	@Autowired
	private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        response.setContentType("text;html;charset=utf-8");
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            if(request.getRequestURI().contains("login")){
            	printWriter.print("{'result':'" + userService.login(123456, "region1") + "'}");
            } else {
            	printWriter.print("{'result':'" + userService.saveUserName(123456, "userName") + "'}");
            }
        } catch (IOException e) {
        	LOG.error("user servlet error.", e);
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }
}
