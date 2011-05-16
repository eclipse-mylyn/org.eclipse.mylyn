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

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
