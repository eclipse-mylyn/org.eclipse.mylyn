/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.ITaskListExternalizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten
 * @author Ken Sueda (XML serialization support)
 */
public class DelegatingLocalTaskExternalizer implements ITaskListExternalizer {

	private static final String DEFAULT_PRIORITY = "P3";

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S z";

	public static final String TAG_QUERY = "Query";

	public static final String TAG_QUERY_HIT = "QueryHit";

	public static final String MAX_HITS = "MaxHits";

	public static final String QUERY_STRING = "QueryString";

	public static final String LABEL = "Label";

	public static final String HANDLE = "Handle";

	public static final String TAG_CATEGORY = "Category";

	public static final String TAG_TASK = "Task";

	public static final String TAG_TASK_CATEGORY = "Task" + TAG_CATEGORY;

	public static final String LINK = "Link";

	public static final String PLAN = "Plan";

	public static final String ESTIMATED = "Estimated";

	public static final String ELAPSED = "Elapsed";

	public static final String ISSUEURL = "IssueURL";

	public static final String NOTES = "Notes";

	public static final String BUGZILLA = "Bugzilla";

	public static final String ACTIVE = "Active";

	public static final String COMPLETE = "Complete";

	public static final String PRIORITY = "Priority";

	public static final String PATH = "Path";

	public static final String FALSE = "false";

	public static final String TRUE = "true";

	public static final String NAME = "Name";

	public static final String END_DATE = "EndDate";

	public static final String CREATION_DATE = "CreationDate";

	public static final String REMINDER_DATE = "ReminderDate";

	public static final String REMINDED = "Reminded";

	public static final String LABEL_AUTOMATIC = "<automatic>";

	private List<ITaskListExternalizer> delegateExternalizers = new ArrayList<ITaskListExternalizer>();

	/**
	 * Set these on the TaskListWriter instead
	 */
	void setDelegateExternalizers(List<ITaskListExternalizer> externalizers) {
		this.delegateExternalizers = externalizers;
	}

	public boolean canCreateElementFor(ITaskCategory category) {
		return category instanceof TaskCategory;
	}

	public Element createCategoryElement(ITaskCategory category, Document doc, Element parent) {
		if (category.isArchive())
			return parent;
		Element node = doc.createElement(getCategoryTagName());
		node.setAttribute(NAME, category.getDescription(false));

		for (ITask task : ((TaskCategory) category).getChildren()) {
			try {
				Element element = null;
				for (ITaskListExternalizer externalizer : delegateExternalizers) {
					if (externalizer.canCreateElementFor(task)) {
						element = externalizer.createTaskElement(task, doc, node);
					}
				}
				if (element == null)
					createTaskElement(task, doc, node);
			} catch (Exception e) {
				ErrorLogger.log(e, e.getMessage());
			}

		}
		parent.appendChild(node);
		return node;
	}

	public boolean canCreateElementFor(ITask task) {
		return true;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = doc.createElement(getTaskTagName());
//		node.setAttribute(PATH, task.getRemoteContextPath());
		node.setAttribute(LABEL, task.getDescription(false));
		node.setAttribute(HANDLE, task.getHandleIdentifier());
		node.setAttribute(PRIORITY, task.getPriority());

		if (task.isCompleted()) {
			node.setAttribute(COMPLETE, TRUE);
		} else {
			node.setAttribute(COMPLETE, FALSE);
		}
		if (task.isActive()) {
			node.setAttribute(ACTIVE, TRUE);
		} else {
			node.setAttribute(ACTIVE, FALSE);
		}
		node.setAttribute(BUGZILLA, FALSE); // TODO: this is not great

		node.setAttribute(ISSUEURL, task.getIssueReportURL());
		node.setAttribute(NOTES, task.getNotes());
		node.setAttribute(ELAPSED, "" + task.getElapsedTime());
		node.setAttribute(ESTIMATED, "" + task.getEstimateTimeHours());
		node.setAttribute(END_DATE, formatExternDate(task.getCompletionDate()));
		node.setAttribute(CREATION_DATE, formatExternDate(task.getCreationDate()));
		node.setAttribute(REMINDER_DATE, formatExternDate(task.getReminderDate()));
		if (task.hasBeenReminded()) {
			node.setAttribute(REMINDED, TRUE);
		} else {
			node.setAttribute(REMINDED, FALSE);
		}
		List<String> rl = task.getRelatedLinks();
		int i = 0;
		for (String link : rl) {
			node.setAttribute(LINK + i, link);
			i++;
		}
		List<String> plans = task.getPlans();
		int currPlan = 0;
		for (String plan : plans) {
			node.setAttribute(PLAN + currPlan, plan);
			currPlan++;
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

	public void readCategory(Node node, TaskList tlist) throws TaskListExternalizerException {
		boolean hasCaughtException = false;
		Element element = (Element) node;
		TaskCategory category = new TaskCategory(element.getAttribute("Name"));
		tlist.internalAddCategory(category);
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			boolean read = false;
			try {
				for (ITaskListExternalizer externalizer : delegateExternalizers) {
					if (externalizer.canReadTask(child)) {
						ITask task = externalizer.readTask(child, tlist, category, null);
						category.addTask(task);
						if (!category.isArchive()) task.setCategory(category);
						read = true;
					}
				}
				if (!read && canReadTask(child)) {
					category.internalAddTask(readTask(child, tlist, category, null));
				}
			} catch (TaskListExternalizerException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException)
			throw new TaskListExternalizerException("Failed to load all tasks");
	}

	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	public ITask readTask(Node node, TaskList tlist, ITaskCategory category, ITask parent)
			throws TaskListExternalizerException {
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new TaskListExternalizerException("Handle not stored for task");
		}
		if (element.hasAttribute(LABEL)) {
			label = element.getAttribute(LABEL);
		} else {
			label = "Description was corrupted in stored tasklist";
		}
		Task task = new Task(handle, label, false);
		readTaskInfo(task, tlist, element, category, parent);
		return task;
	}

	protected void readTaskInfo(ITask task, TaskList tlist, Element element, ITaskCategory category, ITask parent)
			throws TaskListExternalizerException {
		if (element.hasAttribute(PRIORITY)) {
			task.setPriority(element.getAttribute(PRIORITY));
		} else {
			task.setPriority(DEFAULT_PRIORITY);
		}
//		if (element.hasAttribute(PATH)) {
//			task.setRemoteContextPath(element.getAttribute(PATH));
//		} else {
//			task.setRemoteContextPath(task.getHandleIdentifier());
//		}

		if (element.getAttribute(ACTIVE).compareTo(TRUE) == 0) {
			task.setActive(true);
			tlist.setActive(task, true);
		} else {
			task.setActive(false);
		}
		if (element.hasAttribute(ISSUEURL)) {
			task.setIssueReportURL(element.getAttribute(ISSUEURL));
		} else {
			task.setIssueReportURL("");
		}
		if (element.hasAttribute(NOTES)) {
			task.setNotes(element.getAttribute(NOTES));
		} else {
			task.setNotes("");
		}
		if (element.hasAttribute(ELAPSED)) {
			long elapsed = 0;
			try {
				long read = Long.parseLong(element.getAttribute(ELAPSED));
				if (read > 0) elapsed = read;
			} catch (NumberFormatException e) {
				// ignore
			}
			task.setElapsedTime(elapsed);
		} else {
			task.setElapsedTime(0);
		}
		if (element.hasAttribute(ESTIMATED)) {
			String est = element.getAttribute(ESTIMATED);
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
		if (element.getAttribute(COMPLETE).compareTo(TRUE) == 0) {
			task.setCompleted(true);
		} else {
			task.setCompleted(false);
		}
		if (element.hasAttribute(END_DATE)) {
			task.setCompletionDate(getDateFromString(element.getAttribute(END_DATE)));
			// task.setEndDate(element.getAttribute(END_DATE));
		} else {
			task.setCompletionDate(null);
		}
		if (element.hasAttribute(CREATION_DATE)) {
			task.setCreationDate(getDateFromString(element.getAttribute(CREATION_DATE)));
			// task.setCreationDate(element.getAttribute(CREATION_DATE));
		} else {
			task.setCreationDate(Calendar.getInstance().getTime());
		}
		if (element.hasAttribute(REMINDER_DATE)) {
			task.setReminderDate(getDateFromString(element.getAttribute(REMINDER_DATE)));
			// task.setReminderDate(element.getAttribute(REMINDER_DATE));
		} else {
			task.setReminderDate(null);
		}
		if (element.hasAttribute(REMINDED) && element.getAttribute(REMINDED).compareTo(TRUE) == 0) {
			task.setReminded(true);
		} else {
			task.setReminded(false);
		}
		int i = 0;
		while (element.hasAttribute(LINK + i)) {
			task.getRelatedLinks().add(element.getAttribute(LINK + i));
			i++;
		}
		int ii = 0;
		while (element.hasAttribute(PLAN + ii)) {
			task.getPlans().add(element.getAttribute(PLAN + i));
			ii++;
		}
		if (category != null) {
			task.setCategory((TaskCategory) category);
		} else {
			task.setCategory(null);
		}
		task.setParent(parent);
		NodeList list = element.getChildNodes();
		for (int j = 0; j < list.getLength(); j++) {
			Node child = list.item(j);
			task.addSubTask(readTask(child, tlist, null, task));
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
			ErrorLogger.fail(e, "Could not parse end date", false);
		}
		return date;
	}

	public String getCategoryTagName() {
		return TAG_TASK_CATEGORY;
	}

	public String getTaskTagName() {
		return TAG_TASK;
	}

	public void createRegistry(Document doc, Node parent) {
		// nothing to do
	}

	public boolean canCreateElementFor(IQuery category) {
		return true;
	}

	public Element createQueryElement(IQuery query, Document doc, Element parent) {
		String queryTagName = getQueryTagNameForElement(query);
		Element node = doc.createElement(queryTagName);
		node.setAttribute(NAME, query.getDescription(false));
		node.setAttribute(MAX_HITS, query.getMaxHits() + "");
		node.setAttribute(QUERY_STRING, query.getQueryUrl());
		for (IQueryHit hit : query.getHits()) {
			try {
				Element element = null;
				for (ITaskListExternalizer externalizer : delegateExternalizers) {
					if (externalizer.canCreateElementFor(hit))
						element = externalizer.createQueryHitElement(hit, doc, node);
				}
				if (element == null)
					createQueryHitElement(hit, doc, node);
			} catch (Exception e) {
				ErrorLogger.log(e, e.getMessage());
			}
		}
		parent.appendChild(node);
		return node;
	}

	public boolean canReadQuery(Node node) {
		return false;
	}

	public void readQuery(Node node, TaskList tlist) throws TaskListExternalizerException {
		// doesn't know how to read any queries

	}

	public String getQueryTagNameForElement(IQuery query) {
		return "";
	}

	public String getQueryHitTagName() {
		return TAG_QUERY_HIT;
	}

	public boolean canCreateElementFor(IQueryHit queryHit) {
		return true;
	}

	public Element createQueryHitElement(IQueryHit queryHit, Document doc, Element parent) {
		Element node = doc.createElement(getQueryHitTagName());
		node.setAttribute(NAME, queryHit.getDescription(false));
		node.setAttribute(HANDLE, queryHit.getHandleIdentifier());
		node.setAttribute(PRIORITY, queryHit.getPriority());
		if (queryHit.isCompleted()) {
			node.setAttribute(COMPLETE, TRUE);
		} else {
			node.setAttribute(COMPLETE, FALSE);
		}
		parent.appendChild(node);
		return null;
	}

	public boolean canReadQueryHit(Node node) {
		return false;
	}

	public void readQueryHit(Node node, TaskList tlist, IQuery query) throws TaskListExternalizerException {
		// doesn't know how to read a query hit
	}

	public List<ITaskListExternalizer> getDelegateExternalizers() {
		return delegateExternalizers;
	}
}
