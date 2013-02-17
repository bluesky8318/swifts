package cn.org.zeronote.swift.bootstrap.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.org.zeronote.swift.bootstrap.server.IServerContext;

/**
 * Spring Context Server
 * <p>
 * Responsibility :
 * <p>
 * 1, Initialize all beans
 * <p>
 * 2, Startup bean's run context if necessary
 * <p>
 * 3, Shutdown all beans with destory their run context
 * <p>
 * rt.dat format : shutdown_port
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public class SpringServer implements IServerContext {

	final static private Logger logger = LoggerFactory.getLogger(SpringServer.class);

	private String beansLocations = "classpath:/*-beans.xml,classpath:/*Context.xml,classpath:/*-context.xml,classpath*:META-INF/spring/*-beans.xml";

	private static ClassPathXmlApplicationContext classPathXmlApplicationContext = null;	
	
	/**
	 * 启动，并初始化
	 * @throws Exception
	 */
	public void startup() throws Exception {
		init(beansLocations);
	}
	
	/**
	 * 初始化上下文
	 * @param beansLocations
	 */
	private void init(String beansLocations) {
		if (beansLocations == null) {
			throw new RuntimeException("Bean config file location is required!");
		}
		beansLocations = beansLocations.trim();
		if (beansLocations.equals("")) {
			throw new RuntimeException("Bean config file location is required!");
		}
			
		logger.info("Begin to init .........");
		String[] beans_config_array = null;
		if (beansLocations.indexOf(',') != -1) {
			beans_config_array = beansLocations.split(",");
		} else if (beansLocations.indexOf('|') != -1) {
			beans_config_array = beansLocations.split("\\|");
		} else {
			beans_config_array = new String[] { beansLocations };
		}
		if (beans_config_array == null) {
			throw new RuntimeException("separation must use ',' or '|' ");
		}
		logger.info("Loading beans config files ...");
		
		for (int i = 0; i < beans_config_array.length; i++) {
			logger.info("[{}]", beans_config_array[i]);
		}
		
		classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
		classPathXmlApplicationContext.setId("swifts");
		classPathXmlApplicationContext.setConfigLocations(beans_config_array);
		classPathXmlApplicationContext.refresh();
		classPathXmlApplicationContext.registerShutdownHook();	// 注册shutdown hook
		logger.info("End to init !");
	}

	/**
	 * shutdown
	 */
	public void shutdown() {
		// have shutdown hook
	}
}
