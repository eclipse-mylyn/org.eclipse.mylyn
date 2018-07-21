/*******************************************************************************
 * Copyright (c) 2010 Peter Stibrany and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.ui.ITaskAttachmentViewer;
import org.eclipse.mylyn.internal.tasks.ui.TaskAttachmentViewerManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Peter Stibrany
 */
public class OpenTaskAttachmentHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				List<ITaskAttachment> attachments = AttachmentUtil.getSelectedAttachments(event);
				try {
					openAttachments(page, attachments);
				} catch (OperationCanceledException e) {
					// canceled
				}
			}
		}
		return null;
	}

	public static void openAttachments(IWorkbenchPage page, List<ITaskAttachment> attachments) {
		TaskAttachmentViewerManager manager = new TaskAttachmentViewerManager();

		for (ITaskAttachment attachment : attachments) {
			ITaskAttachmentViewer viewer = manager.getPreferredViewer(attachment);
			if (viewer == null) {
				TasksUiInternal.logAndDisplayStatus(Messages.OpenTaskAttachmentHandler_failedToOpenViewer, new Status(
						IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						Messages.OpenTaskAttachmentHandler_noAttachmentViewerFound));
				continue;
			}

			try {
				viewer.openAttachment(page, attachment);
			} catch (CoreException e) {
				TasksUiInternal.logAndDisplayStatus(Messages.OpenTaskAttachmentHandler_failedToOpenViewer,
						e.getStatus());
			}
		}
	}
}
