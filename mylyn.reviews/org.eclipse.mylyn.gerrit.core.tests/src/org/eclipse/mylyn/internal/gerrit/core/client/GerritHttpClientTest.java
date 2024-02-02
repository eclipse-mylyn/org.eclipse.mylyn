/*******************************************************************************
 * Copyright (c) 2011, 2022 Christian Trutz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Christian Trutz - initial API and implementation
 *     Tasktop Technologies - ongoing maintenance
 *     ArSysOp - adapt to SimRel 2022-12
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.JsonEntity;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request.HttpMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.reflect.TypeToken;

/**
 * Unit tests for {@link GerritHttpClient}.
 *
 * @author Christian Trutz
 */
@RunWith(MockitoJUnitRunner.class)
public class GerritHttpClientTest {
	public class TestGerritHttpClient extends GerritHttpClient {
		private final int code;

		private TestGerritHttpClient(AbstractWebLocation location, int code) {
			super(location, GerritCapabilities.MAXIMUM_SUPPORTED_VERSION);
			this.code = code;
		}

		@Override
		public int execute(org.apache.commons.httpclient.HttpMethod method, IProgressMonitor monitor)
				throws IOException {
			return code;
		}

		@Override
		void requestCredentials(IProgressMonitor monitor, AuthenticationType authenticationType)
				throws GerritLoginException {
		}

		@Override
		String getUrl() {
			return "http://mock";
		}
	}

	@Mock
	AbstractWebLocation abstractWebLocation;

	@Mock
	IProgressMonitor progressMonitor;

	/**
	 * Test {@link GerritHttpClient} constructor with {@code null} argument.
	 */
	@Test(expected = AssertionFailedException.class)
	public void constructorNull() {
		new GerritHttpClient((AbstractWebLocation) null, GerritCapabilities.MAXIMUM_SUPPORTED_VERSION);
	}

	/**
	 * Test {@link GerritHttpClient#postJsonRequest(String, JsonEntity, IProgressMonitor)} with {@code null} service URI argument.
	 */
	@Test(expected = AssertionFailedException.class)
	public void postJsonRequestNullServiceUri() throws IOException, GerritException {
		GerritHttpClient gerritHttpClient = new GerritHttpClient(abstractWebLocation,
				GerritCapabilities.MAXIMUM_SUPPORTED_VERSION);
		gerritHttpClient.postJsonRequest(null, new JsonEntity() {
			@Override
			public String getContent() {
				return "[]"; //$NON-NLS-1$
			}
		}, progressMonitor);
	}

	/**
	 * Test {@link GerritHttpClient#postJsonRequest(String, JsonEntity, IProgressMonitor)} with {@code null} {@link JsonEntity} argument.
	 */
	@Test(expected = AssertionFailedException.class)
	public void postJsonRequestNullJsonEntity() throws IOException, GerritException {
		GerritHttpClient gerritHttpClient = new GerritHttpClient(abstractWebLocation,
				GerritCapabilities.MAXIMUM_SUPPORTED_VERSION);
		gerritHttpClient.postJsonRequest("not null", null, progressMonitor); //$NON-NLS-1$
	}

	@Test
	public void restRequestCanReturnBinaryContent() throws IOException {
		// given
		final TypeToken<Byte[]> byteArrayType = new TypeToken<>() {
		};
		Request<byte[]> request = new GerritHttpClient(abstractWebLocation,
				GerritCapabilities.MAXIMUM_SUPPORTED_VERSION).new RestRequest<>(HttpMethod.GET, "serviceUri", //$NON-NLS-1$
						null /*input*/, byteArrayType.getType(), null /*error handler*/);
		HttpMethodBase httpMethodBase = mock(HttpMethodBase.class);
		byte[] binary = "binary".getBytes(); //$NON-NLS-1$
		when(httpMethodBase.getResponseBody()).thenReturn(binary);

		// when
		byte[] result = request.process(httpMethodBase);

		// then
		assertArrayEquals(binary, result);
	}

	@Test
	public void authenticateForm() throws IOException, GerritException {
		GerritHttpClient client = spy(new TestGerritHttpClient(abstractWebLocation, HttpStatus.SC_BAD_REQUEST));
		int result = client.authenticateForm(new AuthenticationCredentials("", ""), new NullProgressMonitor());
		assertEquals(HttpStatus.SC_NOT_FOUND, result);
		InOrder inOrder = inOrder(client);
		inOrder.verify(client).execute(isA(PostMethod.class), any(IProgressMonitor.class));
		inOrder.verify(client).execute(isA(GetMethod.class), any(IProgressMonitor.class));

		client = spy(new TestGerritHttpClient(abstractWebLocation, HttpStatus.SC_METHOD_NOT_ALLOWED));
		result = client.authenticateForm(new AuthenticationCredentials("", ""), new NullProgressMonitor());
		assertEquals(HttpStatus.SC_NOT_FOUND, result);
		inOrder = inOrder(client);
		inOrder.verify(client).execute(isA(PostMethod.class), any(IProgressMonitor.class));
		inOrder.verify(client).execute(isA(GetMethod.class), any(IProgressMonitor.class));

		client = spy(new TestGerritHttpClient(abstractWebLocation, HttpStatus.SC_UNAUTHORIZED));
		result = client.authenticateForm(new AuthenticationCredentials("", ""), new NullProgressMonitor());
		assertEquals(-1, result);
		verify(client).execute(isA(PostMethod.class), any(IProgressMonitor.class));
		verify(client, never()).execute(isA(GetMethod.class), any(IProgressMonitor.class));
	}
}
