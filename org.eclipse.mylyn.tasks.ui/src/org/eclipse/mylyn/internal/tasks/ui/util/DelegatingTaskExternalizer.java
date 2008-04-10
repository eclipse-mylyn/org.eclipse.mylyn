/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.web.core.XmlUtil;
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
final class DelegatingTaskExternalizer {

	static final String DEFAULT_PRIORITY = PriorityLevel.P3.toString();

	static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S z";

	static final String KEY_NOTIFIED_INCOMING = "NotifiedIncoming";

	static final String KEY_NAME = "Name";

	static final String KEY_LABEL = "Label";

	static final String KEY_HANDLE = "Handle";

	static final String KEY_REPOSITORY_URL = "RepositoryUrl";

	static final String KEY_CATEGORY = "Category";

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

	static final String KEY_DATE_CREATION = "CreationDate";

	static final String KEY_DATE_REMINDER = "ReminderDate";

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

	static final String KEY_STALE = "Stale";

	static final String KEY_LAST_REFRESH = "LastRefreshTimeStamp";

	private List<AbstractTaskListFactory> factories = new ArrayList<AbstractTaskListFactory>();

	public void setFactories(List<AbstractTaskListFactory> externalizers) {
		this.factories = externalizers;
	}

	public Element createCategoryElement(AbstractTaskContainer category, Document doc, Element parent) {
		if (category instanceof TaskArchive) {
			return parent;
		} else if (category instanceof UncategorizedTaskContainer) {
			return parent;
		} else {
			Element node = doc.createElement(getCategoryTagName());
			node.setAttribute(DelegatingTaskExternalizer.KEY_NAME, category.getSummary());
			parent.appendChild(node);
			return node;
		}
	}

	public Element createTaskElement(AbstractTask task, Document doc, Element parent) {
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
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "No externalizer for task: " + task));
			return null;
		}
		Element node = doc.createElement(taskTagName);
		factory.setAdditionalAttributes(task, node);

		node.setAttribute(KEY_LABEL, stripControlCharacters(task.getSummary()));
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());
		node.setAttribute(KEY_REPOSITORY_URL, task.getRepositoryUrl());

		AbstractTaskContainer container = TaskCategory.getParentTaskCategory(task);

		if (container != null) {
			if (container.getHandleIdentifier().equals(UncategorizedTaskContainer.HANDLE)) {
				node.setAttribute(KEY_CATEGORY, VAL_ROOT);
			} else {
				node.setAttribute(KEY_CATEGORY, container.getHandleIdentifier());
			}
		} else {
			// TODO: if/when subtasks are supported this should be handled
		}

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
		node.setAttribute(KEY_DATE_DUE, formatExternDate(task.getDueDate()));
		node.setAttribute(KEY_DATE_REMINDER, formatExternDate(task.getScheduledForDate()));
		if (task.isReminded()) {
			node.setAttribute(KEY_REMINDED, VAL_TRUE);
		} else {
			node.setAttribute(KEY_REMINDED, VAL_FALSE);
		}
		if (task.internalIsFloatingScheduledDate()) {
			node.setAttribute(KEY_FLOATING, VAL_TRUE);
		} else {
			node.setAttribute(KEY_FLOATING, VAL_FALSE);
		}
		if (task.isStale()) {
			node.setAttribute(KEY_STALE, VAL_TRUE);
		} else {
			node.setAttribute(KEY_STALE, VAL_FALSE);
		}

		AbstractTask abstractTask = task;
		if (abstractTask.getLastReadTimeStamp() != null) {
			node.setAttribute(KEY_LAST_MOD_DATE, abstractTask.getLastReadTimeStamp());
		}

		if (abstractTask.isNotified()) {
			node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_TRUE);
		} else {
			node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_FALSE);
		}

		if (abstractTask.getSynchronizationState() != null) {
			node.setAttribute(KEY_SYNC_STATE, abstractTask.getSynchronizationState().toString());
		} else {
			node.setAttribute(KEY_SYNC_STATE, RepositoryTaskSyncState.SYNCHRONIZED.toString());
		}

		if (abstractTask.getOwner() != null) {
			node.setAttribute(KEY_OWNER, abstractTask.getOwner());
		}

		for (AbstractTask t : task.getChildren()) {
			createSubTaskElement(t, doc, node);
		}

		parent.appendChild(node);
		return node;
	}

	public void readSubTasks(AbstractTask task, NodeList nodes, TaskList tasklist) {
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

	public void createSubTaskElement(AbstractTask task, Document doc, Element parent) {
		Element node = doc.createElement(KEY_SUBTASK);
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());
		parent.appendChild(node);
	}

	private String stripControlCharacters(String text) {
		if (text == null) {
			return "";
		}
		return XmlUtil.cleanXmlString(text);
	}

	private String formatExternDate(Date date) {
		if (date == null) {
			return "";
		}
		String f = DATE_FORMAT;
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		return format.format(date);
	}

	public void readCategory(Node node, TaskList taskList) throws TaskExternalizationException {
		boolean hasCaughtException = false;
		Element element = (Element) node;

		AbstractTaskCategory category = null;
		if (element.hasAttribute(DelegatingTaskExternalizer.KEY_NAME)) {
			category = new TaskCategory(element.getAttribute(DelegatingTaskExternalizer.KEY_NAME));
			taskList.internalAddCategory((TaskCategory) category);
		} else {
			// LEGACY: registry categories did not have names
			// category = taskList.getArchiveContainer();
			// a null category will now go into appropriate orphaned category
		}

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				// LEGACY: categories used to contain tasks?
				AbstractTask task = readTask(child, category, null);
				if (category != null) {
					category.internalAddChild(task);
				}
				taskList.insertTask(task, category, null);
			} catch (Throwable t) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException) {
			throw new TaskExternalizationException("Failed to load all tasks");
		}
	}

	public final AbstractTask readTask(Node node, AbstractTaskCategory legacyCategory, AbstractTask parent)
			throws TaskExternalizationException {
		AbstractTask task = null;
		String taskId = null;
		String repositoryUrl = null;
		String summary = "";
//		boolean alreadyRead = false;

		Element element = (Element) node;
		if (element.hasAttribute(KEY_HANDLE)) {
			String handle = element.getAttribute(KEY_HANDLE);
			repositoryUrl = RepositoryTaskHandleUtil.getRepositoryUrl(handle);
			taskId = RepositoryTaskHandleUtil.getTaskId(handle);
		} else {
			throw new TaskExternalizationException("Handle not stored for repository task");
		}
		if (element.hasAttribute(KEY_LABEL)) {
			summary = element.getAttribute(KEY_LABEL);
		}

		for (AbstractTaskListFactory externalizer : factories) {
			if (node.getNodeName().equals(externalizer.getTaskElementName())) {
				task = externalizer.createTask(repositoryUrl, taskId, summary, element);
				break;
			}
		}

//		if (!alreadyRead && this.canReadTask(node)) {
//			task = this.createTask(repositoryUrl, taskId, summary, element, taskList, category, parent);
//			alreadyRead = true;
//		}
		if (task != null) {
			readTaskInfo(task, element, parent, legacyCategory);
		}

		return task;
	}

	private void readTaskInfo(AbstractTask task, Element element, AbstractTask parent,
			AbstractTaskCategory legacyCategory) throws TaskExternalizationException {
		if (task == null) {
			return;
		}

		String categoryHandle = element.getAttribute(KEY_CATEGORY);
		if (categoryHandle.equals(VAL_ROOT)) {
			categoryHandle = UncategorizedTaskContainer.HANDLE;
		}
		task.setCategoryHandle(categoryHandle);

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
		if (element.hasAttribute(KEY_DATE_DUE)) {
			task.setDueDate(getDateFromString(element.getAttribute(KEY_DATE_DUE)));
		} else {
			task.setDueDate(null);
		}
		if (element.hasAttribute(KEY_DATE_REMINDER)) {
			task.setScheduledForDate(getDateFromString(element.getAttribute(KEY_DATE_REMINDER)));
		} else {
			task.setScheduledForDate(null);
		}
		if (element.hasAttribute(KEY_REMINDED) && element.getAttribute(KEY_REMINDED).compareTo(VAL_TRUE) == 0) {
			task.setReminded(true);
		} else {
			task.setReminded(false);
		}
		if (element.hasAttribute(KEY_FLOATING) && element.getAttribute(KEY_FLOATING).compareTo(VAL_TRUE) == 0) {
			task.internalSetFloatingScheduledDate(true);
		} else {
			task.internalSetFloatingScheduledDate(false);
		}
		if (element.hasAttribute(KEY_STALE) && element.getAttribute(KEY_STALE).compareTo(VAL_TRUE) == 0) {
			task.setStale(true);
		} else {
			task.setStale(false);
		}

		AbstractTask abstractTask = task;
		abstractTask.setSynchronizing(false);

		if (element.hasAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL)) {
			abstractTask.setRepositoryUrl(element.getAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL));
		}

		if (element.hasAttribute(KEY_LAST_MOD_DATE) && !element.getAttribute(KEY_LAST_MOD_DATE).equals("")) {
			abstractTask.setLastReadTimeStamp(element.getAttribute(KEY_LAST_MOD_DATE));
		}

		if (element.hasAttribute(KEY_OWNER)) {
			abstractTask.setOwner(element.getAttribute(KEY_OWNER));
		}

		if (VAL_TRUE.equals(element.getAttribute(KEY_NOTIFIED_INCOMING))) {
			abstractTask.setNotified(true);
		} else {
			abstractTask.setNotified(false);
		}

		if (element.hasAttribute(KEY_SYNC_STATE)) {
			String syncState = element.getAttribute(KEY_SYNC_STATE);
			if (syncState.compareTo(RepositoryTaskSyncState.SYNCHRONIZED.toString()) == 0) {
				abstractTask.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
			} else if (syncState.compareTo(RepositoryTaskSyncState.INCOMING.toString()) == 0) {
				abstractTask.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
			} else if (syncState.compareTo(RepositoryTaskSyncState.OUTGOING.toString()) == 0) {
				abstractTask.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);
			} else if (syncState.compareTo(RepositoryTaskSyncState.CONFLICT.toString()) == 0) {
				abstractTask.setSynchronizationState(RepositoryTaskSyncState.CONFLICT);
			}
		}

// NodeList list = element.getChildNodes();
// for (int j = 0; j < list.getLength(); j++) {
// Node child = list.item(j);
// task.addSubTask(readTask(child, taskList, null, task));
// }
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
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not parse end date", e));
		}
		return date;
	}

	private String getCategoryTagName() {
		return KEY_TASK_CATEGORY;
	}

	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		AbstractTaskListFactory factory = null;
		String queryTagName = null;
		for (AbstractTaskListFactory currentFactory : factories) {
			if (currentFactory.canCreate(query)) {
				factory = currentFactory;
				queryTagName = factory.getQueryElementName(query);
				break;
			}
		}
		if (factory == null || queryTagName == null) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "Could not externalize query: "
					+ query));
			return null;
		}

		Element node = doc.createElement(queryTagName);
		factory.setAdditionalAttributes(query, node);

		node.setAttribute(DelegatingTaskExternalizer.KEY_NAME, query.getSummary());
		node.setAttribute(AbstractTaskListFactory.KEY_QUERY_STRING, query.getUrl());
		node.setAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL, query.getRepositoryUrl());
		if (query.getLastSynchronizedTimeStamp() != null) {
			node.setAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH, query.getLastSynchronizedTimeStamp());
		}
		for (AbstractTask hit : query.getChildren()) {
			try {
				createQueryHitElement(hit, doc, node);
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
		}
		parent.appendChild(node);
		return node;
	}

//	public boolean canReadQuery(Node node) {
//		return false;
//	}

//	public AbstractRepositoryQuery readQuery(Node node, TaskList tlist) throws TaskExternalizationException {
//		// doesn't know how to read any queries
//		return null;
//	}

//	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
//		return AbstractTaskListElementFactory.KEY_QUERY;
//	}

//	private String getQueryHitTagName() {
//		return AbstractTaskListElementFactory.KEY_QUERY_HIT;
//	}

	public Element createQueryHitElement(AbstractTask queryHit, Document doc, Element parent) {
		Element node = doc.createElement(KEY_QUERY_HIT);
		node.setAttribute(KEY_HANDLE, queryHit.getHandleIdentifier());
		parent.appendChild(node);
		return node;
	}

//	public boolean canReadQueryHit(Node node) {
//		return false;
//	}

	public final void readQueryHit(Element element, TaskList taskList, AbstractRepositoryQuery query)
			throws TaskExternalizationException {

		if (element.hasAttribute(KEY_HANDLE)) {
			String handle = element.getAttribute(KEY_HANDLE);
			AbstractTask hit = taskList.getTask(handle);
			if (hit != null) {
				taskList.addTask(hit, query);
			}
		} else {
			throw new TaskExternalizationException("Handle not stored for repository task");
		}
	}

	public List<AbstractTaskListFactory> getDelegateExternalizers() {
		return factories;
	}
}
