package org.eclipse.mylar.trac.tests;

import java.net.Proxy;

import org.eclipse.mylar.core.net.WebClientUtil;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;

public class TracWebClientProxyTest extends AbstractTracClientTest {

	private TestProxy testProxy;
	private Proxy proxy;
	private int proxyPort;
	
	public TracWebClientProxyTest() {
		super(Version.TRAC_0_9);
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
	
	public void testConnectProxy() throws Exception {
		testProxy.setResponse("404");
		proxy = WebClientUtil.getProxy("localhost", proxyPort + "", "", "");
		ITracClient client = connect(Constants.TEST_TRAC_010_URL, proxy);
		client.validate();
	}
	
}
