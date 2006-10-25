/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.InvalidTicketException;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Steffen Pingel
 */
public class NewTracTaskEditor extends AbstractNewRepositoryTaskEditor {

	private static final String SUBMIT_JOB_LABEL = "Submitting to Trac repository";

	public NewTracTaskEditor(FormEditor editor) {
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

		submitJob.addJobChangeListener(submitJobListener);
		submitJob.schedule();
	}

	@Override
	public SearchHitCollector getDuplicateSearchCollector(String searchString) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	protected void handleErrorStatus(final IJobChangeEvent event) {
		super.handleErrorStatus(event);
		TracUiPlugin.handleTracException(event.getJob().getResult());		
	}
}
