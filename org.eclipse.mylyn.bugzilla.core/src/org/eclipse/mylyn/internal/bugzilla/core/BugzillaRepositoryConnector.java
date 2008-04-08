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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCollector;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.SynchronizationEvent;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.web.core.Policy;

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

	private BugzillaAttachmentHandler attachmentHandler;

	private BugzillaTaskDataHandler taskDataHandler;

	private BugzillaClientManager clientManager;

	private Set<BugzillaLanguageSettings> languages = new LinkedHashSet<BugzillaLanguageSettings>();

	@Override
	public void init(TaskList taskList) {
		super.init(taskList);
		this.taskDataHandler = new BugzillaTaskDataHandler(this);
		this.attachmentHandler = new BugzillaAttachmentHandler(this);
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

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public AbstractAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String id, String summary) {
		BugzillaTask task = new BugzillaTask(repositoryUrl, id, summary);
		task.setCreationDate(new Date());
		return task;
	}

	@Override
	public boolean updateTaskFromTaskData(TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData taskData) {
		BugzillaTask bugzillaTask = (BugzillaTask) repositoryTask;
		if (taskData != null) {
			if (taskData.isPartial()) {
				bugzillaTask.setSummary(taskData.getAttributeValue(RepositoryTaskAttribute.SUMMARY));
				bugzillaTask.setPriority(taskData.getAttributeValue(RepositoryTaskAttribute.PRIORITY));
				bugzillaTask.setOwner(taskData.getAttributeValue(RepositoryTaskAttribute.USER_OWNER));
				return false;
			}
			
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

			// Summary
			String summary = taskData.getSummary();
			bugzillaTask.setSummary(summary);

			// Owner
			String owner = taskData.getAssignedTo();
			if (owner != null && !owner.equals("")) {
				bugzillaTask.setOwner(owner);
			}

			// Creation Date
			String createdString = taskData.getCreated();
			if (createdString != null && createdString.length() > 0) {
				Date dateCreated = taskData.getAttributeFactory().getDateForAttributeType(
						RepositoryTaskAttribute.DATE_CREATION, taskData.getCreated());
				if (dateCreated != null) {
					bugzillaTask.setCreationDate(dateCreated);
				}
			}

			// Completed
			boolean isComplete = false;
			// TODO: use repository configuration to determine what -completed-
			// states are
			if (taskData.getStatus() != null) {
				isComplete = taskData.getStatus().equals(IBugzillaConstants.VALUE_STATUS_RESOLVED)
						|| taskData.getStatus().equals(IBugzillaConstants.VALUE_STATUS_CLOSED)
						|| taskData.getStatus().equals(IBugzillaConstants.VALUE_STATUS_VERIFIED);
			}
			bugzillaTask.setCompleted(isComplete);

			// Completion Date
			if (isComplete) {
				Date completionDate = null;
				try {

					List<TaskComment> taskComments = taskData.getComments();
					if (taskComments != null && !taskComments.isEmpty()) {
						// TODO: fix not to be based on comment
						completionDate = new SimpleDateFormat(COMMENT_FORMAT).parse(taskComments.get(
								taskComments.size() - 1).getCreated());

					}

				} catch (Exception e) {

				}

				if (bugzillaTask.getCompletionDate() != null && completionDate != null) {
					// if changed:
					// TODO: get taskListManger.setDueDate(ITask task, Date
					// dueDate)
				}
				bugzillaTask.setCompletionDate(completionDate);

			}

			// Priority
			String priority = PriorityLevel.getDefault().toString();
			if (taskData.getAttribute(RepositoryTaskAttribute.PRIORITY) != null) {
				priority = taskData.getAttribute(RepositoryTaskAttribute.PRIORITY).getValue();
			}
			bugzillaTask.setPriority(priority);

			// Task Web Url
			String url = getTaskUrl(repository.getRepositoryUrl(), taskData.getTaskId());
			if (url != null) {
				bugzillaTask.setUrl(url);
			}

			// Bugzilla Specific Attributes

			// Product
			if (taskData.getProduct() != null) {
				bugzillaTask.setProduct(taskData.getProduct());
			}

			// Severity
			String severity = taskData.getAttributeValue(BugzillaReportElement.BUG_SEVERITY.getKeyString());
			if (severity != null && !severity.equals("")) {
				bugzillaTask.setSeverity(severity);
			}

			// Due Date
			if (taskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()) != null) {
				Date dueDate = null;
				// HACK: if estimated_time field exists, time tracking is
				// enabled
				try {
					String dueStr = taskData.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString());
					if (dueStr != null) {
						dueDate = new SimpleDateFormat(DEADLINE_FORMAT).parse(dueStr);
					}
				} catch (Exception e) {
					// ignore
				}
				bugzillaTask.setDueDate(dueDate);
			}

		}
		return false;
	}

	@Override
	public boolean updateTaskFromQueryHit(TaskRepository repository, AbstractTask existingTask, AbstractTask newTask) {
//		// these properties are not provided by Bugzilla queries
//		newTask.setCompleted(existingTask.isCompleted());
//		//	newTask.setCompletionDate(existingTask.getCompletionDate());
//
//		// Owner attribute not previously 
//		if (hasTaskPropertyChanged(existingTask.getOwner(), newTask.getOwner())) {
//			existingTask.setOwner(newTask.getOwner());
//		}
//
//		boolean changed = super.updateTaskFromQueryHit(repository, existingTask, newTask);
//
//		if (existingTask instanceof BugzillaTask && newTask instanceof BugzillaTask) {
//			BugzillaTask existingBugzillaTask = (BugzillaTask) existingTask;
//			BugzillaTask newBugzillaTask = (BugzillaTask) newTask;
//
//			if (hasTaskPropertyChanged(existingBugzillaTask.getSeverity(), newBugzillaTask.getSeverity())) {
//				existingBugzillaTask.setSeverity(newBugzillaTask.getSeverity());
//				changed = true;
//			}
//			if (hasTaskPropertyChanged(existingBugzillaTask.getProduct(), newBugzillaTask.getProduct())) {
//				existingBugzillaTask.setProduct(newBugzillaTask.getProduct());
//				changed = true;
//			}
//		}
		return false;
	}

	@Override
	public void preSynchronization(SynchronizationEvent event, IProgressMonitor monitor)
			throws CoreException {
		TaskRepository repository = event.taskRepository;	
		if (event.tasks.isEmpty()) {
			return;
		}
		
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Checking for changed tasks", IProgressMonitor.UNKNOWN);

			if (repository.getSynchronizationTimeStamp() == null) {
				for (AbstractTask task : event.tasks) {
					task.setStale(true);
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

			// Need to replace this with query that would return list of tasks since last sync
			// the trouble is that bugzilla only have 1 hour granularity for "changed since" query
			// so, we can't say that no tasks has changed in repository
			// Retrieve all in one query
//			Set<AbstractTask> changedTasks = new HashSet<AbstractTask>();
//			Iterator<AbstractTask> itr = tasks.iterator();
//			while (itr.hasNext()) {
//				AbstractTask task = itr.next();
//				String newurlQueryString = URLEncoder.encode(task.getTaskId() + ",", repository.getCharacterEncoding());
//				urlQueryString += newurlQueryString;
//			}
//			System.err.println(">>>> markStale "+tasks.size());
//			queryForChanged(repository, changedTasks, urlQueryString);
			
			Set<AbstractTask> changedTasks = new HashSet<AbstractTask>();
			Iterator<AbstractTask> itr = event.tasks.iterator();
			int queryCounter = 0;
			Set<AbstractTask> checking = new HashSet<AbstractTask>();
			while (itr.hasNext()) {
				AbstractTask task = itr.next();
				checking.add(task);
				queryCounter++;
				String newurlQueryString = URLEncoder.encode(task.getTaskId() + ",", repository.getCharacterEncoding());
				urlQueryString += newurlQueryString;
				if (queryCounter >= 1000) {
					queryForChanged(repository, changedTasks, urlQueryString);
					
					queryCounter = 0;
					urlQueryString = urlQueryBase + BUG_ID;
					newurlQueryString = "";
				}
				
				if (!itr.hasNext() && queryCounter != 0) {
					queryForChanged(repository, changedTasks, urlQueryString);
				}
			}
			
			for (AbstractTask task : event.tasks) {
				if (changedTasks.contains(task)) {
					task.setStale(true);
				}
			}

			// FIXME check if new tasks were added
			//return changedTasks.isEmpty();
			return;
		} catch (UnsupportedEncodingException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, "Repository configured with unsupported encoding: "
					+ repository.getCharacterEncoding() + "\n\n Unable to determine changed tasks.", e));
		} finally {
			monitor.done();
		}
	}
	
	private void queryForChanged(final TaskRepository repository, Set<AbstractTask> changedTasks, String urlQueryString)
			throws UnsupportedEncodingException, CoreException {
		QueryHitCollector collector = new QueryHitCollector(new ITaskFactory() {

			public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) {
				// do not construct actual task objects here as query shouldn't result in new tasks
				return taskList.getTask(taskData.getRepositoryUrl(), taskData.getTaskId());
			}
		});

		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(repository.getRepositoryUrl(), urlQueryString, "");
		performQuery(repository, query, collector, null, new NullProgressMonitor());
		
		changedTasks.addAll(collector.getTasks());
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
	public IStatus performQuery(TaskRepository repository, final AbstractRepositoryQuery query,
			AbstractTaskCollector resultCollector, SynchronizationEvent event, IProgressMonitor monitor) {
		try {
			monitor.beginTask("Running query", IProgressMonitor.UNKNOWN);
			BugzillaClient client = getClientManager().getClient(repository);
			boolean hitsReceived = client.getSearchHits(query, resultCollector);
			if (!hitsReceived) {
				// XXX: HACK in case of ip change bugzilla can return 0 hits
				// due to invalid authorization token, forcing relogin fixes
				client.logout();
				client.getSearchHits(query, resultCollector);
			}

			return Status.OK_STATUS;
		} catch (UnrecognizedReponseException e) {
			return new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, Status.INFO,
					"Unrecognized response from server", e);
		} catch (IOException e) {
			return new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, Status.ERROR,
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
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, "Error constructing task url for " + repositoryUrl + "  id:" + taskId, e));
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
		}
		return clientManager;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		if (repository != null) {
			BugzillaCorePlugin.getRepositoryConfiguration(repository, true);
		}
	}

	public boolean isRepositoryConfigurationStale(TaskRepository repository) throws CoreException {
		if (super.isRepositoryConfigurationStale(repository)) {
			boolean result = true;
			try {
				BugzillaClient client = getClientManager().getClient(repository);
				if (client != null) {
					String timestamp = client.getConfigurationTimestamp();
					if (timestamp != null) {
						String oldTimestamp = repository.getProperty(IBugzillaConstants.PROPERTY_CONFIGTIMESTAMP);
						if (oldTimestamp != null) {
							result = !timestamp.equals(oldTimestamp);
						}
						repository.setProperty(IBugzillaConstants.PROPERTY_CONFIGTIMESTAMP, timestamp);
					}
				}
			} catch (MalformedURLException e) {
				StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, "Error retrieving configuration timestamp for " + repository.getRepositoryUrl(), e));
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
	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return getTaskDataHandler().getTaskData(repository, taskId, monitor);
	}

}
