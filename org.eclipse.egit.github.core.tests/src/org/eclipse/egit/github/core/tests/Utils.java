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
package org.eclipse.egit.github.core.tests;

import static org.eclipse.egit.github.core.client.IGitHubConstants.PARAM_PAGE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.PARAM_PER_PAGE;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

/**
 * Test case utilities
 */
public abstract class Utils {

	/**
	 * Add default page query parameters to given URI
	 *
	 * @param uri
	 * @return URI with default page query params
	 */
	public static String page(String uri) {
		String separator = uri.indexOf('?') == -1 ? "?" : "&";
		return uri + separator + PARAM_PER_PAGE + "=" + PAGE_SIZE + "&"
				+ PARAM_PAGE + "=" + PAGE_FIRST;
	}
}
