package com.ys.crw.spring.config;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.Assert;

import com.ys.crw.common.Constants;
import com.ys.crw.common.URL;
import com.ys.crw.common.utils.NetUtils;
import com.ys.crw.meta.AppInfoSystemPropertyDataSource;
import com.ys.crw.meta.ApplicationInfoDataSource;
import com.ys.crw.meta.RegionParam;
import com.ys.crw.meta.Registry;
import com.ys.crw.meta.SupportRemote;
import com.ys.crw.proxy.RemoteMethodProxy;
import com.ys.crw.rpc.Protocol;
import com.ys.crw.rpc.protocol.http.HttpProtocol;
import com.ys.crw.spring.extension.SpringExtensionFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * @author oscar.wu
 *
 */
//@Configuration
public class CrwBeanConfig implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(CrwBeanConfig.class);
	
	private static final String REMOTE_METHOD_PROXY_BEAN_NAME = RemoteMethodProxy.class.getSimpleName() + "@" + RemoteMethodProxy.class.hashCode();
	private static final String AROUND_INVOKE_POINT_CUT_BEAN_ID = SupportRemote.class.getSimpleName() + "_PointCut@" + SupportRemote.class.hashCode();
	private static final String HTTP_PROTOCOL_BEAN_NAME = HttpProtocol.class.getSimpleName() + "@" + HttpProtocol.class.hashCode();
	
	private Protocol protocol;
	
	@Getter
	@Setter
	private String contextPath;
	
	@Getter
	@Setter
	private int port;
	
	private static final String AOP_CONFIG_XML = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n" + 
			"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
			"       xmlns:aop= \"http://www.springframework.org/schema/aop\"\r\n" + 
			"       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd\r\n" + 
			"               http://www.springframework.org/schema/aop \r\n" + 
			"               http://www.springframework.org/schema/aop/spring-aop-3.1.xsd\">\r\n" + 
			"<aop:config proxy-target-class=\"true\">\r\n" + 
			"	<aop:aspect ref=\"%s\">\r\n" + 
			"		<aop:pointcut id=\"%s\"\r\n" + 
			"			expression=\"@annotation(%s)\" />\r\n" + 
			"		<aop:around method=\"aroundInvoke\" pointcut-ref=\"%s\" />\r\n" + 
			"	</aop:aspect>\r\n" + 
			"</aop:config>\r\n" + 
			"</beans>", REMOTE_METHOD_PROXY_BEAN_NAME, AROUND_INVOKE_POINT_CUT_BEAN_ID, SupportRemote.class.getName(), AROUND_INVOKE_POINT_CUT_BEAN_ID);

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException{
		BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry)beanFactory;
		ByteArrayResource resource = new ByteArrayResource(AOP_CONFIG_XML.getBytes());
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanRegistry);
		reader.loadBeanDefinitions(new EncodedResource(resource));
		
		BeanDefinitionBuilder httpProtocolBuilder = BeanDefinitionBuilder.genericBeanDefinition(HttpProtocol.class);
		BeanDefinition httpProtocol = httpProtocolBuilder.getRawBeanDefinition();
		beanRegistry.registerBeanDefinition(HTTP_PROTOCOL_BEAN_NAME, httpProtocol);
		
		BeanDefinitionBuilder methodProxyBuilder = BeanDefinitionBuilder.genericBeanDefinition(RemoteMethodProxy.class);
		methodProxyBuilder.addConstructorArgReference(HTTP_PROTOCOL_BEAN_NAME);
		BeanDefinition remoteMethodProxy = methodProxyBuilder.getRawBeanDefinition();
		beanRegistry.registerBeanDefinition(REMOTE_METHOD_PROXY_BEAN_NAME, remoteMethodProxy);
		
		protocol = beanFactory.getBean(HttpProtocol.class);
	}
	
	private void registrySupportRemoteMethod(Class<?> clazz, Object bean){
		if(Object.class.equals(clazz)){
			return;
		}
		for(Method method : clazz.getMethods()){
			SupportRemote methodAnnotation = AnnotationUtils.findAnnotation(method, SupportRemote.class);
			if(methodAnnotation != null){
				Parameter[] params = method.getParameters();
				OptionalInt paramIndex = IntStream.range(0, params.length).filter(i -> params[i].getAnnotation(RegionParam.class) != null).findFirst();
				
				Assert.isTrue(!paramIndex.isPresent() || params[paramIndex.getAsInt()].getType().equals(String.class), "The class of parameter annotated by RegionParam must be String : " + method.getName());
				
				Registry.register(methodAnnotation, method, paramIndex.isPresent() ? paramIndex.getAsInt() : -1);
				if(Registry.needToExport(method)){
					Map<String, String> parameters = new HashMap<>();
					parameters.put(Constants.TIMEOUT_KEY, methodAnnotation.timeout() + "");
					URL url = new URL("http", NetUtils.getLocalHost(), port, (contextPath == null || contextPath.length() == 0) ? "" : contextPath, parameters);
					protocol.export(method, url, bean);
					LOG.info("export method : %s", method.toGenericString());
				}
			}
		}
		registrySupportRemoteMethod(clazz.getSuperclass(), bean);
	}
	

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException{		
		try{
			registrySupportRemoteMethod(bean.getClass(), bean);
		} catch(Exception ex){
			LOG.warn("registry method error", ex);
		}
		
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
		return bean;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		SpringExtensionFactory.addApplicationContext(applicationContext);
		ApplicationInfoDataSource applicationInfos;
		try {
			applicationInfos = applicationContext.getBean(ApplicationInfoDataSource.class);
		} catch(BeansException ex){
			applicationInfos = new AppInfoSystemPropertyDataSource();
		}
		
		Registry.register(applicationInfos.appInfos());
		Registry.setCurrentApplication(applicationInfos.currentRegion(), applicationInfos.currentAppName());
	}
}
