/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpMethod;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public interface CommonHttpMethod3 extends HttpMethod {

	public static final String CONTENT_ENCODING = "Content-Encoding"; //$NON-NLS-1$

	public static final String ACCEPT_ENCODING = "Accept-encoding"; //$NON-NLS-1$

	public static final String CONTENT_ENCODING_GZIP = "gzip"; //$NON-NLS-1$

	public abstract String getResponseCharSet();

	public abstract InputStream getResponseBodyAsStream(IProgressMonitor monitor) throws IOException;

	public abstract void releaseConnection(IProgressMonitor monitor);

}
