/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import org.eclipse.osgi.util.NLS;

/**
 * Commons message constants.
 *
 * @author Steffen Pingel
 * @since 3.7
 */
public class CommonMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.commons.core.messages"; //$NON-NLS-1$

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
