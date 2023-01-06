/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.ui.gist.messages"; //$NON-NLS-1$

	/** */
	public static String CloneGistHandler_ErrorMessage;

	/** */
	public static String CloneGistHandler_ErrorRepoExists;

	/** */
	public static String CloneGistHandler_ErrorTitle;

	/** */
	public static String CloneGistHandler_TaskCloning;

	/** */
	public static String CloneGistHandler_TaskConnectingProject;

	/** */
	public static String CloneGistHandler_TaskCreatingProject;

	/** */
	public static String CloneGistHandler_TaskRegisteringRepository;

	/** */
	public static String CreateGistHandler_CreateGistJobName;

	/** */
	public static String GistAttachmentPage_Description;

	/** */
	public static String GistAttachmentPage_LabelBinaryWarning;

	/** */
	public static String GistAttachmentPage_LabelFile;

	/** */
	public static String GistAttachmentPage_Title;

	/** */
	public static String GistAttachmentPart_PartName;

	/** */
	public static String GistConnectorSelectionDialog_Message;

	/** */
	public static String GistConnectorSelectionDialog_Title;

	/** */
	public static String GistConnectorUi_LabelTaskKind;

	/** */
	public static String GistNotificationPopup_GistLink;

	/** */
	public static String GistNotificationPopup_GistTitle;

	/** */
	public static String GistNotificationPopup_ShellTitle;

	/** */
	public static String GistRepositoryQueryPage_LabelTitle;

	/** */
	public static String GistRepositoryQueryPage_LabelUser;

	/** */
	public static String GistRepositoryQueryPage_TitleDefault;

	/** */
	public static String GistRepositorySettingsPage_Description;

	/** */
	public static String GistRepositorySettingsPage_RepositoryLabelDefault;

	/** */
	public static String GistRepositorySettingsPage_StatusError;

	/** */
	public static String GistRepositorySettingsPage_StatusSuccess;

	/** */
	public static String GistRepositorySettingsPage_TaskContacting;

	/** */
	public static String GistRepositorySettingsPage_TaskValidating;

	/** */
	public static String GistRepositorySettingsPage_Title;

	/** */
	public static String GistTaskEditorPage_LabelCloneGistAction;

	/** */
	public static String GistTaskEditorPageFactory_PageText;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
