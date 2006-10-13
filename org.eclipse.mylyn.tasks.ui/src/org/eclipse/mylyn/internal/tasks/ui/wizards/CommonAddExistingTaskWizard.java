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

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Brock Janiczak
 * @author Mik Kersten
 */
public class CommonAddExistingTaskWizard extends Wizard {

	private final TaskRepository repository;

	private ExistingTaskWizardPage page;

	public CommonAddExistingTaskWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(AddExistingTaskWizard.TITLE);
		init();
	}

	/**
	 * Retrieves an existing repository task and adds it to the tasklist
	 * 
	 * @author wmitsuda
	 */
	private class AddExistingTaskJob extends Job {

		private String taskId;

		public AddExistingTaskJob(String taskId) {
			super(MessageFormat.format("Adding task: \"{0}\"...", taskId));
			this.taskId = taskId;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getKind());
			try {
				monitor.beginTask("Retrieving task...", IProgressMonitor.UNKNOWN);
				final ITask newTask = connector.createTaskFromExistingKey(repository, taskId, null);
				if (newTask instanceof AbstractRepositoryTask) {
					TasksUiPlugin.getSynchronizationManager().synchronize(connector, (AbstractRepositoryTask) newTask,
							true, null);
				}
				if (newTask != null) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							TaskListView taskListView = TaskListView.getFromActivePerspective();
							Object selectedObject = ((IStructuredSelection) taskListView.getViewer().getSelection())
									.getFirstElement();

							if (selectedObject instanceof TaskCategory) {
								TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(
										((TaskCategory) selectedObject), newTask);
							} else {
								TasksUiPlugin.getTaskListManager().getTaskList().moveToRoot(newTask);
							}
							taskListView.getViewer().setSelection(new StructuredSelection(newTask));
							TaskUiUtil.openEditor(newTask, false, false);
						}

					});
				} else {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
							if (window != null) {
								MessageDialog.openWarning(window.getShell(), "Add Existing Task Failed", MessageFormat
										.format("Unable to retrieve task \"{0}\" from repository.", taskId));
							}
						}

					});
				}
			} catch (final CoreException e) {
				MylarStatusHandler.fail(e.getStatus().getException(), e.getMessage(), true);
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	public final boolean performFinish() {
		final IProgressService svc = PlatformUI.getWorkbench().getProgressService();
		final AddExistingTaskJob job = new AddExistingTaskJob(getTaskId());
		job.schedule();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				svc.showInDialog(getShell(), job);
			}

		});
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
