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
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracClientManager;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.ui.wizard.AddExistingTracTaskWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IAttachmentHandler;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnector extends AbstractRepositoryConnector {

	private final static String CLIENT_LABEL = "Trac (supports 0.9.0 and later)";

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
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
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
	public Wizard getAddExistingTaskWizard(TaskRepository repository) {
		return new AddExistingTracTaskWizard(repository);
	}

	@Override
	public IWizard getNewQueryWizard(TaskRepository repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
			MultiStatus queryStatus) {
		// TODO Auto-generated method stub
		return null;
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
		ITask existingTask = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handleIdentifier);
		if (existingTask instanceof TracTask) {
			task = (TracTask) existingTask;
		} else {
			task = new TracTask(handleIdentifier, ticket.getValue(Key.SUMMARY), true);
			MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task);
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
				task.setDescription("#" + ticket.getId() + ": " + ticket.getValue(Key.SUMMARY));
			}
		}
		if (ticket.getValue(Key.STATUS) != null) {
			TracTask.Status status = TracTask.Status.fromStatus(ticket.getValue(Key.STATUS));
			task.setCompleted(status != null && status == TracTask.Status.CLOSED);
		} else {
			task.setCompleted(false);
		}
		if (ticket.getValue(Key.PRIORITY) != null) {
			String translatedPriority = TracTask.PriorityLevel.fromPriority(ticket.getValue(Key.PRIORITY)).toString();
			task.setPriority(translatedPriority);
		}
		if (ticket.getValue(Key.TYPE) != null) {
			String translatedKind = TracTask.Kind.fromType(ticket.getValue(Key.TYPE)).toString();
			task.setKind(translatedKind);
		}
		if (ticket.getCreated() != null) {
			task.setCreationDate(ticket.getCreated());
		}

		MylarTaskListPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
	}

}
