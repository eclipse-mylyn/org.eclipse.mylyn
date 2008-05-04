/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskAttachmentEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskAttachment2;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class OpenTaskAttachmentInDefaultEditorHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			List<?> items = ((IStructuredSelection) selection).toList();
			for (Object item : items) {
				if (item instanceof ITaskAttachment2) {
					if (page == null) {
						IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
						page = window.getActivePage();
						if (page == null) {
							throw new ExecutionException("No active workbench page");
						}
					}
					openAttachment(page, (ITaskAttachment2) item);
				}
			}
		}
		return null;
	}

	private void openAttachment(IWorkbenchPage page, ITaskAttachment2 attachment) throws ExecutionException {
		TaskAttachmentEditorInput input = new TaskAttachmentEditorInput(attachment);
		IEditorDescriptor description = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(input.getName());
		if (description == null) {
			TasksUiInternal.displayStatus("Open Attachment Failed", new Status(IStatus.WARNING,
					TasksUiPlugin.ID_PLUGIN, "No default editor for \"" + input.getName() + "\" found"));
		} else {
			try {
				page.openEditor(input, description.getId());
			} catch (PartInitException e) {
				throw new ExecutionException("Failed to open editor", e);
			}
		}
	}

}
