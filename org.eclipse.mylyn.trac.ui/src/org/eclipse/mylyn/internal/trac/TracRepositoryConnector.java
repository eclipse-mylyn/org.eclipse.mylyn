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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewWebTaskWizard;
import org.eclipse.mylar.internal.trac.TracTask.Kind;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracClientManager;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.ui.wizard.EditTracQueryWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.NewTracQueryWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.TracCustomQueryPage;
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

	private final static String CLIENT_LABEL = "Trac (supports 0.9 and later or XML-RPC)";

	private List<String> supportedVersions;

	private TracClientManager clientManager;

	private TracOfflineTaskHandler offlineTaskHandler = new TracOfflineTaskHandler(this);

	private TracAttachmentHandler attachmentHandler = new TracAttachmentHandler(this);

	public TracRepositoryConnector() {
		TracUiPlugin.getDefault().setConnector(this);
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return new NewWebTaskWizard(taskRepository, taskRepository.getUrl() + ITracClient.NEW_TICKET_URL);
	}

	@Override
	public String getRepositoryType() {
		return TracUiPlugin.REPOSITORY_KIND;
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
		return attachmentHandler;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		return offlineTaskHandler;
	}

	@Override
	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO Auto-generated method stub
	}

	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (query instanceof TracRepositoryQuery) {
			return new EditTracQueryWizard(repository, query);
		}
		return null;
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
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
			if (repository == null) {
				return;
			}

			IWizard wizard = getQueryWizard(repository, query);
			if (wizard == null) {
				return;
			}

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed()) {
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
				TracUiPlugin.REPOSITORY_KIND, url);
		ITracClient tracRepository;
		try {
			tracRepository = getClientManager().getRepository(taskRepository);
			if (query instanceof TracRepositoryQuery) {
				tracRepository.search(((TracRepositoryQuery) query).getTracSearch(), tickets);
			}
		} catch (Throwable e) {
			queryStatus.add(TracUiPlugin.toStatus(e));
			return hits;
		}

		for (TracTicket ticket : tickets) {
			TracQueryHit hit = new TracQueryHit(query.getRepositoryUrl(), getTicketDescription(ticket), ticket.getId()
					+ "");
			hit.setCompleted(TracTask.isCompleted(ticket.getValue(Key.STATUS)));
			hit.setPriority(TracTask.getMylarPriority(ticket.getValue(Key.PRIORITY)));
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
			updateTaskDetails(task, ticket, true);

			return task;
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Error creating task from key " + id);
		}
		return null;
	}

	public synchronized TracClientManager getClientManager() {
		if (clientManager == null) {
			File cacheFile = null;
			if (TracUiPlugin.getDefault().getRepostioryAttributeCachePath() != null) {
				cacheFile = TracUiPlugin.getDefault().getRepostioryAttributeCachePath().toFile();
			}
			clientManager = new TracClientManager(cacheFile);
		}
		return clientManager;
	}

	public static TracTask createTask(TracTicket ticket, String handleIdentifier) {
		TracTask task;
		ITask existingTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handleIdentifier);
		if (existingTask instanceof TracTask) {
			task = (TracTask) existingTask;
		} else {
			task = new TracTask(handleIdentifier, getTicketDescription(ticket), true);
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		}
		return task;
	}

	/**
	 * Updates fields of <code>task</code> from <code>ticket</code>.
	 */
	public static void updateTaskDetails(TracTask task, TracTicket ticket, boolean notify) {
		if (ticket.getValue(Key.SUMMARY) != null) {
			task.setDescription(getTicketDescription(ticket));
		}
		task.setCompleted(TracTask.isCompleted(ticket.getValue(Key.STATUS)));
		task.setPriority(TracTask.getMylarPriority(ticket.getValue(Key.PRIORITY)));
		if (ticket.getValue(Key.TYPE) != null) {
			Kind kind = TracTask.Kind.fromType(ticket.getValue(Key.TYPE));
			task.setKind((kind != null) ? kind.toString() : ticket.getValue(Key.TYPE));
		}
		if (ticket.getCreated() != null) {
			task.setCreationDate(ticket.getCreated());
		}

		if (notify) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
		}
	}

	private static String getTicketDescription(TracTicket ticket) {
		return ticket.getId() + ": " + ticket.getValue(Key.SUMMARY);
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) {
		try {
			ITracClient client = getClientManager().getRepository(repository);
			client.updateAttributes(monitor, true);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not update attributes", false);
		}
	}

	@Override
	public boolean hasRichEditor() {
		return true;
	}

	@Override
	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new TracCustomQueryPage(repository);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	public boolean hasRichEditor(AbstractRepositoryTask task) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracUiPlugin.REPOSITORY_KIND,
				task.getRepositoryUrl());
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public boolean hasAttachmentSupport(AbstractRepositoryTask task) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracUiPlugin.REPOSITORY_KIND,
				task.getRepositoryUrl());
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public void stop() {
		if (clientManager != null) {
			clientManager.writeCache();
		}
	}

	@Override
	public boolean validate(TaskRepository repository) {
		return true;
	}

	public static String getDisplayUsername(TaskRepository repository) {
		if (!repository.hasCredentials()) {
			return ITracClient.DEFAULT_USERNAME;
		}
		return repository.getUserName();
	}

}