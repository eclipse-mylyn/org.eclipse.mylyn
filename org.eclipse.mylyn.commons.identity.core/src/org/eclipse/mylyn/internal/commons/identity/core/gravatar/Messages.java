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

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.commons.identity.gravatar.messages"; //$NON-NLS-1$

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
