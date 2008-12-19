/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.action;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Frank Becker
 */
public class BugzillaUpdateAttachmentAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	private ISelection currentSelection;

	public BugzillaUpdateAttachmentAction() {
		super("UpdateAttachmentAction"); //$NON-NLS-1$
	}

	protected BugzillaUpdateAttachmentAction(String text) {
		super(text);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		if (activeEditor instanceof TaskEditor) {
			TaskEditor taskEditor = (TaskEditor) activeEditor;
			IStructuredSelection selection = null;
			if (currentSelection instanceof IStructuredSelection) {
				selection = (IStructuredSelection) currentSelection;
			}
			if (selection == null || selection.isEmpty()) {
				return;
			}
			List<ITaskAttachment> attachment = selection.toList();
			if (attachment != null) {
				UpdateAttachmentJob job = new UpdateAttachmentJob(attachment, taskEditor);
				job.setUser(true);
				job.schedule();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
		IStructuredSelection sructuredSelection = null;
		if (selection instanceof IStructuredSelection) {
			sructuredSelection = (IStructuredSelection) currentSelection;
		}
		if (sructuredSelection == null || sructuredSelection.isEmpty()) {
			return;
		}
		TaskAttribute taskAttribute = ((ITaskAttachment) sructuredSelection.getFirstElement()).getTaskAttribute();
		TaskAttribute deprecated = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		if (deprecated.getValue().equals("1")) { //$NON-NLS-1$
			action.setText(Messages.BugzillaUpdateAttachmentAction_mark_not_obsolete);
		} else {
			action.setText(Messages.BugzillaUpdateAttachmentAction_mark_obsolete);
		}
	}
}
