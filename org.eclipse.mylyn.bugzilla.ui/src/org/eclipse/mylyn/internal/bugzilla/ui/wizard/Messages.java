/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.wizard.messages"; //$NON-NLS-1$

	public static String BugzillaAttachmentWizard_Attachment_Details_Dialog_Title;

	public static String BugzillaAttachmentWizardPage_Advanced;

	public static String BugzillaAttachmentWizardPage_Description;

	public static String BugzillaAttachmentWizardPage_RunInBackground;

	public static String BugzillaAttachmentWizardPage_Titel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
