/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jacques Bouthillier - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.documentation;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.documentation.messages"; //$NON-NLS-1$

	public static String RestAPIHandler_selectedServer;

	public static String RestAPIHandler_serverTooOld;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
