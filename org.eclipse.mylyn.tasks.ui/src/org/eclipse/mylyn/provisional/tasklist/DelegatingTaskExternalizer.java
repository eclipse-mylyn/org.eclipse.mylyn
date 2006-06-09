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

package org.eclipse.mylar.provisional.tasklist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskExternalizationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten
 * @author Ken Sueda (XML serialization support)
 */
public class DelegatingTaskExternalizer implements ITaskListExternalizer {

	private static final String DEFAULT_PRIORITY = Task.PriorityLevel.P3.toString();

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S z";

	public static final String KEY_QUERY = "Query";

	public static final String KEY_QUERY_HIT = "QueryHit";

	public static final String KEY_QUERY_MAX_HITS = "MaxHits";

	public static final String KEY_QUERY_STRING = "QueryString";

	public static final String KEY_LAST_REFRESH = "LastRefresh";

	public static final String KEY_LABEL = "Label";

	public static final String KEY_HANDLE = "Handle";

	public static final String KEY_REPOSITORY_URL = "RepositoryUrl";

	public static final String KEY_CATEGORY = "Category";

	public static final String VAL_ROOT = "Root";

	public static final String KEY_TASK = "Task";

	public static final String KEY_KIND = "Kind";

	public static final String KEY_TASK_CATEGORY = "Task" + KEY_CATEGORY;

	public static final String KEY_LINK = "Link";

	public static final String KEY_PLAN = "Plan";

	public static final String KEY_TIME_ESTIMATED = "Estimated";

	public static final String KEY_TIME_ELAPSED = "Elapsed";

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

	public static final String KEY_REMINDED = "Reminded";

	public static final String LABEL_AUTOMATIC = "<automatic>";

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
		} else {
			Element node = doc.createElement(getCategoryTagName());
			node.setAttribute(KEY_NAME, category.getDescription());
			parent.appendChild(node);
			return node;
		}
	}

	public boolean canCreateElementFor(ITask task) {
		return true;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = doc.createElement(getTaskTagName());
		node.setAttribute(KEY_LABEL, task.getDescription());
		node.setAttribute(KEY_HANDLE, task.getHandleIdentifier());

		if (task.getContainer() != null) {
			if (task.getContainer().getHandleIdentifier().equals(TaskList.LABEL_ROOT)) {
				node.setAttribute(KEY_CATEGORY, VAL_ROOT);
			} else {
				node.setAttribute(KEY_CATEGORY, task.getContainer().getHandleIdentifier());
			}
		} else {
			// TODO: if/when subtasks are supported this should be handled
		}

		node.setAttribute(KEY_PRIORITY, task.getPriority());
		node.setAttribute(KEY_KIND, task.getTaskType());

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

		if (task.getUrl() != null) {
			node.setAttribute(KEY_ISSUEURL, task.getUrl());
		}
		node.setAttribute(KEY_NOTES, task.getNotes());
		node.setAttribute(KEY_TIME_ELAPSED, "" + task.getElapsedTime());
		node.setAttribute(KEY_TIME_ESTIMATED, "" + task.getEstimateTimeHours());
		node.setAttribute(KEY_DATE_END, formatExternDate(task.getCompletionDate()));
		node.setAttribute(KEY_DATE_CREATION, formatExternDate(task.getCreationDate()));
		node.setAttribute(KEY_DATE_REMINDER, formatExternDate(task.getReminderDate()));
		if (task.hasBeenReminded()) {
			node.setAttribute(KEY_REMINDED, VAL_TRUE);
		} else {
			node.setAttribute(KEY_REMINDED, VAL_FALSE);
		}

		for (ITask t : task.getChildren()) {
			createTaskElement(t, doc, node);
		}
		parent.appendChild(node);
		return node;
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
			category = new TaskCategory(element.getAttribute(KEY_NAME), taskList);
			taskList.internalAddCategory(category);
		} else {
			// LEGACY: registry categories did not have names
			category = taskList.getArchiveContainer();
		}

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			boolean read = false;
			try {
				for (ITaskListExternalizer externalizer : delegateExternalizers) {
					// LEGACY: categories used to contain tasks
					if (externalizer.canReadTask(child)) {
						externalizer.readTask(child, taskList, category, null);
						read = true;
					}
				}
				if (!read && canReadTask(child)) {
					category.add(readTask(child, taskList, category, null));
				}
			} catch (TaskExternalizationException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException)
			throw new TaskExternalizationException("Failed to load all tasks");
	}

	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	public ITask readTask(Node node, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for task");
		}
		if (element.hasAttribute(KEY_LABEL)) {
			label = element.getAttribute(KEY_LABEL);
		} else {
			label = "Description was corrupted in stored tasklist";
		}

		Task task = new Task(handle, label, false);
		readTaskInfo(task, taskList, element, parent, category);
		return task;
	}

	protected void readTaskInfo(ITask task, TaskList taskList, Element element, ITask parent,
			AbstractTaskContainer legacyCategory) throws TaskExternalizationException {

		String categoryHandle = null;
		if (element.hasAttribute(KEY_CATEGORY)) {
			categoryHandle = element.getAttribute(KEY_CATEGORY);
			AbstractTaskContainer category = null;
			if (categoryHandle != null) {
				category = taskList.getContainerForHandle(categoryHandle);
			}

			if (category != null) {
				if (category.equals(VAL_ROOT)) {
					taskList.internalAddRootTask(task);
				} else {
					taskList.internalAddTask(task, category);
				}
			} else if (parent == null) {
				taskList.internalAddRootTask(task);
			}
		} else if (legacyCategory != null && !(legacyCategory instanceof TaskArchive)) { // &&
																							// !legacyCategory.getHandleIdentifier().equals(TaskList.LABEL_ARCHIVE))
																							// {
			task.setContainer(legacyCategory);
			legacyCategory.add(task);
		} else if (legacyCategory == null && parent == null) {
			if (task instanceof AbstractRepositoryTask) {
				taskList.internalAddTask(task, taskList.getArchiveContainer());
			} else {
				taskList.internalAddRootTask(task);
			}
		} else if (parent != null) {
			task.setParent(parent);
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
			task.setUrl(element.getAttribute(KEY_ISSUEURL));
		} else {
			task.setUrl("");
		}
		if (element.hasAttribute(KEY_NOTES)) {
			task.setNotes(element.getAttribute(KEY_NOTES));
		} else {
			task.setNotes("");
		}
		if (element.hasAttribute(KEY_TIME_ELAPSED)) {
			long elapsed = 0;
			try {
				long read = Long.parseLong(element.getAttribute(KEY_TIME_ELAPSED));
				if (read > 0)
					elapsed = read;
			} catch (NumberFormatException e) {
				// ignore
			}
			task.setElapsedTime(elapsed);
		} else {
			task.setElapsedTime(0);
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
		if (element.hasAttribute(KEY_DATE_REMINDER)) {
			task.setReminderDate(getDateFromString(element.getAttribute(KEY_DATE_REMINDER)));
		} else {
			task.setReminderDate(null);
		}
		if (element.hasAttribute(KEY_REMINDED) && element.getAttribute(KEY_REMINDED).compareTo(VAL_TRUE) == 0) {
			task.setReminded(true);
		} else {
			task.setReminded(false);
		}

		NodeList list = element.getChildNodes();
		for (int j = 0; j < list.getLength(); j++) {
			Node child = list.item(j);
			task.addSubTask(readTask(child, taskList, null, task));
		}
	}

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
	
	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return true;
	}

	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		String queryTagName = getQueryTagNameForElement(query);
		Element node = doc.createElement(queryTagName);
		node.setAttribute(KEY_NAME, query.getDescription());
		node.setAttribute(KEY_QUERY_MAX_HITS, query.getMaxHits() + "");
		node.setAttribute(KEY_QUERY_STRING, query.getQueryUrl());
		node.setAttribute(KEY_REPOSITORY_URL, query.getRepositoryUrl());
		if (query.getLastSynchronized() != null) {
			node.setAttribute(KEY_LAST_REFRESH, String.valueOf(query.getLastSynchronized().getTime()));
		}
		for (AbstractQueryHit hit : new ArrayList<AbstractQueryHit>(query.getHits())) {
			try {
				Element element = null;
				for (ITaskListExternalizer externalizer : delegateExternalizers) {
					if (externalizer.canCreateElementFor(hit))
						element = externalizer.createQueryHitElement(hit, doc, node);
				}
				if (element == null)
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

	public AbstractRepositoryQuery readQuery(Node node, TaskList tlist) throws TaskExternalizationException {
		// doesn't know how to read any queries
		return null;
	}

	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		return KEY_QUERY;
	}

	public String getQueryHitTagName() {
		return KEY_QUERY_HIT;
	}

	public boolean canCreateElementFor(AbstractQueryHit queryHit) {
		return true;
	}

	public Element createQueryHitElement(AbstractQueryHit queryHit, Document doc, Element parent) {
		Element node = doc.createElement(getQueryHitTagName());
		node.setAttribute(KEY_NAME, queryHit.getDescription());
		node.setAttribute(KEY_HANDLE, queryHit.getHandleIdentifier());
		node.setAttribute(KEY_PRIORITY, queryHit.getPriority());
		if (queryHit.isCompleted()) {
			node.setAttribute(KEY_COMPLETE, VAL_TRUE);
		} else {
			node.setAttribute(KEY_COMPLETE, VAL_FALSE);
		}
		parent.appendChild(node);
		return null;
	}

	public boolean canReadQueryHit(Node node) {
		return false;
	}

	public void readQueryHit(Node node, TaskList tlist, AbstractRepositoryQuery query)
			throws TaskExternalizationException {
		// doesn't know how to read a query hit
	}

	public List<ITaskListExternalizer> getDelegateExternalizers() {
		return delegateExternalizers;
	}
}
