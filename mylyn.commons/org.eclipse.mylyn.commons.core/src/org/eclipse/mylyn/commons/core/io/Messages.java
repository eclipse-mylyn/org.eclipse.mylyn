/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * IBM - Initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.io;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.commons.net.messages";//$NON-NLS-1$

	public static String PollingInputStream_readTimeout;

	public static String PollingInputStream_closeTimeout;

	public static String PollingOutputStream_writeTimeout;

	public static String PollingOutputStream_closeTimeout;

	public static String TimeoutOutputStream_cannotWriteToStream;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
