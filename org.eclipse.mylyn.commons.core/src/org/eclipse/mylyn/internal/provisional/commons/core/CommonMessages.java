/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.core;

import org.eclipse.osgi.util.NLS;

public class CommonMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.provisional.commons.core"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, CommonMessages.class);
	}

	public static String Friday;

	public static String Monday;

	public static String Saturday;

	public static String Sunday;

	public static String Thursday;

	public static String Tuesday;

	public static String Wednesday;

}
