/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpRequestProcessor;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class CommonHttpClientTest {

	@Test
	public void testExecuteGet() throws IOException {
		RepositoryLocation location = new RepositoryLocation("http://mylyn.org");
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
	public void testGetRequest() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://mylyn.org/");

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
	public void testHttpAuthenticationTypeHttp() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://mylyn.org/");
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
	public void testHttpAuthenticationTypeRepository() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://mylyn.org/");
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

	@Test(expected = SSLException.class)
	public void testCertificateAuthenticationNoCertificate() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("https://mylyn.org/secure/index.txt");

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse response = client.execute(request, null);
		HttpUtil.release(request, response, null);
	}

	@Test
	public void testCertificateAuthenticationCertificate() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("https://mylyn.org/secure/index.txt");
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

	@Test(expected = SSLException.class)
	public void testCertificateAuthenticationCertificateReset() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("https://mylyn.org/secure/index.txt");
		location.setCredentials(AuthenticationType.CERTIFICATE, CommonTestUtil.getCertificateCredentials());

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse response = client.execute(request, null);
		try {
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		} finally {
			HttpUtil.release(request, response, null);
		}

		location.setCredentials(AuthenticationType.CERTIFICATE, null);
		// the request should now fail
		request = new HttpGet(location.getUrl());
		response = client.execute(request, null);
		HttpUtil.release(request, response, null);
	}

}
