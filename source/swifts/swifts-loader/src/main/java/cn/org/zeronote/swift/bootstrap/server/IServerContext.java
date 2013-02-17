/**
 * 
 */
package cn.org.zeronote.swift.bootstrap.server;

/**
 * 服务器Server Context
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public interface IServerContext {

	/**
	 * 启动服务
	 */
	void startup() throws Exception;
	
	/**
	 * 停止服务
	 */
	void shutdown();
}
