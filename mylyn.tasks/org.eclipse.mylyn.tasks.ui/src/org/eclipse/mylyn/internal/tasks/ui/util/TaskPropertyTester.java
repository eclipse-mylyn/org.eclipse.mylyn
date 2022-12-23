/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class TaskPropertyTester extends PropertyTester {

	private static final String PROPERTY_CAN_GET_ATTACHMENT = "canGetAttachment"; //$NON-NLS-1$

	private static final String PROPERTY_CAN_GET_HISTORY = "canGetHistory"; //$NON-NLS-1$

	private static final String PROPERTY_CAN_POST_ATTACHMENT = "canPostAttachment"; //$NON-NLS-1$

	private static final String PROPERTY_CONNECTOR_KIND = "connectorKind"; //$NON-NLS-1$

	private static final String PROPERTY_HAS_ACTIVE_TIME = "hasActiveTime"; //$NON-NLS-1$

	private static final String PROPERTY_HAS_EDITS = "hasEdits"; //$NON-NLS-1$

	private static final String PROPERTY_HAS_LOCAL_CONTEXT = "hasLocalContext"; //$NON-NLS-1$

	private static final String PROPERTY_HAS_REPOSITORY_CONTEXT = "hasRepositoryContext"; //$NON-NLS-1$

	private static final String PROPERTY_IS_COMPLETED = "isCompleted"; //$NON-NLS-1$

	private static final String PROPERTY_IS_LOCAL = "isLocal"; //$NON-NLS-1$

	private static final String PROPERTY_LOCAL_COMPLETION_STATE = "hasLocalCompletionState"; //$NON-NLS-1$

	private static final String PROPERTY_IS_ARTIFACT = "isArtifact"; //$NON-NLS-1$

	private boolean equals(boolean value, Object expectedValue) {
		return new Boolean(value).equals(expectedValue);
	}

	@SuppressWarnings("deprecation")
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ITask) {
			ITask task = (ITask) receiver;
			if (PROPERTY_CONNECTOR_KIND.equals(property)) {
				return task.getConnectorKind().equals(expectedValue);
			} else if (PROPERTY_CAN_POST_ATTACHMENT.equals(property)) {
				return equals(AttachmentUtil.canUploadAttachment(task), expectedValue);
			} else if (PROPERTY_CAN_GET_ATTACHMENT.equals(property)) {
				return equals(AttachmentUtil.canDownloadAttachment(task), expectedValue);
			} else if (PROPERTY_CAN_GET_HISTORY.equals(property)) {
				return TasksUiInternal.canGetTaskHistory(task);
			} else if (PROPERTY_HAS_ACTIVE_TIME.equals(property)) {
				return equals(TasksUiInternal.getActiveTime(task) > 0, expectedValue);
			} else if (PROPERTY_HAS_EDITS.equals(property)) {
				return equals(ClearOutgoingAction.hasOutgoingChanges(task), expectedValue);
			} else if (PROPERTY_HAS_LOCAL_CONTEXT.equals(property)) {
				return equals(AttachmentUtil.hasLocalContext(task), expectedValue);
			} else if (PROPERTY_HAS_REPOSITORY_CONTEXT.equals(property)) {
				return equals(AttachmentUtil.hasContextAttachment(task), expectedValue);
			} else if (PROPERTY_IS_COMPLETED.equals(property)) {
				return equals(task.isCompleted(), expectedValue);
			} else if (PROPERTY_IS_LOCAL.equals(property)) {
				return (task instanceof AbstractTask) && equals(((AbstractTask) task).isLocal(), expectedValue);
			} else if (PROPERTY_LOCAL_COMPLETION_STATE.equals(property)) {
				return equals(TasksUiInternal.hasLocalCompletionState(task), expectedValue);
			} else if (PROPERTY_IS_ARTIFACT.equals(property)) {
				String artifactFlag = task.getAttribute(ITasksCoreConstants.ATTRIBUTE_ARTIFACT);
				return equals(Boolean.valueOf(artifactFlag), expectedValue);
			}
		}
		return false;
	}

}
