/**
 * swifts properties
 */
package cn.org.zeronote.swift.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Utility class to read the bootstrap Swifts configuration.
 * 
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 * 
 */
public class SwiftsProperties {

	private static Properties properties = null;

	static {
		loadProperties();
	}

	/**
	 * Return specified property value.
	 */
	public static String getProperty(String name) {
		return System.getProperty(name, properties.getProperty(name));
	}

	/**
	 * Return specified property value.
	 */
	public static String getProperty(String name, String defaultValue) {
		return System.getProperty(name,
				properties.getProperty(name, defaultValue));
	}

	/**
	 * Load properties.
	 */
	private static void loadProperties() {

		InputStream is = null;
		Throwable error = null;

		String configUrl = getConfigUrl();
		try {
			if (configUrl != null) {
				is = (new URL(configUrl)).openStream();
			}
			System.out.println("Found Config:" + configUrl);
		} catch (Throwable t) {
//			System.out.println("Not found Config:" + configUrl + "!");
		}

		if (is == null) {
			try {
				File home = new File(getHome());
				File conf = new File(home, "etc");
				File properties = new File(conf, "swifts.properties");
				is = new FileInputStream(properties);
				System.out.println("Found " + getHome() + "/etc/swifts.properties!");
			} catch (Throwable t) {
				// System.out.println("Not found "+getJackeelHome()+"/etc/swifts.properties!");
			}
		}

		if (is == null) {
			try {
				is = SwiftsProperties.class.getResourceAsStream("/swifts.properties");
				System.out.println("Found swifts.properties in runtime-x.x.x.jar!");
			} catch (Throwable t) {
				// System.out.println("Not found jackeel.properties on classpath!");
			}
		}

		if (is != null) {
			try {
				properties = new Properties();
				properties.load(is);
				is.close();
			} catch (Throwable t) {
				error = t;
			}
		} else {
			System.out.println("Not found swifts.properties!");
		}

		if ((is == null) || (error != null)) {
			// That's fine - we have reasonable defaults.
			properties = new Properties();
		}

		// System.out.println("properties="+properties);
	}

	/**
	 * Get the value of the keel.home environment variable.
	 */
	public static String getHome() {
		return System.getProperty("swifts.home", System.getProperty("user.dir"));
	}

	/**
	 * Get the value of the configuration URL.
	 */
	public static String getConfigUrl() {
		return getHome() + "/swifts.properties";
	}
}
