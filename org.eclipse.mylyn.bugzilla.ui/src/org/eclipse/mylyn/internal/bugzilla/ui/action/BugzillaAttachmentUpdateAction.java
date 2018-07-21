/*******************************************************************************
 * Copyright (c) 2009, 2010 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.action;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPage;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.FlagAttributeEditor;
import org.eclipse.mylyn.internal.bugzilla.ui.wizard.BugzillaAttachmentWizard;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Frank Becker
 */
@SuppressWarnings("restriction")
// XXX 3.5 rename to BuzillaAttachmentDetailsAction
public class BugzillaAttachmentUpdateAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	private ISelection currentSelection;

	public BugzillaAttachmentUpdateAction() {
		super("BugzillaAttachmentDetailAction"); //$NON-NLS-1$
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		IStructuredSelection selection = null;
		if (currentSelection instanceof IStructuredSelection) {
			selection = (IStructuredSelection) currentSelection;
		}
		if (selection == null || selection.isEmpty() || selection.size() != 1) {
			return;
		}
		ITaskAttachment attachment = (ITaskAttachment) selection.getFirstElement();
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
				ITask nTask = new TaskTask(attachmentTask.getConnectorKind(), attachmentTask.getRepositoryUrl(),
						attachmentTask.getTaskId() + "attachment"); //$NON-NLS-1$

				TaskData editTaskData = new TaskData(attachment.getTaskAttribute().getTaskData().getAttributeMapper(),
						attachment.getTaskAttribute().getTaskData().getConnectorKind(), attachment.getTaskAttribute()
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
				commentMeta.setLabel(Messages.BugzillaAttachmentUpdateAction_Comment);

				ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(nTask, editTaskData);
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
						attachment.getTaskAttribute().getTaskData().getRepositoryUrl());
				final String repositoryLabel = repository.getRepositoryLabel();
				final TaskDataModel model = new TaskDataModel(repository, nTask, workingCopy);
				AttributeEditorFactory factory = new AttributeEditorFactory(model, repository,
						bugzillaTaskEditorPage.getEditorSite()) {
					@Override
					public AbstractAttributeEditor createEditor(String type, final TaskAttribute taskAttribute) {
						AbstractAttributeEditor editor;
						if (IBugzillaConstants.EDITOR_TYPE_FLAG.equals(type)) {
							editor = new FlagAttributeEditor(model, taskAttribute);
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

				final BugzillaAttachmentWizard attachmentWizard = new BugzillaAttachmentWizard(shell, factory, target,
						taskEditor, attachment, repository.getRepositoryLabel());
				final NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(shell, attachmentWizard, false) {

					@Override
					protected IDialogSettings getDialogBoundsSettings() {
						IDialogSettings settings = BugzillaUiPlugin.getDefault().getDialogSettings();
						IDialogSettings section = settings.getSection(BugzillaUiPlugin.ATTACHMENT_WIZARD_SETTINGS_SECTION
								+ repositoryLabel);
						if (section == null) {
							section = settings.addNewSection(BugzillaUiPlugin.ATTACHMENT_WIZARD_SETTINGS_SECTION
									+ repositoryLabel);
						}
						return section;
					}

				};
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
		if (attachmentList != null && attachmentList.size() == 1) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}
}
