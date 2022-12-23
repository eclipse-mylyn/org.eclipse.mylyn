/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.core;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * @author Steffen Pingel
 */
class CancellableInputStream extends FilterInputStream {

	private volatile boolean cancelled;

	private final CommonHttpResponse response;

	public CancellableInputStream(CommonHttpResponse response, InputStream in) {
		super(in);
		Assert.isNotNull(response);
		this.response = response;
	}

	@Override
	public int available() throws IOException {
		checkCancelled();
		try {
			return super.available();
		} catch (IOException e) {
			checkCancelled();
			throw e;
		}
	}

	@Override
	public void close() throws IOException {
		response.notifyStreamClosed();
		super.close();
	}

	void closeWithoutNotification() throws IOException {
		super.close();
	}

	@Override
	public int read() throws IOException {
		checkCancelled();
		try {
			return super.read();
		} catch (IOException e) {
			checkCancelled();
			throw e;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkCancelled();
		try {
			return super.read(b, off, len);
		} catch (IOException e) {
			checkCancelled();
			throw e;
		}
	}

	private void checkCancelled() {
		if (cancelled) {
			throw new OperationCanceledException();
		}
	}

	void cancel() {
		cancelled = true;
	}

}
