/*******************************************************************************
 * Copyright (c) 2010 Peter Stibrany and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Peter Stibrany
 */
public class TaskAttachmentEditorViewer implements ITaskAttachmentViewer {

	private final IEditorDescriptor descriptor;

	TaskAttachmentEditorViewer(IEditorDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public String getId() {
		return descriptor.getId();
	}

	public String getLabel() {
		return descriptor.getLabel();
	}

	public void openAttachment(final IWorkbenchPage page, final ITaskAttachment attachment) throws CoreException {

		DownloadAndOpenTaskAttachmentJob job = new DownloadAndOpenTaskAttachmentJob(
				MessageFormat.format(Messages.TaskAttachmentEditorViewer_openingAttachment,
						AttachmentUtil.getAttachmentFilename(attachment)), attachment, page, descriptor.getId());

		IProgressService service = page.getWorkbenchWindow().getWorkbench().getProgressService();
		try {
			service.run(true, true, job);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CoreException) {
				StatusHandler.log(((CoreException) e.getTargetException()).getStatus());
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						Messages.TaskAttachmentEditorViewer_ErrorDownloadingAttachment, e));
			}

		} catch (InterruptedException e) {
			// ignore, since it was canceled
		}
	}
}
