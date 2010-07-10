/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.action.UpdateAttachmentJob;
import org.eclipse.mylyn.internal.bugzilla.ui.wizard.BugzillaAttachmentWizard;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

@SuppressWarnings("restriction")
public class BugzillaAttachmentPart extends TaskEditorAttachmentPart {

	@Override
	protected void createAttachmentTable(FormToolkit toolkit, Composite attachmentsComposite) {
		// ignore
		super.createAttachmentTable(toolkit, attachmentsComposite);
		final TaskEditor taskEditor = this.getTaskEditorPage().getTaskEditor();
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {

				final Action detailAction = new Action("Details...") {
					@Override
					public void run() {
						ITaskAttachment attachment = AttachmentUtil.getSelectedAttachment();
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();
						IEditorPart activeEditor = page.getActiveEditor();
						IWorkbenchPartSite site = activeEditor.getSite();
						Shell shell = site.getShell();
						if (activeEditor instanceof TaskEditor) {
							final TaskEditor taskEditor = (TaskEditor) activeEditor;
							IFormPage taskEditorPage = taskEditor.findPage("id"); //$NON-NLS-1$
							if (taskEditorPage instanceof BugzillaTaskEditorPage) {
								BugzillaTaskEditorPage bugzillaTaskEditorPage = (BugzillaTaskEditorPage) taskEditorPage;

								ITask attachmentTask = attachment.getTask();
								ITask nTask = new TaskTask(attachmentTask.getConnectorKind(),
										attachmentTask.getRepositoryUrl(), attachmentTask.getTaskId() + "attachment"); //$NON-NLS-1$

								TaskData editTaskData = new TaskData(attachment.getTaskAttribute()
										.getTaskData()
										.getAttributeMapper(), attachment.getTaskAttribute()
										.getTaskData()
										.getConnectorKind(), attachment.getTaskAttribute()
										.getTaskData()
										.getRepositoryUrl(), attachment.getTaskAttribute().getTaskData().getTaskId());
								editTaskData.setVersion(attachment.getTaskAttribute().getTaskData().getVersion());
								TaskAttribute target0 = editTaskData.getRoot();
								TaskAttribute temp = attachment.getTaskAttribute();
								target0.setValues(temp.getValues());
								for (TaskAttribute child : temp.getAttributes().values()) {
									target0.deepAddCopy(child);
								}

								TaskAttribute comment = target0.createAttribute("comment"); //$NON-NLS-1$
								TaskAttributeMetaData commentMeta = comment.getMetaData();
								commentMeta.setType(TaskAttribute.TYPE_LONG_RICH_TEXT);
								commentMeta.setLabel("Messages.BugzillaAttachmentUpdateAction_Comment");

								ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(
										nTask, editTaskData);
								TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
										attachment.getTaskAttribute().getTaskData().getRepositoryUrl());
								final TaskDataModel model = new TaskDataModel(repository, nTask, workingCopy);
								AttributeEditorFactory factory = new AttributeEditorFactory(model, repository,
										bugzillaTaskEditorPage.getEditorSite()) {
									@Override
									public AbstractAttributeEditor createEditor(String type,
											final TaskAttribute taskAttribute) {
										AbstractAttributeEditor editor;
										if (IBugzillaConstants.EDITOR_TYPE_FLAG.equals(type)) {
											editor = new FlagAttributeEditor(model, taskAttribute, 350);
										} else {
											editor = super.createEditor(type, taskAttribute);
											if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
												editor.setDecorationEnabled(false);
											}
										}
										return editor;
									}
								};

								TaskAttribute target = workingCopy.getLocalData().getRoot();
								target.setValue(target0.getValue());

								final BugzillaAttachmentWizard attachmentWizard = new BugzillaAttachmentWizard(shell,
										factory, target, taskEditor, attachment);
								final NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(shell,
										attachmentWizard, false);
								model.addModelListener(new TaskDataModelListener() {

									@Override
									public void attributeChanged(TaskDataModelEvent event) {
										attachmentWizard.setChanged(true);
										dialog.updateButtons();
									}
								});

								dialog.setBlockOnOpen(false);
								dialog.create();
								dialog.open();
							}
						}
					}
				};

				final Action obsoleteAction = new Action("obsolete") {
					@Override
					public void run() {
						List<ITaskAttachment> attachment = AttachmentUtil.getSelectedAttachments(null);
						if (attachment != null) {
							final UpdateAttachmentJob job = new UpdateAttachmentJob(attachment, taskEditor, true);
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
													bugzillaPage.getTaskEditor().setMessage(
															job.getError().getMessage(), IMessageProvider.ERROR);
												}
											});
										}
									}
								}
							});
							job.schedule();
						}
					}
				};
				final Action notObsoleteAction = new Action("not obsolete") {
					@Override
					public void run() {
						List<ITaskAttachment> attachment = AttachmentUtil.getSelectedAttachments(null);
						if (attachment != null) {
							final UpdateAttachmentJob job = new UpdateAttachmentJob(attachment, taskEditor, false);
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
													bugzillaPage.getTaskEditor().setMessage(
															job.getError().getMessage(), IMessageProvider.ERROR);
												}
											});
										}
									}
								}
							});
							job.schedule();
						}
					}
				};
				MenuManager subMenu = new MenuManager("Mark as");
				subMenu.add(obsoleteAction);
				subMenu.add(notObsoleteAction);

				manager.add(subMenu);
				manager.add(detailAction);

				List<ITaskAttachment> attachments = AttachmentUtil.getSelectedAttachments(null);
				if (attachments != null) {
					detailAction.setEnabled(attachments.size() == 1);
				} else {
					detailAction.setEnabled(false);
				}
				notObsoleteAction.setEnabled(false);
				obsoleteAction.setEnabled(false);
				for (ITaskAttachment taskAttachment : attachments) {
					TaskAttribute taskAttribute = taskAttachment.getTaskAttribute();
					TaskAttribute deprecated = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
					if (deprecated != null && deprecated.getValue().equals("1")) { //$NON-NLS-1$
						notObsoleteAction.setEnabled(true);
						break;
					}
				}
				for (ITaskAttachment taskAttachment : attachments) {
					TaskAttribute taskAttribute = taskAttachment.getTaskAttribute();
					TaskAttribute deprecated = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
					if (deprecated != null && !deprecated.getValue().equals("1")) { //$NON-NLS-1$
						obsoleteAction.setEnabled(true);
						break;
					}
				}

			}
		});
	}

}
