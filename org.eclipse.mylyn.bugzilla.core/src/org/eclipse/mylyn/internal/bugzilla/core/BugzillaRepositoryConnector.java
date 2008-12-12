/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
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
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
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

	protected BugzillaClientManager clientManager;

	protected static BugzillaLanguageSettings enSetting;

	protected final static Set<BugzillaLanguageSettings> languages = new LinkedHashSet<BugzillaLanguageSettings>();

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
		TaskMapper scheme = new TaskMapper(taskData);
		scheme.applyTo(task);

		task.setUrl(BugzillaClient.getBugUrlWithoutLogin(repository.getRepositoryUrl(), taskData.getTaskId()));

		boolean isComplete = false;
		TaskAttribute attributeStatus = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		if (attributeStatus != null) {
			isComplete = attributeStatus.getValue().equals(IBugzillaConstants.VALUE_STATUS_RESOLVED)
					|| attributeStatus.getValue().equals(IBugzillaConstants.VALUE_STATUS_CLOSED)
					|| attributeStatus.getValue().equals(IBugzillaConstants.VALUE_STATUS_VERIFIED);
		}

		if (taskData.isPartial()) {
			if (isComplete && task.getCompletionDate() == null) {
				task.setCompletionDate(new Date(0));
			}
		} else {
			// Completion Date
			if (isComplete) {
				Date completionDate = null;

				List<TaskAttribute> taskComments = taskData.getAttributeMapper().getAttributesByType(taskData,
						TaskAttribute.TYPE_COMMENT);
				if (taskComments != null && taskComments.size() > 0) {
					TaskAttribute lastComment = taskComments.get(taskComments.size() - 1);
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
				}

				if (completionDate == null) {
					// Use last modified date
					TaskAttribute attributeLastModified = taskData.getRoot().getMappedAttribute(
							TaskAttribute.DATE_MODIFICATION);
					if (attributeLastModified != null && attributeLastModified.getValue().length() > 0) {
						completionDate = taskData.getAttributeMapper().getDateValue(attributeLastModified);
					}
				}

				if (completionDate == null) {
					completionDate = new Date();
				}

				task.setCompletionDate(completionDate);
			} else {
				task.setCompletionDate(null);
			}

			// Bugzilla Specific Attributes

			// Product
			if (scheme.getProduct() != null) {
				task.setAttribute(TaskAttribute.PRODUCT, scheme.getProduct());
			}

			// Severity
			TaskAttribute attrSeverity = taskData.getRoot().getMappedAttribute(BugzillaAttribute.BUG_SEVERITY.getKey());
			if (attrSeverity != null && !attrSeverity.getValue().equals("")) {
				task.setAttribute(BugzillaAttribute.BUG_SEVERITY.getKey(), attrSeverity.getValue());
			}

			// Due Date
			if (taskData.getRoot().getMappedAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey()) != null) {
				Date dueDate = null;
				// HACK: if estimated_time field exists, time tracking is
				// enabled
				try {
					TaskAttribute attributeDeadline = taskData.getRoot().getMappedAttribute(
							BugzillaAttribute.DEADLINE.getKey());
					if (attributeDeadline != null) {
						dueDate = new SimpleDateFormat(DEADLINE_FORMAT).parse(attributeDeadline.getValue());
					}
				} catch (Exception e) {
					// ignore
				}
				task.setDueDate(dueDate);
			}
		}

		updateExtendedAttributes(task, taskData);
	}

	private void updateExtendedAttributes(ITask task, TaskData taskData) {
		// Set meta bugzilla task attribute values
		for (BugzillaAttribute bugzillaReportElement : BugzillaAttribute.EXTENDED_ATTRIBUTES) {
			TaskAttribute taskAttribute = taskData.getRoot().getAttribute(bugzillaReportElement.getKey());
			if (taskAttribute != null) {
				task.setAttribute(bugzillaReportElement.getKey(), taskAttribute.getValue());
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
			monitor.beginTask("Checking for changed tasks", session.getTasks().size());

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
					queryForChanged(repository, changedTasks, urlQueryString, session, new SubProgressMonitor(monitor,
							queryCounter));

					queryCounter = 0;
					urlQueryString = urlQueryBase + BUG_ID;
					newurlQueryString = "";
				}

				if (!itr.hasNext() && queryCounter != 0) {
					queryForChanged(repository, changedTasks, urlQueryString, session, new SubProgressMonitor(monitor,
							queryCounter));
				}
			}

			for (ITask task : session.getTasks()) {
				if (changedTasks.contains(task)) {
					session.markStale(task);
				}
			}

			return;
		} catch (UnsupportedEncodingException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
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
			ISynchronizationSession context, IProgressMonitor monitor) throws UnsupportedEncodingException,
			CoreException {

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

		// TODO: Decouple from internals
		IRepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), "");
		query.setSummary("Query for changed tasks");
		query.setUrl(urlQueryString);
		performQuery(repository, query, collector, context, monitor);

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

		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Running query", IProgressMonitor.UNKNOWN);
			BugzillaClient client = getClientManager().getClient(repository, new SubProgressMonitor(monitor, 1));
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
			return new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, IStatus.INFO,
					"Unrecognized response from server", e);
		} catch (IOException e) {
			return new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, IStatus.ERROR,
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
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
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
			enSetting = new BugzillaLanguageSettings(IBugzillaConstants.DEFAULT_LANG);
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
			enSetting.addLanguageAttribute("changes_submitted", "added to Bug");
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
			return result;
		}
		return false;
	}

	public static int getBugId(String taskId) throws CoreException {
		try {
			return Integer.parseInt(taskId);
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, 0, "Invalid bug id: "
					+ taskId, e));
		}
	}

	public static void addLanguageSetting(BugzillaLanguageSettings language) {
		if (!languages.contains(language)) {
			BugzillaRepositoryConnector.languages.add(language);
		}
	}

	public static Set<BugzillaLanguageSettings> getLanguageSettings() {
		return languages;
	}

	/** returns default language if language not found */
	public static BugzillaLanguageSettings getLanguageSetting(String label) {
		for (BugzillaLanguageSettings language : getLanguageSettings()) {
			if (language.getLanguageName().equals(label)) {
				return language;
			}
		}
		return enSetting;
	}

	@Override
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
			if (event.isFullSynchronization() && event.getStatus() == null) {
				event.getTaskRepository().setSynchronizationTimeStamp(getSynchronizationTimestamp(event));
			}
		} finally {
			monitor.done();
		}
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
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		if (taskData.isPartial()) {
			return false;
		}
		String lastKnownMod = task.getAttribute(BugzillaAttribute.DELTA_TS.getKey());
		if (lastKnownMod != null) {
			TaskAttribute attrModification = taskData.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
			if (attrModification != null) {
				return !lastKnownMod.equals(attrModification.getValue());
			}

		}
		return true;
	}

	@Override
	public Collection<TaskRelation> getTaskRelations(TaskData taskData) {
		List<TaskRelation> relations = new ArrayList<TaskRelation>();
		TaskAttribute attribute = taskData.getRoot().getAttribute(BugzillaAttribute.DEPENDSON.getKey());
		if (attribute != null && attribute.getValue().length() > 0) {
			for (String taskId : attribute.getValue().split(",")) {
				relations.add(TaskRelation.subtask(taskId.trim()));
			}
		}
		return relations;
	}

	private String getSynchronizationTimestamp(ISynchronizationSession event) {
		Date mostRecent = new Date(0);
		String mostRecentTimeStamp = event.getTaskRepository().getSynchronizationTimeStamp();
		for (ITask task : event.getChangedTasks()) {
			Date taskModifiedDate = task.getModificationDate();
			if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
				mostRecent = taskModifiedDate;
				mostRecentTimeStamp = task.getAttribute(BugzillaAttribute.DELTA_TS.getKey());
			}
		}
		return mostRecentTimeStamp;
	}

	@Override
	public boolean hasRepositoryDueDate(TaskRepository taskRepository, ITask task, TaskData taskData) {
		return taskData.getRoot().getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey()) != null;
	}

}
