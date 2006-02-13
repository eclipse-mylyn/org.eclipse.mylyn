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

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.DelegatingTaskExternalizer;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskContainer;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskExternalizationException;
import org.eclipse.mylar.internal.tasklist.TaskList;
import org.eclipse.mylar.internal.tasklist.TaskListManager;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The wierd thing here is that the registry gets read in as a normal category,
 * but gets written out by createRegistry
 * 
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class BugzillaTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String STATUS_RESO = "RESO";

	private static final String STATUS_NEW = "NEW";

	public static final String BUGZILLA_ARCHIVE_LABEL = TaskListManager.ARCHIVE_CATEGORY_DESCRIPTION + " ("
			+ BugzillaPlugin.REPOSITORY_KIND + ")";

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

	private AbstractRepositoryClient repositoryClient;
	
	public BugzillaTaskExternalizer() {
		repositoryClient = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
	} 
	
	@Override
	public void createRegistry(Document doc, Node parent) {
		Element node = doc.createElement(BUGZILLA_TASK_REGISTRY);
		for (ITask task : repositoryClient.getArchiveTasks()) {
			try {
				createTaskElement(task, doc, node);
			} catch (Exception e) {
				MylarStatusHandler.log(e, e.getMessage());
			}

		}
		parent.appendChild(node);
	}

	public AbstractRepositoryClient getRepositoryClient() {
		return repositoryClient;
	}
	
	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(getCategoryTagName()) || node.getNodeName().equals(BUGZILLA_TASK_REGISTRY);
	}

	@Override
	public void readCategory(Node node, TaskList taskList) throws TaskExternalizationException {
		Element element = (Element) node;
		if (element.getNodeName().equals(BUGZILLA_TASK_REGISTRY)) {
			readRegistry(node, taskList);
		} else {
			BugzillaRepositoryQuery cat = new BugzillaRepositoryQuery(element.getAttribute(REPOSITORY_URL), element
					.getAttribute(URL), element.getAttribute(DESCRIPTION), element.getAttribute(MAX_HITS));
			taskList.internalAddQuery(cat);
		}
	}

	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		if (query instanceof BugzillaCustomRepositoryQuery) {
			return TAG_BUGZILLA_CUSTOM_QUERY;
		} else if (query instanceof BugzillaRepositoryQuery) {
			return TAG_BUGZILLA_QUERY;
		}
		return "";
	}

	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY) || node.getNodeName().equals(TAG_BUGZILLA_QUERY);
	}

	public void readQuery(Node node, TaskList tlist) throws TaskExternalizationException {
		boolean hasCaughtException = false;
		Element element = (Element) node;
		AbstractRepositoryQuery cat = null;
		if (node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			cat = new BugzillaCustomRepositoryQuery(
					element.getAttribute(REPOSITORY_URL), 
					element.getAttribute(QUERY_STRING), 
					element.getAttribute(NAME),
					element.getAttribute(MAX_HITS));
		} else if (node.getNodeName().equals(TAG_BUGZILLA_QUERY)) {
			cat = new BugzillaRepositoryQuery(
					element.getAttribute(REPOSITORY_URL), 
					element.getAttribute(QUERY_STRING),
					element.getAttribute(NAME), 
					element.getAttribute(MAX_HITS));
		}
		if (cat != null) {
			tlist.internalAddQuery(cat);
		}
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				readQueryHit(child, tlist, cat);
			} catch (TaskExternalizationException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException)
			throw new TaskExternalizationException("Failed to load all tasks");
	}

	public void readRegistry(Node node, TaskList taskList) throws TaskExternalizationException {
		boolean hasCaughtException = false;
		NodeList list = node.getChildNodes();
		TaskCategory archiveCategory = new TaskCategory(BUGZILLA_ARCHIVE_LABEL);
		archiveCategory.setIsArchive(true);
		taskList.internalAddCategory(archiveCategory);
		repositoryClient.setArchiveCategory(archiveCategory);
//		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().setTaskRegistyCategory(cat);
		for (int i = 0; i < list.getLength(); i++) {
			try {
				Node child = list.item(i);
				ITask task = readTask(child, taskList, null, null);
				if (task instanceof AbstractRepositoryTask) {
					repositoryClient.addTaskToArchive((AbstractRepositoryTask)task);
//					BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskArchive(
//							(BugzillaTask) task);
				}
			} catch (TaskExternalizationException e) {
				hasCaughtException = true;
			}
		}

		if (hasCaughtException)
			throw new TaskExternalizationException("Failed to restore all tasks");
	}

	public boolean canCreateElementFor(ITaskContainer cat) {
		return false;
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
	public ITask readTask(Node node, TaskList tlist, ITaskContainer category, ITask parent)
			throws TaskExternalizationException {
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for bug report");
		}
		if (element.hasAttribute(LABEL)) {
			label = element.getAttribute(LABEL);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}
		BugzillaTask task = new BugzillaTask(handle, label, false);
		readTaskInfo(task, tlist, element, category, parent);

//		task.setBugzillaTaskState(BugzillaTaskState.FREE);
		task.setCurrentlyDownloading(false);
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

		AbstractRepositoryClient repositoryClient = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
		if (repositoryClient != null) {
			repositoryClient.addTaskToArchive(task);
		}
		return task;
//		ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(task);
//		if (taskHandler != null) {
//			ITask addedTask = taskHandler.addTaskToArchive(task);
//			if (addedTask instanceof BugzillaTask)
//				task = (BugzillaTask) addedTask;
//		}
	}

	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	public void readQueryHit(Node node, TaskList tlist, AbstractRepositoryQuery query) throws TaskExternalizationException {
		Element element = (Element) node;
		String handle;
		String label;
		String priority;
		String status;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for bug report");
		}
		if (element.hasAttribute(NAME)) {
			label = element.getAttribute(NAME);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}
		if (element.hasAttribute(PRIORITY)) {
			priority = element.getAttribute(PRIORITY);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}
		
		status = STATUS_NEW;
		if (element.hasAttribute(COMPLETE)) {
			status = element.getAttribute(COMPLETE);
			if (status.equals(TRUE)) {
				status = STATUS_RESO;
			} 
		} 
//		else {
//			throw new TaskExternalizationException("Description not stored for bug report");
//		}
		BugzillaQueryHit hit = new BugzillaQueryHit(label, priority, query.getRepositoryUrl(), TaskRepositoryManager
				.getTaskIdAsInt(handle), null, status);
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
