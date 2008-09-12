/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class TaskAttachmentStorage extends PlatformObject implements IStorage {

	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";

	private static final String CTYPE_ZIP = "zip";

	private static final String CTYPE_OCTET_STREAM = "octet-stream";

	private static final String CTYPE_TEXT = "text";

	private static final String CTYPE_HTML = "html";

	private final TaskRepository taskRepository;

	private final ITask task;

	private final TaskAttribute attachmentAttribute;

	private final String name;

	public TaskAttachmentStorage(TaskRepository taskRepository, ITask task, TaskAttribute attachmentAttribute,
			String name) {
		this.taskRepository = taskRepository;
		this.task = task;
		this.attachmentAttribute = attachmentAttribute;
		this.name = name;
	}

	public static IStorage create(ITaskAttachment attachment) throws CoreException {
		Assert.isNotNull(attachment);
		TaskAttribute taskAttribute = attachment.getTaskAttribute();
		if (taskAttribute == null) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to find attachment: "
					+ attachment.getUrl()));
		}
		return new TaskAttachmentStorage(attachment.getTaskRepository(), attachment.getTask(), taskAttribute,
				getName(attachment));
	}

	private static String getName(ITaskAttachment attachment) {
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