/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.commons.identity.core.gravatar.messages"; //$NON-NLS-1$

	/**
	 * GravatarStore_LoadingAvatar
	 */
	public static String GravatarStore_LoadingAvatar;

	/**
	 * GravatarStore_RefreshJobName
	 */
	public static String GravatarStore_RefreshJobName;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
