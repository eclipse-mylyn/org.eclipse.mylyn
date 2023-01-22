/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.action;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPage;
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
@SuppressWarnings("restriction")
public class UpdateAttachmentJob extends Job {

	private final List<ITaskAttachment> attachment;

	private final TaskEditor editor;

	private final boolean obsolete;

	private IStatus error;

	public UpdateAttachmentJob(List<ITaskAttachment> attachment, TaskEditor editor, boolean obsolete) {
		super(Messages.UpdateAttachmentJob_update_attachment);
		this.attachment = attachment;
		this.editor = editor;
		this.obsolete = obsolete;
	}

	public IStatus getError() {
		return error;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final ITask task;
		task = editor.getTaskEditorInput().getTask();

		if (!task.getConnectorKind().equals(BugzillaCorePlugin.CONNECTOR_KIND)) {
			return Status.OK_STATUS;
		}
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(task.getConnectorKind());
		monitor.beginTask(Messages.UpdateAttachmentJob_update_attachments, attachment.size() * 10 + 10);
		try {
			for (ITaskAttachment taskAttachment : attachment) {
				TaskAttribute taskAttribute = taskAttachment.getTaskAttribute();
				TaskAttribute deprecated = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
				if (deprecated != null) {
					if (deprecated.getValue().equals("1") && !obsolete) { //$NON-NLS-1$
						try {
							deprecated.setValue("0"); //$NON-NLS-1$
							((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(
									taskAttachment.getTaskRepository(), taskAttribute, "update", monitor); //$NON-NLS-1$
						} catch (CoreException e) {
							error = e.getStatus();
							deprecated.setValue("1"); //$NON-NLS-1$
							return Status.OK_STATUS;
						}
					} else if (deprecated.getValue().equals("0") && obsolete) { //$NON-NLS-1$
						try {
							deprecated.setValue("1"); //$NON-NLS-1$
							((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(
									taskAttachment.getTaskRepository(), taskAttribute, "update", monitor); //$NON-NLS-1$
						} catch (CoreException e) {
							error = e.getStatus();
							deprecated.setValue("0"); //$NON-NLS-1$
							return Status.OK_STATUS;
						}
					}
				}
				monitor.worked(10);
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
												Control control = bugzillaPage
														.getPart(AbstractTaskEditorPage.ID_PART_ATTACHMENTS)
														.getControl();
												if (control instanceof Section) {
													Section section = (Section) control;
													CommonFormUtil.setExpanded(section, true);
												}
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
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}
