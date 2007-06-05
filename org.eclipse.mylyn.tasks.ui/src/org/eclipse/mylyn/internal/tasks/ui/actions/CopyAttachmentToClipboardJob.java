/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import java.io.ByteArrayOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.MylarStatus;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class CopyAttachmentToClipboardJob extends Job {

	private RepositoryAttachment attachment;

	public CopyAttachmentToClipboardJob(RepositoryAttachment attachment) {
		super("Copying Attachment to Clipboard");

		this.attachment = attachment;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(attachment.getRepositoryKind(),
				attachment.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				attachment.getRepositoryKind());
		IAttachmentHandler handler = connector.getAttachmentHandler();
		if (handler == null) {
			return new MylarStatus(IStatus.INFO, TasksUiPlugin.PLUGIN_ID, IMylarStatusConstants.INTERNAL_ERROR,
					"The repository does not support attachments.");
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			handler.downloadAttachment(repository, attachment, out, monitor);
		} catch (final CoreException e) {
			MylarStatusHandler.displayStatus("Copy Attachment to Clipboard", e.getStatus());
			return Status.OK_STATUS;
		}
		
		String contents = new String(out.toByteArray());
		contents = contents.replaceAll("\r\n|\n", System.getProperty("line.separator"));
		copyToClipboard(contents);
		
		return Status.OK_STATUS;
	}

	/** For testing. */
	public void copyToClipboard(final String contents) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
				clipboard.setContents(new Object[] { contents }, new Transfer[] { TextTransfer.getInstance() });
				clipboard.dispose();
			}
		});
	}

}
