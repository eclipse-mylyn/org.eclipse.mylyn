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

package org.eclipse.mylyn.commons.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpMethod;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 */
public interface CommonHttpMethod extends HttpMethod {

	public static final String CONTENT_ENCODING = "Content-Encoding"; //$NON-NLS-1$

	public static final String ACCEPT_ENCODING = "Accept-encoding"; //$NON-NLS-1$

	public static final String CONTENT_ENCODING_GZIP = "gzip"; //$NON-NLS-1$

	public abstract InputStream getResponseBodyAsStream(IProgressMonitor monitor) throws IOException;

	public abstract void releaseConnection(IProgressMonitor monitor);

}
