/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritLoginException;
import org.eclipse.mylyn.internal.gerrit.core.client.IOpenIdLocation;
import org.eclipse.mylyn.internal.gerrit.core.client.OpenIdAuthenticationRequest;
import org.eclipse.mylyn.internal.gerrit.core.client.OpenIdAuthenticationResponse;

/**
 * @author Steffen Pingel
 */
public class OpenIdAuthenticationTest extends TestCase {

	private class StubRepositoryLocation extends WebLocation implements IOpenIdLocation {

		String providerUrl;

		public StubRepositoryLocation(String url) {
			super(url);
		}

		@Override
		public String getProviderUrl() {
			return providerUrl;
		}

		@Override
		public OpenIdAuthenticationResponse requestAuthentication(OpenIdAuthenticationRequest request,
				IProgressMonitor monitor) throws UnsupportedRequestException {
			return new OpenIdAuthenticationResponse(null, null);
		}

	}

	private static String PROVIDER_URL = "https://www.google.com/accounts/o8/id"; //$NON-NLS-1$

	StubRepositoryLocation location = new StubRepositoryLocation(GerritFixture.current()
			.repository()
			.getRepositoryUrl());

	GerritHttpClient client = new GerritHttpClient(location);

	public void testExecuteNullOpenIdProviderNullCredentials() throws Exception {
		client.execute(createRequest(), null);
	}

	public void testExecuteOpenIdProviderNullCredentials() throws Exception {
		location.providerUrl = PROVIDER_URL;
		try {
			client.execute(createRequest(), null);
			fail("Expected GerritLoginException");
		} catch (GerritLoginException expected) {
			// ignore
		}
	}

	private Request<Object> createRequest() {
		return new Request<Object>() {
			@Override
			public HttpMethodBase createMethod() throws IOException {
				return new GetMethod();
			}

			@Override
			public Object process(HttpMethodBase method) throws IOException {
				// ignore
				return null;
			}
		};
	}

}
