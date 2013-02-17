/**
 * 
 */
package cn.org.zeronote.swift.bootstrap;

import org.junit.Before;
import org.junit.Test;

import cn.org.zeronote.swift.bootstrap.Bootstrap;

/**
 * @author <a href='mailto:lizheng8318@gmail.com'>lizheng</a>
 *
 */
public class BootstrapTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link cn.org.zeronote.swift.bootstrap.Bootstrap#main(java.lang.String[])}.
	 * @throws Exception 
	 */
	@Test
	public void testMain() throws Exception {
		Bootstrap.main(new String[]{"startup"});
		Thread.sleep(1000 * 60);
	}

}
