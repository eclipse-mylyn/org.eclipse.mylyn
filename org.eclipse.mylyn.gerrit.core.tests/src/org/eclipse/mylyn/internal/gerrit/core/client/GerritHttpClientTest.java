/*******************************************************************************
 * Copyright (c) 2011 Christian Trutz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Trutz - initial API and implementation
 *     Tasktop Technologies - ongoing maintenance
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.JsonEntity;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request.HttpMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.reflect.TypeToken;

/**
 * Unit tests for {@link GerritHttpClient}.
 * 
 * @author Christian Trutz
 */
@RunWith(MockitoJUnitRunner.class)
public class GerritHttpClientTest {

	@Mock
	AbstractWebLocation abstractWebLocation;

	@Mock
	IProgressMonitor progressMonitor;

	/**
	 * Test {@link GerritHttpClient} constructor with {@code null} argument.
	 */
	@Test(expected = AssertionFailedException.class)
	public void constructorNull() {
		new GerritHttpClient((AbstractWebLocation) null);
	}

	/**
	 * Test {@link GerritHttpClient#postJsonRequest(String, JsonEntity, IProgressMonitor)} with {@code null} service URI
	 * argument.
	 */
	@Test(expected = AssertionFailedException.class)
	public void postJsonRequestNullServiceUri() throws IOException, GerritException {
		GerritHttpClient gerritHttpClient = new GerritHttpClient(abstractWebLocation);
		gerritHttpClient.postJsonRequest(null, new JsonEntity() {
			@Override
			public String getContent() {
				return "[]"; //$NON-NLS-1$
			}
		}, progressMonitor);
	}

	/**
	 * Test {@link GerritHttpClient#postJsonRequest(String, JsonEntity, IProgressMonitor)} with {@code null}
	 * {@link JsonEntity} argument.
	 */
	@Test(expected = AssertionFailedException.class)
	public void postJsonRequestNullJsonEntity() throws IOException, GerritException {
		GerritHttpClient gerritHttpClient = new GerritHttpClient(abstractWebLocation);
		gerritHttpClient.postJsonRequest("not null", null, progressMonitor); //$NON-NLS-1$
	}

	@Test
	public void restRequestCanReturnBinaryContent() throws IOException {
		// given
		final TypeToken<Byte[]> byteArrayType = new TypeToken<Byte[]>() {
		};
		Request<byte[]> request = new GerritHttpClient(abstractWebLocation).new RestRequest<byte[]>(HttpMethod.GET,
				"serviceUri", null /*input*/, byteArrayType.getType(), null /*error handler*/); //$NON-NLS-1$
		HttpMethodBase httpMethodBase = mock(HttpMethodBase.class);
		byte[] binary = "binary".getBytes(); //$NON-NLS-1$
		when(httpMethodBase.getResponseBody()).thenReturn(binary);

		// when
		byte[] result = request.process(httpMethodBase);

		// then
		assertArrayEquals(binary, result);
	}

}
