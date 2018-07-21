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

import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

/**
 * @author Steffen Pingel
 */
public abstract class HttpRequestProcessor<T> {

	public static final HttpRequestProcessor<CommonHttpResponse> DEFAULT = new HttpRequestProcessor<CommonHttpResponse>() {
		@Override
		protected CommonHttpResponse doProcess(CommonHttpResponse response, IOperationMonitor monitor)
				throws IOException {
			return response;
		}
	};

	private final boolean autoRelease;

	public HttpRequestProcessor() {
		this(true);
	}

	public HttpRequestProcessor(boolean autoRelease) {
		this.autoRelease = autoRelease;
	}

	public boolean autoRelease() {
		return autoRelease;
	}

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
		return null;
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor) throws IOException {
	}

}
