/*******************************************************************************
 * Copyright (c) 2004, 2010 Maarten Meijer and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Maarten Meijer - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Use <code>GzipGetMethod</code> instead of {@link GetMethod} to make Mylyn well-behaved when accessing repositories
 * that can supply gzipped responses.<br />
 * <br>
 * References:
 * <ul>
 * <li><a href="http://www.schroepl.net/projekte/mod_gzip/index.htm">Gzip home</a></li>
 * <li><a href="http://www.oreilly.com/catalog/9780596529307/chapter/ch04.pdf">Gzip site comparison</a></li>
 * </ul>
 * 
 * @see GzipPostMethod, GetMethod
 * @author Maarten Meijer
 */
public class GzipGetMethod extends GetMethod {
	private final boolean gzipWanted;

	/**
	 * @param requestPath
	 *            the URI to request
	 * @param gzipWanted
	 *            is compression desired (for debugging or optionalizing)
	 */
	public GzipGetMethod(String requestPath, boolean gzipWanted) {
		super(requestPath);
		this.gzipWanted = gzipWanted;
	}

	@Override
	public int execute(HttpState state, HttpConnection conn) throws HttpException, IOException {
		// Insert accept-encoding header
		if (gzipWanted) {
			this.setRequestHeader("Accept-encoding", IBugzillaConstants.CONTENT_ENCODING_GZIP); //$NON-NLS-1$
		}
		int result = super.execute(state, conn);
		return result;
	}

	/**
	 * getResponseBodyNoop is meant for clearing the response body in case of error. The result is never used so no need
	 * to unzip it first.
	 * 
	 * @throws IOException
	 * @deprecated this is handled in {@link org.apache.commons.httpclient.HttpClient} connection release
	 */

	@Deprecated
	public void getResponseBodyNoop() throws IOException {
		InputStream instream;
		try {
			instream = getResponseBodyAsStream();
			if (instream != null) {
				byte[] buffer = new byte[4096];
				while (instream.read(buffer) > 0) {
				}
			}
		} catch (IOException e) {
			// ignore
		}
	}

}
