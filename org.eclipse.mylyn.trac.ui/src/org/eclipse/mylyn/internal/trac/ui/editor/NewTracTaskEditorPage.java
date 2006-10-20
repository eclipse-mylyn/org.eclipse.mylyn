/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.InvalidTicketException;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Steffen Pingel
 */
public class NewTracTaskEditorPage extends AbstractNewRepositoryTaskEditor {

	private static final String SUBMIT_JOB_LABEL = "Submitting to Trac repository";

	public NewTracTaskEditorPage(FormEditor editor) {
		super(editor);
	}

	@Override
	protected void submitBug() {
		if (!prepareSubmit()) {
			return;
		}
		
		final TracRepositoryConnector connector = (TracRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());
		
		updateBug();

		JobChangeAdapter listener = new JobChangeAdapter() {
			public void done(final IJobChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (event.getJob().getResult().getCode() == Status.OK
								&& event.getJob().getResult().getMessage() != null) {
							close();
							String newTaskHandle = AbstractRepositoryTask.getHandle(repository.getUrl(), event
									.getJob().getResult().getMessage());
							ITask newTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(newTaskHandle);								
							if (newTask != null) {									
								Calendar selectedDate = datePicker.getDate();
								if(selectedDate != null) {
									//NewLocalTaskAction.scheduleNewTask(newTask);									
									TasksUiPlugin.getTaskListManager().setScheduledFor(newTask, selectedDate.getTime());											
								}
								
								newTask.setEstimatedTimeHours(estimated.getSelection());
								
								Object selectedObject = null;
								if (TaskListView.getFromActivePerspective() != null)
									selectedObject = ((IStructuredSelection) TaskListView
											.getFromActivePerspective().getViewer().getSelection())
											.getFirstElement();

								if (selectedObject instanceof TaskCategory) {
									TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(
											((TaskCategory) selectedObject), newTask);
								}
								TaskUiUtil.refreshAndOpenTaskListElement(newTask);
							}
							return;
						} else if (event.getJob().getResult().getCode() == Status.ERROR) {
							TracUiPlugin.handleTracException(event.getJob().getResult());
							submitButton.setEnabled(true);
							showBusy(false);
						}
					}
				});
			}
		};

		final TracTicket ticket;
		try {
			ticket = TracRepositoryConnector.getTracTicket(repository, getRepositoryTaskData());
		} catch (InvalidTicketException e) {
			TracUiPlugin.handleTracException(e);
			submitButton.setEnabled(true);
			showBusy(false);
			return;
		}
		
		final boolean addToRoot = addToTaskListRoot.getSelection();

		Job submitJob = new Job(SUBMIT_JOB_LABEL) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ITracClient server = connector.getClientManager().getRepository(repository);
					int id = server.createTicket(ticket);

					TracTask newTask = new TracTask(AbstractRepositoryTask.getHandle(repository.getUrl(), id),
							TracRepositoryConnector.getTicketDescription(ticket), true);
					if (addToRoot) {
						TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
								TasksUiPlugin.getTaskListManager().getTaskList().getRootCategory());
					} else {
						TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask);
					}

					java.util.List<TaskRepository> repositoriesToSync = new ArrayList<TaskRepository>();
					repositoriesToSync.add(repository);
					TasksUiPlugin.getSynchronizationScheduler().synchNow(0, repositoriesToSync);

					return Status.OK_STATUS;
				} catch (Exception e) {
					return TracCorePlugin.toStatus(e);
				}
			}
		};

		submitJob.addJobChangeListener(listener);
		submitJob.schedule();
	}

}
