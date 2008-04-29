/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Extend for customizing how new tasks editors are created.
 * 
 * @author Steffen Pingel
 * @since 2.0
 */
public class NewTaskWizard extends Wizard implements INewWizard {

	private final TaskRepository taskRepository;

	private TaskSelection taskSelection;

	public NewTaskWizard(TaskRepository taskRepository, TaskSelection taskSelection) {
		this.taskRepository = taskRepository;
		this.taskSelection = taskSelection;
	}

	public NewTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());

		final AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		if (taskDataHandler == null) {
			StatusHandler.displayStatus("Error creating new task", new RepositoryStatus(IStatus.ERROR,
					TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY,
					"The selected repository does not support creating new tasks."));
			return false;
		}

		AbstractAttributeFactory attributeFactory = taskDataHandler.getAttributeFactory(taskRepository.getRepositoryUrl(),
				taskRepository.getConnectorKind(), AbstractTask.DEFAULT_TASK_KIND);

		final RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, taskRepository.getConnectorKind(),
				taskRepository.getRepositoryUrl(), TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
		taskData.setNew(true);

		try {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						if (!taskDataHandler.initializeTaskData(taskRepository, taskData, monitor)) {
							throw new CoreException(new RepositoryStatus(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
									RepositoryStatus.ERROR_REPOSITORY,
									"The selected repository does not support creating new tasks."));
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};

			getContainer().run(true, true, runnable);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				StatusHandler.displayStatus("Error creating new task", ((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error creating new task",
						e.getCause()));
			}
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		if (taskSelection != null) {
			taskDataHandler.cloneTaskData(taskSelection.getTaskData(), taskData);
		}

		NewTaskEditorInput editorInput = new NewTaskEditorInput(taskRepository, taskData);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
		return true;
	}
}
