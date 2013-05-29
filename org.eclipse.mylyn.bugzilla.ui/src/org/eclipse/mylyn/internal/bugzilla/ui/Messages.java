/*******************************************************************************
 * Copyright (c) 2009 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.messages"; //$NON-NLS-1$

	public static String TaskAttachmentHyperlink_Open_Attachment_X_in_Y;

	public static String TaskAttachmentTableEditorHyperlink_AttachmentNotFound;

	public static String TaskAttachmentTableEditorHyperlink_QuestionMsg;

	public static String TaskAttachmentTableEditorHyperlink_Show_Attachment_X_in_Y;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
