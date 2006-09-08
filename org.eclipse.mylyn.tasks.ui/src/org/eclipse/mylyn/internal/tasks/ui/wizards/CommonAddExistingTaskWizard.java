/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbench;

/**
 * @author Brock Janiczak
 * @author Mik Kersten
 */
public class CommonAddExistingTaskWizard extends Wizard {

	private final TaskRepository repository;

	private ExistingTaskWizardPage page;

	private ITask newTask = null;

	public CommonAddExistingTaskWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(AddExistingTaskWizard.TITLE);
		init();
	}

	@Override
	public final boolean performFinish() {
		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				this.repository.getKind());

		final String taskId = getTaskId();

		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Retrieving task...", IProgressMonitor.UNKNOWN);
					try {
						newTask = connector.createTaskFromExistingKey(repository, taskId, null);
						if (newTask instanceof AbstractRepositoryTask) {
							TasksUiPlugin.getSynchronizationManager().synchronize(connector, (AbstractRepositoryTask)newTask, true, null);
						}
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			String message = e.getCause() != null ? e.getCause().getMessage() : "None provided";
			MessageDialog.openWarning(this.getShell(), "Add Existing Task Failed",
					"Unable to retrieve existing task from repository, error was: \n\n" + message);
			return false;
		} catch (InterruptedException e) {
			// cancelled
			return true;
		}

		if (newTask != null && TaskListView.getFromActivePerspective() != null) {
			Object selectedObject = ((IStructuredSelection) TaskListView.getFromActivePerspective().getViewer()
					.getSelection()).getFirstElement();

			if (selectedObject instanceof TaskCategory) {
				TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(((TaskCategory) selectedObject),
						newTask);
			} else {
				TasksUiPlugin.getTaskListManager().getTaskList().moveToRoot(newTask);
			}
			if (TaskListView.getFromActivePerspective() != null) {
				TaskListView.getFromActivePerspective().getViewer().setSelection(new StructuredSelection(newTask));
			}
			TaskUiUtil.openEditor(newTask, false);
		} else {
			// TODO: createTaskFromExistingKey needs to throw exceptions so that
			// we can provide the correct error handling in
			// the try catch above.
			MessageDialog.openWarning(this.getShell(), "Add Existing Task Failed",
					"Unable to retrieve task from repository.");
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		super.setForcePreviousAndNextButtons(true);
	}

	public void addPages() {
		super.addPages();
		this.page = new ExistingTaskWizardPage();
		addPage(page);
	}

	protected String getTaskId() {
		return page.getTaskId();
	}
}
