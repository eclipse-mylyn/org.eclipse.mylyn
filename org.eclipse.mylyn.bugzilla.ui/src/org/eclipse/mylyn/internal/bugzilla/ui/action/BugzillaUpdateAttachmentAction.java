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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPage;
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
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Frank Becker
 */
public class BugzillaUpdateAttachmentAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	private ISelection currentSelection;

	private final boolean obsolete;

	public BugzillaUpdateAttachmentAction(boolean obsolete) {
		super("UpdateAttachmentAction"); //$NON-NLS-1$
		this.obsolete = obsolete;
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
			final TaskEditor taskEditor = (TaskEditor) activeEditor;
			IStructuredSelection selection = null;
			if (currentSelection instanceof IStructuredSelection) {
				selection = (IStructuredSelection) currentSelection;
			}
			if (selection == null || selection.isEmpty()) {
				return;
			}
			List<ITaskAttachment> attachment = selection.toList();
			if (attachment != null) {
				final UpdateAttachmentJob job = new UpdateAttachmentJob(attachment, taskEditor, obsolete);
				job.setUser(true);
				job.addJobChangeListener(new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						if (job.getError() != null) {
							IFormPage formPage = taskEditor.getActivePageInstance();
							if (formPage instanceof BugzillaTaskEditorPage) {
								final BugzillaTaskEditorPage bugzillaPage = (BugzillaTaskEditorPage) formPage;
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
									public void run() {
										bugzillaPage.getTaskEditor().setMessage(job.getError().getMessage(),
												IMessageProvider.ERROR);
									}
								});
							}
						}
					}
				});
				job.schedule();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
		IStructuredSelection sructuredSelection = null;
		if (selection instanceof IStructuredSelection) {
			sructuredSelection = (IStructuredSelection) currentSelection;
		}
		if (sructuredSelection == null || sructuredSelection.isEmpty()) {
			return;
		}
		List<ITaskAttachment> attachmentList = sructuredSelection.toList();
		action.setEnabled(false);
		for (ITaskAttachment taskAttachment : attachmentList) {
			TaskAttribute taskAttribute = taskAttachment.getTaskAttribute();
			TaskAttribute deprecated = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);

			if (deprecated.getValue().equals("1") != obsolete) { //$NON-NLS-1$
				action.setEnabled(true);
				break;
			}
		}
	}
}
