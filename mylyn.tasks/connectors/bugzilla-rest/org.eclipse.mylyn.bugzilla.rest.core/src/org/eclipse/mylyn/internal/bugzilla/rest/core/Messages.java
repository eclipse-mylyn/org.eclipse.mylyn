/*******************************************************************************
 * Copyright © 2024 George
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String BugzillaRestAttachmentMapper_AttachmentAuthor;

	public static String BugzillaRestAttachmentMapper_ContentType;

	public static String BugzillaRestAttachmentMapper_CreationDate;

	public static String BugzillaRestAttachmentMapper_Description;

	public static String BugzillaRestAttachmentMapper_Filename;

	public static String BugzillaRestAttachmentMapper_ModificationDate;

	public static String BugzillaRestAttachmentMapper_Obsolete;

	public static String BugzillaRestAttachmentMapper_Patch;

	public static String BugzillaRestAttachmentMapper_Private;

	public static String BugzillaRestAttachmentMapper_SizeInBytes;

	public static String BugzillaRestAttachmentMapper_URL;

	public static String BugzillaRestClient_MissingValidCredentials;

	public static String BugzillaRestConfiguration_OsAll;

	public static String BugzillaRestConfiguration_PlatformAll;

	public static String BugzillaRestConfiguration_UnknownCustomFieldType;

	public static String BugzillaRestConnector_Bugzilla_5_OrLater;

	public static String BugzillaRestConnector_BugzillaRestExceptionFromPerformQuery;

	public static String BugzillaRestConnector_CoreExceptionFromPerformQuery;

	public static String BugzillaRestConnector_ErrorDuringGetBugzillaRestConfiguration;

	public static String BugzillaRestConnector_PerformQuery;

	public static String BugzillaRestConnector_RepositoryIsInvalid;

	public static String BugzillaRestCreateTaskSchema_Alias;

	public static String BugzillaRestCreateTaskSchema_Assigned_To;

	public static String BugzillaRestCreateTaskSchema_Blocks;

	public static String BugzillaRestCreateTaskSchema_CC;

	public static String BugzillaRestCreateTaskSchema_Depends_On;

	public static String BugzillaRestCreateTaskSchema_Description_Private;

	public static String BugzillaRestCreateTaskSchema_Keywords;

	public static String BugzillaRestCreateTaskSchema_Operation;

	public static String BugzillaRestCreateTaskSchema_Platform;

	public static String BugzillaRestCreateTaskSchema_QA_Contact;

	public static String BugzillaRestCreateTaskSchema_Target_Milestone;

	public static String BugzillaRestCreateTaskSchema_Version;

	public static String BugzillaRestGetTaskData_RequestedResourceDoesNotExist;

	public static String BugzillaRestGetTaskData_UnexpectedResponseFromServer;

	public static String BugzillaRestPostNewAttachment_CouldNotCloseStreamFromSource;

	public static String BugzillaRestPostNewAttachment_CouldNotCreateRequestEntity;

	public static String BugzillaRestPostNewAttachment_CouldNotGetStreamFromSource;

	public static String BugzillaRestPostNewAttachment_DesccriptionRequiredWhenSubmittingAttachments;

	public static String BugzillaRestPostNewAttachment_RequestedResourceDoesNotExist;

	public static String BugzillaRestPostNewAttachment_StatusFromServer;

	public static String BugzillaRestPostNewAttachment_UnexpectedResponseFromServer;

	public static String BugzillaRestRequest_ResourceDoesNotExist;

	public static String BugzillaRestRequest_UnexpectedResponseFromServer;

	public static String BugzillaRestTaskAttachmentHandler_AddAttachmentData;

	public static String BugzillaRestTaskAttachmentHandler_ErrorAddAttachmentData;

	public static String BugzillaRestTaskAttachmentHandler_GetAttachmentData;

	public static String BugzillaRestTaskDataHandler_ErrorGetTaskdata;

	public static String BugzillaRestTaskDataHandler_ErrorPostTaskdata;

	public static String BugzillaRestTaskDataHandler_PostTaskdata;

	public static String BugzillaRestTaskDataHandler_RetrieveTask;

	public static String BugzillaRestTaskDataHandler_SubmittingTask;

	public static String BugzillaRestTaskSchema_Depends_On;

	public static String BugzillaRestTaskSchema_Blocks;

	public static String BugzillaRestTaskSchema_Keywords;

	public static String BugzillaRestTaskSchema_Reset_Qa_Contact;

	public static String BugzillaRestTaskSchema_Reset_Assigned_To;

	public static String BugzillaRestTaskSchema_Add_CC;

	public static String BugzillaRestTaskSchema_Alias;

	public static String BugzillaRestTaskSchema_Assigned_To;

	public static String BugzillaRestTaskSchema_CC_Selected;

	public static String BugzillaRestTaskSchema_Dup_Of;

	public static String BugzillaRestTaskSchema_ID;

	public static String BugzillaRestTaskSchema_Operation;

	public static String BugzillaRestTaskSchema_OS;

	public static String BugzillaRestTaskSchema_Platform;

	public static String BugzillaRestTaskSchema_QA_Contact;

	public static String BugzillaRestTaskSchema_Remove_CC;

	public static String BugzillaRestTaskSchema_Target_Milestone;

	public static String BugzillaRestTaskSchema_Version;

	public static String ListenableFutureJob_TimeoutWaitingForResult;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
