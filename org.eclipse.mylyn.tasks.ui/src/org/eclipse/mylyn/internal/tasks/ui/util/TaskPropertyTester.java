/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class TaskPropertyTester extends PropertyTester {

	private static final String PROPERTY_CAN_GET_ATTACHEMNT = "canGetAttachment";

	private static final String PROPERTY_CAN_POST_ATTACHMENT = "canPostAttachment";

	private static final String PROPERTY_CONNECTOR_KIND = "connectorKind";

	private static final String PROPERTY_HAS_EDITS = "hasEdits";

	private static final String PROPERTY_HAS_LOCAL_CONTEXT = "hasLocalContext";

	private static final String PROPERTY_HAS_REPOSITORY_CONTEXT = "hasRepositoryContext";

	private static final String PROPERTY_IS_COMPLETED = "isCompleted";

	private static final String PROPERTY_IS_LOCAL = "isLocal";

	private static final String PROPERTY_LOCAL_COMPLETION_STATE = "hasLocalCompletionState";

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
				return equals(AttachmentUtil.canDownloadAttachment(task), expectedValue);
			} else if (PROPERTY_CAN_GET_ATTACHEMNT.equals(property)) {
				return equals(AttachmentUtil.canUploadAttachment(task), expectedValue);
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
			}
		}
		return false;
	}

}
