/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

/**
 * GitHub constants.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IGitHubConstants {

	/**
	 * REPOS_SEGMENT
	 */
	String SEGMENT_REPOS = "/repos"; //$NON-NLS-1$

	/**
	 * SEGMENT_ISSUES
	 */
	String SEGMENT_ISSUES = "/issues"; //$NON-NLS-1$

	/**
	 * SEGMENT_COMMENTS
	 */
	String SEGMENT_COMMENTS = "/comments"; //$NON-NLS-1$

	/**
	 * SUFFIX_JSON
	 */
	String SUFFIX_JSON = ".json"; //$NON-NLS-1$

	/**
	 * HOST_API
	 */
	String HOST_API = "api.github.com"; //$NON-NLS-1$

	/**
	 * PROTOCOL_HTTPS
	 */
	String PROTOCOL_HTTPS = "https"; //$NON-NLS-1$

	/**
	 * DATE_FORMAT
	 */
	String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$

}
