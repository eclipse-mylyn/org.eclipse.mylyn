/*******************************************************************************
 * Copyright (c) 2006 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.trac.ui.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String TracConnectorUi_Replying_to__comment_X_X_;

	public static String TracConnectorUi_Replying_to__comment_ticket_X_X_X_;

	public static String TracConnectorUi_Replying_to__ticket_X_X_;

	public static String TracConnectorUi_Ticket;

	public static String WebHyperlink_Open_URL_X;
}
