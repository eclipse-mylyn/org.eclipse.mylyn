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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.util.Date;

import org.eclipse.mylar.internal.bugzilla.ui.OfflineReportsFile;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskExternalizationException;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.DelegatingTaskExternalizer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String STATUS_RESO = "RESO";

	private static final String STATUS_NEW = "NEW";

	private static final String LAST_DATE = "LastDate";

	private static final String DIRTY = "Dirty";

	private static final String SYNC_STATE = "offlineSyncState";

	private static final String TAG_BUGZILLA_QUERY_HIT = "Bugzilla" + KEY_QUERY_HIT;

	private static final String TAG_BUGZILLA_QUERY = "Bugzilla" + KEY_QUERY;

	private static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustom" + KEY_QUERY;

	private static final String TAG_BUGZILLA_REPORT = "BugzillaReport";

	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		if (query instanceof BugzillaRepositoryQuery) {
			if (((BugzillaRepositoryQuery)query).isCustomQuery()) {
				return TAG_BUGZILLA_CUSTOM_QUERY;
			} else {
				return TAG_BUGZILLA_QUERY;
			}
		}
		return "";
	}

	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY) || node.getNodeName().equals(TAG_BUGZILLA_QUERY);
	}

	public AbstractRepositoryQuery readQuery(Node node, TaskList taskList) throws TaskExternalizationException {
		boolean hasCaughtException = false;
		Element element = (Element) node;
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(
				element.getAttribute(KEY_REPOSITORY_URL), 
				element.getAttribute(KEY_QUERY_STRING), 
				element.getAttribute(KEY_NAME),
				element.getAttribute(KEY_QUERY_MAX_HITS), taskList);
		if (node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			query.setCustomQuery(true);
		}
		if(!element.getAttribute(KEY_LAST_REFRESH).equals("")) {
			Date refreshDate = new Date();
			try {
				refreshDate.setTime(Long.parseLong(element.getAttribute(KEY_LAST_REFRESH)));
				query.setLastRefresh(refreshDate);
			} catch (NumberFormatException e) {
				// ignore
			}			
		}
		
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				readQueryHit(child, taskList, query);
			} catch (TaskExternalizationException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException) {
			throw new TaskExternalizationException("Failed to load all tasks");
		} else {
			return query;
		}
	}

//	public void readRegistry(Node node, TaskList taskList) throws TaskExternalizationException {
//		boolean hasCaughtException = false;
//		NodeList list = node.getChildNodes();
//		for (int i = 0; i < list.getLength(); i++) {
//			try {
//				Node child = list.item(i);
//				ITask task = readTask(child, taskList, null, null);
//				if (task instanceof AbstractRepositoryTask) {
//					taskList.addTaskToArchive((AbstractRepositoryTask)task);
//				}
//			} catch (TaskExternalizationException e) {
//				hasCaughtException = true;
//			}
//		}
//
//		if (hasCaughtException)
//			throw new TaskExternalizationException("Failed to restore all tasks");
//	}

//	public boolean canCreateElementFor(ITaskContainer cat) {
//		return false;
//	}

	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof BugzillaRepositoryQuery;
	}

	public boolean canCreateElementFor(ITask task) {
		return task instanceof BugzillaTask;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		BugzillaTask bugzillaTask = (BugzillaTask) task;
		if (bugzillaTask.getLastRefresh() != null) {
			node.setAttribute(LAST_DATE, new Long(bugzillaTask.getLastRefresh().getTime()).toString());
		} else {
			node.setAttribute(LAST_DATE, new Long(new Date().getTime()).toString());
		}

		node.setAttribute(SYNC_STATE, bugzillaTask.getSyncState().toString());

		if (bugzillaTask.isDirty()) {
			node.setAttribute(DIRTY, VAL_TRUE);
		} else {
			node.setAttribute(DIRTY, VAL_FALSE);
		}
		return node;
	}

	@Override
	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	@Override
	public ITask readTask(Node node, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for bug report");
		}
		if (element.hasAttribute(KEY_LABEL)) {
			label = element.getAttribute(KEY_LABEL);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}
		BugzillaTask task = new BugzillaTask(handle, label, false);
		readTaskInfo(task, taskList, element, parent, category);

		task.setCurrentlyDownloading(false);
		task.setLastRefresh(new Date(new Long(element.getAttribute("LastDate")).longValue()));

		if (element.getAttribute("Dirty").compareTo("true") == 0) {
			task.setDirty(true);
		} else {
			task.setDirty(false);
		}
		try {
			if (readBugReport(task) == false) {
				MylarStatusHandler.log("Failed to read bug report", null);
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Failed to read bug report");
		}

		if (element.hasAttribute(SYNC_STATE)) {
			String syncState = element.getAttribute(SYNC_STATE);
			if (syncState.compareTo(RepositoryTaskSyncState.SYNCHRONIZED.toString()) == 0) {
				task.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
			} else if (syncState.compareTo(RepositoryTaskSyncState.INCOMING.toString()) == 0) {
				task.setSyncState(RepositoryTaskSyncState.INCOMING);
			} else if (syncState.compareTo(RepositoryTaskSyncState.OUTGOING.toString()) == 0) {
				task.setSyncState(RepositoryTaskSyncState.OUTGOING);
			} else if (syncState.compareTo(RepositoryTaskSyncState.CONFLICT.toString()) == 0) {
				task.setSyncState(RepositoryTaskSyncState.CONFLICT);
			}
		}

		// TODO: put back, checking for null category?
//		taskList.internalAddTask(task, category);
		return task;
	}

	/**
	 * TODO: move?
	 */
	public boolean readBugReport(BugzillaTask bugzillaTask) {		
		IBugzillaBug tempBug = OfflineReportsFile.findBug(bugzillaTask.getRepositoryUrl(), AbstractRepositoryTask.getTaskIdAsInt(bugzillaTask.getHandleIdentifier()));
		if (tempBug == null) {
			bugzillaTask.setBugReport(null);
			return true;
		}
		bugzillaTask.setBugReport((BugzillaReport)tempBug);

		if (bugzillaTask.getBugReport().hasChanges())
			bugzillaTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
		return true;
	}

	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	public void readQueryHit(Node node, TaskList taskList, AbstractRepositoryQuery query) throws TaskExternalizationException {
		Element element = (Element) node;
		String handle;
		String label;
		String priority;
		String status;
		if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for bug report");
		}
		if (element.hasAttribute(KEY_NAME)) {
			label = element.getAttribute(KEY_NAME);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}
		if (element.hasAttribute(KEY_PRIORITY)) {
			priority = element.getAttribute(KEY_PRIORITY);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}
		
		status = STATUS_NEW;
		if (element.hasAttribute(KEY_COMPLETE)) {
			status = element.getAttribute(KEY_COMPLETE);
			if (status.equals(VAL_TRUE)) {
				status = STATUS_RESO; 
			} 
		} 
		BugzillaQueryHit hit = new BugzillaQueryHit(label, priority, query.getRepositoryUrl(), AbstractRepositoryTask
				.getTaskIdAsInt(handle), null, status);
		ITask correspondingTask = taskList.getTask(hit.getHandleIdentifier());
		if (correspondingTask instanceof BugzillaTask) {
			hit.setCorrespondingTask((BugzillaTask)correspondingTask);
		}
		
		query.addHit(hit);
	}

	@Override
	public String getTaskTagName() {
		return TAG_BUGZILLA_REPORT;
	}

	@Override
	public String getQueryHitTagName() {
		return TAG_BUGZILLA_QUERY_HIT;
	}
}
