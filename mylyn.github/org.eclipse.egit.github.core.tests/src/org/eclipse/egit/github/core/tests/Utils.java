/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
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
