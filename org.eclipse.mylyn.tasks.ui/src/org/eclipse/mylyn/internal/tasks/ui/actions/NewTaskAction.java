/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewLocalTaskWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewTaskAction extends Action implements IViewActionDelegate, IExecutableExtension {

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create";

	private boolean skipRepositoryPage = false;

	private boolean localTask = false;

	private boolean supportsTaskSelection;

	@Override
	public void run() {
		showWizard(null);
	}

	public int showWizard(TaskSelection taskSelection) {
		IWizard wizard;
		List<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
		if (localTask) {
			wizard = new NewLocalTaskWizard(taskSelection);
		} else {
			TaskRepository taskRepository = null;
			if (repositories.size() == 1) {
				taskRepository = repositories.get(0);
			} else if (skipRepositoryPage) {
				taskRepository = TasksUiUtil.getSelectedRepository();
			}

			if (taskRepository != null) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
				wizard = createNewTaskWizard(connectorUi, taskRepository, taskSelection);
			} else {
				wizard = new NewTaskWizard(taskSelection);
			}
		}

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setBlockOnOpen(true);

			// make sure the wizard has created its pages
			dialog.create();
			if (!(wizard instanceof NewTaskWizard) && wizard.canFinish()) {
				wizard.performFinish();
				if (!supportsTaskSelection) {
					handleSelection(taskSelection);
				}
				return Dialog.OK;
			}

			int result = dialog.open();
			if (result == Dialog.OK) {
				if (wizard instanceof NewTaskWizard) {
					supportsTaskSelection = ((NewTaskWizard) wizard).supportsTaskSelection();
				}
				if (!supportsTaskSelection) {
					handleSelection(taskSelection);
				}
			}
			return result;
		} else {
			return Dialog.CANCEL;
		}
	}

	// API-3.0: remove legacy support
	@SuppressWarnings("deprecation")
	private IWizard createNewTaskWizard(AbstractRepositoryConnectorUi connectorUi, TaskRepository taskRepository,
			TaskSelection taskSelection) {
		IWizard wizard = connectorUi.getNewTaskWizard(taskRepository, taskSelection);
		if (wizard == null) {
			// API-3.0: remove legacy support
			wizard = connectorUi.getNewTaskWizard(taskRepository);
			supportsTaskSelection = false;
		} else {
			supportsTaskSelection = true;
		}
		return wizard;
	}

	// API-3.0: remove method when AbstractRepositoryConnector.getNewTaskWizard(TaskRepository) is removed
	private void handleSelection(final TaskSelection taskSelection) {
		if (taskSelection == null) {
			return;
		}

		// need to defer execution to make sure the task editor has been created by the wizard
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (page == null) {
					return;
				}

				RepositoryTaskData taskData = taskSelection.getTaskData();
				String summary = taskData.getSummary();
				String description = taskData.getDescription();

				if (page.getActiveEditor() instanceof TaskEditor) {
					TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
					if (taskEditor.getActivePageInstance() instanceof AbstractRepositoryTaskEditor) {
						AbstractRepositoryTaskEditor repositoryTaskEditor = (AbstractRepositoryTaskEditor) taskEditor.getActivePageInstance();
						repositoryTaskEditor.setSummaryText(summary);
						repositoryTaskEditor.setDescriptionText(description);
						return;
					}
				}

				Clipboard clipboard = new Clipboard(page.getWorkbenchWindow().getShell().getDisplay());
				clipboard.setContents(new Object[] { summary + "\n" + description },
						new Transfer[] { TextTransfer.getInstance() });

				MessageDialog.openInformation(
						page.getWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG,
						"This connector does not provide a rich task editor for creating tasks.\n\n"
								+ "The error contents have been placed in the clipboard so that you can paste them into the entry form.");
			}
		});
	}

	public void run(IAction action) {
		run();
	}

	public void init(IViewPart view) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if ("skipFirstPage".equals(data)) {
			this.skipRepositoryPage = true;
		}
		if ("local".equals(data)) {
			this.localTask = true;
		}
	}

}
