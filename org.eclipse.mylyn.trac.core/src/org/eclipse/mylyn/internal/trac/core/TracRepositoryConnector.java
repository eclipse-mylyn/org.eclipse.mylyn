/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.mylyn.internal.trac.core.client.AbstractWikiHandler;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracWikiClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
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
			if (type.equals("Defect")) { //$NON-NLS-1$
				return DEFECT;
			}
			if (type.equals("Enhancement")) { //$NON-NLS-1$
				return ENHANCEMENT;
			}
			if (type.equals("Task")) { //$NON-NLS-1$
				return TASK;
			}
			return null;
		}

		public static TaskKind fromType(String type) {
			if (type == null) {
				return null;
			}
			if (type.equals("defect")) { //$NON-NLS-1$
				return DEFECT;
			}
			if (type.equals("enhancement")) { //$NON-NLS-1$
				return ENHANCEMENT;
			}
			if (type.equals("task")) { //$NON-NLS-1$
				return TASK;
			}
			return null;
		}

		@Override
		public String toString() {
			switch (this) {
			case DEFECT:
				return "Defect"; //$NON-NLS-1$
			case ENHANCEMENT:
				return "Enhancement"; //$NON-NLS-1$
			case TASK:
				return "Task"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}

	}

	public enum TaskStatus {
		ASSIGNED, CLOSED, NEW, REOPENED;

		public static TaskStatus fromStatus(String status) {
			if (status == null) {
				return null;
			}
			if (status.equals("new")) { //$NON-NLS-1$
				return NEW;
			}
			if (status.equals("assigned")) { //$NON-NLS-1$
				return ASSIGNED;
			}
			if (status.equals("reopened")) { //$NON-NLS-1$
				return REOPENED;
			}
			if (status.equals("closed")) { //$NON-NLS-1$
				return CLOSED;
			}
			return null;
		}

		public String toStatusString() {
			switch (this) {
			case NEW:
				return "new"; //$NON-NLS-1$
			case ASSIGNED:
				return "assigned"; //$NON-NLS-1$
			case REOPENED:
				return "reopened"; //$NON-NLS-1$
			case CLOSED:
				return "closed"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case NEW:
				return "New"; //$NON-NLS-1$
			case ASSIGNED:
				return "Assigned"; //$NON-NLS-1$
			case REOPENED:
				return "Reopened"; //$NON-NLS-1$
			case CLOSED:
				return "Closed"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}

	}

	public enum TracPriorityLevel {
		BLOCKER, CRITICAL, MAJOR, MINOR, TRIVIAL;

		public static TracPriorityLevel fromPriority(String priority) {
			if (priority == null) {
				return null;
			}
			if (priority.equals("blocker")) { //$NON-NLS-1$
				return BLOCKER;
			}
			if (priority.equals("critical")) { //$NON-NLS-1$
				return CRITICAL;
			}
			if (priority.equals("major")) { //$NON-NLS-1$
				return MAJOR;
			}
			if (priority.equals("minor")) { //$NON-NLS-1$
				return MINOR;
			}
			if (priority.equals("trivial")) { //$NON-NLS-1$
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
				return "blocker"; //$NON-NLS-1$
			case CRITICAL:
				return "critical"; //$NON-NLS-1$
			case MAJOR:
				return "major"; //$NON-NLS-1$
			case MINOR:
				return "minor"; //$NON-NLS-1$
			case TRIVIAL:
				return "trivial"; //$NON-NLS-1$
			default:
				return null;
			}
		}
	}

	private final static String CLIENT_LABEL = Messages.TracRepositoryConnector_TRAC_SUPPORTS_0_9_OR_0_10_THROUGH_WEB_AND_XML_RPC;

	private final static Date DEFAULT_COMPLETION_DATE = new Date(0);

	private static int TASK_PRIORITY_LEVELS = 5;

	public static final String TASK_KEY_SUPPORTS_SUBTASKS = "SupportsSubtasks"; //$NON-NLS-1$

	public static final String TASK_KEY_UPDATE_DATE = "UpdateDate"; //$NON-NLS-1$

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
					"Invalid ticket id: " + taskId, e)); //$NON-NLS-1$
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
			return ""; //$NON-NLS-1$
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
			this.repositoryConfigurationCacheFile = path.toFile();
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
	public boolean canSynchronizeTask(TaskRepository taskRepository, ITask task) {
		return hasRichEditor(taskRepository, task);
	}

	@Override
	public TracAttachmentHandler getTaskAttachmentHandler() {
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
	public TracTaskDataHandler getTaskDataHandler() {
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
		return "#"; //$NON-NLS-1$
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
			ISynchronizationSession session, IProgressMonitor monitor) {
		try {
			monitor.beginTask(Messages.TracRepositoryConnector_Querying_repository, IProgressMonitor.UNKNOWN);

			TracSearch search = TracUtil.toTracSearch(query);
			if (search == null) {
				return new RepositoryStatus(repository.getRepositoryUrl(), IStatus.ERROR, TracCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY, "The query is invalid: \"" + query.getUrl() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}

			ITracClient client;
			try {
				client = getClientManager().getTracClient(repository);
				List<TracTicket> tickets = new ArrayList<TracTicket>();
				client.search(search, tickets, monitor);

				Map<String, ITask> taskById = null;
				client.updateAttributes(monitor, false);
				for (TracTicket ticket : tickets) {
					TaskData taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, monitor);
					taskData.setPartial(true);
					if (session != null && !session.isFullSynchronization() && hasRichEditor(repository)) {
						if (taskById == null) {
							taskById = new HashMap<String, ITask>();
							for (ITask task : session.getTasks()) {
								taskById.put(task.getTaskId(), task);
							}
						}
						ITask task = taskById.get(ticket.getId() + ""); //$NON-NLS-1$
						if (task != null && hasTaskChanged(repository, task, taskData)) {
							session.markStale(task);
						}
					}
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
			monitor.beginTask("", 1); //$NON-NLS-1$
			if (event.isFullSynchronization() && event.getStatus() == null) {
				Date date = getSynchronizationTimestamp(event);
				if (date != null) {
					event.getTaskRepository().setSynchronizationTimeStamp(TracUtil.toTracTime(date) + ""); //$NON-NLS-1$
				}
			}
		} finally {
			monitor.done();
		}
	}

	private Date getSynchronizationTimestamp(ISynchronizationSession event) {
		Date mostRecent = new Date(0);
		Date mostRecentTimeStamp = TracUtil.parseDate(event.getTaskRepository().getSynchronizationTimeStamp());
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
			monitor.beginTask(Messages.TracRepositoryConnector_Getting_changed_tasks, IProgressMonitor.UNKNOWN);

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
				since = TracUtil.parseDate(Integer.parseInt(repository.getSynchronizationTimeStamp()));
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
					TracCorePlugin.ID_PLUGIN, "Could not update attributes")); //$NON-NLS-1$
		}
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskMapper mapper = getTaskMapping(taskData);
		mapper.applyTo(task);
		if (isCompleted(mapper.getStatus())) {
			Date modificationDate = mapper.getModificationDate();
			if (modificationDate == null) {
				// web mode does not set a date
				modificationDate = DEFAULT_COMPLETION_DATE;
			}
			task.setCompletionDate(modificationDate);
		} else {
			task.setCompletionDate(null);
		}
		task.setUrl(taskRepository.getRepositoryUrl() + ITracClient.TICKET_URL + taskData.getTaskId());
		task.setAttribute(TASK_KEY_SUPPORTS_SUBTASKS, Boolean.toString(taskDataHandler.supportsSubtasks(taskData)));
		if (!taskData.isPartial()) {
			Date date = task.getModificationDate();
			task.setAttribute(TASK_KEY_UPDATE_DATE, (date != null) ? TracUtil.toTracTime(date) + "" : null); //$NON-NLS-1$
		}
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskMapper mapper = getTaskMapping(taskData);
		if (taskData.isPartial()) {
			return mapper.hasChanges(task);
		} else {
			Date repositoryDate = mapper.getModificationDate();
			Date localDate = TracUtil.parseDate(task.getAttribute(TASK_KEY_UPDATE_DATE));
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
			StringTokenizer t = new StringTokenizer(attribute.getValue(), ", "); //$NON-NLS-1$
			while (t.hasMoreTokens()) {
				result.add(TaskRelation.subtask(t.nextToken()));
			}
			return result;
		}
		return Collections.emptySet();
	}

	@Override
	public TracTaskMapper getTaskMapping(TaskData taskData) {
		TaskRepository taskRepository = taskData.getAttributeMapper().getTaskRepository();
		ITracClient client = (taskRepository != null) ? getClientManager().getTracClient(taskRepository) : null;
		return new TracTaskMapper(taskData, client);
	}

}
