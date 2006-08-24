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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.OfflineTaskManager;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskExternalizationException;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
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

	private static final String KEY_OLD_LAST_DATE = "LastDate";

	private static final String KEY_DIRTY = "Dirty";

	private static final String SYNC_STATE = "offlineSyncState";

	private static final String TAG_BUGZILLA_QUERY_HIT = "Bugzilla" + KEY_QUERY_HIT;

	private static final String TAG_BUGZILLA_QUERY = "Bugzilla" + KEY_QUERY;

	private static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustom" + KEY_QUERY;

	private static final String TAG_BUGZILLA_REPORT = "BugzillaReport";

	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		if (query instanceof BugzillaRepositoryQuery) {
			if (((BugzillaRepositoryQuery) query).isCustomQuery()) {
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
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(element.getAttribute(KEY_REPOSITORY_URL), element
				.getAttribute(KEY_QUERY_STRING), element.getAttribute(KEY_NAME), element
				.getAttribute(KEY_QUERY_MAX_HITS), taskList);
		if (node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			query.setCustomQuery(true);
		}
		if (element.getAttribute(KEY_LAST_REFRESH) != null && !element.getAttribute(KEY_LAST_REFRESH).equals("")) {
			query.setLastRefreshTimeStamp(element.getAttribute(KEY_LAST_REFRESH));
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

	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof BugzillaRepositoryQuery;
	}

	public boolean canCreateElementFor(ITask task) {
		return task instanceof BugzillaTask;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		BugzillaTask bugzillaTask = (BugzillaTask) task;
		if (bugzillaTask.getLastModifiedDateStamp() != null) {
			node.setAttribute(KEY_LAST_MOD_DATE, bugzillaTask.getLastModifiedDateStamp());
		}

		if(bugzillaTask.isNotified()) {
			node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_TRUE);		
		} else {
			node.setAttribute(KEY_NOTIFIED_INCOMING, VAL_FALSE);
		}
		
		node.setAttribute(SYNC_STATE, bugzillaTask.getSyncState().toString());

		if (bugzillaTask.isDirty()) {
			node.setAttribute(KEY_DIRTY, VAL_TRUE);
		} else {
			node.setAttribute(KEY_DIRTY, VAL_FALSE);
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
		super.readTaskInfo(task, taskList, element, parent, category);

		task.setCurrentlySynchronizing(false);
		if (element.hasAttribute(KEY_LAST_MOD_DATE) && !element.getAttribute(KEY_LAST_MOD_DATE).equals("")) {
			task.setModifiedDateStamp(element.getAttribute(KEY_LAST_MOD_DATE));
		} else {
			// migrate to new time stamp 0.5.3 -> 0.6.0
			try {
				if (element.hasAttribute(KEY_OLD_LAST_DATE)) {
					String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
					SimpleDateFormat delta_ts_format = new SimpleDateFormat(DATE_FORMAT_2);
					String oldDateStamp = "";
					try {
						oldDateStamp = delta_ts_format.format(new Date(
								new Long(element.getAttribute(KEY_OLD_LAST_DATE)).longValue()));
						task.setModifiedDateStamp(oldDateStamp);
					} catch (NumberFormatException e) {
						// For those who may have been working from head...
						Date parsedDate = delta_ts_format.parse(element.getAttribute(KEY_OLD_LAST_DATE));
						if (parsedDate != null) {
							oldDateStamp = element.getAttribute(KEY_OLD_LAST_DATE);
							task.setModifiedDateStamp(oldDateStamp);
						}
					}
				}
			} catch (Exception e) {
				// invalid date format/parse
			}
		}

		if (element.getAttribute(KEY_DIRTY).compareTo(VAL_TRUE) == 0) {
			task.setDirty(true);
		} else {
			task.setDirty(false);
		}
		
		if (element.hasAttribute(KEY_NOTIFIED_INCOMING) && element.getAttribute(KEY_NOTIFIED_INCOMING).compareTo(VAL_TRUE) == 0) {
			task.setNotified(true);
		} else {
			task.setNotified(false);
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
				
		return task;
	}

	/**
	 * TODO: move?
	 */
	public boolean readBugReport(BugzillaTask bugzillaTask) {
		RepositoryTaskData tempBug = OfflineTaskManager.findBug(bugzillaTask.getRepositoryUrl(), AbstractRepositoryTask
				.getTaskId(bugzillaTask.getHandleIdentifier()));
		if (tempBug == null) {
			bugzillaTask.setTaskData(null);
			return true;
		}
		bugzillaTask.setTaskData((RepositoryTaskData) tempBug);

		if (bugzillaTask.getTaskData().hasLocalChanges())
			bugzillaTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
		return true;
	}

	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	public void readQueryHit(Node node, TaskList taskList, AbstractRepositoryQuery query)
			throws TaskExternalizationException {
		Element element = (Element) node;
		String handle;
		String status;
		if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for bug report");
		}

		status = STATUS_NEW;
		if (element.hasAttribute(KEY_COMPLETE)) {
			status = element.getAttribute(KEY_COMPLETE);
			if (status.equals(VAL_TRUE)) {
				status = STATUS_RESO;
			}
		}
		BugzillaQueryHit hit = new BugzillaQueryHit("", "", query.getRepositoryUrl(), AbstractRepositoryTask
				.getTaskId(handle), null, status);
		readQueryHitInfo(hit, taskList, query, element);
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
