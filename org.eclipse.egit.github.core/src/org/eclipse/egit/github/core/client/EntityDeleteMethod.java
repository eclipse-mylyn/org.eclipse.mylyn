/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.client;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * DELETE request that supports an enclosed entity
 */
public class EntityDeleteMethod extends HttpEntityEnclosingRequestBase {

	/**
	 * Create DELETE
	 */
	public EntityDeleteMethod() {
		super();
	}

	/**
	 * Create DELETE
	 *
	 * @param uri
	 */
	public EntityDeleteMethod(final URI uri) {
		super();
		setURI(uri);
	}

	/**
	 * Create DELETE
	 *
	 * @param uri
	 */
	public EntityDeleteMethod(final String uri) {
		super();
		setURI(URI.create(uri));
	}

	public String getMethod() {
		return HttpDelete.METHOD_NAME;
	}
}
