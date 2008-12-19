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

package org.eclipse.mylyn.internal.bugzilla.ui.action;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.action.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BugzillaUpdateAttachmentAction_mark_not_obsolete;

	public static String BugzillaUpdateAttachmentAction_mark_obsolete;

	public static String UpdateAttachmentJob_obsolete_not_toggled;

	public static String UpdateAttachmentJob_obsolete_toggled_successfully;

	public static String UpdateAttachmentJob_update_attachments;

	public static String UpdateAttachmentJob_update_attachment;

}
