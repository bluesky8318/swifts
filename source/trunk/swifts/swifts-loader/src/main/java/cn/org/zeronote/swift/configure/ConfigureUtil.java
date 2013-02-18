/**
 * 
 */
package cn.org.zeronote.swift.configure;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Server配置文件读取工具
 * 
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public class ConfigureUtil {

	private static JAXBContext jc;
	
	/**
	 * 
	 */
	private ConfigureUtil() {
	}

	/**
	 * 读取配置文件
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static Server getConfigure(InputStream input) throws Exception {
		Unmarshaller um = getJc().createUnmarshaller();
		Server config = (Server) um.unmarshal(input);
		return config;
	}
	
	/**
	 * @return the jc
	 * @throws JAXBException 
	 */
	private synchronized static JAXBContext getJc() throws JAXBException {
		if (jc == null) {
			jc = JAXBContext.newInstance(ObjectFactory.class);
		}
		return jc;
	}
}
