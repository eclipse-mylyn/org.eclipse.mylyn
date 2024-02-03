/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.net.http.CommonHttpMethod3;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest.StubProgressMonitor;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.commons.net.http.CommonGetMethod3;
import org.eclipse.mylyn.internal.commons.net.http.CommonPostMethod3;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class CommonHttpMethod3Test extends TestCase {

	public void testGetOpenStreamAndCancel() throws Exception {
		CommonGetMethod3 method = new CommonGetMethod3("/");
		openStreamAndCancel(method);
	}

	public void testPostOpenStreamAndCancel() throws Exception {
		CommonPostMethod3 method = new CommonPostMethod3("/");
		openStreamAndCancel(method);
	}

	void openStreamAndCancel(CommonHttpMethod3 method) throws Exception {
		StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		String url = "http://mylyn.org/";
		WebLocation location = new WebLocation(url);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, monitor);

		InputStream in;
		try {
			WebUtil.execute(client, hostConfiguration, method, monitor);
			in = method.getResponseBodyAsStream(monitor);
		} finally {
			monitor.setCanceled(true);
			method.releaseConnection(monitor);
		}
		assertNotNull(in);
		Thread.sleep(500); // wait for executor to release
		assertEquals(0, ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount());
		try {
			in.read();
			fail("Expected channel to be closed");
		} catch (IOException e) {
			// expected
		}
	}
}
