/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
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
	public static String GistAttachmentPart_PartName;

	/** */
	public static String GistConnectorUi_LabelTaskKind;

	/** */
	public static String GistRepositoryQueryPage_LabelTitle;

	/** */
	public static String GistRepositoryQueryPage_LabelUser;

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
	public static String GistTaskEditorPageFactory_PageText;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
