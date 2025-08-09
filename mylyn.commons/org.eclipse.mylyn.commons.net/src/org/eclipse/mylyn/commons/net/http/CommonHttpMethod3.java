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

	String CONTENT_ENCODING = "Content-Encoding"; //$NON-NLS-1$

	String ACCEPT_ENCODING = "Accept-encoding"; //$NON-NLS-1$

	String CONTENT_ENCODING_GZIP = "gzip"; //$NON-NLS-1$

	String getResponseCharSet();

	InputStream getResponseBodyAsStream(IProgressMonitor monitor) throws IOException;

	void releaseConnection(IProgressMonitor monitor);

}
