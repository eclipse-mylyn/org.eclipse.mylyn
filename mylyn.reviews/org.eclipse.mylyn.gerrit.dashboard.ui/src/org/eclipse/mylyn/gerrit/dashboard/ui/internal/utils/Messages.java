/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 *
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Jacques Bouthillier - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.messages"; //$NON-NLS-1$

	public static String SelectionDialog_question;

	public static String SelectionDialog_selectTitle;

	public static String UIUtils_dashboardInfo;

	public static String UIUtils_dashboardInformation;

	public static String UIUtils_methodNotReady;

	public static String UIUtils_notImplemented;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
