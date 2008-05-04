/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.ByteArrayOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class CopyAttachmentToClipboardJob extends Job {

	private final RepositoryAttachment attachment;

	public CopyAttachmentToClipboardJob(RepositoryAttachment attachment) {
		super("Copying Attachment to Clipboard");

		if (attachment == null) {
			throw new IllegalArgumentException("attachment may not be null");
		}

		this.attachment = attachment;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(attachment.getRepositoryKind(),
				attachment.getRepositoryUrl());
		AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(attachment.getRepositoryKind());
		AbstractAttachmentHandler handler = connector.getAttachmentHandler();
		if (handler == null) {
			return new RepositoryStatus(IStatus.INFO, TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_INTERNAL,
					"The repository does not support attachments.");
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			handler.downloadAttachment(repository, attachment, out, monitor);
		} catch (final CoreException e) {
			TasksUiInternal.displayStatus("Copy Attachment to Clipboard", e.getStatus());
			return Status.OK_STATUS;
		}

		String contents = new String(out.toByteArray());
		contents = contents.replaceAll("\r\n|\n", System.getProperty("line.separator"));
		copyToClipboard(contents);

		return Status.OK_STATUS;
	}

	private void copyToClipboard(final String contents) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
				clipboard.setContents(new Object[] { contents }, new Transfer[] { TextTransfer.getInstance() });
				clipboard.dispose();
			}
		});
	}

}
