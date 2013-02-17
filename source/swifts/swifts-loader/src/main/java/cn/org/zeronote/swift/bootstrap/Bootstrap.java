package cn.org.zeronote.swift.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import cn.org.zeronote.swift.bootstrap.server.IServerContext;
import cn.org.zeronote.swift.bootstrap.server.ShutdownClient;
import cn.org.zeronote.swift.bootstrap.server.ShutdownServer;
import cn.org.zeronote.swift.configure.ConfigureUtil;
import cn.org.zeronote.swift.configure.Server;

/**
 * 启动入口
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public class Bootstrap {

	protected static final String SWIFTS_HOME_TOKEN = "${swifts.home}";

	/** common classloader */
	protected ClassLoader commonLoader = null;
	/** server私有classloader */
	protected ClassLoader serverLoader = null;

	protected Server config;
	protected IServerContext serverContext;
	
	private ShutdownServer shutdownServer = null;
	private ShutdownClient shutdownClient = null;
	
	private transient String configureFile = "etc/Server.xml";
	
	/**
	 * 创建classloader
	 * @param name
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	private ClassLoader createClassLoader(String name, ClassLoader parent) throws Exception {
		String value = SwiftsProperties.getProperty(name + ".loader");
		System.out.println("Create ClassLoader : " + name + "; path:" + value + "; home:" + SwiftsProperties.getHome());
		if ((value == null) || (value.equals(""))) {
			return parent;
		}

		List<String> repositoryLocations = new ArrayList<String>();
		List<Integer> repositoryTypes = new ArrayList<Integer>();
		int i;

		StringTokenizer tokenizer = new StringTokenizer(value, ",");
		while (tokenizer.hasMoreElements()) {
			String repository = tokenizer.nextToken();
			// Local repository
			while ((i = repository.indexOf(SWIFTS_HOME_TOKEN)) >= 0) {
				if (i > 0) {
					repository = repository.substring(0, i)
							+ SwiftsProperties.getHome()
							+ repository.substring(i + SWIFTS_HOME_TOKEN.length());
				} else {
					repository = SwiftsProperties.getHome()
							+ repository.substring(SWIFTS_HOME_TOKEN.length());
				}
			}

			// Check for a JAR URL repository
			try {
				new URL(repository);
				repositoryLocations.add(repository);
				repositoryTypes.add(ClassLoaderFactory.IS_URL);
				continue;
			} catch (MalformedURLException e) {
				// Ignore
			}

			if (repository.endsWith("*.jar")) {
				repository = repository.substring(0, repository.length() - "*.jar".length());
				repositoryLocations.add(repository);
				repositoryTypes.add(ClassLoaderFactory.IS_GLOB);
			} else if (repository.endsWith(".jar")) {
				repositoryLocations.add(repository);
				repositoryTypes.add(ClassLoaderFactory.IS_JAR);
			} else {
				repositoryLocations.add(repository);
				repositoryTypes.add(ClassLoaderFactory.IS_DIR);
			}
		}

		String[] locations = repositoryLocations.toArray(new String[0]);
		Integer[] types = repositoryTypes.toArray(new Integer[0]);

		ClassLoader classLoader = ClassLoaderFactory.createClassLoader(locations, types, parent);

		/*
		 * // Retrieving MBean server MBeanServer mBeanServer = null; if
		 * (MBeanServerFactory.findMBeanServer(null).size() > 0) { mBeanServer =
		 * (MBeanServer) MBeanServerFactory .findMBeanServer(null).get(0); }
		 * else { mBeanServer = ManagementFactory.getPlatformMBeanServer(); }
		 * 
		 * // Register the server classloader ObjectName objectName = new
		 * ObjectName( "Catalina:type=ServerClassLoader,name=" + name);
		 * mBeanServer.registerMBean(classLoader, objectName);
		 */
		return classLoader;
	}

	/**
	 * 
	 */
	private void initClassLoaders() {
		try {
			commonLoader = createClassLoader("common", this.getClass().getClassLoader());
			if (commonLoader == null) {
				// no config file, default to this loader - we might be in a
				// 'single' env.
				commonLoader = this.getClass().getClassLoader();
			}
			serverLoader = createClassLoader("server", commonLoader);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * 初始化，并启动Server
	 * @param args
	 * @throws Exception
	 */
	public void init() throws Exception {
		// 加载classloader
		initClassLoaders();
		// 设置当前classloader
		Thread.currentThread().setContextClassLoader(serverLoader);
		// 加载Server配置文件
		File conf = new File(SwiftsProperties.getHome(), configureFile);
		config = ConfigureUtil.getConfigure(new FileInputStream(conf));
		// 验证配置文件
		if (config.getPort() <= 0) {
			System.out.println("Shutdown port Not Set, Please fill it in server.xml!");
			System.out.println("System exit!");
			System.exit(1);
		}
		// 加载Server上下文
		serverContext = loadServerContext();
		serverContext.startup();
		// 创建shutdown监听
		startShutdownListener();
	}
	
	/**
	 * 创建shutdown监听
	 */
	private void startShutdownListener() {
		shutdownServer = new ShutdownServer(this.getClass().getSimpleName(), serverContext, config.getPort(), config.getShutdown());
		shutdownServer.start();
	}
	
	/**
	 * 加载真实的Server上下文环境
	 * @param args
	 * @throws Exception
	 */
	private IServerContext loadServerContext() throws Exception {
		// load startup class
		String startupClassName = SwiftsProperties.getProperty("entrance.class");
		if (startupClassName == null || startupClassName.trim().equals("")) {
			System.out.println("'entrance.class' system property is ["+ startupClassName + "]");
			System.out.println("You should set 'entrance.class' system property!");
			System.exit(1);
		}
		Class<?> startupClass = serverLoader.loadClass(startupClassName);
		IServerContext startupInstance = (IServerContext) startupClass.newInstance();
		return (IServerContext) startupInstance;
	}

	/**
	 * 主启动方法
	 * @param args
	 */
	public void entrance(String command, String configure) {
		if (configure != null) {
			configureFile = configure;
		}
		if (command.equalsIgnoreCase("startup")) {
			System.out.println("Begin to startup swifts ...");
			try {
				init();
				return;
			} catch (Exception ex) {
				System.out.println("Startup error, system exit");
				ex.printStackTrace();
				System.exit(1);
			}
		} else if (command.equalsIgnoreCase("shutdown")) {
			System.out.println("Begin to shutdown swifts ...");
			try {
				File conf = new File(SwiftsProperties.getHome(), configureFile);
				Server sc = ConfigureUtil.getConfigure(new FileInputStream(conf));
				shutdownClient = new ShutdownClient(sc.getPort(), sc.getShutdown());
				shutdownClient.shutdown();
				return;
			} catch (Exception ex) {
				System.out.println("Shutdown Spring Server error");
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 启动主程序
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		if (args == null || args.length == 0 || args.length > 2) {
			StringBuffer sb = new StringBuffer();
			sb.append("Wrong parameter!").append('\n');
			sb.append("Accepted parameter : ").append('\n');
			sb.append("[startup]  : Startup Server").append('\n');
			sb.append("[shutdown] : Shutdown Server").append('\n');
			System.out.println(sb.toString());
			System.exit(1);
		}
		Bootstrap bootstrap = new Bootstrap();
		if (args.length == 1) {
			bootstrap.entrance(args[0], null);
		} else if (args.length == 2) {
			bootstrap.entrance(args[0], args[1]);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("Wrong parameter!").append('\n');
			sb.append("Accepted parameter : ").append('\n');
			sb.append("[startup]  : Startup Server").append('\n');
			sb.append("[shutdown] : Shutdown Server").append('\n');
			System.out.println(sb.toString());
			System.exit(1);
		}
	}
}
