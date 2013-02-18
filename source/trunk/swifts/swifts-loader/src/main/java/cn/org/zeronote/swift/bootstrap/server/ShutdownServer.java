package cn.org.zeronote.swift.bootstrap.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 用来监听服务停止
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public class ShutdownServer implements Runnable {

	private static int num = 0;
	
	private String shutdownServerName = null;
	private ServerSocket shutdownServer = null;
	private IServerContext shutdownableObj = null;
	
	private int port = 0;
	private String shutdownMsg = "shutdown";
	
	private boolean acceptable = true;

	/**
	 * 构造ShutdownServer
	 * @param shutdownServerName
	 * @param shutdownableObj
	 * @param shutdownPort
	 */
	public ShutdownServer(String shutdownServerName, IServerContext shutdownableObj, int shutdownPort, String shutdownMsg) {
		this.shutdownServerName = shutdownServerName;
		this.shutdownableObj = shutdownableObj;
		this.port = shutdownPort;
		if (shutdownMsg != null) {
			this.shutdownMsg = shutdownMsg;
		}
	}

	/**
	 * Server 启动
	 */
	public void start() {
		Thread thread = new Thread(this, "ShutdownServer-" + shutdownServerName + "-" + (++num));
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			System.out.println("Shutdown listener bind on " + port);
			shutdownServer = new ServerSocket(port);
			System.out.println(shutdownServer.getInetAddress().toString());
		} catch (BindException e) {
			// 如果端口已被占用，则退出，并提示
			System.err.println("Port Address already in use! System exit!");
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (Exception ex) {
			System.err.println("Start Server error!");
			ex.printStackTrace(System.err);
			System.exit(1);
		}
		if (shutdownServer != null) {
			Socket clientSocket = null;
			while (acceptable) {
				PrintWriter out = null;
				BufferedReader in = null;
				try {
					System.out.println("Shutdown listener is waitting client ... ");
					clientSocket = shutdownServer.accept();
					System.out.println("Shutdown listener receive client: " + clientSocket.getRemoteSocketAddress().toString());
					out = new PrintWriter(clientSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String inputLine = null;

					while ((inputLine = in.readLine()) != null) {
						System.out.println("Shutdown listener recive :" + inputLine);
						if (shutdownMsg.equals(inputLine)) {
							out.println("I am doing shutdown myself...Done.");
							System.out.println("Shutdown server begin to shutdown all services ... ");
							shutdownableObj.shutdown();
							acceptable = false;
						}
					}
				} catch (Exception ex) {
					System.err.println("unkonwn exception, please kill this pid.");
					ex.printStackTrace(System.err);
				} finally {
					try {
						out.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
					try {
						in.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}

			try {
				shutdownServer.close();
			} catch (Exception ex) {
				System.err.println("shutdown socket close error!");
				ex.printStackTrace(System.err);
			}

			System.out.println("Shutdown listener down! ");
			System.exit(0);
			System.out.println("Exit system! ");
		}
	}

}
