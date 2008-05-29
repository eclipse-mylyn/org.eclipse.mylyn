/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final String BUG_ID = "&bug_id=";

	private static final String CHANGED_BUGS_CGI_ENDDATE = "&chfieldto=Now";

	private static final String CHANGED_BUGS_CGI_QUERY = "/buglist.cgi?query_format=advanced&chfieldfrom=";

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.18-3.0)";

	private static final String COMMENT_FORMAT = "yyyy-MM-dd HH:mm";

	private static final String DEADLINE_FORMAT = "yyyy-MM-dd";

	private final BugzillaTaskAttachmentHandler attachmentHandler = new BugzillaTaskAttachmentHandler(this);

	private final BugzillaTaskDataHandler taskDataHandler = new BugzillaTaskDataHandler(this);

	private BugzillaClientManager clientManager;

	private final Set<BugzillaLanguageSettings> languages = new LinkedHashSet<BugzillaLanguageSettings>();

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData taskData) {
		if (taskData != null) {

			TaskMapper scheme = new TaskMapper(taskData);
			scheme.applyTo(task);

////			// subtasks
//			repositoryTask.dropSubTasks();
//			Set<String> subTaskIds = taskDataHandler.getSubTaskIds(taskData);
//			if (subTaskIds != null && !subTaskIds.isEmpty()) {
//				for (String subId : subTaskIds) {
//					ITask subTask = taskList.getTask(repository.getUrl(), subId);
////					if (subTask == null && retrieveSubTasks) {
////						if (!subId.trim().equals(taskData.getId()) && !subId.equals("")) {
////							try {
////								subTask = createTaskFromExistingId(repository, subId, false, new NullProgressMonitor());
////							} catch (CoreException e) {
////								// ignore
////							}
////						}
////					}
//					if (subTask != null) {
//						bugzillaTask.addSubTask(subTask);
//					}
//				}
//			}

			// Completed
			boolean isComplete = false;
			// TODO: use repository configuration to determine what -completed-
			// states are

			TaskAttribute attributeStatus = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);

			if (attributeStatus != null) {
				isComplete = attributeStatus.getValue().equals(IBugzillaConstants.VALUE_STATUS_RESOLVED)
						|| attributeStatus.getValue().equals(IBugzillaConstants.VALUE_STATUS_CLOSED)
						|| attributeStatus.getValue().equals(IBugzillaConstants.VALUE_STATUS_VERIFIED);
			}

			// Completion Date
			if (isComplete) {
				Date completionDate = null;

				TaskAttribute[] taskComments = taskData.getAttributeMapper().getAttributesByType(taskData,
						TaskAttribute.TYPE_COMMENT);

				if (taskComments != null && taskComments.length > 0) {
					TaskAttribute lastComment = taskComments[taskComments.length - 1];
					if (lastComment != null) {
						TaskAttribute attributeCommentDate = lastComment.getMappedAttribute(TaskAttribute.COMMENT_DATE);
						if (attributeCommentDate != null) {
							try {
								completionDate = new SimpleDateFormat(COMMENT_FORMAT).parse(attributeCommentDate.getValue());
							} catch (ParseException e) {
								// ignore
							}
						}
					}

				} else {
					// Use last modified date
					TaskAttribute attributeLastModified = taskData.getRoot().getMappedAttribute(
							TaskAttribute.DATE_MODIFICATION);
					if (attributeLastModified != null && attributeLastModified.getValue().length() > 0) {
						completionDate = taskData.getAttributeMapper().getDateValue(attributeLastModified);
					}
				}

				if (task.getCompletionDate() != null && completionDate != null) {
					// TODO: if changed notify via task list
				}
				task.setCompletionDate(completionDate);

			}

			// Task Web Url
			String url = getTaskUrl(repository.getRepositoryUrl(), taskData.getTaskId());
			if (url != null) {
				task.setUrl(url);
			}

			// Bugzilla Specific Attributes

			// Product
			if (scheme.getProduct() != null) {
				task.setAttribute(TaskAttribute.PRODUCT, scheme.getProduct());
			}

			// Severity
			TaskAttribute attrSeverity = taskData.getRoot().getMappedAttribute(
					BugzillaReportElement.BUG_SEVERITY.getKey());
			if (attrSeverity != null && !attrSeverity.getValue().equals("")) {
				task.setAttribute(BugzillaReportElement.BUG_SEVERITY.getKey(), attrSeverity.getValue());
			}

			// Due Date
			if (taskData.getRoot().getMappedAttribute(BugzillaReportElement.ESTIMATED_TIME.getKey()) != null) {
				Date dueDate = null;
				// HACK: if estimated_time field exists, time tracking is
				// enabled
				try {
					TaskAttribute attributeDeadline = taskData.getRoot().getMappedAttribute(
							BugzillaReportElement.DEADLINE.getKey());
					if (attributeDeadline != null) {
						dueDate = new SimpleDateFormat(DEADLINE_FORMAT).parse(attributeDeadline.getValue());
					}
				} catch (Exception e) {
					// ignore
				}
				task.setDueDate(dueDate);
			}

			// This attribute is used to determine if local copy is stale
			TaskAttribute attrModification = taskData.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
			if (attrModification != null) {
				task.setAttribute(IBugzillaConstants.ATTRIBUTE_LAST_READ_DATE, attrModification.getValue());
			}
		}
	}

	@Override
	public void preSynchronization(ISynchronizationSession session, IProgressMonitor monitor) throws CoreException {
		TaskRepository repository = session.getTaskRepository();
		if (session.getTasks().isEmpty()) {
			return;
		}

		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Checking for changed tasks", IProgressMonitor.UNKNOWN);

			if (repository.getSynchronizationTimeStamp() == null) {
				for (ITask task : session.getTasks()) {
					session.markStale(task);
				}
				return;
			}

			String dateString = repository.getSynchronizationTimeStamp();
			if (dateString == null) {
				dateString = "";
			}

			String urlQueryBase = repository.getRepositoryUrl() + CHANGED_BUGS_CGI_QUERY
					+ URLEncoder.encode(dateString, repository.getCharacterEncoding()) + CHANGED_BUGS_CGI_ENDDATE;

			String urlQueryString = urlQueryBase + BUG_ID;

			Set<ITask> changedTasks = new HashSet<ITask>();
			Iterator<ITask> itr = session.getTasks().iterator();
			int queryCounter = 0;
			Set<ITask> checking = new HashSet<ITask>();
			while (itr.hasNext()) {
				ITask task = itr.next();
				checking.add(task);
				queryCounter++;
				String newurlQueryString = URLEncoder.encode(task.getTaskId() + ",", repository.getCharacterEncoding());
				urlQueryString += newurlQueryString;
				if (queryCounter >= 1000) {
					queryForChanged(repository, changedTasks, urlQueryString, session);

					queryCounter = 0;
					urlQueryString = urlQueryBase + BUG_ID;
					newurlQueryString = "";
				}

				if (!itr.hasNext() && queryCounter != 0) {
					queryForChanged(repository, changedTasks, urlQueryString, session);
				}
			}

			for (ITask task : session.getTasks()) {
				if (changedTasks.contains(task)) {
					session.markStale(task);
				}
			}

			return;
		} catch (UnsupportedEncodingException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					"Repository configured with unsupported encoding: " + repository.getCharacterEncoding()
							+ "\n\n Unable to determine changed tasks.", e));
		} finally {
			monitor.done();
		}
	}

	/**
	 * TODO: clean up use of BugzillaTaskDataCollector
	 */
	private void queryForChanged(final TaskRepository repository, Set<ITask> changedTasks, String urlQueryString,
			ISynchronizationSession context) throws UnsupportedEncodingException, CoreException {

		HashMap<String, ITask> taskById = new HashMap<String, ITask>();
		for (ITask task : context.getTasks()) {
			taskById.put(task.getTaskId(), task);
		}
		final Set<TaskData> changedTaskData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				changedTaskData.add(taskData);
			}
		};

		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(repository.getRepositoryUrl(), urlQueryString, "");
		performQuery(repository, query, collector, context, new NullProgressMonitor());

		for (TaskData data : changedTaskData) {
			ITask changedTask = taskById.get(data.getTaskId());
			if (changedTask != null) {
				changedTasks.add(changedTask);
			}
		}

	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, final IRepositoryQuery query,
			TaskDataCollector resultCollector, ISynchronizationSession event, IProgressMonitor monitor) {
		try {
			monitor.beginTask("Running query", IProgressMonitor.UNKNOWN);
			BugzillaClient client = getClientManager().getClient(repository, monitor);
			TaskAttributeMapper mapper = getTaskDataHandler().getAttributeMapper(repository);
			boolean hitsReceived = client.getSearchHits(query, resultCollector, mapper, monitor);
			if (!hitsReceived) {
				// XXX: HACK in case of ip change bugzilla can return 0 hits
				// due to invalid authorization token, forcing relogin fixes
				client.logout(monitor);
				client.getSearchHits(query, resultCollector, mapper, monitor);
			}

			return Status.OK_STATUS;
		} catch (UnrecognizedReponseException e) {
			return new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.INFO,
					"Unrecognized response from server", e);
		} catch (IOException e) {
			return new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.ERROR,
					"Check repository configuration: " + e.getMessage(), e);
		} catch (CoreException e) {
			return e.getStatus();
		} finally {
			monitor.done();
		}
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.indexOf(IBugzillaConstants.URL_GET_SHOW_BUG);
		return index == -1 ? null : url.substring(0, index);
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int anchorIndex = url.lastIndexOf("#");
		String bugUrl = url;
		if (anchorIndex != -1) {
			bugUrl = url.substring(0, anchorIndex);
		}

		int index = bugUrl.indexOf(IBugzillaConstants.URL_GET_SHOW_BUG);
		return index == -1 ? null : bugUrl.substring(index + IBugzillaConstants.URL_GET_SHOW_BUG.length());
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		try {
			return BugzillaClient.getBugUrlWithoutLogin(repositoryUrl, taskId);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					"Error constructing task url for " + repositoryUrl + "  id:" + taskId, e));
		}
		return null;
	}

	@Override
	public String getTaskIdPrefix() {
		return "bug";
	}

	public BugzillaClientManager getClientManager() {
		if (clientManager == null) {
			clientManager = new BugzillaClientManager();
			// TODO: Move this initialization elsewhere
			BugzillaCorePlugin.setConnector(this);
			BugzillaLanguageSettings enSetting = new BugzillaLanguageSettings(IBugzillaConstants.DEFAULT_LANG);
			enSetting.addLanguageAttribute("error_login", "Login");
			enSetting.addLanguageAttribute("error_login", "log in");
			enSetting.addLanguageAttribute("error_login", "check e-mail");
			enSetting.addLanguageAttribute("error_login", "Invalid Username Or Password");
			enSetting.addLanguageAttribute("error_collision", "Mid-air collision!");
			enSetting.addLanguageAttribute("error_comment_required", "Comment Required");
			enSetting.addLanguageAttribute("error_logged_out", "logged out");
			enSetting.addLanguageAttribute("bad_login", "Login");
			enSetting.addLanguageAttribute("bad_login", "log in");
			enSetting.addLanguageAttribute("bad_login", "check e-mail");
			enSetting.addLanguageAttribute("bad_login", "Invalid Username Or Password");
			enSetting.addLanguageAttribute("bad_login", "error");
			enSetting.addLanguageAttribute("processed", "processed");
			enSetting.addLanguageAttribute("changes_submitted", "Changes submitted");
			languages.add(enSetting);

		}
		return clientManager;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		if (repository != null) {
			BugzillaCorePlugin.getRepositoryConfiguration(repository, true, monitor);
		}
	}

	@Override
	public boolean isRepositoryConfigurationStale(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if (super.isRepositoryConfigurationStale(repository, monitor)) {
			boolean result = true;
			try {
				BugzillaClient client = getClientManager().getClient(repository, monitor);
				if (client != null) {
					String timestamp = client.getConfigurationTimestamp(monitor);
					if (timestamp != null) {
						String oldTimestamp = repository.getProperty(IBugzillaConstants.PROPERTY_CONFIGTIMESTAMP);
						if (oldTimestamp != null) {
							result = !timestamp.equals(oldTimestamp);
						}
						repository.setProperty(IBugzillaConstants.PROPERTY_CONFIGTIMESTAMP, timestamp);
					}
				}
			} catch (MalformedURLException e) {
				StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						"Error retrieving configuration timestamp for " + repository.getRepositoryUrl(), e));
			}
			return result;
		}
		return false;
	}

	public static int getBugId(String taskId) throws CoreException {
		try {
			return Integer.parseInt(taskId);
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Invalid bug id: "
					+ taskId, e));
		}
	}

	public void addLanguageSetting(BugzillaLanguageSettings language) {
		if (!languages.contains(language)) {
			this.languages.add(language);
		}
	}

	public Set<BugzillaLanguageSettings> getLanguageSettings() {
		return languages;
	}

	/** returns default language if language not found */
	public BugzillaLanguageSettings getLanguageSetting(String label) {
		for (BugzillaLanguageSettings language : getLanguageSettings()) {
			if (language.getLanguageName().equals(label)) {
				return language;
			}
		}
		return BugzillaCorePlugin.getDefault().getLanguageSetting(IBugzillaConstants.DEFAULT_LANG);
	}

	@Override
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
//		try {
//			monitor.beginTask("", 1);
//			if (event.isFullSynchronization()) {
//				event.getTaskRepository().setSynchronizationTimeStamp(
//						getSynchronizationTimestamp(event.getTaskRepository(), event.getChangedTasks()));
//			}
//		} finally {
//			monitor.done();
//		}
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
	public boolean hasChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		if (taskData.isPartial()) {
			return false;
		}
		String lastKnownMod = task.getAttribute(IBugzillaConstants.ATTRIBUTE_LAST_READ_DATE);
		if (lastKnownMod != null) {
			TaskAttribute attrModification = taskData.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
			if (attrModification != null) {
				return !lastKnownMod.equals(attrModification.getValue());
			}

		}
		return true;
	}

}
