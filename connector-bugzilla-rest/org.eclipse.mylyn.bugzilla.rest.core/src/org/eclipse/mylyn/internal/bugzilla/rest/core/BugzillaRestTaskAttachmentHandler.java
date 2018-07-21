/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class BugzillaRestTaskAttachmentHandler extends AbstractTaskAttachmentHandler {
	private final BugzillaRestConnector connector;

	public BugzillaRestTaskAttachmentHandler(BugzillaRestConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean canGetContent(TaskRepository repository, ITask task) {
		return true;
	}

	@Override
	public boolean canPostContent(TaskRepository repository, ITask task) {
		return true;
	}

	@Override
	public InputStream getContent(@NonNull TaskRepository repository, @NonNull ITask task,
			@NonNull TaskAttribute attachmentAttribute, @Nullable IProgressMonitor monitor) throws CoreException {

		try {
			monitor = Policy.monitorFor(monitor);
			monitor.beginTask("getAttachment Data", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			BugzillaRestClient client = connector.getClient(repository);
			try {
				IOperationMonitor progress = OperationUtil.convert(monitor, "get Attachment Data", 3); //$NON-NLS-1$
				return client.getAttachmentData(attachmentAttribute, progress);
			} catch (BugzillaRestException e) {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, 2,
						"Error get attachment data.\n\n" + e.getMessage(), e)); //$NON-NLS-1$
			}
		} finally {
			monitor.done();
		}
	}

	@Override
	public void postContent(@NonNull TaskRepository repository, @NonNull ITask task,
			@NonNull AbstractTaskAttachmentSource source, @Nullable String comment,
			@Nullable TaskAttribute attachmentAttribute, @Nullable IProgressMonitor monitor) throws CoreException {
		try {
			monitor = Policy.monitorFor(monitor);
			monitor.beginTask("addAttachment Data", IProgressMonitor.UNKNOWN);
			BugzillaRestClient client = connector.getClient(repository);
			try {
				IOperationMonitor progress = OperationUtil.convert(monitor, "get Attachment Data", 3);
				client.addAttachment(task.getTaskId(), comment, source, attachmentAttribute, progress);
			} catch (BugzillaRestException e) {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, 2,
						"Error add attachment data.\n\n" + e.getMessage(), e));
			}
		} finally {
			monitor.done();
		}
	}

}
