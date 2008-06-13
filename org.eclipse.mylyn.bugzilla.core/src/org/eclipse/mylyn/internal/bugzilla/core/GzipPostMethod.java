/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Use <code>GzipPostMethod</code> instead of {@link PostMethod} to make Mylyn well-behaved when accessing repositories
 * that can supply gzipped responses.<br /> <br>
 * References:
 * <ul>
 * <li><a href="http://www.schroepl.net/projekte/mod_gzip/index.htm">Gzip home</a></li>
 * <li><a href="http://www.oreilly.com/catalog/9780596529307/chapter/ch04.pdf">Gzip site comparison</a></li>
 * </ul>
 * 
 * @see GzipGetMethod, PostMethod
 * 
 * @author Maarten Meijer
 */
public class GzipPostMethod extends PostMethod {
	private final boolean gzipWanted;

	/**
	 * @param requestPath
	 *            the URI to request
	 * @param gzipWanted
	 *            is compression desired (for debugging or optionalizing)
	 */
	public GzipPostMethod(String requestPath, boolean gzipWanted) {
		super(requestPath);
		this.gzipWanted = gzipWanted;
	}

	@Override
	public int execute(HttpState state, HttpConnection conn) throws HttpException, IOException {
		// Insert accept-encoding header
		if (gzipWanted) {
			this.setRequestHeader("Accept-encoding", IBugzillaConstants.CONTENT_ENCODING_GZIP);
		}
		int result = super.execute(state, conn);
		return result;
	}

	/**
	 * getResponseBodyNoop is meant for clearing the response body in case of error. The result is never used so no need
	 * to unzip it first.
	 * 
	 * @throws IOException
	 */
	public void getResponseBodyNoop() throws IOException {
		// result is ignored
		super.getResponseBody();
	}
}
