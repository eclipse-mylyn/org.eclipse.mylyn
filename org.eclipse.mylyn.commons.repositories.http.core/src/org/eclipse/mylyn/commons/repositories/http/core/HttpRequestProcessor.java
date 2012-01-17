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
