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
package org.eclipse.mylyn.internal.github.core;

import org.eclipse.osgi.util.NLS;

/**
 * NLS for Mylyn GitHub Core
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.core.messages"; //$NON-NLS-1$

	/** */
	public static String FieldError_InvalidField;

	/** */
	public static String FieldError_InvalidFieldWithValue;

	/** */
	public static String FieldError_MissingField;

	/** */
	public static String FieldError_ResourceError;

	/** */
	public static String FieldError_AlreadyExists;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
