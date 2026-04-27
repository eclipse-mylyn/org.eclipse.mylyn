/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.tests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.eclipse.mylyn.commons.core.net.SslSupport;
import org.eclipse.mylyn.commons.core.net.TrustAllTrustManager;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpRequestProcessor;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestUrl;
import org.eclipse.mylyn.commons.sdk.util.junit5.EnabledIfCI;
import org.eclipse.mylyn.internal.commons.repositories.http.core.PollingSslProtocolSocketFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings({ "nls" })
public class CommonHttpClientTest {

	private static final String HOST_URL = TestUrl.DEFAULT.getHttpsOk().toString(); //"https://mylyn.org/secure/index.txt";

	private static final String MYLYN_URL = HOST_URL + "mylyn_idx";

	private static final String SERVICE_URL = MYLYN_URL + "/service";

	@BeforeAll
	public static void setUpClass() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
	}

	@Test
	@EnabledIfCI
	@Disabled("Requires authentication that is not available in CI environment") // FIXME
	public void testCertificateAuthenticationCertificate() throws Exception {
		assumeFalse(CommonTestUtil.isCertificateAuthBroken() || CommonTestUtil.isBehindProxy(), "incompatible JVM"); // TODO is this still an issue with Java 21+?
		assumeTrue(CommonTestUtil.hasCertificateCredentials(), "missing credentials");

		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(SERVICE_URL);
		location.setCredentials(AuthenticationType.CERTIFICATE, CommonTestUtil.getCertificateCredentials());

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse response = client.execute(request, null);
		try {
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
			assertEquals("secret\n", EntityUtils.toString(response.getEntity()));
		} finally {
			HttpUtil.release(request, response, null);
		}
	}

	@Test
	@EnabledIfCI
	public void testCertificateAuthenticationCertificateReset() throws Exception {
		assumeFalse(CommonTestUtil.isCertificateAuthBroken() || CommonTestUtil.isBehindProxy(), "incompatible JVM"); // TODO is this still an issue with Java 21+?
		assumeTrue(CommonTestUtil.hasCertificateCredentials(), "missing credentials");

		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(SERVICE_URL);
		location.setCredentials(AuthenticationType.CERTIFICATE, CommonTestUtil.getCertificateCredentials());

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		// work-around for bug 369805
		Scheme oldScheme = setUpDefaultFactory(client);
		try {
			try {
				HttpResponse response = client.execute(request, null);
				try {
					assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
				} finally {
					HttpUtil.release(request, response, null);
				}
			} catch (SSLException e) {
				throw new IllegalStateException("Unexpected exception", e);
			}

			location.setCredentials(AuthenticationType.CERTIFICATE, null);
			// the request should now fail
			request = new HttpGet(location.getUrl());
			HttpResponse response = client.execute(request, null);
			HttpUtil.release(request, response, null);
		} finally {
			tearDownDefaultFactory(client, oldScheme);
		}
	}

	@Test
	@EnabledIfCI
	public void testCertificateAuthenticationNoCertificate() throws Exception {
		assumeTrue(CommonTestUtil.isHttpsProxyBroken(), "broken https proxy");

		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(SERVICE_URL);

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		// work-around for bug 369805
		Scheme oldScheme = setUpDefaultFactory(client);
		try {
			HttpResponse response = client.execute(request, null);
			HttpUtil.release(request, response, null);
		} finally {
			tearDownDefaultFactory(client, oldScheme);
		}
	}

	@Test
	@EnabledIfCI
	public void testExecuteGet() throws IOException {
		RepositoryLocation location = new RepositoryLocation(MYLYN_URL);
		CommonHttpClient client = new CommonHttpClient(location);
		Integer result = client.executeGet("/", null, new HttpRequestProcessor<Integer>() {
			@Override
			protected Integer doProcess(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
				return response.getStatusCode();
			}
		});
		assertEquals(HttpStatus.SC_OK, result.intValue());
	}

	@Test
	@EnabledIfCI
	public void testGetRequest() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(SERVICE_URL);

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse response = client.execute(request, null);
		try {
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		} finally {
			HttpUtil.release(request, response, null);
		}
	}

	@Test
	@EnabledIfCI
	@Disabled("Requires authentication that is not available in CI environment") // FIXME
	public void testHttpAuthenticationTypeHttp() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(TestUrl.DEFAULT.getHttpsOk().toString());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("username", "password"));

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse response = client.execute(request, null);
		try {
			AuthScope authScope = new AuthScope("mylyn.org", 80, AuthScope.ANY_REALM);
			Credentials httpCredentials = client.getHttpClient().getCredentialsProvider().getCredentials(authScope);
			assertEquals(new UsernamePasswordCredentials("username", "password"), httpCredentials);
		} finally {
			HttpUtil.release(request, response, null);
		}
	}

	@Test
	@EnabledIfCI
	@Disabled("Requires authentication that is not available in CI environment") // FIXME
	public void testHttpAuthenticationTypeRepository() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(TestUrl.DEFAULT.getHttpsOk().toString());
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("username", "password"));

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		AuthScope authScope = new AuthScope("mylyn.org", 80, AuthScope.ANY_REALM);

		// credentials should be ignored
		HttpResponse response = client.execute(request, null);
		try {
			Credentials httpCredentials = client.getHttpClient().getCredentialsProvider().getCredentials(authScope);
			assertEquals(null, httpCredentials);
		} finally {
			HttpUtil.release(request, response, null);
		}

		client.setHttpAuthenticationType(AuthenticationType.REPOSITORY);
		// credentials should now be used
		response = client.execute(request, null);
		try {
			Credentials httpCredentials = client.getHttpClient().getCredentialsProvider().getCredentials(authScope);
			assertEquals(new UsernamePasswordCredentials("username", "password"), httpCredentials);
		} finally {
			HttpUtil.release(request, response, null);
		}
	}

	@Test
	@EnabledIfCI
	public void testHttpContextPerThread() throws Exception {
		RepositoryLocation location = new RepositoryLocation(HOST_URL);
		final CommonHttpClient client = new CommonHttpClient(location);
		final AtomicReference<HttpContext> otherThreadContext = new AtomicReference<>();
		Thread t = new Thread() {
			@Override
			public void run() {
				otherThreadContext.set(client.getContext());
			}
		};
		t.start();
		t.join();
		assertNotNull(otherThreadContext.get());
		assertNotNull(client.getContext());
		assertFalse(otherThreadContext.get() == client.getContext());
	}

	private static Scheme setUpDefaultFactory(CommonHttpClient client) {
		PollingSslProtocolSocketFactory factory = new PollingSslProtocolSocketFactory(
				new SslSupport(new TrustManager[] { new TrustAllTrustManager() }, null, null, null));
		Scheme oldScheme = client.getHttpClient()
				.getConnectionManager()
				.getSchemeRegistry()
				.register(new Scheme("https", 443, factory)); //$NON-NLS-1$
		return oldScheme;
	}

	private void tearDownDefaultFactory(CommonHttpClient client, Scheme oldScheme) {
		client.getHttpClient().getConnectionManager().getSchemeRegistry().register(oldScheme);
	}

}
