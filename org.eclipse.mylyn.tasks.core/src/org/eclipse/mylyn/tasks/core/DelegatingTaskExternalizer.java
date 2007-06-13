/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Subclass externalizers must override the get*TagName() methods for the types
 * of externalized items they support to ensure that their externalizer does not
 * externalize tasks from other connectors incorrectly.
 * 
 * These tag names uniquely identify the externalizer to be used to read the
 * task from externalized form on disk.
 * 
 * The canCreateElementFor methods specify which tasks the externalizer should
 * write to disk.
 * 
 * The TaskList is read on startup, so externalizers extending this should not
 * perform any slow (i.e., network) operations when overriding methods.
 * 
 * @author Mik Kersten
 * @author Ken Sueda (XML serialization support)
 * @author Steffen Pingel
 */
public class DelegatingTaskExternalizer implements ITaskListExternalizer {

	private static final String DEFAULT_PRIORITY = PriorityLevel.P3.toString();

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S z";

	public static final String KEY_QUERY = "Query";

	public static final String KEY_QUERY_HIT = "QueryHit";

	// public static final String KEY_QUERY_MAX_HITS = "MaxHits";

	public static final String KEY_QUERY_STRING = "QueryString";

	public static final String KEY_NOTIFIED_INCOMING = "NotifiedIncoming";

	public static final String KEY_LAST_REFRESH = "LastRefreshTimeStamp";

	public static final String KEY_LABEL = "Label";

	public static final String KEY_HANDLE = "Handle";

	public static final String KEY_REPOSITORY_URL = "RepositoryUrl";

	public static final String KEY_CATEGORY = "Category";

	public static final String VAL_ROOT = "Root";

	public static final String KEY_TASK = "Task";

	public static final String KEY_SUBTASK = "SubTask";

	public static final String KEY_KIND = "Kind";

	public static final String KEY_TASK_CATEGORY = "Task" + KEY_CATEGORY;

	public static final String KEY_LINK = "Link";

	public static final String KEY_PLAN = "Plan";

	public static final String KEY_TIME_ESTIMATED = "Estimated";

	public static final String KEY_ISSUEURL = "IssueURL";

	public static final String KEY_NOTES = "Notes";

	public static final String KEY_ACTIVE = "Active";

	public static final String KEY_COMPLETE = "Complete";

	public static final String KEY_PRIORITY = "Priority";

	public static final String KEY_PATH = "Path";

	public static final String VAL_FALSE = "false";

	public static final String VAL_TRUE = "true";

	public static final String KEY_NAME = "Name";

	public static final String KEY_DATE_END = "EndDate";

	public static final String KEY_DATE_CREATION = "CreationDate";

	public static final String KEY_DATE_REMINDER = "ReminderDate";

	public static final String KEY_DATE_DUE = "DueDate";

	public static final String KEY_REMINDED = "Reminded";

	/**
	 * This element holds the date stamp recorded upon last transition to a
	 * synchronized state.
	 */
	public static final String KEY_LAST_MOD_DATE = "LastModified";

	public static final String KEY_DIRTY = "Dirty";

	public static final String KEY_SYNC_STATE = "offlineSyncState";

	public static final String KEY_OWNER = "Owner";

	private List<ITaskListExternalizer> delegateExternalizers = new ArrayList<ITaskListExternalizer>();

	/**
	 * Set these on the TaskListWriter instead
	 */
	public void setDelegateExternalizers(List<ITaskListExternalizer> externalizers) {
		this.delegateExternalizers = externalizers;
	}

	public Element createCategoryElement(AbstractTaskContainer category, Document doc, Element parent) {
		if (category instanceof TaskArchive) {
			return parent;
		} else if (category instanceof UncategorizedCategory) {
			return parent;
		} else {
			Element node = doc.createElement(getCategoryTagName());
			node.setAttribute(KEY_NAME, category.getSummary());
			parent.appendChild(node);
			return node;
		}
	}

	/**
	 * Override to create specific elements
	 */
	public boolean canCreateElementFor(ITask task) {
		return false;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = doc.createElement(getTaskTagName());
		node.setAttribute(KEY_LABEL, stripControlCharacters(task.getSummary()));
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());

		if (task.getContainer() != null) {
			if (task.getContainer().getHandleIdentifier().equals(UncategorizedCategory.HANDLE)) {
				node.setAttribute(KEY_CATEGORY, VAL_ROOT);
			} else {
				node.setAttribute(KEY_CATEGORY, task.getContainer().getHandleIdentifier());
			}
		} else {
			// TODO: if/when subtasks are supported this should be handled
		}

		node.setAttribute(KEY_PRIORITY, task.getPriority());
		node.setAttribute(KEY_KIND, task.getTaskKind());

		if (task.isCompleted()) {
			node.setAttribute(KEY_COMPLETE, VAL_TRUE);
		} else {
			node.setAttribute(KEY_COMPLETE, VAL_FALSE);
		}
		if (task.isActive()) {
			node.setAttribute(KEY_ACTIVE, VAL_TRUE);
		} else {
			node.setAttribute(KEY_ACTIVE, VAL_FALSE);
		}

		if (task.getTaskUrl() != null) {
			node.setAttribute(KEY_ISSUEURL, task.getTaskUrl());
		}
		node.setAttribute(KEY_NOTES, stripControlCharacters(task.getNotes()));
		node.setAttribute(KEY_TIME_ESTIMATED, "" + task.getEstimateTimeHours());
		node.setAttribute(KEY_DATE_END, formatExternDate(task.getCompletionDate()));
		node.setAttribute(KEY_DATE_CREATION, formatExternDate(task.getCreationDate()));
		node.setAttribute(KEY_DATE_DUE, formatExternDate(task.getDueDate()));
		node.setAttribute(KEY_DATE_REMINDER, formatExternDate(task.getScheduledForDate()));
		if (task.hasBeenReminded()) {
			node.setAttribute(KEY_REMINDED, VAL_TRUE);
		} else {
			node.setAttribute(KEY_REMINDED, VAL_FALSE);
		}

		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask abstractTask = (AbstractRepositoryTask) task;
			if (abstractTask.getLastSyncDateStamp() != null) {
				node.setAttribute(KEY_LAST_MOD_DATE, abstractTask.getLastSyncDateStamp());
			}

			if (abstractTask.isNotified()) {
				node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_TRUE);
			} else {
				node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_FALSE);
			}

			if (abstractTask.getSyncState() != null) {
				node.setAttribute(KEY_SYNC_STATE, abstractTask.getSyncState().toString());
			} else {
				node.setAttribute(KEY_SYNC_STATE, RepositoryTaskSyncState.SYNCHRONIZED.toString());
			}

			if (abstractTask.getOwner() != null) {
				node.setAttribute(KEY_OWNER, abstractTask.getOwner());
			}
			// if (abstractTask.isDirty()) {
			// node.setAttribute(KEY_DIRTY, VAL_TRUE);
			// } else {
			// node.setAttribute(KEY_DIRTY, VAL_FALSE);
			// }
		}

		for (ITask t : task.getChildren()) {
			createSubTaskElement(t, doc, node);
		}

		parent.appendChild(node);
		return node;
	}

	public void readSubTasks(ITask task, NodeList nodes, TaskList tasklist) {
		for (int j = 0; j < nodes.getLength(); j++) {
			Node child = nodes.item(j);
			Element element = (Element) child;
			if (element.hasAttribute(KEY_HANDLE)) {
				String handle = element.getAttribute(KEY_HANDLE);
				ITask subTask = tasklist.getTask(handle);
				if (subTask != null) {
					tasklist.addTask(subTask, (Task)task);
				}
			}
		}
	}

	public void createSubTaskElement(ITask task, Document doc, Element parent) {
		Element node = doc.createElement(KEY_SUBTASK);
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());
		parent.appendChild(node);
	}

	protected String stripControlCharacters(String text) {
		if (text == null)
			return "";
		StringBuilder builder = new StringBuilder(text.length());
		for (int x = 0; x < text.length(); x++) {
			char temp = text.charAt(x);
			if (!Character.isISOControl(temp) || temp == '\n' || temp == '\r' || temp == '\t') {
				builder.append(temp);
			}
		}
		return builder.toString();
	}

	protected String formatExternDate(Date date) {
		if (date == null)
			return "";
		String f = DATE_FORMAT;
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		return format.format(date);
	}

	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(getCategoryTagName());
	}

	public void readCategory(Node node, TaskList taskList) throws TaskExternalizationException {
		boolean hasCaughtException = false;
		Element element = (Element) node;

		AbstractTaskContainer category;
		if (element.hasAttribute(KEY_NAME)) {
			category = new TaskCategory(element.getAttribute(KEY_NAME));
			taskList.internalAddCategory(category);
		} else {
			// LEGACY: registry categories did not have names
			category = taskList.getArchiveContainer();
		}

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				// LEGACY: categories used to contain tasks?
				category.add(readTask(child, taskList, category, null));
				// boolean read = false;
				// for (ITaskListExternalizer externalizer :
				// delegateExternalizers) {
				// // LEGACY: categories used to contain tasks
				// if (externalizer.canReadTask(child)) {
				// externalizer.createTask(child, taskList, category, null);
				// read = true;
				// }
				// }
				// if (!read && canReadTask(child)) {
				// category.add(createTask(child, taskList, category, null));
				// }
			} catch (Throwable t) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException) {
			throw new TaskExternalizationException("Failed to load all tasks");
		}
	}

	/**
	 * First tries to use a delegate externalizer to read, if none available,
	 * reads itself.
	 */
	public final ITask readTask(Node node, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {
		ITask task = null;
		String taskId = null;
		String repositoryUrl = null;
		String summary = "";
		boolean alreadyRead = false;

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

		for (ITaskListExternalizer externalizer : delegateExternalizers) {
			if (!alreadyRead && externalizer.canReadTask(node)) {
				task = externalizer.createTask(repositoryUrl, taskId, summary, element, taskList, category, parent);
				alreadyRead = true;
			}
		}

		if (!alreadyRead && this.canReadTask(node)) {
			task = this.createTask(repositoryUrl, taskId, summary, element, taskList, category, parent);
			alreadyRead = true;
		}
		if (task != null) {
			readTaskInfo(task, taskList, element, parent, category);
		}

		return task;
	}

	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	/**
	 * Override for connector-specific implementation
	 */
	public ITask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList,
			AbstractTaskContainer category, ITask parent) throws TaskExternalizationException {
		String handle;
		if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for task");
		}
		Task task = new LocalTask(handle, summary);
		return task;
	}

	private void readTaskInfo(ITask task, TaskList taskList, Element element, ITask parent,
			AbstractTaskContainer legacyCategory) throws TaskExternalizationException {

		String categoryHandle = null;
		if (element.hasAttribute(KEY_CATEGORY)) {
			categoryHandle = element.getAttribute(KEY_CATEGORY);
			AbstractTaskContainer category = null;
			if (categoryHandle != null) {
				category = taskList.getContainerForHandle(categoryHandle);
			}

			if (category != null) {
				taskList.internalAddTask(task, category);
			} else if (parent == null) {
				taskList.internalAddRootTask(task);
			}
		} else if (legacyCategory != null && !(legacyCategory instanceof TaskArchive)) {
			task.setContainer(legacyCategory);
			legacyCategory.add(task);
		} else if (legacyCategory == null && parent == null) {
			if (task instanceof AbstractRepositoryTask) {
				taskList.internalAddTask(task, taskList.getArchiveContainer());
			} else {
				taskList.internalAddRootTask(task);
			}
		} else {
			taskList.internalAddTask(task, taskList.getArchiveContainer());
		}
		if (element.hasAttribute(KEY_PRIORITY)) {
			task.setPriority(element.getAttribute(KEY_PRIORITY));
		} else {
			task.setPriority(DEFAULT_PRIORITY);
		}

		if (element.hasAttribute(KEY_KIND)) {
			task.setKind(element.getAttribute(KEY_KIND));
		}

		if (element.getAttribute(KEY_ACTIVE).compareTo(VAL_TRUE) == 0) {
			task.setActive(true);
			taskList.setActive(task, true);
		} else {
			task.setActive(false);
		}
		if (element.hasAttribute(KEY_ISSUEURL)) {
			task.setTaskUrl(element.getAttribute(KEY_ISSUEURL));
		} else {
			task.setTaskUrl("");
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
		// NOTE: do not change the order of complete and end date!!
		if (element.getAttribute(KEY_COMPLETE).compareTo(VAL_TRUE) == 0) {
			task.setCompleted(true);
		} else {
			task.setCompleted(false);
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

		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask abstractTask = (AbstractRepositoryTask) task;
			abstractTask.setCurrentlySynchronizing(false);

			if (element.hasAttribute(KEY_REPOSITORY_URL)) {
				abstractTask.setRepositoryUrl(element.getAttribute(KEY_REPOSITORY_URL));
			}

			if (element.hasAttribute(KEY_LAST_MOD_DATE) && !element.getAttribute(KEY_LAST_MOD_DATE).equals("")) {
				abstractTask.setLastSyncDateStamp(element.getAttribute(KEY_LAST_MOD_DATE));
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
					abstractTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
				} else if (syncState.compareTo(RepositoryTaskSyncState.INCOMING.toString()) == 0) {
					abstractTask.setSyncState(RepositoryTaskSyncState.INCOMING);
				} else if (syncState.compareTo(RepositoryTaskSyncState.OUTGOING.toString()) == 0) {
					abstractTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
				} else if (syncState.compareTo(RepositoryTaskSyncState.CONFLICT.toString()) == 0) {
					abstractTask.setSyncState(RepositoryTaskSyncState.CONFLICT);
				}
			}
		}

// NodeList list = element.getChildNodes();
// for (int j = 0; j < list.getLength(); j++) {
// Node child = list.item(j);
// task.addSubTask(readTask(child, taskList, null, task));
// }
	}

	// /**
	// * @return task ID, or null if not found
	// */
	// protected void readLegacyHandleFormat(ITask task, Element element) throws
	// TaskExternalizationException {
	// if (task instanceof AbstractRepositoryTask) {
	// AbstractRepositoryTask abstractTask = (AbstractRepositoryTask) task;
	//			
	// if (element.hasAttribute(KEY_HANDLE)) {
	// String handle = element.getAttribute(KEY_HANDLE);
	// String repositoryUrl = RepositoryTaskHandleUtil.getRepositoryUrl(handle);
	// String taskId = RepositoryTaskHandleUtil.getTaskId(handle);
	// abstractTask.setRepositoryUrl(repositoryUrl);
	// abstractTask.setTaskId(taskId);
	// } else {
	// throw new TaskExternalizationException("Handle not stored for repository
	// task");
	// }
	// }
	// }

	// protected void readTaskData(AbstractRepositoryTask task) {
	// RepositoryTaskData data =
	// taskDataManager.getRepositoryTaskData(task.getHandleIdentifier());
	// // RepositoryTaskData data =
	// //
	// TasksUiPlugin.getDefault().getTaskDataManager().getTaskData(task.getHandleIdentifier());
	// task.setTaskData(data);
	//		
	// if (data != null && data.hasLocalChanges()) {
	// task.setSyncState(RepositoryTaskSyncState.OUTGOING);
	// }
	// }

	protected Date getDateFromString(String dateString) {
		Date date = null;
		if ("".equals(dateString))
			return null;
		String formatString = DATE_FORMAT;
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			MylarStatusHandler.fail(e, "Could not parse end date", false);
		}
		return date;
	}

	public String getCategoryTagName() {
		return KEY_TASK_CATEGORY;
	}

	public String getTaskTagName() {
		return KEY_TASK;
	}

	public boolean canCreateElementFor(AbstractRepositoryQuery query) {
		return false;
	}

	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		String queryTagName = getQueryTagNameForElement(query);
		Element node = doc.createElement(queryTagName);
		node.setAttribute(KEY_NAME, query.getSummary());
		// node.setAttribute(KEY_QUERY_MAX_HITS, query.getMaxHits() + "");
		node.setAttribute(KEY_QUERY_STRING, query.getUrl());
		node.setAttribute(KEY_REPOSITORY_URL, query.getRepositoryUrl());
		if (query.getLastRefreshTimeStamp() != null) {
			node.setAttribute(KEY_LAST_REFRESH, query.getLastRefreshTimeStamp());
		}
		for (AbstractRepositoryTask hit : query.getHits()) {
			try {
// Element element = null;
// for (ITaskListExternalizer externalizer : delegateExternalizers) {
// if (externalizer.canCreateElementFor(hit))
// element = externalizer.createQueryHitElement(hit, doc, node);
// }
// if (element == null)
				createQueryHitElement(hit, doc, node);
			} catch (Exception e) {
				MylarStatusHandler.log(e, e.getMessage());
			}
		}
		parent.appendChild(node);
		return node;
	}

	public boolean canReadQuery(Node node) {
		return false;
	}

	/**
	 * This happens on startup, so connectors should not perform any network
	 * operations when reading queries.
	 */
	public AbstractRepositoryQuery readQuery(Node node, TaskList tlist) throws TaskExternalizationException {
		// doesn't know how to read any queries
		return null;
	}

	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		return KEY_QUERY;
	}

	private String getQueryHitTagName() {
		return KEY_QUERY_HIT;
	}

	public Element createQueryHitElement(AbstractRepositoryTask queryHit, Document doc, Element parent) {
		Element node = doc.createElement(getQueryHitTagName());
		node.setAttribute(KEY_HANDLE, queryHit.getHandleIdentifier());
		parent.appendChild(node);
		return node;
	}

	public boolean canReadQueryHit(Node node) {
		return false;
	}

	public final void readQueryHit(Element element, TaskList taskList, AbstractRepositoryQuery query)
			throws TaskExternalizationException {

		if (element.hasAttribute(KEY_HANDLE)) {
			String handle = element.getAttribute(KEY_HANDLE);
			ITask hit = taskList.getTask(handle);
			if (hit != null) {
				taskList.addTask(hit, query);
			}

		} else {
			throw new TaskExternalizationException("Handle not stored for repository task");
		}
	}

	public List<ITaskListExternalizer> getDelegateExternalizers() {
		return delegateExternalizers;
	}
}
