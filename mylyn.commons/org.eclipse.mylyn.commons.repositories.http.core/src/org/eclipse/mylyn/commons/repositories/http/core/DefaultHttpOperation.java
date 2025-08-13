/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

import java.io.IOException;

import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

/**
 * @author Steffen Pingel
 */
class DefaultHttpOperation<T> extends CommonHttpOperation<T> {

	private final HttpRequestProcessor<T> processor;

	private final HttpRequestBase request;

	public DefaultHttpOperation(CommonHttpClient client, HttpRequestBase request, HttpRequestProcessor<T> processor) {
		super(client);
		Assert.isNotNull(processor);
		this.request = request;
		this.processor = processor;
	}

	public T run(IOperationMonitor monitor) throws IOException {
		CommonHttpResponse response = execute(request, monitor);
		return processor.autoRelease() ? processAndRelease(response, monitor) : process(response, monitor);
	}

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
		return processor.doProcess(response, monitor);
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
		processor.doValidate(response, monitor);
	}

	protected T process(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} catch (IOException | RuntimeException e) {
			response.release();
			throw e;
		}
	}

	protected T processAndRelease(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} finally {
			response.release();
		}
	}

}
