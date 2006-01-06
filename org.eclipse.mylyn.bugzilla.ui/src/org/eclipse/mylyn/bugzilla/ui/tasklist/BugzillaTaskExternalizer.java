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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.util.Date;

import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugReportSyncState;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.DelegatingLocalTaskExternalizer;
import org.eclipse.mylar.tasklist.internal.TaskListExternalizerException;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The wierd thing here is that the registry gets read in as a normal category,
 * but gets written out by createRegistry
 * 
 * @author Mik Kersten and Ken Sueda
 */
public class BugzillaTaskExternalizer extends DelegatingLocalTaskExternalizer {

	private static final String STATUS_RESO = "RESO";

	private static final String STATUS_NEW = "NEW";

	public static final String BUGZILLA_ARCHIVE_LABEL = "Archived Reports "
			+ DelegatingLocalTaskExternalizer.LABEL_AUTOMATIC;

	private static final String BUGZILLA = "Bugzilla";

	private static final String LAST_DATE = "LastDate";

	private static final String DIRTY = "Dirty";

	private static final String SYNC_STATE = "offlineSyncState";

	private static final String DESCRIPTION = "Description";

	private static final String URL = "URL";

	private static final String BUGZILLA_TASK_REGISTRY = "BugzillaTaskRegistry" + TAG_CATEGORY;

	private static final String TAG_BUGZILLA_CATEGORY = "BugzillaQuery" + TAG_CATEGORY;

	private static final String TAG_BUGZILLA_QUERY_HIT = "Bugzilla" + TAG_QUERY_HIT;

	private static final String TAG_BUGZILLA_QUERY = "Bugzilla" + TAG_QUERY;

	private static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustom" + TAG_QUERY;

	private static final String TAG_TASK = "BugzillaReport";

	@Override
	public void createRegistry(Document doc, Node parent) {
		Element node = doc.createElement(BUGZILLA_TASK_REGISTRY);
		for (BugzillaTask task : BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().getBugzillaTaskRegistry()
				.values()) {
			try {
				createTaskElement(task, doc, node);
			} catch (Exception e) {
				MylarStatusHandler.log(e, e.getMessage());
			}

		}
		parent.appendChild(node);
	}

	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(getCategoryTagName()) || node.getNodeName().equals(BUGZILLA_TASK_REGISTRY);
	}

	@Override
	public void readCategory(Node node, TaskList taskList) throws TaskListExternalizerException {
		Element e = (Element) node;
		if (e.getNodeName().equals(BUGZILLA_TASK_REGISTRY)) {
			readRegistry(node, taskList);
		} else {
			BugzillaQueryCategory cat = new BugzillaQueryCategory(e.getAttribute(DESCRIPTION), e.getAttribute(URL), e
					.getAttribute(MAX_HITS));
			taskList.internalAddQuery(cat);
		}
	}

	public String getQueryTagNameForElement(IQuery query) {
		if (query instanceof BugzillaCustomQueryCategory) {
			return TAG_BUGZILLA_CUSTOM_QUERY;
		} else if (query instanceof BugzillaQueryCategory) {
			return TAG_BUGZILLA_QUERY;
		}
		return "";
	}

	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY) || node.getNodeName().equals(TAG_BUGZILLA_QUERY);
	}

	public void readQuery(Node node, TaskList tlist) throws TaskListExternalizerException {
		boolean hasCaughtException = false;
		Element element = (Element) node;
		IQuery cat = null;
		if (node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			cat = new BugzillaCustomQueryCategory(element.getAttribute(NAME), element.getAttribute(QUERY_STRING), element
					.getAttribute(MAX_HITS));
		} else if (node.getNodeName().equals(TAG_BUGZILLA_QUERY)) {
			cat = new BugzillaQueryCategory(element.getAttribute(NAME), element.getAttribute(QUERY_STRING), element
					.getAttribute(MAX_HITS));
		}
		if (cat != null) {
			tlist.internalAddQuery(cat);
		}
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				readQueryHit(child, tlist, cat);
			} catch (TaskListExternalizerException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException)
			throw new TaskListExternalizerException("Failed to load all tasks");
	}

	public void readRegistry(Node node, TaskList taskList) throws TaskListExternalizerException {
		boolean hasCaughtException = false;
		NodeList list = node.getChildNodes();
		TaskCategory cat = new TaskCategory(BUGZILLA_ARCHIVE_LABEL);
		cat.setIsArchive(true);
		taskList.internalAddCategory(cat);
		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().setTaskRegistyCategory(cat);
		for (int i = 0; i < list.getLength(); i++) {
			try {
				Node child = list.item(i);
				ITask task = readTask(child, taskList, null, null);
				if (task instanceof BugzillaTask) {
					BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry(
							(BugzillaTask) task);
				}
			} catch (TaskListExternalizerException e) {
				hasCaughtException = true;
			}
		}

		if (hasCaughtException)
			throw new TaskListExternalizerException("Failed to restore all tasks");
	}

	public boolean canCreateElementFor(ITaskCategory cat) {
		return false;
	}

	public boolean canCreateElementFor(ITask task) {
		return task instanceof BugzillaTask;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		BugzillaTask bugzillaTask = (BugzillaTask) task;
		node.setAttribute(BUGZILLA, TRUE);
		if (bugzillaTask.getLastRefresh() != null) {
			node.setAttribute(LAST_DATE, new Long(bugzillaTask.getLastRefresh().getTime()).toString());
		} else {
			node.setAttribute(LAST_DATE, new Long(new Date().getTime()).toString());
		}

		node.setAttribute(SYNC_STATE, bugzillaTask.getSyncState().toString());

		if (bugzillaTask.isDirty()) {
			node.setAttribute(DIRTY, TRUE);
		} else {
			node.setAttribute(DIRTY, FALSE);
		}
		return node;
	}

	@Override
	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	@Override
	public ITask readTask(Node node, TaskList tlist, ITaskCategory category, ITask parent)
			throws TaskListExternalizerException {
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new TaskListExternalizerException("Handle not stored for bug report");
		}
		if (element.hasAttribute(LABEL)) {
			label = element.getAttribute(LABEL);
		} else {
			throw new TaskListExternalizerException("Description not stored for bug report");
		}
		BugzillaTask task = new BugzillaTask(handle, label, true, false);
		readTaskInfo(task, tlist, element, category, parent);

		task.setState(BugTaskState.FREE);
		task.setLastRefresh(new Date(new Long(element.getAttribute("LastDate")).longValue()));

		if (element.getAttribute("Dirty").compareTo("true") == 0) {
			task.setDirty(true);
		} else {
			task.setDirty(false);
		}
		try {
			if (task.readBugReport() == false) {
				MylarStatusHandler.log("Failed to read bug report", null);
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Failed to read bug report");
		}

		if (element.hasAttribute(SYNC_STATE)) {
			String syncState = element.getAttribute(SYNC_STATE);
			if (syncState.compareTo(BugReportSyncState.OK.toString()) == 0) {
				task.setSyncState(BugReportSyncState.OK);
			} else if (syncState.compareTo(BugReportSyncState.INCOMMING.toString()) == 0) {
				task.setSyncState(BugReportSyncState.INCOMMING);
			} else if (syncState.compareTo(BugReportSyncState.OUTGOING.toString()) == 0) {
				task.setSyncState(BugReportSyncState.OUTGOING);
			} else if (syncState.compareTo(BugReportSyncState.CONFLICT.toString()) == 0) {
				task.setSyncState(BugReportSyncState.CONFLICT);
			}
		}

		ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(task);
		if (taskHandler != null) {
			ITask addedTask = taskHandler.taskAdded(task);
			if (addedTask instanceof BugzillaTask)
				task = (BugzillaTask) addedTask;
		}
		return task;
	}

	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	public void readQueryHit(Node node, TaskList tlist, IQuery query) throws TaskListExternalizerException {
		Element element = (Element) node;
		String handle;
		String label;
		String priority;
		String status;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new TaskListExternalizerException("Handle not stored for bug report");
		}
		if (element.hasAttribute(NAME)) {
			label = element.getAttribute(NAME);
		} else {
			throw new TaskListExternalizerException("Description not stored for bug report");
		}
		if (element.hasAttribute(PRIORITY)) {
			priority = element.getAttribute(PRIORITY);
		} else {
			throw new TaskListExternalizerException("Description not stored for bug report");
		}
		if (element.hasAttribute(COMPLETE)) {
			status = element.getAttribute(COMPLETE);
			if (status.equals(TRUE))
				status = STATUS_RESO;
			else
				status = STATUS_NEW;
		} else {
			throw new TaskListExternalizerException("Description not stored for bug report");
		}
		BugzillaHit hit = new BugzillaHit(label, priority, BugzillaTask.getBugId(handle), null, status);
		query.addHit(hit);
	}

	@Override
	public String getCategoryTagName() {
		return TAG_BUGZILLA_CATEGORY;
	}

	@Override
	public String getTaskTagName() {
		return TAG_TASK;
	}

	@Override
	public String getQueryHitTagName() {
		return TAG_BUGZILLA_QUERY_HIT;
	}
}
