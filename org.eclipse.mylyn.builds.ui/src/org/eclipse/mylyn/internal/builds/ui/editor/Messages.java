/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.editor.messages"; //$NON-NLS-1$

	public static String BuildEditor_Build;

	public static String BuildEditor_Build_X;

	public static String BuildEditor_Details;

	public static String BuildEditor_Open_with_Web_Browser;

	public static String BuildEditor_X_Failed_Retrieve_Build_Information;

	public static String BuildEditor_X_Retrieving_Build;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
