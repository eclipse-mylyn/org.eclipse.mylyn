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

package org.eclipse.mylyn.commons.repositories.http.tests;


import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.core.operations.CancellableOperationMonitorThread;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestUrl;
import org.eclipse.mylyn.commons.sdk.util.junit5.EnabledIfCI;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@EnabledIfCI
public class CommonHttpResponseTest {

	private final TestUrl urls = TestUrl.DEFAULT;

	private NullOperationMonitor monitor;

	private CommonHttpResponse response;

	private final CancellableOperationMonitorThread monitorThread = new CancellableOperationMonitorThread();

	@BeforeAll
	public static void setUpClass() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		monitor = new NullOperationMonitor();
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(urls.getHttpsOk().toString());

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse clientResponse = client.execute(request, monitor);
		response = new CommonHttpResponse(request, clientResponse, monitorThread, monitor);
	}

	@AfterEach
	public void tearDown() {
		if (response != null) {
			response.release();
		}
	}

	@Test
	public void testCancel() throws Exception {
		monitor.setCanceled(true);
		InputStream in = response.getResponseEntityAsStream();
		monitorThread.processOperations();
		assertThrows(OperationCanceledException.class, () -> in.read());
	}

	@Test
	public void testCancelAfterRead() throws Exception {
		InputStream in = response.getResponseEntityAsStream();
		in.read();
		monitor.setCanceled(true);
		monitorThread.processOperations();
		assertThrows(OperationCanceledException.class, () -> in.read());
	}

}
