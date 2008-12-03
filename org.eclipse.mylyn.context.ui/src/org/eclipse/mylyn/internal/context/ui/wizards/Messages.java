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

package org.eclipse.mylyn.internal.context.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.wizards.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ContextAttachWizard_Attach_Context;

	public static String ContextAttachWizardPage_Enter_comment;

	public static String ContextAttachWizardPage_Attaches_local_context_to_repository_task;

	public static String ContextAttachWizardPage_Comment_;

	public static String ContextAttachWizardPage_Repository_;

	public static String ContextAttachWizardPage_Task_;

	public static String ContextRetrieveWizard_Retrieve_Context;

	public static String ContextRetrieveWizardPage_Author;

	public static String ContextRetrieveWizardPage_Date;

	public static String ContextRetrieveWizardPage_Description;

	public static String ContextRetrieveWizardPage_Select_context;

	public static String ContextRetrieveWizardPage_SELECT_A_CONTEXT_TO_RETTRIEVE_FROM_TABLE_BELOW;

	public static String ContextRetrieveWizardPage_Task;
}
