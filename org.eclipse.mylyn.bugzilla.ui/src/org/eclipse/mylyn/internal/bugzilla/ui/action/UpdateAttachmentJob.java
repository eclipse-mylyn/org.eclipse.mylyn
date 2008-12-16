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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPage;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Frank Becker
 */
public class UpdateAttachmentJob extends Job {

	private final List<ITaskAttachment> attachment;

	private final TaskEditor editor;

	public UpdateAttachmentJob(List<ITaskAttachment> attachment, TaskEditor editor) {
		super(Messages.getString("UpdateAttachmentJob.update_attachment")); //$NON-NLS-1$
		this.attachment = attachment;
		this.editor = editor;
	}

	@SuppressWarnings("restriction")
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final ITask task;
		task = editor.getTaskEditorInput().getTask();

		if (!task.getConnectorKind().equals(BugzillaCorePlugin.CONNECTOR_KIND)) {
			return Status.OK_STATUS;
		}
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				task.getConnectorKind());
		monitor.beginTask(Messages.getString("UpdateAttachmentJob.update_attachments"), attachment.size() * 10 + 10); //$NON-NLS-1$
		try {
			for (ITaskAttachment taskAttachment : attachment) {
				TaskAttribute taskAttribute = taskAttachment.getTaskAttribute();
				TaskAttribute deprecated = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
				if (deprecated.getValue().equals("1")) { //$NON-NLS-1$
					deprecated.setValue("0"); //$NON-NLS-1$
				} else {
					deprecated.setValue("1"); //$NON-NLS-1$
				}
				monitor.worked(10);
				((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(
						taskAttachment.getTaskRepository(), taskAttribute, "update", monitor); //$NON-NLS-1$
			}
			if (task != null) {
				if (connector != null) {
					TasksUiInternal.synchronizeTask(connector, task, true, new JobChangeAdapter() {
						@Override
						public void done(IJobChangeEvent event) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									try {
										if (editor != null) {
											editor.refreshPages();
											editor.getEditorSite().getPage().activate(editor);
											IFormPage formPage = editor.getActivePageInstance();
											if (formPage instanceof BugzillaTaskEditorPage) {
												BugzillaTaskEditorPage bugzillaPage = (BugzillaTaskEditorPage) formPage;
												Control control = bugzillaPage.getPart(
														AbstractTaskEditorPage.ID_PART_ATTACHMENTS).getControl();
												if (control instanceof Section) {
													Section section = (Section) control;
													EditorUtil.toggleExpandableComposite(true, section);
												}
												bugzillaPage.getTaskEditor().setMessage(Messages.getString("UpdateAttachmentJob.obsolete_toggled_successful"), //$NON-NLS-1$
														IMessageProvider.INFORMATION);
											}

										}
									} finally {
										if (editor != null) {
											editor.showBusy(false);
										}
									}
								}
							});
						}
					});
				}
				monitor.worked(10);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (editor != null) {
							editor.showBusy(true);
						}
					}
				});
			}
		} catch (Exception e) {
			IFormPage formPage = editor.getActivePageInstance();
			if (formPage instanceof BugzillaTaskEditorPage) {
				final BugzillaTaskEditorPage bugzillaPage = (BugzillaTaskEditorPage) formPage;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						bugzillaPage.getTaskEditor().setMessage(Messages.getString("UpdateAttachmentJob.obsolete_not_toggled"), //$NON-NLS-1$
								IMessageProvider.ERROR);
					}
				});
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}
