/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Steffen Pingel
 */
public class NewSubTaskAction extends BaseSelectionListenerAction implements IViewActionDelegate, IExecutableExtension {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.new.subtask"; //$NON-NLS-1$

	private AbstractTask selectedTask;

	public NewSubTaskAction() {
		super(Messages.NewSubTaskAction_Subtask);
		setToolTipText(Messages.NewSubTaskAction_Create_a_new_subtask);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_NEW_SUB);
	}

	@Override
	public void run() {
		if (selectedTask == null) {
			return;
		}

		if (selectedTask instanceof LocalTask) {
			TaskList taskList = TasksUiPlugin.getTaskList();
			LocalTask newTask = TasksUiInternal.createNewLocalTask(null);
			taskList.addTask(newTask, selectedTask);
			TasksUiUtil.openTask(newTask);
			return;
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(selectedTask.getConnectorKind());
		IWizard wizard = getNewSubTaskWizard();
		if (wizard != null) {
			WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
			dialog.setBlockOnOpen(true);
			dialog.open();
			return;
		}
		TaskData taskData = createTaskData(connector);
		if (taskData != null) {
			try {
				TasksUiInternal.createAndOpenNewTask(taskData);
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to open new sub task", e)); //$NON-NLS-1$
				TasksUiInternal.displayStatus(Messages.NewSubTaskAction_Unable_to_create_subtask,
						new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								Messages.NewSubTaskAction_Failed_to_create_new_sub_task_ + e.getMessage()));
			}
		}
	}

	private IWizard getNewSubTaskWizard() {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(selectedTask.getConnectorKind(),
				selectedTask.getRepositoryUrl());
		AbstractRepositoryConnectorUi connectorUi = TasksUi.getRepositoryConnectorUi(selectedTask.getConnectorKind());
		return connectorUi.getNewSubTaskWizard(repository, selectedTask);
	}

	private TaskData createTaskData(AbstractRepositoryConnector connector) {
		final AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		if (taskDataHandler == null) {
			return null;
		}

		String repositoryUrl = selectedTask.getRepositoryUrl();
		TaskData parentTaskData = null;
		try {
			parentTaskData = TasksUi.getTaskDataManager().getTaskData(selectedTask);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not retrieve task data for task:" + selectedTask.getUrl(), e)); //$NON-NLS-1$
		}
		if (parentTaskData == null) {
			TasksUiInternal.displayStatus(Messages.NewSubTaskAction_Unable_to_create_subtask,
					new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
							Messages.NewSubTaskAction_Could_not_retrieve_task_data_for_task_ + selectedTask.getUrl()));
			return null;
		}

		final TaskRepository taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl);
		if (!taskDataHandler.canInitializeSubTaskData(taskRepository, selectedTask)) {
			return null;
		}

		final TaskData selectedTaskData = parentTaskData;
		final TaskAttributeMapper attributeMapper = taskDataHandler.getAttributeMapper(taskRepository);
		final TaskData taskData = new TaskData(attributeMapper, taskRepository.getConnectorKind(),
				taskRepository.getRepositoryUrl(), ""); //$NON-NLS-1$
		final boolean[] result = new boolean[1];
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			CommonUiUtil.run(service, new ICoreRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					result[0] = taskDataHandler.initializeSubTaskData(taskRepository, taskData, selectedTaskData,
							monitor);
				}
			});
		} catch (CoreException e) {
			TasksUiInternal.displayStatus(Messages.NewSubTaskAction_Unable_to_create_subtask, e.getStatus());
			return null;
		} catch (OperationCanceledException e) {
			// canceled
			return null;
		}

		if (result[0]) {
			// open editor
			return taskData;
		} else {
			TasksUiInternal.displayStatus(Messages.NewSubTaskAction_Unable_to_create_subtask,
					new Status(IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
							Messages.NewSubTaskAction_The_connector_does_not_support_creating_subtasks_for_this_task));
		}
		return null;
	}

	public void run(IAction action) {
		run();
	}

	public void init(IViewPart view) {
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		selectedTask = null;
		if (selection.size() == 1) {
			Object selectedObject = selection.getFirstElement();
			if (selectedObject instanceof LocalTask) {
				selectedTask = (AbstractTask) selectedObject;
			} else if (selectedObject instanceof ITask) {
				selectedTask = (AbstractTask) selectedObject;
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
						.getRepositoryConnector(selectedTask.getConnectorKind());
				AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();

				TaskRepository repository = TasksUiPlugin.getRepositoryManager()
						.getRepository(selectedTask.getRepositoryUrl());
				if (taskDataHandler == null || !taskDataHandler.canInitializeSubTaskData(repository, selectedTask)) {
					selectedTask = null;
				}
			}
		}
		return selectedTask != null;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			selectionChanged((IStructuredSelection) selection);
		} else {
			setEnabled(false);
		}
		action.setEnabled(isEnabled());
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
	}

}
