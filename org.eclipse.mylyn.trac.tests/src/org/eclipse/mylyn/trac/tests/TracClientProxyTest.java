package org.eclipse.mylyn.trac.tests;

import java.net.Proxy;

import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.web.core.WebClientUtil;

public class TracClientProxyTest extends AbstractTracClientTest {

	private TestProxy testProxy;
	private Proxy proxy;
	private int proxyPort;
	
	public TracClientProxyTest() {
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testProxy = new TestProxy();
		proxyPort = testProxy.startAndWait();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		testProxy.stop();
	}
	
	public void testConnectProxyWeb() throws Exception {
		version = Version.TRAC_0_9;
		connectProxy(Constants.TEST_TRAC_010_URL, "GET");
	}

	public void testConnectProxyXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connectProxy(Constants.TEST_TRAC_010_URL, "POST");
	}

	public void testConnectProxySslWeb() throws Exception {
		version = Version.TRAC_0_9;
		connectProxy(Constants.TEST_TRAC_010_SSL_URL, "CONNECT");
	}

	public void testConnectProxySslXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connectProxy(Constants.TEST_TRAC_010_SSL_URL, "CONNECT");
	}

	private void connectProxy(String url, String expectedMethod) throws Exception {
		testProxy.setResponse(TestProxy.NOT_FOUND);
		proxy = WebClientUtil.getProxy("localhost", proxyPort + "", "", "");
		ITracClient client = connect(url, proxy);
		try {
			client.validate();
		} catch (TracException e) {
		}

		assertEquals(expectedMethod, testProxy.getRequest().getMethod());
	}
	
}
