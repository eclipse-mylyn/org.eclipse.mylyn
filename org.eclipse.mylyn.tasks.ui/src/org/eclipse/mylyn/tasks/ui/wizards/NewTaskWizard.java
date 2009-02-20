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

package org.eclipse.mylyn.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Extend for customizing how new tasks editors are created.
 * 
 * @author Steffen Pingel
 * @since 2.0
 */
public class NewTaskWizard extends Wizard implements INewWizard {

	private final TaskRepository taskRepository;

	private ITaskMapping taskSelection;

	/**
	 * @since 3.0
	 */
	public NewTaskWizard(TaskRepository taskRepository, ITaskMapping taskSelection) {
		Assert.isNotNull(taskRepository);
		this.taskRepository = taskRepository;
		this.taskSelection = taskSelection;
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		setNeedsProgressMonitor(true);
	}

	@Deprecated
	public NewTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
	}

	/**
	 * @since 3.0
	 */
	protected ITaskMapping getInitializationData() {
		return null;
	}

	/**
	 * @since 3.0
	 */
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	/**
	 * @since 3.0
	 */
	public ITaskMapping getTaskSelection() {
		return taskSelection;
	}

	@Override
	public boolean performFinish() {
		final TaskData[] taskData = new TaskData[1];
		final ITaskMapping initializationData = getInitializationData();
		final ITaskMapping selectionData = getTaskSelection();
		try {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						taskData[0] = TasksUiInternal.createTaskData(taskRepository, initializationData, selectionData,
								monitor);
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			if (getContainer().getShell().isVisible()) {
				getContainer().run(true, true, runnable);
			} else {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
			}
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				TasksUiInternal.displayStatus(Messages.NewTaskWizard_Error_creating_new_task,
						((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						Messages.NewTaskWizard_Error_creating_new_task, e.getCause()));
			}
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		try {
			TasksUiInternal.createAndOpenNewTask(taskData[0]);
			return true;
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to open new task", e)); //$NON-NLS-1$
			TasksUiInternal.displayStatus(Messages.NewTaskWizard_Create_Task, new Status(IStatus.ERROR,
					TasksUiPlugin.ID_PLUGIN, Messages.NewTaskWizard_Failed_to_create_new_task_ + e.getMessage()));
			return false;
		}
	}

}
