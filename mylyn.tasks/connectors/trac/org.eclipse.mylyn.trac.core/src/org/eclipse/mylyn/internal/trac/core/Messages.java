/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.trac.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.trac.core.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String TracAttachmentHandler_Downloading_attachment;

	public static String TracAttachmentHandler_Uploading_attachment;

	public static String TracAttribute_Assigned_to;

	public static String TracAttribute_CC;

	public static String TracAttribute_Component;

	public static String TracAttribute_Created;

	public static String TracAttribute_Description;

	public static String TracAttribute_ID;

	public static String TracAttribute_Keywords;

	public static String TracAttribute_Last_Modification;

	public static String TracAttribute_Milestone;

	public static String TracAttribute_Priority;

	public static String TracAttribute_Reporter;

	public static String TracAttribute_Resolution;

	public static String TracAttribute_Severity;

	public static String TracAttribute_Status;

	public static String TracAttribute_Summary;

	public static String TracAttribute_Type;

	public static String TracAttribute_Version;

	public static String TracCorePlugin_I_O_error_has_occured;

	public static String TracCorePlugin_Repository_URL_is_invalid;

	public static String TracCorePlugin_the_SERVER_RETURNED_an_UNEXPECTED_RESOPNSE;

	public static String TracCorePlugin_Unexpected_error;

	public static String TracCorePlugin_Unexpected_server_response_;

	public static String TracRepositoryConnector_Getting_changed_tasks;

	public static String TracRepositoryConnector_Querying_repository;

	public static String TracRepositoryConnector_Trac_Client_Label;

	public static String TracTaskDataHandler_Accept;

	public static String TracTaskDataHandler_Leave;

	public static String TracTaskDataHandler_Leave_as_Status;

	public static String TracTaskDataHandler_Leave_as_Status_Resolution;

	public static String TracTaskDataHandler_Reopen;

	public static String TracTaskDataHandler_Resolve_as;

	public static String TracWikiHandler_Download_Wiki_Page;

	public static String TracWikiHandler_Download_Wiki_Page_Names;

	public static String TracWikiHandler_Retrieve_Wiki_Page_History;

	public static String TracWikiHandler_Upload_Wiki_Page;
}
