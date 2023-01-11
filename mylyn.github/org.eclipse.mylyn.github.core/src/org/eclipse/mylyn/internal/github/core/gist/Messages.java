/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.gist;

import org.eclipse.osgi.util.NLS;

/**
 * NLS for Mylyn GitHub Core
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.core.gist.messages"; //$NON-NLS-1$

	/** */
	public static String GistAttribute_LabelAuthor;

	/** */
	public static String GistAttribute_LabelAuthorGravatar;

	/** */
	public static String GistAttribute_LabelCloneUrl;

	/** */
	public static String GistAttribute_LabelCreated;

	/** */
	public static String GistAttribute_LabelDescription;

	/** */
	public static String GistAttribute_LabelFileUrl;

	/** */
	public static String GistAttribute_LabelKey;

	/** */
	public static String GistAttribute_LabelModified;

	/** */
	public static String GistAttribute_LabelNewComment;

	/** */
	public static String GistAttribute_LabelSummary;

	/** */
	public static String GistAttribute_LabelUrl;

	/** */
	public static String GistConnector_LabelConnector;

	/** */
	public static String GistTaskDataHandler_FilesMultiple;

	/** */
	public static String GistTaskDataHandler_FilesSingle;

	/** */
	public static String GistTaskDataHandler_SizeByte;

	/** */
	public static String GistTaskDataHandler_SizeBytes;

	/** */
	public static String GistTaskDataHandler_SizeGigabytes;

	/** */
	public static String GistTaskDataHandler_SizeKilobytes;

	/** */
	public static String GistTaskDataHandler_SizeMegabytes;

	/** */
	public static String GistTaskDataHandler_SummaryNewGist;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
