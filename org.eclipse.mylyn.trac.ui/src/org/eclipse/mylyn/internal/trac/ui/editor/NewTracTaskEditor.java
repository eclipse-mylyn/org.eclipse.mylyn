/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import java.util.Collections;

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
import org.eclipse.mylar.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;
import org.eclipse.mylar.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylar.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
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
	public void submitToRepository() {
		if (!prepareSubmit()) {
			return;
		}

		final TracRepositoryConnector connector = (TracRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(repository.getKind());

		updateTask();

		final TracTicket ticket;
		try {
			ticket = TracRepositoryConnector.getTracTicket(repository, getRepositoryTaskData());
		} catch (InvalidTicketException e) {
			TracUiPlugin.handleTracException(e);
			submitButton.setEnabled(true);
			showBusy(false);
			return;
		}

		final AbstractTaskContainer category = getCategory();
		
		Job submitJob = new Job(SUBMIT_JOB_LABEL) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ITracClient server = connector.getClientManager().getRepository(repository);
					int id = server.createTicket(ticket);

					TracTask newTask = new TracTask(AbstractRepositoryTask.getHandle(repository.getUrl(), id),
							TracRepositoryConnector.getTicketDescription(ticket), true);
					
					if (category != null) {
						TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, category);
					} else {
						TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask);
					}

					TasksUiPlugin.getSynchronizationScheduler().synchNow(0, Collections.singletonList(repository));

					return Status.OK_STATUS;
				} catch (Exception e) {
					return TracCorePlugin.toStatus(e);
				}
			}
		};

		submitJob.addJobChangeListener(getSubmitJobListener());
		submitJob.schedule();
	}

	@Override
	public SearchHitCollector getDuplicateSearchCollector(String searchString) {
		TracSearchFilter filter = new TracSearchFilter("description");
		filter.setOperator(CompareOperator.CONTAINS);
		filter.addValue(searchString);

		TracSearch search = new TracSearch();
		search.addFilter(filter);

		// TODO copied from TracCustomQueryPage.getQueryUrl()
		StringBuilder sb = new StringBuilder();
		sb.append(repository.getUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());

		TracRepositoryQuery query = new TracRepositoryQuery(repository.getUrl(), sb.toString(), "<Duplicate Search>",
				TasksUiPlugin.getTaskListManager().getTaskList());

		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, query);
		return collector;
	}

	@Override
	protected void handleSubmitError(final IJobChangeEvent event) {
		super.handleSubmitError(event);
		TracUiPlugin.handleTracException(event.getJob().getResult());
	}

	@Override
	protected String getPluginId() {
		return TracUiPlugin.PLUGIN_ID;
	}

}
