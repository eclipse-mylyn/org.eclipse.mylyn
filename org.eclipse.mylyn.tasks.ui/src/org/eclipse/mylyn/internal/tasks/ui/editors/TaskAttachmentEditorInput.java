/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskAttachment2;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class TaskAttachmentEditorInput extends PlatformObject implements IStorageEditorInput {

	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";

	private static final String CTYPE_ZIP = "zip";

	private static final String CTYPE_OCTET_STREAM = "octet-stream";

	private static final String CTYPE_TEXT = "text";

	private static final String CTYPE_HTML = "html";

	private final ITaskAttachment2 attachment;

	private final String name;

	public TaskAttachmentEditorInput(ITaskAttachment2 attachment) {
		this.attachment = attachment;
		this.name = getName(attachment);
	}

	private String getName(ITaskAttachment2 attachment) {
		String name = attachment.getFileName();
		// if no filename is set, make one up with the proper extension so
		// we can support opening in that filetype's default editor
		if (name == null || "".equals(name)) {
			String ctype = attachment.getContentType();
			if (ctype.endsWith(CTYPE_HTML)) {
				name = ATTACHMENT_DEFAULT_NAME + ".html";
			} else if (ctype.startsWith(CTYPE_TEXT)) {
				name = ATTACHMENT_DEFAULT_NAME + ".txt";
			} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
				name = ATTACHMENT_DEFAULT_NAME;
			} else if (ctype.endsWith(CTYPE_ZIP)) {
				name = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP;
			} else {
				name = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1);
			}
		}
		// treat .patch files as text files
		if (name.endsWith(".patch")) {
			name += ".txt";
		}
		return name;
	}

	public IStorage getStorage() throws CoreException {
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(attachment.getConnectorKind(),
				attachment.getRepositoryUrl());
		AbstractTask task = TasksUi.getTaskListManager().getTaskList().getTask(attachment.getRepositoryUrl(),
				attachment.getTaskId());
		TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task, taskRepository.getConnectorKind());
		TaskAttribute[] attachments = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT);
		for (TaskAttribute taskAttribute : attachments) {
			ITaskAttachment2 existingAttachment = taskData.getAttributeMapper().getTaskAttachment(taskAttribute);
			if (existingAttachment.getAttachmentId().equals(attachment.getAttachmentId())) {
				return new TaskAttachmentStorage(taskRepository, task, taskAttribute, name);
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to find attachment: "
				+ attachment.getUrl()));
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		// ignore
		return null;
	}

	public String getName() {
		return attachment.getFileName();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Attachment: " + attachment.getAttachmentId() + " [" + attachment.getUrl() + "]";
	}

	private static class TaskAttachmentStorage extends PlatformObject implements IStorage {

		private final TaskRepository taskRepository;

		private final AbstractTask task;

		private final TaskAttribute attachmentAttribute;

		private final String name;

		public TaskAttachmentStorage(TaskRepository taskRepository, AbstractTask task,
				TaskAttribute attachmentAttribute, String name) {
			this.taskRepository = taskRepository;
			this.task = task;
			this.attachmentAttribute = attachmentAttribute;
			this.name = name;
		}

		public InputStream getContents() throws CoreException {
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					taskRepository.getConnectorKind());
			AbstractTaskAttachmentHandler handler = connector.getTaskAttachmentHandler();
			return handler.getContent(taskRepository, task, attachmentAttribute, new NullProgressMonitor());
		}

		public IPath getFullPath() {
			// ignore
			return null;
		}

		public String getName() {
			return name;
		}

		public boolean isReadOnly() {
			return true;
		}

	}
}
