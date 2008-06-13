/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.util.TracUtils;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnector extends AbstractRepositoryConnector {

	public enum TaskKind {
		DEFECT, ENHANCEMENT, TASK;

		public static TaskKind fromString(String type) {
			if (type == null) {
				return null;
			}
			if (type.equals("Defect")) {
				return DEFECT;
			}
			if (type.equals("Enhancement")) {
				return ENHANCEMENT;
			}
			if (type.equals("Task")) {
				return TASK;
			}
			return null;
		}

		public static TaskKind fromType(String type) {
			if (type == null) {
				return null;
			}
			if (type.equals("defect")) {
				return DEFECT;
			}
			if (type.equals("enhancement")) {
				return ENHANCEMENT;
			}
			if (type.equals("task")) {
				return TASK;
			}
			return null;
		}

		@Override
		public String toString() {
			switch (this) {
			case DEFECT:
				return "Defect";
			case ENHANCEMENT:
				return "Enhancement";
			case TASK:
				return "Task";
			default:
				return "";
			}
		}

	}

	public enum TaskStatus {
		ASSIGNED, CLOSED, NEW, REOPENED;

		public static TaskStatus fromStatus(String status) {
			if (status == null) {
				return null;
			}
			if (status.equals("new")) {
				return NEW;
			}
			if (status.equals("assigned")) {
				return ASSIGNED;
			}
			if (status.equals("reopened")) {
				return REOPENED;
			}
			if (status.equals("closed")) {
				return CLOSED;
			}
			return null;
		}

		public String toStatusString() {
			switch (this) {
			case NEW:
				return "new";
			case ASSIGNED:
				return "assigned";
			case REOPENED:
				return "reopened";
			case CLOSED:
				return "closed";
			default:
				return "";
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case NEW:
				return "New";
			case ASSIGNED:
				return "Assigned";
			case REOPENED:
				return "Reopened";
			case CLOSED:
				return "Closed";
			default:
				return "";
			}
		}

	}

	public enum TracPriorityLevel {
		BLOCKER, CRITICAL, MAJOR, MINOR, TRIVIAL;

		public static TracPriorityLevel fromPriority(String priority) {
			if (priority == null) {
				return null;
			}
			if (priority.equals("blocker")) {
				return BLOCKER;
			}
			if (priority.equals("critical")) {
				return CRITICAL;
			}
			if (priority.equals("major")) {
				return MAJOR;
			}
			if (priority.equals("minor")) {
				return MINOR;
			}
			if (priority.equals("trivial")) {
				return TRIVIAL;
			}
			return null;
		}

		public PriorityLevel toPriorityLevel() {
			switch (this) {
			case BLOCKER:
				return PriorityLevel.P1;
			case CRITICAL:
				return PriorityLevel.P2;
			case MAJOR:
				return PriorityLevel.P3;
			case MINOR:
				return PriorityLevel.P4;
			case TRIVIAL:
				return PriorityLevel.P5;
			default:
				return null;
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case BLOCKER:
				return "blocker";
			case CRITICAL:
				return "critical";
			case MAJOR:
				return "major";
			case MINOR:
				return "minor";
			case TRIVIAL:
				return "trivial";
			default:
				return null;
			}
		}
	}

	private final static String CLIENT_LABEL = "Trac (supports 0.9 or 0.10 through Web and XML-RPC)";

	private static int TASK_PRIORITY_LEVELS = 5;

	public static final String TASK_KEY_SUPPORTS_SUBTASKS = "SupportsSubtasks";

	public static String getDisplayUsername(TaskRepository repository) {
		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null && credentials.getUserName().length() > 0) {
			return ITracClient.DEFAULT_USERNAME;
		}
		return repository.getUserName();
	}

	public static PriorityLevel getTaskPriority(String tracPriority) {
		if (tracPriority != null) {
			TracPriorityLevel priority = TracPriorityLevel.fromPriority(tracPriority);
			if (priority != null) {
				return priority.toPriorityLevel();
			}
		}
		return PriorityLevel.getDefault();
	}

	public static PriorityLevel getTaskPriority(String priority, TracPriority[] tracPriorities) {
		if (priority != null && tracPriorities != null && tracPriorities.length > 0) {
			int minValue = tracPriorities[0].getValue();
			int range = tracPriorities[tracPriorities.length - 1].getValue() - minValue;
			for (TracPriority tracPriority : tracPriorities) {
				if (priority.equals(tracPriority.getName())) {
					float relativeValue = (float) (tracPriority.getValue() - minValue) / range;
					int value = (int) (relativeValue * TASK_PRIORITY_LEVELS) + 1;
					return PriorityLevel.fromLevel(value);
				}
			}
		}
		return getTaskPriority(priority);
	}

	public static int getTicketId(String taskId) throws CoreException {
		try {
			return Integer.parseInt(taskId);
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.ID_PLUGIN, IStatus.OK,
					"Invalid ticket id: " + taskId, e));
		}
	}

	static List<String> getAttributeValues(TaskData data, String attributeId) {
		TaskAttribute attribute = data.getRoot().getMappedAttribute(attributeId);
		if (attribute != null) {
			return attribute.getValues();
		} else {
			return Collections.emptyList();
		}
	}

	static String getAttributeValue(TaskData data, String attributeId) {
		TaskAttribute attribute = data.getRoot().getMappedAttribute(attributeId);
		if (attribute != null) {
			return attribute.getValue();
		} else {
			return "";
		}
	}

	public static boolean hasAttachmentSupport(TaskRepository repository, ITask task) {
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public static boolean hasChangedSince(TaskRepository repository) {
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public static boolean hasRichEditor(TaskRepository repository) {
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public static boolean hasRichEditor(TaskRepository repository, ITask task) {
		return hasRichEditor(repository);
	}

	public static boolean isCompleted(String tracStatus) {
		TaskStatus taskStatus = TaskStatus.fromStatus(tracStatus);
		return taskStatus == TaskStatus.CLOSED;
	}

	private final TracAttachmentHandler attachmentHandler = new TracAttachmentHandler(this);

	private TracClientManager clientManager;

	private File repositoryConfigurationCacheFile;

	private final TracTaskDataHandler taskDataHandler = new TracTaskDataHandler(this);

	private TaskRepositoryLocationFactory taskRepositoryLocationFactory = new TaskRepositoryLocationFactory();

	private final TracWikiHandler wikiHandler = new TracWikiHandler(this);

	public TracRepositoryConnector() {
		if (TracCorePlugin.getDefault() != null) {
			TracCorePlugin.getDefault().setConnector(this);
			IPath path = TracCorePlugin.getDefault().getRepostioryAttributeCachePath();
			repositoryConfigurationCacheFile = path.toFile();
		}
	}

	public TracRepositoryConnector(File repositoryConfigurationCacheFile) {
		this.repositoryConfigurationCacheFile = repositoryConfigurationCacheFile;

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
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}

	public synchronized TracClientManager getClientManager() {
		if (clientManager == null) {
			clientManager = new TracClientManager(repositoryConfigurationCacheFile, taskRepositoryLocationFactory);
		}
		return clientManager;
	}

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return taskDataHandler.getTaskData(repository, taskId, monitor);
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(ITracClient.TICKET_URL);
		return index == -1 ? null : url.substring(0, index);
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(ITracClient.TICKET_URL);
		return index == -1 ? null : url.substring(index + ITracClient.TICKET_URL.length());
	}

	@Override
	public String getTaskIdPrefix() {
		return "#";
	}

	public TaskRepositoryLocationFactory getTaskRepositoryLocationFactory() {
		return taskRepositoryLocationFactory;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + ITracClient.TICKET_URL + taskId;
	}

	public AbstractWikiHandler getWikiHandler() {
		return wikiHandler;
	}

	public boolean hasWiki(TaskRepository repository) {
		// check the access mode to validate Wiki support
		ITracClient client = getClientManager().getTracClient(repository);
		if (client instanceof ITracWikiClient) {
			return true;
		}
		return false;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationSession event, IProgressMonitor monitor) {
		try {
			monitor.beginTask("Querying repository", IProgressMonitor.UNKNOWN);

			TracSearch search = TracUtils.toTracSearch(query);
			if (search == null) {
				return new RepositoryStatus(repository.getRepositoryUrl(), IStatus.ERROR, TracCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY, "The query is invalid: \"" + query.getUrl() + "\"");
			}

			ITracClient client;
			try {
				client = getClientManager().getTracClient(repository);
				final List<TracTicket> tickets = new ArrayList<TracTicket>();
				client.search(search, tickets, monitor);

				client.updateAttributes(monitor, false);
				for (TracTicket ticket : tickets) {
					TaskData taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, monitor);
					resultCollector.accept(taskData);
				}
			} catch (Throwable e) {
				return TracCorePlugin.toStatus(e, repository);
			}

			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	@Override
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
			if (event.isFullSynchronization()) {
				Date date = getSynchronizationTimestamp(event);
				if (date != null) {
					event.getTaskRepository().setSynchronizationTimeStamp(TracUtils.toTracTime(date) + "");
				}
			}
		} finally {
			monitor.done();
		}
	}

	private Date getSynchronizationTimestamp(ISynchronizationSession event) {
		Date mostRecent = new Date(0);
		Date mostRecentTimeStamp = TracUtils.parseDate(event.getTaskRepository().getSynchronizationTimeStamp());
		for (ITask task : event.getChangedTasks()) {
			Date taskModifiedDate = task.getModificationDate();
			if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
				mostRecent = taskModifiedDate;
				mostRecentTimeStamp = task.getModificationDate();
			}
		}
		return mostRecentTimeStamp;
	}

	@Override
	public void preSynchronization(ISynchronizationSession session, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Getting changed tasks", IProgressMonitor.UNKNOWN);

			if (!session.isFullSynchronization()) {
				return;
			}

			// there are no Trac tasks in the task list, skip contacting the repository
			if (session.getTasks().isEmpty()) {
				return;
			}

			TaskRepository repository = session.getTaskRepository();
			if (!TracRepositoryConnector.hasChangedSince(repository)) {
				// always run the queries for web mode
				return;
			}

			if (repository.getSynchronizationTimeStamp() == null
					|| repository.getSynchronizationTimeStamp().length() == 0) {
				for (ITask task : session.getTasks()) {
					session.markStale(task);
				}
				return;
			}

			Date since = new Date(0);
			try {
				since = TracUtils.parseDate(Integer.parseInt(repository.getSynchronizationTimeStamp()));
			} catch (NumberFormatException e) {
			}

			try {
				ITracClient client = getClientManager().getTracClient(repository);
				Set<Integer> ids = client.getChangedTickets(since, monitor);
				if (ids.isEmpty()) {
					// repository is unchanged
					session.setNeedsPerformQueries(false);
					return;
				}

				if (ids.size() == 1) {
					// getChangedTickets() is expected to always return at least
					// one ticket because
					// the repository synchronization timestamp is set to the
					// most recent modification date
					Integer id = ids.iterator().next();
					Date lastChanged = client.getTicketLastChanged(id, monitor);
					if (since.equals(lastChanged)) {
						// repository didn't actually change
						session.setNeedsPerformQueries(false);
						return;
					}
				}

				for (ITask task : session.getTasks()) {
					Integer id = getTicketId(task.getTaskId());
					if (ids.contains(id)) {
						session.markStale(task);
					}
				}
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Exception e) {
				// TODO catch TracException
				throw new CoreException(TracCorePlugin.toStatus(e, repository));
			}
		} finally {
			monitor.done();
		}
	}

	public synchronized void setTaskRepositoryLocationFactory(
			TaskRepositoryLocationFactory taskRepositoryLocationFactory) {
		this.taskRepositoryLocationFactory = taskRepositoryLocationFactory;
		if (this.clientManager != null) {
			clientManager.setTaskRepositoryLocationFactory(taskRepositoryLocationFactory);
		}
	}

	public void stop() {
		if (clientManager != null) {
			clientManager.writeCache();
		}
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			ITracClient client = getClientManager().getTracClient(repository);
			client.updateAttributes(monitor, true);
		} catch (Exception e) {
			throw new CoreException(RepositoryStatus.createStatus(repository.getRepositoryUrl(), IStatus.WARNING,
					TracCorePlugin.ID_PLUGIN, "Could not update attributes"));
		}
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskMapper mapper = getTaskMapper(taskRepository, taskData);
		mapper.applyTo(task);
		if (isCompleted(mapper.getStatus())) {
			task.setCompletionDate(mapper.getModificationDate());
		} else {
			task.setCompletionDate(null);
		}
		task.setUrl(taskRepository.getRepositoryUrl() + ITracClient.TICKET_URL + taskData.getTaskId());
		task.setAttribute(TASK_KEY_SUPPORTS_SUBTASKS, Boolean.toString(taskDataHandler.supportsSubtasks(taskData)));
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskMapper mapper = getTaskMapper(taskRepository, taskData);
		if (taskData.isPartial()) {
			return mapper.hasChanges(task);
		} else {
			Date repositoryDate = mapper.getModificationDate();
			Date localDate = task.getModificationDate();
			if (repositoryDate != null && repositoryDate.equals(localDate)) {
				return false;
			}
			return true;
		}
	}

	@Override
	public Collection<TaskRelation> getTaskRelations(TaskData taskData) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY);
		if (attribute != null) {
			List<TaskRelation> result = new ArrayList<TaskRelation>();
			StringTokenizer t = new StringTokenizer(attribute.getValue(), ", ");
			while (t.hasMoreTokens()) {
				result.add(TaskRelation.subtask(t.nextToken()));
			}
			return result;
		}
		return Collections.emptySet();
	}

	@Override
	public ITaskMapping getTaskMapping(TaskData taskData) {
		return getTaskMapper(null, taskData);
	}

	public TaskMapper getTaskMapper(TaskRepository taskRepository, TaskData taskData) {
		final ITracClient client = (taskRepository != null) ? getClientManager().getTracClient(taskRepository) : null;
		return new TaskMapper(taskData) {
			@Override
			public PriorityLevel getPriorityLevel() {
				if (client != null) {
					String priority = getPriority();
					TracPriority[] tracPriorities = client.getPriorities();
					return getTaskPriority(priority, tracPriorities);
				}
				return null;
			}

			@Override
			public String getTaskKind() {
				TaskKind taskKind = TaskKind.fromType(super.getTaskKind());
				return (taskKind != null) ? taskKind.toString() : null;
			}

		};
	}

}