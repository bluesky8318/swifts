package cn.org.zeronote.swift.bootstrap.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public class ShutdownClient {

	private int shutdownPort = 0;
	private String shutdownMsg = "shutdown";

	public ShutdownClient(int shutdownPort, String shutdownMsg) {
		this.shutdownPort = shutdownPort;
		this.shutdownMsg = shutdownMsg;
	}

	public void shutdown() {
		// send shutdown message
		Socket kkSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			System.out.println("access shutdown port " + shutdownPort);
			kkSocket = new Socket("localhost", shutdownPort);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
		} catch (UnknownHostException ex) {
			System.err.println("unkonwn host");
			ex.printStackTrace();
			System.exit(1);
		} catch (IOException ex) {
			System.err.println("access error");
			ex.printStackTrace(System.err);
			System.exit(1);
		}

		String fromServer;
		try {
			out.println(shutdownMsg);
			if ((fromServer = in.readLine()) != null) {
				System.out.println("From server: " + fromServer);
			}
		} catch (Exception ex) {
			System.err.println("Send Message Error!");
			ex.printStackTrace(System.err);
		}

		try {
			out.close();
		} catch (Exception ex) {
			System.err.println("access error");
			ex.printStackTrace(System.err);
		}
		try {
			in.close();
		} catch (Exception ex) {
			System.err.println("access error");
			ex.printStackTrace(System.err);
		}
		try {
			kkSocket.close();
		} catch (Exception ex) {
			System.err.println("access error");
			ex.printStackTrace(System.err);
		}

	}

}
