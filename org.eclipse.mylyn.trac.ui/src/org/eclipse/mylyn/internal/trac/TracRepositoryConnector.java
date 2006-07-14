/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.trac.TracTask.Kind;
import org.eclipse.mylar.internal.trac.TracTask.PriorityLevel;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracClientManager;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.ui.wizard.EditTracQueryWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.NewTracQueryWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnector extends AbstractRepositoryConnector {

	private final static String CLIENT_LABEL = "Trac";

	private List<String> supportedVersions;

	private TracClientManager clientManager;

	@Override
	public boolean canCreateNewTask() {
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey() {
		return true;
	}

	@Override
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws Exception {
		return Collections.emptySet();
	}

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return null;
	}

	@Override
	public String getRepositoryType() {
		return MylarTracPlugin.REPOSITORY_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int i = url.lastIndexOf(ITracClient.TICKET_URL);
		return (i != -1) ? url.substring(0, i) : null;
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new TracRepositorySettingsPage(this);
	}

	@Override
	public List<String> getSupportedVersions() {
		if (supportedVersions == null) {
			supportedVersions = new ArrayList<String>();
			for (Version version : Version.values()) {
				supportedVersions.add(version.toString());
			}
		}
		return supportedVersions;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO Auto-generated method stub
	}

	@Override
	public IWizard getNewQueryWizard(TaskRepository repository, IStructuredSelection selection) {
		return new NewTracQueryWizard(repository);
	}

	@Override
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		if (!(query instanceof TracRepositoryQuery)) {
			return;
		}

		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					query.getRepositoryKind(), query.getRepositoryUrl());
			if (repository == null) {
				return;
			}

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed()) {
				IWizard wizard = new EditTracQueryWizard(repository, query);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Trac Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	@Override
	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
			MultiStatus queryStatus) {
		List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();
		final List<TracTicket> tickets = new ArrayList<TracTicket>();

		String url = query.getRepositoryUrl();
		TaskRepository taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(
				MylarTracPlugin.REPOSITORY_KIND, url);
		ITracClient tracRepository;
		try {
			tracRepository = getClientManager().getRepository(taskRepository);
			if (query instanceof TracRepositoryQuery) {
				tracRepository.search(((TracRepositoryQuery) query).getTracSearch(), tickets);
			}
		} catch (Throwable e) {
			queryStatus.add(new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
					"Could not log in to server: " + query.getRepositoryUrl() + "\n\nCheck network connection.", e));
			return hits;
		}

		for (TracTicket ticket : tickets) {
			String handleIdentifier = AbstractRepositoryTask.getHandle(url, ticket.getId());
			ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handleIdentifier);
			if (!(task instanceof TracTask)) {
				task = createTask(ticket, handleIdentifier);
			}
			updateTaskDetails(url, (TracTask) task, ticket);

			TracQueryHit hit = new TracQueryHit((TracTask) task, query.getRepositoryUrl(), ticket.getId() + "");
			hits.add(hit);
		}
		queryStatus.add(Status.OK_STATUS);
		return hits;
	}

	@Override
	public ITask createTaskFromExistingKey(TaskRepository repository, String id) {
		try {
			// TODO do this in a non-blocking way like
			// BugzillaRepositoryConnector once IOfflineTaskHandler has been
			// implemented
			
			ITracClient connection = getClientManager().getRepository(repository);
			TracTicket ticket = connection.getTicket(Integer.parseInt(id));

			String handleIdentifier = AbstractRepositoryTask.getHandle(repository.getUrl(), ticket.getId());
			TracTask task = createTask(ticket, handleIdentifier);
			updateTaskDetails(repository.getUrl(), task, ticket);

			return task;
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Error creating task from key " + id);
		}
		return null;
	}

	public synchronized TracClientManager getClientManager() {
		if (clientManager == null) {
			clientManager = new TracClientManager();
		}
		return clientManager;
	}

	public static TracTask createTask(TracTicket ticket, String handleIdentifier) {
		TracTask task;
		ITask existingTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handleIdentifier);
		if (existingTask instanceof TracTask) {
			task = (TracTask) existingTask;
		} else {
			task = new TracTask(handleIdentifier, ticket.getValue(Key.SUMMARY), true);
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		}
		return task;
	}

	/**
	 * Updates fields of <code>task</code> from <code>ticket</code>.
	 */
	public static void updateTaskDetails(String repositoryUrl, TracTask task, TracTicket ticket) {
		if (ticket.isValid()) {
			String url = repositoryUrl + ITracClient.TICKET_URL + ticket.getId();
			task.setUrl(url);
			if (ticket.getValue(Key.SUMMARY) != null) {
				task.setDescription(ticket.getId() + ": " + ticket.getValue(Key.SUMMARY));
			}
		}
		if (ticket.getValue(Key.STATUS) != null) {
			TracTask.Status status = TracTask.Status.fromStatus(ticket.getValue(Key.STATUS));
			task.setCompleted(status != null && status == TracTask.Status.CLOSED);
		} else {
			task.setCompleted(false);
		}
		if (ticket.getValue(Key.PRIORITY) != null) {
			PriorityLevel priority = TracTask.PriorityLevel.fromPriority(ticket.getValue(Key.PRIORITY));
			task.setPriority((priority != null) ? priority.toString() : ticket.getValue(Key.PRIORITY));
		}
		if (ticket.getValue(Key.TYPE) != null) {
			Kind kind = TracTask.Kind.fromType(ticket.getValue(Key.TYPE));
			task.setKind((kind != null) ? kind.toString() : ticket.getValue(Key.TYPE));
		}
		if (ticket.getCreated() != null) {
			task.setCreationDate(ticket.getCreated());
		}

		TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
	}

}
