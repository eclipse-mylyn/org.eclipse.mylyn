/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ThreadPoolExecutor;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.commons.net.TimeoutInputStream;

/**
 * @author Steffen Pingel
 */
public class TimeoutInputStreamTest extends TestCase {

	volatile int value;

	volatile IOException e;

	InputStream stream = new InputStream() {
		@Override
		public int read() throws IOException {
			if (e != null) {
				throw e;
			}
			return value;
		}
	};

	private ServerSocket server;

	@Override
	protected void setUp() throws Exception {
		assertEquals(0, ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount());
		server = new ServerSocket();
		new Thread() {
			@Override
			public void run() {
				try {
					server.accept();
				} catch (IOException e) {
					// ignore
				}
			};
		}.start();
	}

	@Override
	protected void tearDown() throws Exception {
		server.close();
	}

	public void testClose() throws Exception {
		TimeoutInputStream in = new TimeoutInputStream(stream, 1, 500, 500);
		assertEquals(0, in.read());
		value = -1;
		// clear buffer
		in.read();
		assertEquals(-1, in.read());
		in.close();
		Thread.sleep(200);
		assertEquals(0, ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount());
	}

	public void testCloseTimeout() throws Exception {
		e = new SocketTimeoutException();
		TimeoutInputStream in = new TimeoutInputStream(stream, 1, 500, 500);
		try {
			in.read();
			fail("expected InterruptedIOException");
		} catch (InterruptedIOException e) {
			// expected
		} finally {
			in.close();
		}

		// wait 30 seconds for executor to complete 
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 30 * 1000
				&& ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount() > 0) {
			Thread.sleep(500);
		}
		assertEquals(0, ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount());
	}

}
