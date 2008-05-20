/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.TasksModel;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IAttributeContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Subclass externalizers must override the get*TagName() methods for the types of externalized items they support to
 * ensure that their externalizer does not externalize tasks from other connectors incorrectly.
 * 
 * These tag names uniquely identify the externalizer to be used to read the task from externalized form on disk.
 * 
 * The canCreateElementFor methods specify which tasks the externalizer should write to disk.
 * 
 * The TaskList is read on startup, so externalizers extending this should not perform any slow (i.e., network)
 * operations when overriding methods.
 * 
 * @author Mik Kersten
 * @author Ken Sueda (XML serialization support)
 * @author Steffen Pingel
 */
public final class DelegatingTaskExternalizer {

	static final String DEFAULT_PRIORITY = PriorityLevel.P3.toString();

	static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S z";

	static final String KEY_NOTIFIED_INCOMING = "NotifiedIncoming";

	public static final String KEY_NAME = "Name";

	public static final String KEY_LABEL = "Label";

	static final String KEY_HANDLE = "Handle";

	public static final String KEY_REPOSITORY_URL = "RepositoryUrl";

	public static final String KEY_CATEGORY = "Category";

	static final String VAL_ROOT = "Root";

	static final String KEY_SUBTASK = "SubTask";

	static final String KEY_KIND = "Kind";

	static final String KEY_TASK_CATEGORY = "Task" + KEY_CATEGORY;

	static final String KEY_LINK = "Link";

	static final String KEY_PLAN = "Plan";

	static final String KEY_TIME_ESTIMATED = "Estimated";

	static final String KEY_ISSUEURL = "IssueURL";

	static final String KEY_NOTES = "Notes";

	static final String KEY_ACTIVE = "Active";

	static final String KEY_PRIORITY = "Priority";

	static final String KEY_PATH = "Path";

	static final String VAL_FALSE = "false";

	static final String VAL_TRUE = "true";

	static final String KEY_DATE_END = "EndDate";

	static final String KEY_QUERY_HIT = "QueryHit";

	static final String KEY_TASK_REFERENCE = "TaskReference";

	static final String KEY_DATE_CREATION = "CreationDate";

	static final String KEY_DATE_REMINDER = "ReminderDate";

	static final String KEY_DATE_SCHEDULED_START = "ScheduledStartDate";

	static final String KEY_DATE_SCHEDULED_END = "ScheduledEndDate";

	static final String KEY_DATE_MODIFICATION = "ModificationDate";

	static final String KEY_DATE_DUE = "DueDate";

	static final String KEY_REMINDED = "Reminded";

	static final String KEY_FLOATING = "Floating";

	/**
	 * This element holds the date stamp recorded upon last transition to a synchronized state.
	 */
	static final String KEY_LAST_MOD_DATE = "LastModified";

	static final String KEY_DIRTY = "Dirty";

	static final String KEY_SYNC_STATE = "offlineSyncState";

	static final String KEY_OWNER = "Owner";

	static final String KEY_MARK_READ_PENDING = "MarkReadPending";

	static final String KEY_STALE = "Stale";

	static final String KEY_CONNECTOR_KIND = "ConnectorKind";

	static final String KEY_TASK_ID = "TaskId";

	public static final String KEY_LAST_REFRESH = "LastRefreshTimeStamp";

	static final String NODE_ATTRIBUTE = "Attribute";

	static final String NODE_QUERY = "Query";

	static final String NODE_TASK = "Task";

	static final String KEY_KEY = "Key";

	private List<AbstractTaskListFactory> factories;

	// 2.0 -> 3.0 migration holds tasks to category handles 
	private final Map<AbstractTask, String> parentCategoryMap;

	private final TasksModel tasksModel;

	private List<AbstractTaskListMigrator> migrators;

	public DelegatingTaskExternalizer(TasksModel tasksModel) {
		Assert.isNotNull(tasksModel);
		this.tasksModel = tasksModel;
		this.parentCategoryMap = new HashMap<AbstractTask, String>();
		this.factories = Collections.emptyList();
		this.migrators = Collections.emptyList();
	}

	public void initialize(List<AbstractTaskListFactory> externalizers, List<AbstractTaskListMigrator> migrators) {
		Assert.isNotNull(externalizers);
		Assert.isNotNull(migrators);
		this.factories = externalizers;
		this.migrators = migrators;
	}

	public Element createCategoryElement(ITaskElement category, Document doc, Element parent) {
		Element node = doc.createElement(getCategoryTagName());
		node.setAttribute(DelegatingTaskExternalizer.KEY_NAME, category.getSummary());
		parent.appendChild(node);
		for (ITask task : category.getChildren()) {
			createTaskReference(KEY_TASK_REFERENCE, task, doc, node);
		}
		return node;
	}

	public Element createTaskElement(AbstractTask task, Document doc, Element parent) {
		Element node;
		if (task.getClass() == TaskTask.class || task instanceof LocalTask) {
			node = doc.createElement(NODE_TASK);
		} else {
			AbstractTaskListFactory factory = null;
			for (AbstractTaskListFactory currentFactory : factories) {
				if (currentFactory.canCreate(task)) {
					factory = currentFactory;
					break;
				}
			}
			String taskTagName;
			if (factory != null) {
				taskTagName = factory.getTaskElementName();
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						"No externalizer for task: " + task));
				return null;
			}
			node = doc.createElement(taskTagName);
			factory.setAdditionalAttributes(task, node);
		}

		node.setAttribute(KEY_CONNECTOR_KIND, task.getConnectorKind());
		node.setAttribute(KEY_REPOSITORY_URL, task.getRepositoryUrl());
		node.setAttribute(KEY_TASK_ID, task.getTaskId());
		if (task.getTaskKey() != null) {
			node.setAttribute(KEY_KEY, task.getTaskKey());
		}
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());
		node.setAttribute(KEY_LABEL, stripControlCharacters(task.getSummary()));

		node.setAttribute(KEY_PRIORITY, task.getPriority());
		node.setAttribute(KEY_KIND, task.getTaskKind());

		if (task.isActive()) {
			node.setAttribute(KEY_ACTIVE, VAL_TRUE);
		} else {
			node.setAttribute(KEY_ACTIVE, VAL_FALSE);
		}

		if (task.getUrl() != null) {
			node.setAttribute(KEY_ISSUEURL, task.getUrl());
		}
		node.setAttribute(KEY_NOTES, stripControlCharacters(task.getNotes()));
		node.setAttribute(KEY_TIME_ESTIMATED, "" + task.getEstimatedTimeHours());
		node.setAttribute(KEY_DATE_END, formatExternDate(task.getCompletionDate()));
		node.setAttribute(KEY_DATE_CREATION, formatExternDate(task.getCreationDate()));
		node.setAttribute(KEY_DATE_MODIFICATION, formatExternDate(task.getModificationDate()));
		node.setAttribute(KEY_DATE_DUE, formatExternDate(task.getDueDate()));
		if (task.getScheduledForDate() != null) {
			node.setAttribute(KEY_DATE_SCHEDULED_START, formatExternCalendar(task.getScheduledForDate().getStartDate()));
			node.setAttribute(KEY_DATE_SCHEDULED_END, formatExternCalendar(task.getScheduledForDate().getEndDate()));
		}
		if (task.isReminded()) {
			node.setAttribute(KEY_REMINDED, VAL_TRUE);
		} else {
			node.setAttribute(KEY_REMINDED, VAL_FALSE);
		}
		if (task.isStale()) {
			node.setAttribute(KEY_STALE, VAL_TRUE);
		} else {
			node.setAttribute(KEY_STALE, VAL_FALSE);
		}
		if (task.isMarkReadPending()) {
			node.setAttribute(KEY_MARK_READ_PENDING, VAL_TRUE);
		} else {
			node.setAttribute(KEY_MARK_READ_PENDING, VAL_FALSE);
		}
		if (task.getLastReadTimeStamp() != null) {
			node.setAttribute(KEY_LAST_MOD_DATE, task.getLastReadTimeStamp());
		}
		if (task.isNotified()) {
			node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_TRUE);
		} else {
			node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_FALSE);
		}
		if (task.getSynchronizationState() != null) {
			node.setAttribute(KEY_SYNC_STATE, task.getSynchronizationState().toString());
		} else {
			node.setAttribute(KEY_SYNC_STATE, SynchronizationState.SYNCHRONIZED.toString());
		}
		if (task.getOwner() != null) {
			node.setAttribute(KEY_OWNER, task.getOwner());
		}
		createAttributes(task, doc, node);
		for (ITask t : task.getChildren()) {
			createTaskReference(KEY_SUBTASK, t, doc, node);
		}

		parent.appendChild(node);
		return node;
	}

	private void createAttributes(IAttributeContainer container, Document doc, Element parent) {
		Map<String, String> attributes = container.getAttributes();
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			Element node = doc.createElement(NODE_ATTRIBUTE);
			node.setAttribute(KEY_KEY, entry.getKey());
			node.setTextContent(entry.getValue());
			parent.appendChild(node);
		}

	}

	/**
	 * creates nested task reference nodes named nodeName which include a handle to the task
	 * 
	 * @return
	 */
	public Element createTaskReference(String nodeName, ITask task, Document doc, Element parent) {
		Element node = doc.createElement(nodeName);
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());
		parent.appendChild(node);
		return node;
	}

	/**
	 * create tasks from the nodes provided and places them within the given container
	 */
	public void readTaskReferences(AbstractTaskContainer task, NodeList nodes, TaskList tasklist) {
		for (int j = 0; j < nodes.getLength(); j++) {
			Node child = nodes.item(j);
			Element element = (Element) child;
			if (element.hasAttribute(KEY_HANDLE)) {
				String handle = element.getAttribute(KEY_HANDLE);
				AbstractTask subTask = tasklist.getTask(handle);
				if (subTask != null) {
					tasklist.addTask(subTask, task);
				}
			}
		}

	}

	@SuppressWarnings( { "deprecation", "restriction" })
	private String stripControlCharacters(String text) {
		if (text == null) {
			return "";
		}
		return org.eclipse.mylyn.internal.commons.core.XmlStringConverter.cleanXmlString(text);
	}

	private String formatExternDate(Date date) {
		if (date == null) {
			return "";
		}
		String f = DATE_FORMAT;
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		return format.format(date);
	}

	private String formatExternCalendar(Calendar date) {
		if (date == null) {
			return "";
		}
		String f = DATE_FORMAT;
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		return format.format(date.getTime());
	}

	public void readCategory(Node node, TaskList taskList) {
		Element element = (Element) node;
		AbstractTaskCategory category = null;
		if (element.hasAttribute(KEY_NAME)) {
			String handle = element.getAttribute(KEY_NAME);
			category = taskList.getContainerForHandle(handle);
			if (category == null) {
				category = new TaskCategory(element.getAttribute(KEY_NAME));
				taskList.addCategory((TaskCategory) category);
			}
		} else {
			// LEGACY: registry categories did not have names
			// category = taskList.getArchiveContainer();
			// a null category will now go into appropriate orphaned category
		}

		NodeList list = node.getChildNodes();
		readTaskReferences(category, list, taskList);
	}

	public final AbstractTask readTask(Node node, AbstractTaskCategory legacyCategory, ITask parent)
			throws CoreException {
		String handle;
		String taskId;
		String repositoryUrl;
		String summary = "";
		Element element = (Element) node;
		if (element.hasAttribute(KEY_REPOSITORY_URL) && element.hasAttribute(KEY_TASK_ID)
				&& element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
			repositoryUrl = element.getAttribute(KEY_REPOSITORY_URL);
			taskId = element.getAttribute(KEY_TASK_ID);
		} else if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
			repositoryUrl = RepositoryTaskHandleUtil.getRepositoryUrl(handle);
			taskId = RepositoryTaskHandleUtil.getTaskId(handle);
		} else {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Handle not stored for repository task"));
		}
		if (element.hasAttribute(KEY_LABEL)) {
			summary = element.getAttribute(KEY_LABEL);
		}

		AbstractTask task = null;
		if (NODE_TASK.equals(node.getNodeName())) {
			String connectorKind = element.getAttribute(DelegatingTaskExternalizer.KEY_CONNECTOR_KIND);
			task = readDefaultTask(connectorKind, repositoryUrl, taskId, summary, element);
		}
		// attempt migration from < 3.0 task list
		if (task == null) {
			for (AbstractTaskListMigrator migrator : migrators) {
				if (node.getNodeName().equals(migrator.getTaskElementName())) {
					task = readDefaultTask(migrator.getConnectorKind(), repositoryUrl, taskId, summary, element);
					migrator.migrateTask(task, element);
					break;
				}
			}
		}
		// legacy support
		if (task == null) {
			for (AbstractTaskListFactory externalizer : factories) {
				if (node.getNodeName().equals(externalizer.getTaskElementName())) {
					task = externalizer.createTask(repositoryUrl, taskId, summary, element);
					break;
				}
			}
		}
		// populate common attributes
		if (task != null) {
			readTaskInfo(task, element, parent, legacyCategory);
			readAttributes(task, element);
		}
		return task;
	}

	private void readAttributes(IAttributeContainer container, Element parent) {
		NodeList list = parent.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child instanceof Element && child.getNodeName().equals(NODE_ATTRIBUTE)) {
				Element element = (Element) child;
				String key = element.getAttribute(KEY_KEY);
				if (key.length() > 0) {
					container.setAttribute(key, element.getTextContent());
				}
			}
		}
	}

	private void readTaskInfo(AbstractTask task, Element element, ITask parent, AbstractTaskCategory legacyCategory) {
		if (element.hasAttribute(KEY_CATEGORY)) {
			// Migration 2.0 -> 3.0 task list.  Category no longer maintained on the task element but
			// task handles held within category nodes similar to query children
			String categoryHandle = element.getAttribute(KEY_CATEGORY);
			if (categoryHandle.equals(VAL_ROOT)) {
				categoryHandle = UncategorizedTaskContainer.HANDLE;
			}
			//task.setCategoryHandle(categoryHandle);
			parentCategoryMap.put(task, categoryHandle);
		}
		if (element.hasAttribute(KEY_PRIORITY)) {
			task.setPriority(element.getAttribute(KEY_PRIORITY));
		} else {
			task.setPriority(DEFAULT_PRIORITY);
		}
		if (element.hasAttribute(KEY_KIND)) {
			task.setTaskKind(element.getAttribute(KEY_KIND));
		}
		if (element.getAttribute(KEY_ACTIVE).compareTo(VAL_TRUE) == 0) {
			task.setActive(true);
		} else {
			task.setActive(false);
		}
		if (element.hasAttribute(KEY_ISSUEURL)) {
			task.setUrl(element.getAttribute(KEY_ISSUEURL));
		} else {
			task.setUrl("");
		}
		if (element.hasAttribute(KEY_NOTES)) {
			task.setNotes(element.getAttribute(KEY_NOTES));
		} else {
			task.setNotes("");
		}
		if (element.hasAttribute(KEY_TIME_ESTIMATED)) {
			String est = element.getAttribute(KEY_TIME_ESTIMATED);
			try {
				int estimate = Integer.parseInt(est);
				task.setEstimatedTimeHours(estimate);
			} catch (Exception e) {
				task.setEstimatedTimeHours(0);
			}
		} else {
			task.setEstimatedTimeHours(0);
		}
		if (element.hasAttribute(KEY_DATE_END)) {
			task.setCompletionDate(getDateFromString(element.getAttribute(KEY_DATE_END)));
		} else {
			task.setCompletionDate(null);
		}
		if (element.hasAttribute(KEY_DATE_CREATION)) {
			task.setCreationDate(getDateFromString(element.getAttribute(KEY_DATE_CREATION)));
		} else {
			task.setCreationDate(Calendar.getInstance().getTime());
		}
		if (element.hasAttribute(KEY_DATE_MODIFICATION)) {
			task.setModificationDate(getDateFromString(element.getAttribute(KEY_DATE_MODIFICATION)));
		} else {
			task.setModificationDate(null);
		}
		if (element.hasAttribute(KEY_DATE_DUE)) {
			task.setDueDate(getDateFromString(element.getAttribute(KEY_DATE_DUE)));
		} else {
			task.setDueDate(null);
		}
		// Legacy 2.3.2 -> 3.0 migration of scheduled date
		boolean isFloating = false;
		if (element.hasAttribute(KEY_FLOATING) && element.getAttribute(KEY_FLOATING).compareTo(VAL_TRUE) == 0) {
			isFloating = true;
		} else {
			isFloating = false;
		}
		if (element.hasAttribute(KEY_DATE_REMINDER)) {
			Date date = getDateFromString(element.getAttribute(KEY_DATE_REMINDER));
			if (date != null) {
				if (isFloating) {
					task.setScheduledForDate(TaskActivityUtil.getWeekOf(date));
				} else {
					task.setScheduledForDate(TaskActivityUtil.getDayOf(date));
				}
			}
		}
		// Scheduled date range (3.0)
		if (element.hasAttribute(KEY_DATE_SCHEDULED_START) && element.hasAttribute(KEY_DATE_SCHEDULED_END)) {
			Date startDate = getDateFromString(element.getAttribute(KEY_DATE_SCHEDULED_START));
			Date endDate = getDateFromString(element.getAttribute(KEY_DATE_SCHEDULED_END));
			if (startDate != null && endDate != null && startDate.compareTo(endDate) <= 0) {
				Calendar calStart = TaskActivityUtil.getCalendar();
				calStart.setTime(startDate);
				Calendar calEnd = TaskActivityUtil.getCalendar();
				calEnd.setTime(endDate);
				task.setScheduledForDate(new DateRange(calStart, calEnd));
			}
		}
		if (element.hasAttribute(KEY_REMINDED) && element.getAttribute(KEY_REMINDED).compareTo(VAL_TRUE) == 0) {
			task.setReminded(true);
		} else {
			task.setReminded(false);
		}
		if (element.hasAttribute(KEY_STALE) && element.getAttribute(KEY_STALE).compareTo(VAL_TRUE) == 0) {
			task.setStale(true);
		} else {
			task.setStale(false);
		}
		if (element.hasAttribute(KEY_MARK_READ_PENDING)
				&& element.getAttribute(KEY_MARK_READ_PENDING).compareTo(VAL_TRUE) == 0) {
			task.setMarkReadPending(true);
		} else {
			task.setMarkReadPending(false);
		}
		task.setSynchronizing(false);
		if (element.hasAttribute(KEY_REPOSITORY_URL)) {
			task.setRepositoryUrl(element.getAttribute(KEY_REPOSITORY_URL));
		}
		if (element.hasAttribute(KEY_LAST_MOD_DATE) && !element.getAttribute(KEY_LAST_MOD_DATE).equals("")) {
			task.setLastReadTimeStamp(element.getAttribute(KEY_LAST_MOD_DATE));
		}
		if (element.hasAttribute(KEY_OWNER)) {
			task.setOwner(element.getAttribute(KEY_OWNER));
		}
		if (VAL_TRUE.equals(element.getAttribute(KEY_NOTIFIED_INCOMING))) {
			task.setNotified(true);
		} else {
			task.setNotified(false);
		}
		if (element.hasAttribute(KEY_SYNC_STATE)) {
			String syncState = element.getAttribute(KEY_SYNC_STATE);
			if (syncState.compareTo(SynchronizationState.SYNCHRONIZED.toString()) == 0) {
				task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
			} else if (syncState.compareTo(SynchronizationState.INCOMING.toString()) == 0) {
				task.setSynchronizationState(SynchronizationState.INCOMING);
			} else if (syncState.compareTo(SynchronizationState.OUTGOING.toString()) == 0) {
				task.setSynchronizationState(SynchronizationState.OUTGOING);
			} else if (syncState.compareTo(SynchronizationState.CONFLICT.toString()) == 0) {
				task.setSynchronizationState(SynchronizationState.CONFLICT);
			}
		}
		if (element.hasAttribute(KEY_KEY)) {
			task.setTaskKey(element.getAttribute(KEY_KEY));
		}
	}

	private Date getDateFromString(String dateString) {
		Date date = null;
		if ("".equals(dateString)) {
			return null;
		}
		String formatString = DATE_FORMAT;
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not parse end date", e));
		}
		return date;
	}

	private String getCategoryTagName() {
		return KEY_TASK_CATEGORY;
	}

	public Element createQueryElement(RepositoryQuery query, Document doc, Element parent) {
		Element node;
		if (query.getClass() == RepositoryQuery.class) {
			node = doc.createElement(NODE_QUERY);
		} else {
			String queryTagName = null;
			AbstractTaskListFactory factory = null;
			for (AbstractTaskListFactory currentFactory : factories) {
				if (currentFactory.canCreate(query)) {
					factory = currentFactory;
					queryTagName = factory.getQueryElementName(query);
					break;
				}
			}
			if (factory == null || queryTagName == null) {
				StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						"Could not externalize query: " + query));
				return null;
			}
			node = doc.createElement(queryTagName);
			factory.setAdditionalAttributes(query, node);
		}

		node.setAttribute(KEY_HANDLE, query.getHandleIdentifier());
		node.setAttribute(KEY_CONNECTOR_KIND, query.getConnectorKind());
		node.setAttribute(KEY_NAME, query.getSummary());
		node.setAttribute(AbstractTaskListFactory.KEY_QUERY_STRING, query.getUrl());
		node.setAttribute(KEY_REPOSITORY_URL, query.getRepositoryUrl());
		if (query.getLastSynchronizedTimeStamp() != null) {
			node.setAttribute(KEY_LAST_REFRESH, query.getLastSynchronizedTimeStamp());
		}
		createAttributes(query, doc, node);
		for (ITask hit : query.getChildren()) {
			createTaskReference(KEY_QUERY_HIT, hit, doc, node);
		}

		parent.appendChild(node);
		return node;
	}

	public List<AbstractTaskListFactory> getDelegateExternalizers() {
		return factories;
	}

	public Map<AbstractTask, String> getLegacyParentCategoryMap() {
		return parentCategoryMap;
	}

	/**
	 * Reads the Query from the specified Node. If taskList is not null, then also adds this query to the TaskList
	 * 
	 * @throws TaskExternalizationException
	 */
	public RepositoryQuery readQuery(Node node, TaskList taskList) {
		Element element = (Element) node;
		String repositoryUrl = element.getAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL);
		String queryString = element.getAttribute(AbstractTaskListFactory.KEY_QUERY_STRING);
		if (queryString.length() == 0) { // fallback for legacy
			queryString = element.getAttribute(AbstractTaskListFactory.KEY_QUERY);
		}
		String label = element.getAttribute(DelegatingTaskExternalizer.KEY_NAME);
		if (label.length() == 0) { // fallback for legacy
			label = element.getAttribute(DelegatingTaskExternalizer.KEY_LABEL);
		}

		RepositoryQuery query = null;
		if (NODE_QUERY.equals(node.getNodeName())) {
			String connectorKind = element.getAttribute(DelegatingTaskExternalizer.KEY_CONNECTOR_KIND);
			query = readDefaultQuery(connectorKind, repositoryUrl, queryString, label, element);
		}
		// attempt migration from < 3.0 task list
		if (query == null) {
			for (AbstractTaskListMigrator migrator : migrators) {
				Set<String> queryTagNames = migrator.getQueryElementNames();
				if (queryTagNames != null && queryTagNames.contains(node.getNodeName())) {
					query = readDefaultQuery(migrator.getConnectorKind(), repositoryUrl, queryString, label, element);
					migrator.migrateQuery(query, element);
					break;
				}
			}
		}
		// legacy support
		if (query == null) {
			for (AbstractTaskListFactory externalizer : factories) {
				Set<String> queryTagNames = externalizer.getQueryElementNames();
				if (queryTagNames != null && queryTagNames.contains(node.getNodeName())) {
					query = externalizer.createQuery(repositoryUrl, queryString, label, element);
					break;
				}
			}
		}
		// populate common attributes
		if (query != null) {
			if (element.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH) != null
					&& !element.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH).equals("")) {
				query.setLastSynchronizedStamp(element.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH));
			}
			String handle = element.getAttribute(DelegatingTaskExternalizer.KEY_HANDLE);
			if (handle.length() > 0) {
				query.setHandleIdentifier(handle);
			}
			readAttributes(query, element);
		}
		return query;
	}

	private RepositoryQuery readDefaultQuery(String connectorKind, String repositoryUrl, String queryString,
			String label, Element childElement) {
		TaskRepository taskRepository = tasksModel.getTaskRepository(connectorKind, repositoryUrl);
		IRepositoryQuery query = tasksModel.createQuery(taskRepository);
		query.setSummary(label);
		query.setUrl(queryString);
		return (RepositoryQuery) query;
	}

	private AbstractTask readDefaultTask(String connectorKind, String repositoryUrl, String taskId, String summary,
			Element element) {
		TaskRepository taskRepository = tasksModel.getTaskRepository(connectorKind, repositoryUrl);
		if (repositoryUrl.equals(LocalRepositoryConnector.REPOSITORY_URL)) {
			LocalTask task = new LocalTask(taskId, summary);
			return task;
		}
		ITask task = tasksModel.createTask(taskRepository, taskId);
		task.setSummary(summary);
		return (AbstractTask) task;
	}

}
