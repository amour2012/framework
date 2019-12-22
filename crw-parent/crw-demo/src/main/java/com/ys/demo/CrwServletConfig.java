package com.ys.demo;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ys.crw.remoting.DispatcherServlet;
import com.ys.crw.spring.config.CrwBeanConfig;

/**
 * @author oscar.wu
 *
 */
@Configuration
public class CrwServletConfig {
	private final static String CRW_INVOKE_PATH = "/crw/invoke";

    @Bean
    public UserServlet userServlet() {
        return new UserServlet();
    }

    @Bean
    public ServletRegistrationBean userServletRegistrationBean(UserServlet userServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(userServlet);
        registration.setEnabled(true);
        registration.setLoadOnStartup(1);
        registration.addUrlMappings("/user/login","/user/saveName");
        return registration;
    }
    
    @Bean
    public CrwBeanConfig crwBeanConfig(){
    	System.setProperty("crw.current.region", "region2");
    	System.setProperty("crw.current.appname","crw-demo");
    	System.setProperty("crw.appinfos","[{'region':'region1','name':'crw-demo','host':'http://127.0.0.1:8080','path':'" + CRW_INVOKE_PATH + "','center':true},"
    			+ "{'region':'region2','name':'crw-demo','host':'http://127.0.0.1:8081','path':'" + CRW_INVOKE_PATH + "','center':false}]");
    	CrwBeanConfig crwBeanConfig = new CrwBeanConfig();
    	crwBeanConfig.setContextPath(CRW_INVOKE_PATH);
    	crwBeanConfig.setPort(8080);
    	return crwBeanConfig;
    }
    
    
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public ServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
        registration.setEnabled(true);
        registration.setLoadOnStartup(1);
        registration.addUrlMappings(CRW_INVOKE_PATH);
        return registration;
    }
}
